package ro.any.c12153.opexpl.view.key;

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
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.KeyHead;
import ro.any.c12153.opexpl.entities.KeyVal;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.DataSetPerServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.KeyServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "keyasgnoval")
@ViewScoped
public class KeyAssignNoValList implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(KeyAssignNoValList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private List<Short> dataset_ani;
    
    private List<KeyHead> list;
    private String[] filterValues;
    private List<KeyHead> filtered;
    
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
                        (this.dataset == null ? "" : "&ds=" + Utils.paramEncode(this.dataset.getId().toString()));            
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
                            this.list = KeyServ.getAsignNoVal(this.dataset.getId(), this.coarea.getCod(), cuser.getUname());
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
    
    public boolean getFlagByCheiAndAn(List<KeyVal> vals, short an){
        if (vals == null || vals.isEmpty()) return false;
        double rezultat = vals.stream()
                .filter(x -> x.getAn().equals(an))
                .map(KeyVal::getValoare)
                .findFirst()
                .orElse(Double.valueOf(0));
        return (rezultat != 0);
    }
    
    public String exportFunction(Boolean value){
        String rezultat = "";
        if (value != null) rezultat = Boolean.toString(value).toUpperCase();
        return rezultat;
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
}
