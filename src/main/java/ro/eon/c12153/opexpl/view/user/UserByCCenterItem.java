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
import ro.any.c12153.opexpl.entities.CostCenter;
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
@Named(value = "usercc")
@ViewScoped
public class UserByCCenterItem implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UserByCCenterItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject UserByCCenterList owner;
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
            if (this.owner.getCcenter() == null || this.owner.getCcenter().getData() == null)
                throw new Exception(App.getBeanMess("err.ccenter.not", clocale));
            
            User rezultat = UserOxplServ.addToCCenter(
                    this.selected.getUname(),
                    this.owner.getCoarea().getHier(),
                    this.owner.getDataset().getId(),
                    ((CostCenter) this.owner.getCcenter().getData()).getCod(),
                    cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));            
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
            if (this.owner.getCcenter() == null || this.owner.getCcenter().getData() == null)
                throw new Exception(App.getBeanMess("err.ccenter.not", clocale));
            
            if (!UserOxplServ.deleteFromCCenter(
                    this.selected.getUname(),
                    this.owner.getCoarea().getHier(),
                    this.owner.getDataset().getId(),
                    ((CostCenter) this.owner.getCcenter().getData()).getCod(),
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
    
    public CostCenter getCcenter(){
        CostCenter rezultat = null;
        if (this.owner.getCcenter() != null) rezultat = (CostCenter) this.owner.getCcenter().getData();
        return rezultat;
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
