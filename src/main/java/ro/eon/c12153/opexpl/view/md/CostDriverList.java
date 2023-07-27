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
import ro.any.c12153.opexpl.entities.CostDriver;
import ro.any.c12153.opexpl.services.CostDriverServ;
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
@Named(value = "cdriverlist")
@ViewScoped
public class CostDriverList implements Serializable, SelectTableView<CostDriver>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CostDriverList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<CostDriver> item;
    private List<CostDriver> list;
    private CostDriver selected;
    
    public void datainit(){
        try {
            this.list = CostDriverServ.getAll(cuser.getUname());
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.cdriver.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        this.item.setSelected(new CostDriver());
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new CostDriver(this.selected.getJson()));
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void clear(){
        this.dialog.clear();
        this.item.clear();
    }

    @Override
    public String getInitError() {
        return null;
    }

    @Override
    public List<CostDriver> getList() {
        return list;
    }

    public CostDriver getSelected() {
        return selected;
    }

    @Override
    public void setSelected(CostDriver selected) {
        this.selected = selected;
    }
}
