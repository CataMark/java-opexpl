package ro.any.c12153.opexpl.view.user;

import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostDriver;
import ro.any.c12153.opexpl.services.UserOxplServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.services.UserService;

/**
 *
 * @author C12153
 */
@Named(value = "usercd")
@ViewScoped
public class UserByCDriverItem implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UserByCDriverItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject UserByCDriverList owner;
    private User selected;
    private String finishScript;
    
    protected void clear(){
        this.selected = null;
        this.finishScript = null;
    }
    
    public void onUserInputChange(){
        try{
            if (!Utils.stringNotEmpty(this.selected.getUname())){
                this.selected = new User();
                return;
            }            
            Optional<User> rezultat = UserService.getByUname(this.selected.getUname(), cuser.getUname());
            if (rezultat.isPresent()){
                this.selected = rezultat.get();
            } else {
                String newValue = this.selected.getUname();
                this.selected = new User();
                this.selected.setUname(newValue);
            }            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
    }
    
    public void save(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.duser.sel.nok", clocale));
            
            User rezultat = UserOxplServ.addToCDriver(
                    this.selected.getUname(),
                    this.owner.getCoarea().getCod(),
                    this.owner.getCdriver().getCod(),
                    cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));            
            this.owner.getList().add(rezultat);
            this.owner.setSelected(rezultat);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.duser.add", clocale), App.getBeanMess("info.success",  clocale)));            
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.duser.add", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.duser.sel.nok", clocale));
            
            if (!UserOxplServ.deleteFromCDriver(
                    this.selected.getUname(),
                    this.owner.getCoarea().getCod(),
                    this.owner.getCdriver().getCod(),
                    cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));            
            this.owner.getList().removeIf(x -> x.getUname().equals(this.selected.getUname()));
            this.owner.setSelected(null);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.duser.del.rights", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.duser.del.rights", clocale), ex.getMessage()));
        }
    }
    
    public CoArea getCoarea(){
        return this.owner.getCoarea();
    }
    
    public CostDriver getCdriver(){
        return this.owner.getCdriver();
    }
    
    public User getSelected() {
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
