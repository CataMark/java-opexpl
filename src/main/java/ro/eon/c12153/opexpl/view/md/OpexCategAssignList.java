package ro.any.c12153.opexpl.view.md;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.util.Constants;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostDriver;
import ro.any.c12153.opexpl.entities.OpexCateg;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostDriverServ;
import ro.any.c12153.opexpl.services.OpexCategServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "ocategaslist")
@ViewScoped
public class OpexCategAssignList implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(OpexCategAssignList.class.getName());

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject OpexCategAssignItem item;
    
    private String initError;
    private CoArea coarea;
    private CostDriver cdriver;    
    private List<OpexCateg> list;
    private OpexCateg selected;
    private String[] filterValues;
    private List<OpexCateg> filtered;
    
    public void clearFilters(){
        this.filterValues = new String[]{"",""};
    }
    
    @PostConstruct
    private void init(){
        try {
            this.clearFilters();
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String co = Optional.ofNullable(params.get("co"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            String cd = Optional.ofNullable(params.get("cd"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.cdriver.not", clocale)));
            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.coarea = CoAreaServ.getByCod(Utils.paramDecode(co), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            String cd_decoded = Utils.paramDecode(cd);
                            String co_decoded = Utils.paramDecode(co);
                            this.cdriver = CostDriverServ.getAssignByCod(cd_decoded, co_decoded, cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("err.cdriver.not", clocale)));
                            if(!CostDriverServ.checkIsAssigned(co_decoded, cd_decoded, cuser.getUname()))
                                throw new Exception(App.getBeanMess("err.cdriver.nasg.coarea", clocale));
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).get(30, TimeUnit.SECONDS);    
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            rezultat += (this.coarea == null ? "" : "&co=" + Utils.paramEncode(this.coarea.getCod())) +
                        (this.cdriver == null ? "" : "&cd=" + Utils.paramEncode(this.cdriver.getCod()));            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        try{
            this.clearFilters();
            this.list= OpexCategServ.getAssignByCostDriver(this.coarea.getCod(), this.cdriver.getCod(), cuser.getUname());
            
        } catch (Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.opexcat.listinit", clocale), ex.getMessage()));
        }
    }
    
    public void newItem(){
        OpexCateg rezultat = new OpexCateg();
        rezultat.setCost_driver(this.cdriver.getCod());
        rezultat.setCost_driver_nume(this.cdriver.getNume());
        rezultat.setBlocat(this.cdriver.getBlocat());
        this.item.setSelected(rezultat);
        this.item.initLists();
    }
    
    public void passSelected(boolean initLists){
        try {            
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new OpexCateg(this.selected.getJson()));
                if (initLists) this.item.initLists();
                
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }
    
    public void clear(){
        this.dialog.clear();
        this.item.clear();
    }
    
    public String exportFunction(Boolean value){
        String rezultat = "";
        if (value != null) rezultat = Boolean.toString(value).toUpperCase();
        return rezultat;
    }
    
    public void exportByCoarea(){        
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ExternalContext econtext = fcontext.getExternalContext();
        econtext.responseReset();
        econtext.setResponseCharacterEncoding(StandardCharsets.UTF_8.name());
        econtext.setResponseContentType(Utils.MEDIA_EXCEL);
        econtext.setResponseHeader("Content-Disposition", "attachment; filename=raport.xlsx");
        econtext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<>()); //setare cookie pentru PrimeFaces.monitorDownload
               
        try(OutputStream stream = econtext.getResponseOutputStream();){
            OpexCategServ.listAssignByCoAreaToXlsx(this.coarea.getCod(), cuser.getUname(), stream);
            
        }catch(Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.data.downt", clocale), ex.getMessage()));
        }
        fcontext.responseComplete();
    }

    public String getInitError() {
        return initError;
    }

    public CoArea getCoarea() {
        return coarea;
    }

    public CostDriver getCdriver() {
        return cdriver;
    }

    public List<OpexCateg> getList() {
        return list;
    }

    public OpexCateg getSelected() {
        return selected;
    }

    public void setSelected(OpexCateg selected) {
        this.selected = selected;
    }

    public String[] getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(String[] filterValues) {
        this.filterValues = filterValues;
    }

    public List<OpexCateg> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<OpexCateg> filtered) {
        this.filtered = filtered;
    }
}
