package ro.any.c12153.opexpl.view.records;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.util.Constants;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.RecordsPlanDocUpdate;
import ro.any.c12153.opexpl.services.RecordsPlanServ;
import ro.any.c12153.opexpl.services.RecordsPlanValUpdate;
import ro.any.c12153.opexpl.view.CopyFromDataSet;
import ro.any.c12153.opexpl.view.CopyFromDataSetContract;
import ro.any.c12153.opexpl.view.DataSetType;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.DataBaseLoadView;
import ro.any.c12153.shared.beans.DialogController;

/**
 *
 * @author C12153
 */
@Named(value = "precs")
@ViewScoped
public class PlanRecordsView implements Serializable, CopyFromDataSetContract{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(PlanRecordsView.class.getName());

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject DataBaseLoadView dataLoad;
    private @Inject CopyFromDataSet copyForm;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private PlanLazyDataModel list;
    
    private List<Map<String, Object>> selected;
    private RecordsPlanDocUpdate updateDocValues;
    private RecordsPlanValUpdate updateValValues;
    private String finishScript;
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String co = Optional.ofNullable(params.get("co"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            String ds = Optional.ofNullable(params.get("ds"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.per.noset", clocale)));
            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.coarea = CoAreaServ.getByCod(Utils.paramDecode(co), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.dataset = DataSetServ.getById(Integer.valueOf(Utils.paramDecode(ds)), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.per.noset", clocale)));
                            if(!Boolean.FALSE.equals(this.dataset.getActual()))
                                throw new Exception(App.getBeanMess("err.dset.noplan", clocale));
                            if(!Boolean.FALSE.equals(this.dataset.getIncheiat()))
                                throw new Exception(App.getBeanMess("err.cgroup.dset.close", clocale));;
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).get(30, TimeUnit.SECONDS);
            
            this.copyForm.init(this, DataSetType.PLAN);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            rezultat += (this.coarea == null ? "" : "&co=" + Utils.paramEncode(this.coarea.getCod())) +
                        (this.dataset == null ? "" : "&ds=" + Utils.paramEncode(this.dataset.getId().toString()));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        this.list = new PlanLazyDataModel(this.dataset.getId(), this.coarea.getCod(), cuser.getUname(), clocale);
    }
    
    public void clear(){
        this.dialog.clear();
        this.dataLoad.clear();
        this.copyForm.clear();
        this.updateDocValues = null;
        this.updateValValues = null;
        this.finishScript = null;
    }
    
    public RecordsPlanDocUpdate newUpdateDocValues(){
        return new RecordsPlanDocUpdate();
    }
    
    public RecordsPlanValUpdate newUpdateValValues(){
        return new RecordsPlanValUpdate();
    }
    
    public void deleteDocSelected(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.records.nosel", clocale));
            
            String rezultat = RecordsPlanServ.deleteDocById(
                    this.selected.stream().map(x -> (String) x.get("doc_sys_id")).distinct().collect(Collectors.toList()),
                    cuser.getUname()
            );
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.doc.del.sel", clocale), rezultat));           
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.doc.del.sel", clocale), ex.getMessage()));
        }
    }
    
    public void deleteDocByFilter(){
        try {
            String rezultat = RecordsPlanServ.deleteDocByFilter(this.coarea.getCod(), this.dataset.getId(), Optional.ofNullable(this.list.getFilter()), cuser.getUname());            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.doc.del.flt", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.doc.del.flt", clocale), ex.getMessage()));
        }
    }
    
