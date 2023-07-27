package ro.any.c12153.opexpl.view.key;

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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.util.Constants;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.KeyHead;
import ro.any.c12153.opexpl.entities.KeyVal;
import ro.any.c12153.opexpl.entities.KeyValGroup;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.DataSetPerServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.KeyG01Serv;
import ro.any.c12153.opexpl.view.CopyFromDataSet;
import ro.any.c12153.opexpl.view.CopyFromDataSetContract;
import ro.any.c12153.opexpl.view.DataSetType;
import ro.any.c12153.opexpl.view.help.KeyG01ValReduce;
import ro.any.c12153.shared.App;
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
@Named(value = "kg01vlist")
@ViewScoped
public class KeyG01ValList implements Serializable, CopyFromDataSetContract{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(KeyG01ValList.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject KeyG01ValItem item;
    private @Inject DataBaseLoadView dataLoad;
    private @Inject CopyFromDataSet copyForm;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private KeyHead cheie;
    private List<Short> dataset_ani;
    
    private List<KeyValGroup> list;
    private TreeNode hier;
    private TreeNode selected;
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
            String ky = Optional.ofNullable(params.get("ky"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.keys.not", clocale)));
            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.coarea = CoAreaServ.getByCod(Utils.paramDecode(co), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
                            if(!Boolean.TRUE.equals(this.coarea.getAlocare()))
                                throw new Exception(App.getBeanMess("title.coarea.nasgn", clocale));
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
                                throw new Exception(App.getBeanMess("err.cgroup.dset.close", clocale));
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }                        
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.cheie = KeyG01Serv.getById(
                                    Integer.valueOf(Utils.paramDecode(ky)),
                                    Integer.valueOf(Utils.paramDecode(ds)),
                                    cuser.getUname()
                            ).orElseThrow(() -> new Exception(App.getBeanMess("err.keys.not", clocale)));
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
                        (this.dataset == null ? "" : "&ds=" + Utils.paramEncode(this.dataset.getId().toString())) +
                        (this.cheie == null ? "" : "&ky=" + Utils.paramEncode(this.cheie.getId().toString()));            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        try{
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            if (this.dataset_ani == null || this.dataset_ani.isEmpty())
                                this.dataset_ani = DataSetPerServ.getByDataSet(this.dataset.getId(), cuser.getUname()).stream()
                                        .map(x -> x.getAn())
                                        .distinct()
                                        .sorted()
                                        .collect(Collectors.toList()); 
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.list = KeyG01Serv.getValsGrouped(this.cheie.getId(), this.coarea.getHier(), this.dataset.getId(), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).get(60, TimeUnit.SECONDS);            
            this.reinit = true; //setare flag pentru reinitializare ierarhie
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.val.listinit", clocale), ex.getMessage()));
        }
    }
    
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null || this.selected.getData() == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new KeyValGroup(((KeyValGroup) this.selected.getData()).getJson()));
                if (initLists) this.item.initLists();
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }
    
    public void clear(){
        this.dialog.clear();
        this.item.clear();
        this.dataLoad.clear();
        this.copyForm.clear();
        this.finishScript = null;
    }
    
    public double getTotalByCheiAndAn(List<KeyVal> vals, short an){
        if (vals == null || vals.isEmpty()) return 0;
        return vals.stream()
            .filter(x -> x.getAn().equals(an))
            .mapToDouble(KeyVal::getValoare)
            .sum();
    }
    
    public void deleteAll(){
        try {
            KeyG01Serv.deleteValsByCheiAndSet(this.cheie.getId(), this.dataset.getId(), cuser.getUname());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.val.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.val.del", clocale), ex.getMessage()));
        }
    }
    
    @Override
    public void copiaza(Integer fromset, String finishScript, byte alternativa){
        try {
            if (fromset == null) throw new Exception(App.getBeanMess("err.dset.per.noset", clocale));
            KeyG01Serv.takeOverValsByCheiAndSet(this.cheie.getId(), fromset, this.dataset.getId(), cuser.getUname());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.vals.copy", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(finishScript)) PrimeFaces.current().executeScript(finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.vals.copy", clocale), ex.getMessage()));
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
            KeyG01Serv.valsByCheiAndSetToXlsx(this.cheie.getId(), this.dataset.getId(), cuser.getUname(), stream);
            
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

    public KeyHead getCheie() {
        return cheie;
    }

    public void setCheie(KeyHead cheie) {
        this.cheie = cheie;
    }

    public List<Short> getDataset_ani() {
        return dataset_ani;
    }

    public List<KeyValGroup> getList() {
        return list;
    }

    public void setList(List<KeyValGroup> list) {
        this.list = list;
    }

    public void setReinit(boolean reinit) {
        this.reinit = reinit;
    }
    
    public TreeNode getHier() {
        if (this.hier == null) this.hier = new DefaultTreeNode();
        if (!this.reinit) return this.hier;
        if (this.list == null || this.list.isEmpty()) return new DefaultTreeNode();
        
        this.hier = new DefaultTreeNode();
        try {            
            //stabilire pozitie selectata
            final KeyValGroup selectat = (this.selected == null ? null : (KeyValGroup) this.selected.getData());
            
            Map<String, TreeNode> noduri = new HashMap<>();
            KeyG01ValReduce.setValuesForCostCenterGroup(this.list);
            this.list.forEach((poz) -> {
                TreeNode iterator = new DefaultTreeNode(poz,
                    (poz.getSuperior_cod() == null ? this.hier : noduri.get(poz.getSuperior_cod())));
                
                if (poz.getLeaf() == null || poz.getLeaf().equals(false)){
                    noduri.put(poz.getCod(), iterator);
                    iterator.setExpanded(true);
                }
                
                //setare pozitie selectata
                if (selectat != null && selectat.getLeaf() != null &&
                        poz.getCod().equals(selectat.getCod())) iterator.setSelected(true);
            });
            this.reinit = false; //setare flag pentru oprire reinitializare rendering ierarhie
            
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.ccenter.hier.get", clocale), ex.getMessage())
            );
        }
        return this.hier;
    }

    public TreeNode getSelected() {
        return selected;
    }

    public void setSelected(TreeNode selected) {
        this.selected = selected;
    }
    
    public void setSelected(KeyValGroup selected){
        this.selected = new DefaultTreeNode(selected);
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
