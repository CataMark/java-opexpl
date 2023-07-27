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
import ro.any.c12153.opexpl.entities.IcPartener;
import ro.any.c12153.opexpl.services.IcPartenerServ;
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
@Named(value = "icpartlist")
@ViewScoped
public class IcPartenerList implements Serializable, SelectTableView<IcPartener>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(IcPartenerList.class.getName());

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<IcPartener> item;    
    private List<IcPartener> list;
    private IcPartener selected;
    private String[] filterValue;
    private List<IcPartener> filtered;
    
    public void clearFilters(){
        this.filterValue = new String[]{"","",""};
    }
    
    public void datainit(){
        try {
            this.clearFilters();
            this.list = IcPartenerServ.getAll(cuser.getUname());
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.icpart.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        this.item.setSelected(new IcPartener());
        this.item.initLists();
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new IcPartener(this.selected.getJson()));
                if (initLists) this.item.initLists();
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
    public List<IcPartener> getList() {
        return list;
    }

    public IcPartener getSelected() {
        return selected;
    }

    @Override
    public void setSelected(IcPartener selected) {
        this.selected = selected;
    }

    public String[] getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String[] filterValue) {
        this.filterValue = filterValue;
    }

    public List<IcPartener> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<IcPartener> filtered) {
        this.filtered = filtered;
    }
}
