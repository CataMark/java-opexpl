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
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.DataSetPer;
import ro.any.c12153.opexpl.services.DataSetPerServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.opexpl.view.CopyFromDataSet;
import ro.any.c12153.opexpl.view.CopyFromDataSetContract;
import ro.any.c12153.opexpl.view.DataSetType;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.DialogController;

/**
 *
 * @author C12153
 */
@Named(value = "dperlist")
@ViewScoped
public class DataSetPerList implements Serializable, SelectTableView<DataSetPer>, CopyFromDataSetContract{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(DataSetPerList.class.getName());

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<DataSetPer> item;
    private @Inject CopyFromDataSet copyForm;
    
    private String initError;
    private Short an;
    private DataSet dataset;    
    private List<DataSetPer> list;
    private DataSetPer selected;
    private String finishScript;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String pAn = Optional.ofNullable(params.get("an"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.noan", clocale)));
            this.an = Short.parseShort(Utils.paramDecode(pAn));
            
            String ds = Optional.ofNullable(params.get("ds"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.per.noset", clocale)));         
            this.dataset = DataSetServ.getById(Integer.valueOf(Utils.paramDecode(ds)), cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.per.noset", clocale)));
            
            this.copyForm.init(this, DataSetType.ALL);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            rezultat += (this.an == null ? "" : "&an=" + Utils.paramEncode(this.an.toString())) +
                        (this.dataset == null ? "" : "&ds=" + Utils.paramEncode(this.dataset.getId().toString()));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }

    public void datainit(){
        try{
            this.list = DataSetPerServ.getByDataSet(this.dataset.getId(), cuser.getUname());            
        } catch (Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dset.per.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        DataSetPer rezultat = new DataSetPer();
        rezultat.setData_set(this.dataset.getId());
        this.item.setSelected(rezultat);
        this.item.initLists();
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new DataSetPer(this.selected.getJson()));
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
        this.copyForm.clear();
        this.finishScript = null;
    }
    
    public void deleteAll(){
        try {
            String rezultat = DataSetPerServ.deleteAllByDataSet(this.dataset.getId(), cuser.getUname());
            this.selected = null;
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("title.dset.per.delall", clocale), rezultat));            
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dset.per.delall", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void copiaza(Integer fromDataSet, String finishScript, byte alternativa){
        try {
            if (fromDataSet == null) throw new Exception(App.getBeanMess("err.dset.per.noset", clocale));
            String rezultat = DataSetPerServ.takeOverFromSet(this.dataset.getId(), fromDataSet, cuser.getUname());
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("title.dset.per.copy", clocale), rezultat));
            if (Utils.stringNotEmpty(finishScript)) PrimeFaces.current().executeScript(finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dset.per.copy", clocale), ex.getMessage()));
        }
    }

    @Override
    public String getInitError() {
        return initError;
    }

    public Short getAn() {
        return an;
    }

    @Override
    public DataSet getDataset() {
        return dataset;
    }

    @Override
    public List<DataSetPer> getList() {
        return list;
    }

    public DataSetPer getSelected() {
        return selected;
    }

    @Override
    public void setSelected(DataSetPer selected) {
        this.selected = selected;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
