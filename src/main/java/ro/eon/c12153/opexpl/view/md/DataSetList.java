package ro.any.c12153.opexpl.view.md;

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
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.services.DataSetServ;
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
@Named(value = "dsetlist")
@ViewScoped
public class DataSetList implements Serializable, SelectTableView<DataSet>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(DataSetList.class.getName());

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<DataSet> item;
    
    private String initError;
    private Short an;
    private List<DataSet> list;
    private DataSet selected;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String pAn = Optional.ofNullable(params.get("an"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.noan", clocale)));
            this.an = Short.parseShort(Utils.paramDecode(pAn));
            
            Optional<String> ds = Optional.ofNullable(params.get("ds"));
            if (ds.isPresent())
                this.selected = DataSetServ.getById(Integer.valueOf(Utils.paramDecode(ds.get())), cuser.getUname())
                        .orElse(null);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            rezultat += (this.an == null ? "" : "&an=" + Utils.paramEncode(this.an.toString())) +
                        (this.selected == null ? "" : "&ds=" + Utils.paramEncode(this.selected.getId().toString()));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        try{
            this.list = DataSetServ.getAllByAn(this.an, cuser.getUname());
        } catch (Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dset.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        DataSet rezultat = new DataSet();
        rezultat.setAn(this.an);
        this.item.setSelected(rezultat);
        this.item.initLists();
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new DataSet(this.selected.getJson()));
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
        return initError;
    }

    public Short getAn() {
        return an;
    }

    @Override
    public List<DataSet> getList() {
        return list;
    }

    public DataSet getSelected() {
        return selected;
    }

    @Override
    public void setSelected(DataSet selected) {
        this.selected = selected;
    }
}
