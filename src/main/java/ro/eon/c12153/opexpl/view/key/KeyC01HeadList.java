package ro.any.c12153.opexpl.view.key;

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
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.util.Constants;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.KeyHead;
import ro.any.c12153.opexpl.entities.KeyVal;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.DataSetPerServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.KeyC01Serv;
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
@Named(value = "kc01hlist")
@ViewScoped
public class KeyC01HeadList implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(KeyC01HeadList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject KeyC01HeadItem item;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private List<Short> dataset_ani;
    
    private List<KeyHead> list;
    private KeyHead selected;
    private String[] filterValues;  
    private List<KeyHead> filtered;
    private String finishScript;
    
    public void clearFilters(){
        this.filterValues = new String[]{"","","",""};
    }
    
    @PostConstruct
    private void init(){
        try {
            this.clearFilters();            
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String co = Optional.ofNullable(params.get("co"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            String ds = Optional.ofNullable(params.get("ds"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.per.noset", clocale)));
            Optional<String> ky = Optional.ofNullable(params.get("ky"));
            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.coarea = CoAreaServ.getByCod(Utils.paramDecode(co), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
                            if(!Boolean.TRUE.equals(this.coarea.getAlocare()))
                                throw new Exception(App.getBeanMess("title.coarea.nasgn", clocale));
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.dataset = DataSetServ.getById(Integer.valueOf(Utils.paramDecode(ds)), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.per.noset", clocale)));
                            if(!Boolean.FALSE.equals(this.dataset.getActual()))
                                throw new Exception(App.getBeanMess("err.dset.noplan", clocale));
                            if(!Boolean.FALSE.equals(this.dataset.getIncheiat()))
                                throw new Exception(App.getBeanMess("err.cgroup.dset.close", clocale));
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }                        
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            if (ky.isPresent())
                                this.selected = KeyC01Serv.getById(
                                        Integer.valueOf(Utils.paramDecode(ky.get())),
                                        Integer.valueOf(Utils.paramDecode(ds)),
                                        cuser.getUname()
                                ).orElse(null);
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
                        (this.dataset == null ? "" : "&ds=" + Utils.paramEncode(this.dataset.getId().toString())) +
                        (this.selected == null ? "" : "&ky=" + Utils.paramEncode(this.selected.getId().toString()));            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        try {
            this.clearFilters();
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            if (this.dataset_ani == null || this.dataset_ani.isEmpty())
                                this.dataset_ani = DataSetPerServ.getByDataSet(this.dataset.getId(), cuser.getUname()).stream()
                                        .map(x -> x.getAn())
                                        .distinct()
                                        .sorted()
                                        .collect(Collectors.toList()); 
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.list = KeyC01Serv.getAll(this.coarea.getCod(), this.dataset.getId(), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).get(60, TimeUnit.SECONDS);
                
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.listinit", clocale), ex.getMessage()));
        }
    }
    
    public void newItem(){
        KeyHead rezultat = new KeyHead();
        rezultat.setCoarea(this.coarea.getCod());
        rezultat.setTip("C01");
        rezultat.setBlocat(false);
        this.item.setSelected(rezultat);
    }
    
    public void passSelected(boolean initLists){
        try {            
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new KeyHead(this.selected.getJson(true)));
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
        this.finishScript = null;
    }
    
    public double getTotalByCheiAndAn(List<KeyVal> vals, short an){
        if (vals == null || vals.isEmpty()) return 0;
        return vals.stream()
            .filter(x -> x != null && x.getValoare() != null && x.getAn().equals(an))
            .mapToDouble(KeyVal::getValoare)
            .sum();
    }

    public void checkValsAll(){
        try{
            List<KeyHead> rezultat = KeyC01Serv.calcCheckValsAllNotBlocked(this.dataset.getId(), this.coarea.getCod(), cuser.getUname());
            if (rezultat == null || rezultat.isEmpty()){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        App.getBeanMess("title.keys.val.chk", clocale), App.getBeanMess("info.keys.val.ok", clocale)));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        App.getBeanMess("title.keys.val.chk", clocale), App.getBeanMess("info.keys.val.nok", clocale) +
                            rezultat.stream()
                                .map(x -> x.getId() + " " + x.getNume())
                                .collect(Collectors.joining("\n")))
                );
            }            
        } catch (Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.val.chk", clocale), ex.getMessage()));
        }
    }

    public void calcValsAll(){
        try {
            KeyC01Serv.calcAllNotBlocked(this.dataset.getId(), this.coarea.getCod(), cuser.getUname());            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        App.getBeanMess("title.keys.val.calc", clocale), App.getBeanMess("info.success", clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.val.calc", clocale), ex.getMessage()));
        }
    }
    
    public void deleteValsAll(){
        try {
            KeyC01Serv.deleteValsByCoareaAndSet(this.coarea.getCod(), this.dataset.getId(), cuser.getUname());            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        App.getBeanMess("title.keys.val.del", clocale), App.getBeanMess("info.success", clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.val.del", clocale), ex.getMessage()));
        }
    }
    
    public void export(){        
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ExternalContext econtext = fcontext.getExternalContext();
        econtext.responseReset();
        econtext.setResponseCharacterEncoding(StandardCharsets.UTF_8.name());
        econtext.setResponseContentType(Utils.MEDIA_EXCEL);
        econtext.setResponseHeader("Content-Disposition", "attachment; filename=raport.xlsx");
        econtext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<>()); //setare cookie pentru PrimeFaces.monitorDownload
               
        try(OutputStream stream = econtext.getResponseOutputStream();){
            KeyC01Serv.valsToXlsx(this.coarea.getCod(), this.dataset.getId(), cuser.getUname(), stream);
            
        }catch(Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.data.down", clocale), ex.getMessage()));
        }
        fcontext.responseComplete();
    }

    public String getInitError() {
        return initError;
    }
    
    public CoArea getCoarea() {
        return coarea;
    }

    public void setCoarea(CoArea coarea) {
        this.coarea = coarea;
    }

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }
    
    public List<Short> getDataset_ani() {
        return dataset_ani;
    }
    
    public List<KeyHead> getList() {
        return list;
    }

    public KeyHead getSelected() {
        return selected;
    }

    public void setSelected(KeyHead selected) {
        this.selected = selected;
    }

    public String[] getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(String[] filterValues) {
        this.filterValues = filterValues;
    }

    public List<KeyHead> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<KeyHead> filtered) {
        this.filtered = filtered;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
