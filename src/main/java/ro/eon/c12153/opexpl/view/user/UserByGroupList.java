package ro.any.c12153.opexpl.view.user;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexpl.entities.UserGroup;
import ro.any.c12153.opexpl.services.UserGroupServ;
import ro.any.c12153.opexpl.services.UserOxplServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "usersByGroupList")
@ViewScoped
public class UserByGroupList implements Serializable, SelectTableView<User>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UserByGroupList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    private @Inject DialogController dialog;
    private @Inject SelectItemView<User> item;
    
    private String initError;
    private UserGroup group;
    private User selected;
    private List<User> list;
    private String[] filterValues;
    private List<User> filtered;
    
    public void clearFilters(){
        this.filterValues = new String[]{"",""};
    }
    
    @PostConstruct
    private void init(){
        try {
            this.clearFilters();
            
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String ug =  Optional.ofNullable(params.get("ug"))
                    .orElseThrow(() ->  new Exception(App.getBeanMess("err.duser.nogroup", clocale)));
            this.group = UserGroupServ.getByCod(Utils.paramDecode(ug), cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.duser.nogroup", clocale)));
            if(!Boolean.FALSE.equals(this.group.getImplicit())) throw new Exception(App.getBeanMess("err.duser.group.nok", clocale));
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            rezultat += (this.group == null ? "" : "&ug=" + Utils.paramEncode(this.group.getCod()));            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        try{
            this.clearFilters();
            this.list = UserOxplServ.getByGroup(this.group.getCod(), cuser.getUname());
            
        } catch (Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.duser.listinit", clocale), ex.getMessage())
            );
        }
    }
    
    @Override
    public void newItem(){
        this.item.setSelected(new User());
        this.item.initLists();
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new User(this.selected.getJson()));
                if (initLists) this.item.initLists();
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage())
            );
        }
    }
    
    @Override
    public void clear(){
        this.dialog.clear();
        this.item.clear();
    }

    @Override
    public String getInitError() {
        return initError;
    }
    
    public UserGroup getGroup() {
        return group;
    }

    @Override
    public List<User> getList() {
        return list;
    }

    public User getSelected() {
        return selected;
    }

    @Override
    public void setSelected(User selected) {
        this.selected = selected;
    }

    public String[] getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(String[] filterValues) {
        this.filterValues = filterValues;
    }

    public List<User> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<User> filtered) {
        this.filtered = filtered;
    }
}
