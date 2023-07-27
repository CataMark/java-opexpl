package ro.any.c12153.shared.beans;

import ro.any.c12153.shared.App;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author C12153
 */
@Named(value = "portal_lang")
@SessionScoped
public class LocaleController implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LocaleController.class.getName());
    private static final List<String> ACCEPTED_LANGS = Arrays.asList("en","ro");
    
    private FacesMessage startMessage;
    private String language;
    private Locale locale;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String lang_param = params.get("lang");
            if(Utils.stringNotEmpty(lang_param) && ACCEPTED_LANGS.contains(lang_param)){
                this.setLanguage(lang_param);
                return;
            }
            
            this.locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
            String _lang = this.locale.getLanguage();
            if (ACCEPTED_LANGS.contains(_lang)){
                this.language = _lang;
            } else {
                this.setLanguage(ACCEPTED_LANGS.get(0));
            }
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, null, ex);
            this.startMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Initializare limbă", ex.getMessage());
        }
    }
    
    public void renderInitMessage(){
        if (this.startMessage != null){
            FacesContext.getCurrentInstance().addMessage(null, this.startMessage);
            this.startMessage = null;
        }
    }
    
    public String getLanguage(){
        return this.language;
    }
    
    public void setLanguage(String language){
        this.language = language;
        this.locale = new Locale(this.language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(this.locale);
    }
    
    @Produces @CurrentLocale
    public Locale getLocale() {
        return this.locale;
    }
}
