package ro.any.c12153.opexpl.view.records;

import java.io.Serializable;
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
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostCenterMap;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostCenterMapServ;
import ro.any.c12153.opexpl.services.DataSetServ;
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
@Named(value = "cmaplist")
@ViewScoped
public class CostCenterMapList implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CostCenterMapList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject CostCenterMapItem item;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private String[] filterValues;
    private List<CostCenterMap> filtered;
    
    private List<CostCenterMap> notMapped;
    private List<CostCenterMap> mapped;
    private CostCenterMap selected;
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
                            this.dataset = DataSetServ.getById(Integer.valueOf(Utils.paramDecode(ds)), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.per.noset", clocale)));
                            if(!Boolean.FALSE.equals(this.dataset.getActual()))
                                throw new Exception(App.getBeanMess("err.dset.noplan", clocale));
                            if(!Boolean.FALSE.equals(this.dataset.getIncheiat()))
                                throw new Exception(App.getBeanMess("err.cgroup.dset.close", clocale));
                            if(this.dataset.getCompar() == null && this.dataset.getActual_set() == null)
                                throw new Exception(App.getBeanMess("err.dset.noref", clocale));
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
            rezultat += (this.dataset == null ? "" : "&ds=" + Utils.paramEncode(this.dataset.getId().toString())) +
                        (this.coarea == null ? "" : "&co=" + Utils.paramEncode(this.coarea.getCod()));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void listNotMappedInit(){
        try {
            this.clearFilters();
            this.notMapped = CostCenterMapServ.getNotMapped(this.coarea.getHier(), this.dataset.getId(), cuser.getUname());
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.ccenter.map.listinit.nomap", clocale), ex.getMessage()));
        }
    }
    
    public void listMappedInit(){
        try {
            this.clearFilters();
            this.mapped = CostCenterMapServ.getMapped(this.coarea.getHier(), this.dataset.getId(), cuser.getUname());
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.ccenter.map.listinit.wmap", clocale), ex.getMessage()));
        }
    }
    
    public void passSelected(boolean initLists){
        try {            
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new CostCenterMap(this.selected.getJson()));
                if (initLists) this.item.initLists();
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }
    
    public void onTabChange(TabChangeEvent event){
        if (this.filtered != null) this.filtered.clear();
        switch (event.getTab().getId()){
            case "not-mapped":
                this.selected = null;
                this.mapped = null;
                this.listNotMappedInit();
                break;
            case "mapped":
                this.selected = null;
                this.notMapped = null;
                this.listMappedInit();
                break;
            default:
                break;
        }
    }
    
    public void clear(){
        this.dialog.clear();
        this.item.clear();
        this.finishScript = null;
    }
    
    public void deleteAll(){
        try {
            if (!CostCenterMapServ.deleteAll(this.coarea.getHier(), this.dataset.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));            
            this.selected = null;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.ccenter.map.delall", clocale), App.getBeanMess("info.success", clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.ccenter.map.delall", clocale), ex.getMessage()));
        }
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

    public String[] getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(String[] filterValues) {
        this.filterValues = filterValues;
    }

    public List<CostCenterMap> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<CostCenterMap> filtered) {
        this.filtered = filtered;
    }

    public List<CostCenterMap> getNotMapped() {
        return notMapped;
    }
    
    public double getTotalComparSetVal(){
        if (this.notMapped == null || this.notMapped.isEmpty()) return 0;
        return this.notMapped.stream()
                .mapToDouble(CostCenterMap::getVal_compar_set)
                .sum();
    }
    
    public double getTotalActualSetVal(){
        if (this.notMapped == null || this.notMapped.isEmpty()) return 0;
        return this.notMapped.stream()
                .mapToDouble(CostCenterMap::getVal_actual_set)
                .sum();
    }

    public List<CostCenterMap> getMapped() {
        return mapped;
    }

    public CostCenterMap getSelected() {
        return selected;
    }

    public void setSelected(CostCenterMap selected) {
        this.selected = selected;
    }
    
    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
