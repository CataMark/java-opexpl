package ro.any.c12153.opexpl.view.md;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
@Named(value = "cdriveras")
@ViewScoped
public class CostDriverAssignItem implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CostDriverAssignItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject CostDriverAssignList owner;
    private String initError;
    private List<CostDriver> notAssigned;    
    private CostDriver selected;
    private String finishScript;
    
    public void initLists(){
        try {
            if (this.selected.getMod_timp() == null)
                this.notAssigned = CostDriverServ.getNotAssign(this.owner.getCoarea().getCod(), cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public void clear(){
        this.initError = null;
        this.selected = null;
        this.notAssigned = null;
        this.finishScript = null;
    }
    
    public void save(){
        try {
            if (this.selected == null || !Utils.stringNotEmpty(this.selected.getCod()))
                throw new Exception(App.getBeanMess("err.cdriver.nok", clocale));
            
            if (this.selected.getMod_timp() == null){
                CostDriver rezultat = CostDriverServ.insertAssign(this.selected, this.owner.getCoarea().getCod(), cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                this.owner.getList().add(rezultat);
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.cdriver.asg.ins", clocale), App.getBeanMess("info.success",  clocale)));                
            } else {
                CostDriver rezultat = CostDriverServ.updateAssign(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                for (int i = 0; i < this.owner.getList().size(); i++){
                    if (this.owner.getList().get(i).getId().equals(rezultat.getId())){
                        this.owner.getList().set(i, rezultat);
                        break;
                    }
                }
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.cdriver.asg.upd", clocale), App.getBeanMess("info.success",  clocale)));
            }
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.cdriver.asg.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.cdriver.nok", clocale));
            
            if (!CostDriverServ.deleteAssign(this.selected.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));            
            this.owner.getList().removeIf(x -> x.getId().equals(this.selected.getId()));
            this.owner.setSelected(null);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.cdriver.asg.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.cdriver.asg.del", clocale), ex.getMessage()));
        }
    }

    public String getInitError() {
        return initError;
    }
    
    public List<CostDriver> getNotAssigned() {
        return this.notAssigned;
    }
    
    public CoArea getCoarea(){
        return this.owner.getCoarea();
    }

    public CostDriver getSelected() {
        return selected;
    }

    public void setSelected(CostDriver selected) {
        this.selected = selected;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