    public void updateDocSelected(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.records.nosel", clocale));
            if (!this.updateDocValues.hasValues()) throw new Exception(App.getBeanMess("err.records.chg.noval", clocale));
            
            String rezultat = RecordsPlanServ.updateDocById(
                    this.selected.stream().map(x -> (String) x.get("doc_sys_id")).distinct().collect(Collectors.toList()),
                    this.updateDocValues,
                    cuser.getUname()
            );            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.doc.chg.sel", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.doc.chg.sel", clocale), ex.getMessage()));
        }
    }
    
    public void updateDocByFilter(){
        try {
            if (!this.updateDocValues.hasValues()) throw new Exception(App.getBeanMess("err.records.chg.noval", clocale));
            
            String rezultat = RecordsPlanServ.updateDocByFilter(this.coarea.getCod(), this.dataset.getId(),
                    Optional.ofNullable(this.list.getFilter()), this.updateDocValues, cuser.getUname());            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.doc.chg.flt", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.doc.chg.flt", clocale), ex.getMessage()));
        }
    }
    
    public void deleteValSelected(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.records.nosel", clocale));
            
            String rezultat = RecordsPlanServ.deleteValById(
                    this.selected.stream().map(x -> (String) x.get("val_sys_id")).filter(Utils::stringNotEmpty).collect(Collectors.toList()),
                    cuser.getUname()
            );
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.val.del.sel", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.val.del.sel", clocale), ex.getMessage()));
        }
    }
    
    public void deleteValByFilter(){
        try {
            String rezultat = RecordsPlanServ.deleteValByFilter(this.coarea.getCod(), this.dataset.getId(), Optional.ofNullable(this.list.getFilter()), cuser.getUname());            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.val.del.flt", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.val.del.flt", clocale), ex.getMessage()));
        }
    }
    
    public void updateValSelected(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.records.nosel", clocale));
            if (!this.updateValValues.hasValues()) throw new Exception(App.getBeanMess("err.records.chg.noval", clocale));
            
            String rezultat = RecordsPlanServ.updateValById(
                    this.selected.stream().map(x -> (String) x.get("val_sys_id")).filter(Utils::stringNotEmpty).collect(Collectors.toList()),
                    this.updateValValues,
                    cuser.getUname()
            );            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.val.chg.sel", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.val.chg.sel", clocale), ex.getMessage()));
        }
    }
    
    public void updateValByFilter(){
        try {
            if (!this.updateValValues.hasValues()) throw new Exception(App.getBeanMess("err.records.chg.noval", clocale));
            
            String rezultat = RecordsPlanServ.updateValByFilter(this.coarea.getCod(), this.dataset.getId(),
                    Optional.ofNullable(this.list.getFilter()), this.updateValValues, cuser.getUname());            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.val.chg.flt", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.val.chg.flt", clocale), ex.getMessage()));
        }
    }
    
    public void takeOverActual(){
        try {
            double rezultat = RecordsPlanServ.takeOverActual(this.coarea.getHier(), this.dataset.getId(), cuser.getUname());            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.actual.copy", clocale),
                    String.format(App.getBeanMess("info.records.actual.copy", clocale), rezultat)
            ));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.actual.copy", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void copiaza(Integer fromDataSet, String finishScript, byte alternativa){
        try {
            String rezultat = RecordsPlanServ.takeOverAssign(this.coarea.getCod(), fromDataSet, this.dataset.getId(), cuser.getUname());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.asign.copy", clocale), rezultat));
            if (Utils.stringNotEmpty(finishScript)) PrimeFaces.current().executeScript(finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.asign.copy", clocale), ex.getMessage()));
        }
    }
    
    public void export(){        
        FacesContext fcontext = FacesContext.getCurrentInstance();
        ExternalContext econtext = fcontext.getExternalContext();
        econtext.responseReset();
        econtext.setResponseCharacterEncoding(StandardCharsets.UTF_8.name());
        econtext.setResponseContentType(Utils.MEDIA_EXCEL);
        econtext.setResponseHeader("Content-Disposition", "attachment; filename=raport.xlsx");
        econtext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", new HashMap<>()); //setare cookie pentru PrimeFaces.monitorDownload
               
        try(OutputStream stream = econtext.getResponseOutputStream();){
            RecordsPlanServ.recordsToXlsx(this.coarea.getCod(), this.dataset.getId(), Optional.ofNullable(this.list.getFilter()), cuser.getUname(), stream);
            
        }catch(Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.data.down", clocale), ex.getMessage()));
        }
        fcontext.responseComplete();
    }
    

    public String getInitError() {
        return initError;
    }
    
     public CoArea getCoarea() {
        return coarea;
    }

    public void setCoarea(CoArea coarea) {
        this.coarea = coarea;
    }

    @Override
    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }
    
    public PlanLazyDataModel getList() {
        return list;
    }
    
    public String getFilterJsonEncoded(){
        if (this.list == null || this.list.getFilter() == null) return "";
        return Utils.mapToJsonStringEncoded(this.list.getFilter());
    }
    
    public List<Map<String, Object>> getSelected() {
        return selected;
    }

    public void setSelected(List<Map<String, Object>> selected) {
        this.selected = selected;
    }

    public RecordsPlanDocUpdate getUpdateDocValues() {
        return updateDocValues;
    }

    public void setUpdateDocValues(RecordsPlanDocUpdate updateDocValues) {
        this.updateDocValues = updateDocValues;
    }

    public RecordsPlanValUpdate getUpdateValValues() {
        return updateValValues;
    }

    public void setUpdateValValues(RecordsPlanValUpdate updateValValues) {
        this.updateValValues = updateValValues;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
