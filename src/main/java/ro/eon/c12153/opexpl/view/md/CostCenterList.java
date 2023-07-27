package ro.any.c12153.opexpl.view.md;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
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
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.util.Constants;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostCenter;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostCenterServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.view.CopyFromDataSet;
import ro.any.c12153.opexpl.view.CopyFromDataSetContract;
import ro.any.c12153.opexpl.view.DataSetType;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DataBaseLoadView;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "ccenterlist")
@ViewScoped
public class CostCenterList implements Serializable, CopyFromDataSetContract, SelectTableView<CostCenter>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CostCenterList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject SelectItemView<CostCenter> item;
    private @Inject CopyFromDataSet copyForm;
    private @Inject DataBaseLoadView dataLoad;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    
    private List<CostCenter> list;
    private TreeNode selected;
    private TreeNode hier;
    private boolean reinit;    
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
            rezultat += (this.dataset == null ? "" : "&ds=" + Utils.paramEncode(this.dataset.getId().toString())) +
                        (this.coarea == null ? "" : "&co=" + Utils.paramEncode(this.coarea.getCod()));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }

    public void datainit(){
        try{
            this.list = CostCenterServ.getAll(this.coarea.getHier(), this.dataset.getId(), cuser.getUname());
            this.reinit = true; //setare flag pentru reinitializare redering ierarhii
            
        } catch (Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.ccenter.listinit", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void newItem(){
        CostCenter rezultat = new CostCenter();
        rezultat.setData_set(this.dataset.getId());
        rezultat.setHier(this.coarea.getHier());
        rezultat.setLeaf(true);
        this.item.setSelected(rezultat);
        this.item.initLists();
    }
    
    @Override
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null || this.selected.getData() == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new CostCenter(((CostCenter) this.selected.getData()).getJson()));
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
        this.dataLoad.clear();
        this.finishScript = null;
    }

    public void deleteAll(){
        try{
            if (!CostCenterServ.deleteAll(this.coarea.getHier(), this.dataset.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));            
            this.list.clear();
            this.selected = null;
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.ccenter.hier.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        }catch(Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.ccenter.hier.del", clocale), ex.getMessage()));
        }
    }

    @Override
    public void copiaza(Integer fromDataSet, String finishScript, byte alternativa) {
        try {
            if (fromDataSet == null) throw new Exception(App.getBeanMess("err.dset.per.noset", clocale));
            CostCenterServ.takeOverFromSet(this.dataset.getId(), fromDataSet, this.coarea.getHier(), cuser.getUname());
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.ccenter.hier.copy", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(finishScript)) PrimeFaces.current().executeScript(finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.ccenter.hier.copy", clocale), ex.getMessage()));
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
            CostCenterServ.hierToXlsx(this.coarea.getHier(), this.dataset.getId(), cuser.getUname(), stream);
            
        }catch(Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.data.down", clocale), ex.getMessage()));
        }
        fcontext.responseComplete();
    }

    @Override
    public String getInitError() {
        return initError;
    }

    public CoArea getCoarea() {
        return coarea;
    }

    @Override
    public DataSet getDataset() {
        return dataset;
    }
    
    @Override
    public List<CostCenter> getList(){
        return this.list;
    }

    public TreeNode getSelected() {
        return selected;
    }

    public void setSelected(TreeNode selected) {
        this.selected = selected;
    }
    
    @Override
    public void setSelected(CostCenter selected) {
        this.selected = (selected == null ? null : new DefaultTreeNode(selected));
    }
    
    public TreeNode getHier(){
        if (this.hier == null) this.hier = new DefaultTreeNode();        
        if (!this.reinit) return this.hier;
        if (this.list == null || this.list.isEmpty()) return new DefaultTreeNode();
        
        this.hier = new DefaultTreeNode();
        try {                        
            //stabilire pozitie selectata
            final CostCenter selectat = (this.selected == null ? null: (CostCenter) this.selected.getData());
            
            Map<String, TreeNode> noduri = new HashMap<>();
            this.list.sort(Comparator.comparing(CostCenter::getNivel).thenComparing(CostCenter::getCod));
            this.list.forEach((center) -> {
                TreeNode iterator = new DefaultTreeNode(center,
                        (center.getSuperior_cod()== null? this.hier: noduri.get(center.getSuperior_cod())));                
                if (center.getLeaf() == null || center.getLeaf().equals(false)){
                    noduri.put(center.getCod(), iterator);
                    iterator.setExpanded(true);
                    iterator.setSelectable(false);
                }                
                //setare pozitie selectata
                if (selectat != null && center.getCod().equals(selectat.getCod())) iterator.setSelected(true);
            });
            this.reinit = false; //setare flag pentru oprire reinitializare redering ierarhii
            
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.ccenter.hier.init", clocale), ex.getMessage())
            );
        }
        return this.hier;
    }
    
    public void setReinit(boolean reinit) {
        this.reinit = reinit;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
