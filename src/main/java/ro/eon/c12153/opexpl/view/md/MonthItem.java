package ro.any.c12153.opexpl.view.md;

import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexpl.entities.Month;
import ro.any.c12153.opexpl.services.MonthServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "month")
@ViewScoped
public class MonthItem implements Serializable, SelectItemView<Month>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MonthItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject SelectTableView<Month> owner;
    private Month selected;
    private String finishScript;

    @Override
    public void initLists() {
        
    }
    
    @Override
    public void clear(){
        this.selected = null;
        this.finishScript = null;
    }
    
    public void save(){
        try {
            if (this.selected == null || !Utils.stringNotEmpty(this.selected.getCod()))
                throw new Exception(App.getBeanMess("err.month.nok", clocale));
            
            if (this.selected.getMod_timp() == null){
                Month rezultat = MonthServ.insert(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                this.owner.getList().add(rezultat);
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.month.ins", clocale), App.getBeanMess("info.success",  clocale)));
                
            } else {
                Month rezultat = MonthServ.update(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                for (int i = 0; i < this.owner.getList().size(); i++){
                    if (this.owner.getList().get(i).getCod().equals(rezultat.getCod())){
                        this.owner.getList().set(i, rezultat);
                        break;
                    }
                }
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.month.upd", clocale), App.getBeanMess("info.success",  clocale)));
            }
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.month.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.month.nok", clocale));
            
            if (!MonthServ.delete(this.selected.getCod(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));       
            this.owner.getList().removeIf(x -> x.getCod().equals(this.selected.getCod()));
            this.owner.setSelected(null);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.month.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.month.del", clocale), ex.getMessage()));
        }
    }

    @Override
    public String getInitError() {
        return null;
    }
    
    @Override
    public Month getSelected() {
        return selected;
    }

    @Override
    public void setSelected(Month selected) {
        this.selected = selected;
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
