package ro.any.c12153.opexpl.view.model;

import java.io.Serializable;
import java.util.HashMap;
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
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.ModelServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "apisave")
@ViewScoped
public class ApiSave implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ApiSave.class.getName());
    
    private static Map<String, String> flag;

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    
    private Map<String, Object> data;
    
    @PostConstruct
    private void init(){
        try {
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
                            if(!Boolean.FALSE.equals(this.dataset.getIncheiat()))
                                throw new Exception(App.getBeanMess("err.cgroup.dset.close", clocale));;
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).get(30, TimeUnit.SECONDS);
            if (flag == null) flag = new HashMap<>();
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
        try{
            this.data = ModelServ.getSumar(this.dataset.getId(), this.coarea.getCod(), cuser.getUname())
                    .orElse(new HashMap<>());
            
        } catch (Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.rest.sumarinit", clocale), ex.getMessage()));
        }
    }
    
    public void procesare(){
        boolean putFlag = false;
        String flagKey = this.coarea.getCod().concat("_").concat(this.dataset.getId().toString());        
        try {
            if (flag.containsKey(flagKey))
                throw new Exception(App.getBeanMess("err.rest.started", clocale) + " " + flag.get(flagKey));
            flag.put(flagKey, cuser.getUname().toUpperCase());
            putFlag = true;
            
            ModelServ.persistAlocare(this.dataset.getId(), this.coarea.getCod(), cuser.getUname());
            this.datainit();
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.rest.proces", clocale), App.getBeanMess("info.success", clocale)));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.rest.proces", clocale), ex.getMessage()));
        } finally {
            if (putFlag) flag.remove(flagKey);
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

    public Map<String, Object> getData() {
        return data;
    }
}
