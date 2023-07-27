package ro.any.c12153.shared.beans;

import ro.any.c12153.shared.App;
import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.services.UserService;

/**
 *
 * @author C12153
 */

//TODO: document class
@Named(value = "portal_user")
@SessionScoped
public class UserController implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());
    
    private @Inject @CurrentLocale Locale locale;
    private User user;
    private String pass1;
    private String pass2;
    
    @PostConstruct
    private void init(){
        
        try {            
            String userId = Optional.ofNullable(FacesContext.getCurrentInstance().getExternalContext()
                                                    .getUserPrincipal().getName())
                    .filter(Utils::stringNotEmpty)
                    .orElseThrow(() -> new Exception(App.getBeanMess("user.nouser", locale)));
            this.user = UserService.getByUname(userId, userId).orElse(null);                
            
            String path = FacesContext.getCurrentInstance().getExternalContext().getRequestServerName() + ":" +
                            FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort() + 
                            FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath();

            UserService.newSessionRecord(FacesContext.getCurrentInstance().getExternalContext().getSessionId(false),
                    userId, path);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, (this.user == null? null: this.user.getUname()), ex);
            
            try{
                FacesContext.getCurrentInstance().getExternalContext().redirect(
                        FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath() +
                        "/logoff"
                );
            } catch (Exception exx){
                App.log(LOG, Level.SEVERE, Optional.ofNullable(this.user).map(User::getUname).orElse(null), exx);
            }
        }
    }
    
    public void changePass(){
        try {
            if (this.pass1 == null || this.pass1.isEmpty()) throw new Exception(App.getBeanMess("user.nopass", locale));            
            if (this.pass2 == null || this.pass2.isEmpty()) throw new Exception(App.getBeanMess("user.noverif", locale));            
            if(!this.pass1.equals(this.pass2)) throw new Exception(App.getBeanMess("user.pass.nocheck", locale));
            
            String rezultat = UserService.changePass(this.user.getUname(), this.pass1, this.user.getUname());
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("user.pass.change.title", locale), rezultat)
            );
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, Optional.ofNullable(this.user).map(User::getUname).orElse(null), ex);
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("user.pass.change.title", locale), ex.getMessage())
            );
        } finally {
            this.pass1 = null;
            this.pass2 = null;
        }
    }

    @Produces @CurrentUser
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPass1() {
        return pass1;
    }

    public void setPass1(String pass1) {
        this.pass1 = pass1;
    }

    public String getPass2() {
        return pass2;
    }

    public void setPass2(String pass2) {
        this.pass2 = pass2;
    }
}
