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
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.DataSetPer;
import ro.any.c12153.opexpl.entities.Month;
import ro.any.c12153.opexpl.services.DataSetPerServ;
import ro.any.c12153.opexpl.services.MonthServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "dper")
@ViewScoped
public class DataSetPerItem implements Serializable, SelectItemView<DataSetPer>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(DataSetPerItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DataSetPerList owner;
    private String initError;
    private List<Month> luni;
    private DataSetPer selected;
    private String finishScript;

    @Override
    public void initLists() {
        try {
            if (this.selected.getMod_timp() == null)
                this.luni = MonthServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    @Override
    public void clear(){
        this.initError = null;
        this.selected = null;
        this.luni = null;
        this.finishScript = null;
    }
    
    public void save(){
        try {
            if (this.selected == null || !Utils.stringNotEmpty(this.selected.getPer()))
                throw new Exception(App.getBeanMess("title.dset.per.nok", clocale));            
            if (this.owner.getDataset().getActual() != null && this.owner.getDataset().getActual().equals(true))
                this.selected.setActual(false);
            
            if (this.selected.getMod_timp() == null){
                DataSetPer rezultat = DataSetPerServ.insert(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                this.owner.getList().add(rezultat);
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.dset.per.ins", clocale), App.getBeanMess("info.success",  clocale)));                
            } else {
                DataSetPer rezultat = DataSetPerServ.update(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                for (int i = 0; i < this.owner.getList().size(); i++){
                    if (this.owner.getList().get(i).getId().equals(rezultat.getId())){
                        this.owner.getList().set(i, rezultat);
                        break;
                    }
                }
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.dset.per.upd", clocale), App.getBeanMess("info.success",  clocale)));
            }
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dset.per.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("title.dset.per.nok", clocale));
            
            if (!DataSetPerServ.deleteById(this.selected.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));            
            this.owner.getList().removeIf(x -> x.getId().equals(this.selected.getId()));
            this.owner.setSelected(null);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.dset.per.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dset.per.del", clocale), ex.getMessage()));
        }
    }

    @Override
    public String getInitError() {
        return initError;
    }
    
    public DataSet getDataset(){
        return this.owner.getDataset();
    }
    
    public List<Month> getLuni() {
        return this.luni;
    }
    
    @Override
    public DataSetPer getSelected() {
        return selected;
    }

    @Override
    public void setSelected(DataSetPer selected) {
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
