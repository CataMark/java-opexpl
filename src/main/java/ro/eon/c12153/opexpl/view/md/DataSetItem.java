package ro.any.c12153.opexpl.view.md;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.PlanVers;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.PlanVersServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "dset")
@ViewScoped
public class DataSetItem implements Serializable, SelectItemView<DataSet>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(DataSetItem.class.getName());

    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject SelectTableView<DataSet> owner;
    private String initError;
    private DataSet selected;
    private String finishScript;    
    private List<Short> ani;
    private List<DataSet> compares;
    private List<DataSet> actualSets;
    private List<PlanVers> versiuni;
    private Boolean hasValues;
    
    private void initHasValues(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null){
                this.hasValues = false;
            } else {
                this.hasValues = DataSetServ.hasValues(this.selected.getId(), cuser.getUname());
            }
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    private void initVersiuni(){
        try {
            if (this.selected.getMod_timp() == null)
                this.versiuni = PlanVersServ.getAll(cuser.getUname());
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    private void initAni(){
        try {
            this.ani = DataSetServ.getAllAni(cuser.getUname());
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    private void initCompares(){
        try {
            if (this.selected.getCompar_an() == null){
                this.compares = new ArrayList<>();
            } else {
                this.compares = DataSetServ.getRaportareByAn(this.selected.getCompar_an(), cuser.getUname()).stream()
                        .filter(x -> !x.getId().equals(Optional.ofNullable(this.selected.getId()).orElse(0)))
                        .collect(Collectors.toList());
            }
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    private void initActualSets(){
        try {
            if (this.selected.getActual_set_an() == null) {
                this.actualSets = new ArrayList<>();
            } else {
                this.actualSets = DataSetServ.getAllByAn(this.selected.getActual_set_an(), cuser.getUname()).stream()
                        .filter(x -> x.getActual().equals(true))
                        .collect(Collectors.toList());
            }
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    public boolean versIsActual(){
        if (this.selected == null) return false;
        
        if (this.selected.getMod_timp() == null){
            if (this.selected.getVers() == null || this.selected.getVers().isEmpty()) return false;
            if (this.versiuni == null || this.versiuni.isEmpty()) return false;
            return this.versiuni.stream()
                    .filter(x -> x.getCod().equals(this.selected.getVers()))
                    .map(x -> x.getActual())
                    .findFirst()
                    .orElse(false);
        } else {
            if (this.selected.getActual() == null) return false;
            return this.selected.getActual();
        }
    }

    @Override
    public void initLists() {
        try {
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(this::initHasValues),
                    CompletableFuture.runAsync(this::initVersiuni)
            ).thenRun(() -> {
                try {
                    if (!Boolean.TRUE.equals(this.selected.getActual()) && !this.versIsActual())
                        CompletableFuture.allOf(
                                CompletableFuture.runAsync(this::initAni),
                                CompletableFuture.runAsync(this::initCompares),
                                CompletableFuture.runAsync(() -> {
                                    if (Boolean.FALSE.equals(this.hasValues)) this.initActualSets();
                                })
                        ).get(50, TimeUnit.SECONDS);
                } catch (Exception ex) {
                    throw new CompletionException(ex);
                }
            }).get(90, TimeUnit.SECONDS);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }    

    @Override
    public void clear(){
        this.initError = null;
        this.selected = null;
        this.finishScript = null;
        this.ani = null;
        this.compares = null;
        this.actualSets = null;
        this.versiuni = null;
        this.hasValues = null;
    }
    
    
    public void clearCompare(){
        try {
            this.selected.setCompar(null);
            this.selected.setCompar_nume(null);
            this.selected.setCompar_vers(null);
            this.initCompares();
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.dset.getcomp", clocale), ex.getMessage()));
        }            
    }
    
    public void clearActualSet(){
        try {
            this.selected.setActual_set(null);
            this.selected.setActual_set_nume(null);
            this.selected.setActual_set_vers(null);
            this.initActualSets();
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.dest.getact", clocale), ex.getMessage()));
        }   
    }
    
    public void clearRefSets(){
        try {
            this.selected.setCompar(null);
            this.selected.setCompar_nume(null);
            this.selected.setCompar_vers(null);
            this.selected.setActual_set(null);
            this.selected.setActual_set_nume(null);
            this.selected.setActual_set_vers(null);
            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(this::initCompares),
                    CompletableFuture.runAsync(this::initActualSets)
            ).get(60, TimeUnit.SECONDS);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.dset.get", clocale), ex.getMessage()));
        }
    }
    
    public void save(){
        try {
            if (this.selected == null || !Utils.stringNotEmpty(this.selected.getNume()))
                throw new Exception(App.getBeanMess("err.dset.nok", clocale));
            
            if (this.selected.getMod_timp() == null){               
                DataSet rezultat = DataSetServ.insert(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                this.owner.getList().add(rezultat);
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.dset.ins", clocale), App.getBeanMess("info.success",  clocale)));
                
            } else {
                DataSet rezultat = DataSetServ.update(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                for (int i = 0; i < this.owner.getList().size(); i++){
                    if (this.owner.getList().get(i).getId().equals(rezultat.getId())){
                        this.owner.getList().set(i, rezultat);
                        break;
                    }
                }
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.dset.upd", clocale), App.getBeanMess("info.success",  clocale)));
            }
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dset.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.dset.nok", clocale));
            
            if (!DataSetServ.delete(this.selected.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));            
            this.owner.getList().removeIf(x -> x.getId().equals(this.selected.getId()));
            this.owner.setSelected(null);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.dset.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dset.del", clocale), ex.getMessage()));
        }
    }

    @Override
    public String getInitError() {
        return initError;
    }
    
    public List<Short> getAni() {
        return this.ani;
    }
    
    public List<DataSet> getCompares() {
        return this.compares;
    }
    
    public List<DataSet> getActualSets() {
        return this.actualSets;
    }
    
    public List<PlanVers> getVersiuni() {
        return this.versiuni;
    }
    
    public Boolean getHasValues() {
        return this.hasValues;
    }
    
    @Override
    public DataSet getSelected() {
        return selected;
    }

    @Override
    public void setSelected(DataSet selected) {
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
