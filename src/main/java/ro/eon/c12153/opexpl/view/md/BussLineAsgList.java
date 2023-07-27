package ro.any.c12153.opexpl.view.md;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexpl.entities.BussLineAsg;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.services.BussLineAsgServ;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "bussasglist")
@ViewScoped
public class BussLineAsgList implements Serializable, SelectTableView<BussLineAsg>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(BussLineAsgList.class.getName());

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<BussLineAsg> item;
    
    private String initError;
    private CoArea coarea;
    private List<BussLineAsg> list;
    private BussLineAsg selected;
    private String[] filterValues;
    private List<BussLineAsg> filtered;
    
    public void clearFilters(){
        this.filterValues = new String[]{"","",""};
    }
    
    @PostConstruct
    private void init(){
        try {           
            this.clearFilters(); 
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String co = Optional.ofNullable(params.get("co"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            this.coarea = CoAreaServ.getByCod(Utils.paramDecode(co), cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            if (!Boolean.TRUE.equals(this.coarea.getAlocare()))
                throw new Exception(App.getBeanMess("title.coarea.nasgn", clocale));
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            rezultat += (this.coarea == null ? "" : "&co=" + Utils.paramEncode(this.coarea.getCod()));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        try{
            this.clearFilters();
            this.list = BussLineAsgServ.getListAsignToCoArea(this.coarea.getCod(), cuser.getUname());
        } catch (Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.bline.asg.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        BussLineAsg rezultat = new BussLineAsg();
        rezultat.setCoarea(this.coarea.getCod());
        rezultat.setCoarea_nume(this.coarea.getNume());
        this.item.setSelected(rezultat);
        this.item.initLists();
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new BussLineAsg(this.selected.getJson()));
                if (initLists) this.item.initLists();
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void clear(){
        this.item.clear();
        this.dialog.clear();
    }

    @Override
    public String getInitError() {
        return initError;
    }

    public CoArea getCoarea() {
        return coarea;
    }

    @Override
    public List<BussLineAsg> getList() {
        return list;
    }

    public BussLineAsg getSelected() {
        return selected;
    }

    @Override
    public void setSelected(BussLineAsg selected) {
        this.selected = selected;
    }

    public String[] getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(String[] filterValues) {
        this.filterValues = filterValues;
    }

    public List<BussLineAsg> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<BussLineAsg> filtered) {
        this.filtered = filtered;
    }
}
