package ro.any.c12153.opexpl.view.md;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "cdriverasprompt")
@ViewScoped
public class CostDriverAssignPrompt implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CostDriverAssignPrompt.class.getName());

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private List<CoArea> arii;
    private String selected;
    
    @PostConstruct
    private void init(){
        try {
            //initializare query parameters
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            Optional<String> co = Optional.ofNullable(params.get("co"));
            if (co.isPresent()) this.selected = Utils.paramDecode(co.get());
            
            this.arii = CoAreaServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            rezultat += (this.selected == null ? "" : "&co=" + Utils.paramEncode(this.selected));            
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

    public String getSelected() {
        return this.selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
