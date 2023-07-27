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
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "arecsprompt")
@ViewScoped
public class ActualRecordsPrompt implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ActualRecordsPrompt.class.getName());

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private List<CoArea> arii;
    private List<DataSet> seturi;
    private String arie_sel;
    private String set_sel;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            Optional<String> co = Optional.ofNullable(params.get("co"));
            if (co.isPresent()) this.arie_sel = Utils.paramDecode(co.get());
            Optional<String> ds = Optional.ofNullable(params.get("ds"));
            if (ds.isPresent()) this.set_sel = Utils.paramDecode(ds.get());
            
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
                            this.seturi = this.seturi = DataSetServ.getActualNotClosed(cuser.getUname());
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
                        (this.set_sel == null ? "" : "&ds=" + Utils.paramEncode(this.set_sel));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
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
}
