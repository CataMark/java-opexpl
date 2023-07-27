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
import ro.any.c12153.opexpl.services.RecordsActualServ;
import ro.any.c12153.opexpl.services.RecordsActualDocUpdate;
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
@Named(value = "arecs")
@ViewScoped
public class ActualRecordsView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ActualRecordsView.class.getName());

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject DataBaseLoadView dataLoad;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private ActualLazyDataModel list;
    
    private List<Map<String, Object>> selected;
    private RecordsActualDocUpdate updateValues;
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
                            if(!Boolean.TRUE.equals(this.dataset.getActual()))
                                throw new Exception(App.getBeanMess("err.dset.noact", clocale));
                            if(!Boolean.FALSE.equals(this.dataset.getIncheiat()))
                                throw new Exception(App.getBeanMess("err.cgroup.dset.close", clocale));
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).get(30, TimeUnit.SECONDS);
            
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
        this.list = new ActualLazyDataModel(this.dataset.getId(), this.coarea.getCod(), cuser.getUname(), clocale);
    }
    
    public void clear(){
        this.dialog.clear();
        this.dataLoad.clear();
        this.updateValues = null;
        this.finishScript = null;
    }
    
    public RecordsActualDocUpdate newUpdateValues(){
        return new RecordsActualDocUpdate();
    }
    
    public void deleteSelected(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.records.nosel", clocale));
            
            String rezultat = RecordsActualServ.deleteById(
                    this.selected.stream().map(x -> (String) x.get("id")).collect(Collectors.toList()),
                    cuser.getUname()
            );            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.del.sel", clocale), rezultat));           
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.del.sel", clocale), ex.getMessage()));
        }
    }
    
    public void deleteByFilter(){
        try {
            String rezultat = RecordsActualServ.deleteByFilter(this.coarea.getCod(), this.dataset.getId(), Optional.ofNullable(this.list.getFilter()), cuser.getUname());            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.del.flt", clocale), rezultat)); 
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.del.flt", clocale), ex.getMessage()));
        }
    }
    
    public void updateSelected(){
        try {
            if (this.selected == null || this.selected.isEmpty())
                throw new Exception(App.getBeanMess("err.records.nosel", clocale));
            if (!this.updateValues.hasValues()) throw new Exception(App.getBeanMess("err.records.chg.noval", clocale));
            
            String rezultat = RecordsActualServ.updateById(
                    this.selected.stream().map(x -> (String) x.get("id")).collect(Collectors.toList()),
                    this.updateValues,
                    cuser.getUname()
            );            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.chg.sel", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.chg.sel", clocale), ex.getMessage()));
        }
    }
    
    public void updateByFilter(){
        try {
            if (!this.updateValues.hasValues()) throw new Exception(App.getBeanMess("err.records.chg.noval", clocale));
            
            String rezultat = RecordsActualServ.updateByFilter(this.coarea.getCod(), this.dataset.getId(),
                    Optional.ofNullable(this.list.getFilter()), this.updateValues, cuser.getUname());            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.records.chg.flt", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.chg.flt", clocale), ex.getMessage()));
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
            RecordsActualServ.recordsToXlsx(this.coarea.getCod(), this.dataset.getId(), Optional.ofNullable(this.list.getFilter()), cuser.getUname(), stream);
            
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

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }

    public ActualLazyDataModel getList() {
        return list;
    }

    public List<Map<String, Object>> getSelected() {
        return selected;
    }

    public void setSelected(List<Map<String, Object>> selected) {
        this.selected = selected;
    }

    public RecordsActualDocUpdate getUpdateValues() {
        return updateValues;
    }

    public void setUpdateValues(RecordsActualDocUpdate updateValues) {
        this.updateValues = updateValues;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
