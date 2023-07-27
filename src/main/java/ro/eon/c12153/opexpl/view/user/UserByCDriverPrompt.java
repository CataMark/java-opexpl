package ro.any.c12153.opexpl.view.user;

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
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostDriver;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostDriverServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "usercdprompt")
@ViewScoped
public class UserByCDriverPrompt implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UserByCDriverPrompt.class.getName());

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private List<CoArea> arii;
    private List<CostDriver> cdrivers;
    private String arie_sel;
    private String driver_sel;
    
    @PostConstruct
    private void init(){
        try {
            //initializare query parameters
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            Optional<String> co = Optional.ofNullable(params.get("co"));
            if (co.isPresent()) this.arie_sel = Utils.paramDecode(co.get());
            Optional<String> cd = Optional.ofNullable(params.get("cd"));
            if (cd.isPresent()) this.driver_sel = Utils.paramDecode(cd.get());
            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.arii = CoAreaServ.getAll(cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.cdrivers = CostDriverServ.getByCentral(true, cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).get(60, TimeUnit.SECONDS);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            rezultat += (this.arie_sel == null ? "" : "&co=" + Utils.paramEncode(this.arie_sel)) +
                        (this.driver_sel == null ? "" : "&cd=" + Utils.paramEncode(this.driver_sel));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }

    public String getInitError() {
        return initError;
    }

    public List<CoArea> getArii(){
        return this.arii;
    }

    public List<CostDriver> getCdrivers() {
        return this.cdrivers;
    }

    public String getArie_sel() {
        return arie_sel;
    }

    public void setArie_sel(String arie_sel) {
        this.arie_sel = arie_sel;
    }

    public String getDriver_sel() {
        return driver_sel;
    }

    public void setDriver_sel(String driver_sel) {
        this.driver_sel = driver_sel;
    }
}