package ro.any.c12153.opexpl.view.key;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexpl.entities.KeyHead;
import ro.any.c12153.opexpl.services.KeyG01Serv;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "kg01h")
@ViewScoped
public class KeyG01HeadItem implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(KeyG01HeadItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject KeyG01HeadList owner;
    private KeyHead selected;
    private String finishScript;
    
    public void initLists(){
        
    }
    
    protected void clear(){
        this.selected = null;
        this.finishScript = null;
    }
    
    public void save(){
        try {
            if (this.selected == null) throw new Exception(App.getBeanMess("err.keys.nok", clocale));
            
            if (this.selected.getMod_timp() == null){
                KeyHead rezultat = KeyG01Serv.insertHead(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                this.owner.getList().add(rezultat);
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.ins", clocale), App.getBeanMess("info.success",  clocale)));
            } else {
                KeyHead rezultat = KeyG01Serv.updateHead(this.owner.getDataset().getId(), this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                for (int i = 0; i < this.owner.getList().size(); i++){
                    if (this.owner.getList().get(i).getId().equals(rezultat.getId())){
                        this.owner.getList().set(i, rezultat);
                        break;
                    }
                }
                this.owner.setSelected(rezultat);
                this.owner.getList().sort(Comparator.comparing(KeyHead::getId));
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.upd", clocale), App.getBeanMess("info.success",  clocale)));
            }
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.keys.nok", clocale));
            KeyG01Serv.deleteHead(this.selected.getId(), cuser.getUname());            
            this.owner.getList().removeIf(x -> x.getId().equals(this.selected.getId()));
            this.owner.setSelected(null);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.del", clocale), ex.getMessage()));
        }
    }
    
    public String getInitError() {
        return null;
    }
    
    public KeyHead getSelected() {
        return selected;
    }

    public void setSelected(KeyHead selected) {
        this.selected = selected;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
