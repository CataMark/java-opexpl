package ro.any.c12153.opexpl.view.raps;

import java.io.Serializable;
import java.util.ArrayList;
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
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostDriver;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.OpexCateg;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostDriverServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.OpexCategServ;
import ro.any.c12153.opexpl.services.UserOxplServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "cdrapprompt")
@ViewScoped
public class CdRapPrompt implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CdRapPrompt.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private List<CoArea> arii;
    private List<DataSet> seturi;
    private List<CostDriver> cdrivers;
    private List<OpexCateg> ocategs;    
    private boolean userCdriverBound;
    
    private String initError;
    private String arie_sel;
    private String set_sel;
    private String cdriver_sel;
    private String ocateg_sel;
    
    private void initArii(){
        try {
            if (this.userCdriverBound){
                this.arii = CoAreaServ.getListCDriverBound(cuser.getUname(), cuser.getUname());
            } else {
                this.arii = CoAreaServ.getAll(cuser.getUname());
            }
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    private void initSeturi(){
        try {
            this.seturi = DataSetServ.getRaportareAll(cuser.getUname());
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    private void initCdrivers() throws Exception{
        this.cdrivers = new ArrayList<>();
        if (!Utils.stringNotEmpty(this.arie_sel)) return;
        if (this.userCdriverBound){
            this.cdrivers = CostDriverServ.getAssignByRights(this.arie_sel, cuser.getUname());
        } else {
            this.cdrivers = CostDriverServ.getAssignAll(this.arie_sel, cuser.getUname());
        }
    }
    
    private void initOcategs() throws Exception{
        this.ocategs = new ArrayList<>();
        if (!Utils.stringNotEmpty(this.arie_sel) || !Utils.stringNotEmpty(this.cdriver_sel)) return;
        this.ocategs = OpexCategServ.getAssignByCostDriver(this.arie_sel, this.cdriver_sel, cuser.getUname());        
    }
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            Optional<String> co = Optional.ofNullable(params.get("co"));
            if (co.isPresent()) this.arie_sel = Utils.paramDecode(co.get());
            Optional<String> ds = Optional.ofNullable(params.get("ds"));
            if (ds.isPresent()) this.set_sel = Utils.paramDecode(ds.get());
            Optional<String> cd = Optional.ofNullable(params.get("cd"));
            if (cd.isPresent()) this.cdriver_sel = Utils.paramDecode(cd.get());
            Optional<String> oc = Optional.ofNullable(params.get("oc"));
            if (oc.isPresent()) this.ocateg_sel = Utils.paramDecode(oc.get());            
            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(this::initSeturi),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.userCdriverBound = UserOxplServ.checkUserCostDriverBound(cuser.getUname(), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).thenRun(() -> {
                try {
                    CompletableFuture.allOf(
                            CompletableFuture.runAsync(this::initArii),
                            CompletableFuture.runAsync(() -> {
                                try {
                                    this.initCdrivers();
                                } catch (Exception ex) {
                                    throw new CompletionException(ex);
                                }
                            }),
                            CompletableFuture.runAsync(() -> {
                                try {
                                    this.initOcategs();
                                } catch (Exception ex) {
                                    throw new CompletionException(ex);
                                }
                            })
                    ).get(60, TimeUnit.SECONDS);
                } catch (Exception ex) {
                    throw new CompletionException(ex);
                }
            }).get(90, TimeUnit.SECONDS);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {            
            rezultat += (this.arie_sel == null ? "" : "&co=" + Utils.paramEncode(this.arie_sel)) +
                        (this.set_sel == null ? "" : "&ds=" + Utils.paramEncode(this.set_sel)) +
                        (this.cdriver_sel == null ? "" : "&cd=" + Utils.paramEncode(this.cdriver_sel)) +
                        (this.ocateg_sel == null ? "" : "&oc=" + Utils.paramEncode(this.ocateg_sel));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void onCoareaChange(){
        this.cdriver_sel = null;
        this.ocateg_sel = null;
        this.ocategs = null;        
        try {
            this.initCdrivers();
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.cdriver.get", clocale), ex.getMessage()));
        }
    }
    
    public void onCdriverChange(){
        this.ocateg_sel = null;
        try {
            this.initOcategs();
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.opexcat.get", clocale), ex.getMessage()));
        }
    }
    
    public String getInitError() {
        return initError;
    }
    
    public List<CoArea> getArii() {
        return this.arii;
    }

    public List<DataSet> getSeturi() {
        return this.seturi;
    }

    public List<CostDriver> getCdrivers() {
        return this.cdrivers;
    }

    public List<OpexCateg> getOcategs() {
        return this.ocategs;
    }

    public String getArie_sel() {
        return arie_sel;
    }

    public void setArie_sel(String arie_sel) {
        this.arie_sel = arie_sel;
    }

    public String getSet_sel() {
        return set_sel;
    }

    public void setSet_sel(String set_sel) {
        this.set_sel = set_sel;
    }

    public String getCdriver_sel() {
        return cdriver_sel;
    }

    public void setCdriver_sel(String cdriver_sel) {
        this.cdriver_sel = cdriver_sel;
    }

    public String getOcateg_sel() {
        return ocateg_sel;
    }

    public void setOcateg_sel(String ocateg_sel) {
        this.ocateg_sel = ocateg_sel;
    }
}
