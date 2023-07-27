package ro.any.c12153.opexpl.view.md;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexpl.entities.PlanVers;
import ro.any.c12153.opexpl.services.PlanVersServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "pverslist")
@ViewScoped
public class PlanVersList implements Serializable, SelectTableView<PlanVers>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(PlanVersList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<PlanVers> item;
    private List<PlanVers> list;
    private PlanVers selected;
    
    public void datainit(){
        try {
            this.list = PlanVersServ.getAll(cuser.getUname());            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.pvers.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        this.item.setSelected(new PlanVers());
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {            
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new PlanVers(this.selected.getJson()));
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }

    @Override
    public String getInitError() {
        return null;
    }
    
    @Override
    public void clear(){
        this.dialog.clear();
        this.item.clear();
    }

    @Override
    public List<PlanVers> getList() {
        return list;
    }

    public PlanVers getSelected() {
        return selected;
    }

    @Override
    public void setSelected(PlanVers selected) {
        this.selected = selected;
    }
}
