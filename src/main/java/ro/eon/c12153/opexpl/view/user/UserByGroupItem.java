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
import ro.any.c12153.opexpl.entities.UserGroup;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Crypto;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.services.UserService;

/**
 *
 * @author C12153
 */
@Named(value = "usersByGroup")
@ViewScoped
public class UserByGroupItem implements Serializable, SelectItemView<User>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UserByGroupItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject UserByGroupList owner;
    private User selected;
    private String password_$1;
    private String password_$2;
    private String finishScript;
    
    @Override
    public void initLists(){
        
    }
    
    @Override
    public void clear(){
        this.selected = null;
        this.password_$1 = null;
        this.password_$2 = null;
        this.finishScript = null;
    }
    
    public void getUserData(){        
        try {            
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
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.duser.get", clocale), ex.getMessage()));
        }
    }
    
    public void save(){
        try {
            if (this.selected == null || !Utils.stringNotEmpty(this.selected.getUname()))
                throw new Exception(App.getBeanMess("err.duser.sel.nok", clocale));
            
            if (this.selected.getMod_timp() == null){
                String phrase = Crypto.decrypt(FacesContext.getCurrentInstance().getExternalContext().getInitParameter("ro.any.c12153.DEFAULT_PHRASE"));
                
                User rezultat = UserService.insert(this.selected, cuser.getUname(),UserService.pseudoPassGenerator(this.selected.getUname(), phrase))
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                if (!UserService.addToGroup(rezultat.getUname(), this.owner.getGroup().getCod(), cuser.getUname()))
                    throw new Exception(App.getBeanMess("err.nosuccess", clocale));                
                this.owner.getList().add(rezultat);
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("title.duser.ins", clocale), App.getBeanMess("info.success", clocale)));
                
            } else {
                User rezultat = UserService.update(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                if (!UserService.addToGroup(rezultat.getUname(), this.owner.getGroup().getCod(), cuser.getUname()))
                    throw new Exception(App.getBeanMess("err.nosuccess", clocale));                
                boolean gasit = false;
                for (int i = 0; i < this.owner.getList().size(); i++){
                    if (this.owner.getList().get(i).getUname().equals(rezultat.getUname())){
                        this.owner.getList().set(i, rezultat);
                        gasit = true;
                        break;
                    }
                }
                if (!gasit) this.owner.getList().add(rezultat);
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("title.duser.upd", clocale), App.getBeanMess("info.success", clocale)));
            }
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.duser.save", clocale), ex.getMessage()));
        }
    }
    
    public void removeFromGroup(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.duser.sel.nok", clocale));
            
            if (!UserService.removeFromGroup(this.selected.getUname(), this.owner.getGroup().getCod(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));            
            this.owner.getList().removeIf(x -> x.getUname().equals(this.selected.getUname()));
            this.owner.setSelected(null);
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("title.duser.del", clocale), App.getBeanMess("info.success", clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.duser.del", clocale), ex.getMessage()));
        }
    }
    
    public void passwordReset(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.duser.sel.nok", clocale));
            if (!Utils.stringNotEmpty(this.password_$1)) throw new Exception(App.getBeanMess("err.duser.nopass", clocale));            
            if (!this.password_$1.equals(this.password_$2)) throw new Exception (App.getBeanMess("err.duser.pass.nochk", clocale));
            
            String rezultat = UserService.changePass(this.selected.getUname(), this.password_$1, cuser.getUname());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("title.duser.pass.chg", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.duser.pass.chg", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public String getInitError(){
        return null;
    }
    
    public UserGroup getGroup(){
        return this.owner.getGroup();
    }
    
    @Override
    public User getSelected() {
        return selected;
    }

    @Override
    public void setSelected(User selected) {
        this.selected = selected;
    }

    public String getPassword_$1() {
        return password_$1;
    }

    public void setPassword_$1(String password_$1) {
        this.password_$1 = password_$1;
    }

    public String getPassword_$2() {
        return password_$2;
    }

    public void setPassword_$2(String password_$2) {
        this.password_$2 = password_$2;
    }

    @Override
    public String getFinishScript() {
        return finishScript;
    }

    @Override
    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
