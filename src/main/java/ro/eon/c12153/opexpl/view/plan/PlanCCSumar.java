package ro.any.c12153.opexpl.view.plan;

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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.util.Constants;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostCenter;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.DataSetPer;
import ro.any.c12153.opexpl.entities.PlanDoc;
import ro.any.c12153.opexpl.entities.PlanVal;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostCenterGroupServ;
import ro.any.c12153.opexpl.services.CostCenterServ;
import ro.any.c12153.opexpl.services.DataSetPerServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.PlanDocServ;
import ro.any.c12153.opexpl.services.UserOxplServ;
import ro.any.c12153.opexpl.view.help.CostCenterCompoundUrlParamHelp;
import ro.any.c12153.opexpl.view.help.PlanCCSumarReduce;
import ro.any.c12153.opexpl.view.help.PlanDocListContract;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.beans.DialogController;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "pccsumar")
@ViewScoped
public class PlanCCSumar implements Serializable, PlanDocListContract{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(PlanCCSumar.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject PlanDocItem item;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private CostCenter ccenter;
    
    private List<Short> ani;
    private List<DataSetPer> perioade;
    private Map<Short, Boolean> group; 
    private List<PlanDoc> list;
    
    private TreeNode aggregare;
    private TreeNode selected;
    
    private void initCcenter(String cc_param){
        try {
            CostCenterCompoundUrlParamHelp param = new CostCenterCompoundUrlParamHelp(Utils.paramDecode(cc_param));
            if (Boolean.TRUE.equals(param.getLeaf())){
                this.ccenter = CostCenterServ.getById(param.getCcenter_id(), cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.ccenter.not", clocale)));
                this.ccenter.setLeaf(Boolean.TRUE);
            } else {
                this.ccenter = CostCenterGroupServ.getById(param.getCcenter_id(), cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.ccenter.not", clocale)))
                        .cast();
                this.ccenter.setLeaf(Boolean.FALSE);
            }
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    @Override
    public void initAggregare(boolean newItem) throws Exception{
        this.aggregare = new DefaultTreeNode();

        if (this.list == null || this.list.isEmpty()) return;
        if (newItem) this.list = PlanCCSumarReduce.groupByOpexCateg(this.list);

        List<PlanDoc> cdrivers = PlanCCSumarReduce.getCostDriverGroups(this.list);
        PlanDoc total = PlanCCSumarReduce.getTotalGroup(cdrivers)
                .orElseThrow(() -> new Exception(App.getBeanMess("err.plan.list.nok", clocale)));

        //nodul total
        TreeNode totalNod = new DefaultTreeNode(total, this.aggregare);
        totalNod.setExpanded(true);
        totalNod.setSelectable(false);

        //noduri cost driver
        Map<String, TreeNode> noduri = new HashMap<>();
        cdrivers.forEach(x -> {
            TreeNode iterator = new DefaultTreeNode(x, totalNod);
            iterator.setExpanded(true);
            iterator.setSelectable(false);
            noduri.put(x.getCost_driver(), iterator);
        });

        //categorii opex
        final PlanDoc selectat = (this.selected == null ? null : (PlanDoc) this.selected.getData());
        this.list.forEach(x -> {
            TreeNode iterator = new DefaultTreeNode("ocateg", x, noduri.get(x.getCost_driver()));
            iterator.setSelectable(true);
            if (selectat != null && x.getOpex_categ().equals(selectat.getOpex_categ()) &&
                    ((x.getIc_part() == null && selectat.getIc_part() == null) || (x.getIc_part() != null && x.getIc_part().equals(selectat.getIc_part()))))
                iterator.setSelected(true);
        });
    }
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String hr = Optional.ofNullable(params.get("hr"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            String ds = Optional.ofNullable(params.get("ds"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.per.noset", clocale)));
            String cc = Optional.ofNullable(params.get("cc"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.ccenter.not", clocale)));
            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.coarea = CoAreaServ.getByHier(Utils.paramDecode(hr), cuser.getUname())
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
                                throw new Exception(App.getBeanMess("err.cgroup.dset.close", clocale));
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> this.initCcenter(cc))
            ).thenRun(() -> {
                try {
                    CompletableFuture<Boolean> costCenterBound = CompletableFuture.supplyAsync(() -> {
                        try {
                            return UserOxplServ.checkUserCostCenterBound(cuser.getUname(), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    });
                    CompletableFuture<Boolean> userHasRightsOnCcenter = CompletableFuture.supplyAsync(() -> {
                        try {
                            return UserOxplServ.checkUserHasRightsOnCcenter(this.coarea.getHier(), this.dataset.getId(), this.ccenter.getCod(), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    });
                    
                    if (costCenterBound.get()){
                        if(!Boolean.FALSE.equals(this.dataset.getBlocat()))
                            throw new Exception(App.getBeanMess("err.cgroup.dset.close", clocale));
                        if (!userHasRightsOnCcenter.get())
                            throw new Exception(App.getBeanMess("err.user.norights", clocale));
                    }
                } catch (Exception ex) {
                    throw new CompletionException(ex);
                }
            }).get(30, TimeUnit.SECONDS);
            
            this.aggregare = new DefaultTreeNode();
            this.item.inject(this, false);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            CostCenterCompoundUrlParamHelp ccenter_param = null;
            if (this.ccenter != null)
                ccenter_param = new CostCenterCompoundUrlParamHelp(this.ccenter.getId(), this.ccenter.getLeaf());

            rezultat += (this.coarea == null ? "" : "&hr=" + Utils.paramEncode(this.coarea.getHier())) +
                        (this.dataset == null ? "" : "&ds=" + Utils.paramEncode(this.dataset.getId().toString())) +
                        (ccenter_param == null ? "" : "&cc=" + Utils.paramEncode(ccenter_param.getJson().toString()));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }
    
    public void datainit(){
        try {
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        try {
                            if (this.perioade == null || this.perioade.isEmpty()){
                                this.perioade = DataSetPerServ.getByDataSet(this.dataset.getId(), cuser.getUname());
                                this.ani = this.perioade.stream()
                                        .map(DataSetPer::getAn)
                                        .distinct()
                                        .collect(Collectors.toList());
                            }
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.list = PlanDocServ.getCCenterNCentralSumar(this.dataset.getId(), this.coarea.getHier(), this.ccenter.getCod(), Optional.of(Boolean.FALSE), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).thenRun(() -> {
                try {
                    this.initAggregare(false);
                } catch (Exception ex) {
                    throw new CompletionException(ex);
                }
            }).get(60, TimeUnit.SECONDS);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.plan.listinit", clocale), ex.getMessage()));
        }
    }
    
    public void toggleGroup(){
        String an_param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("an");
        if (Utils.stringNotEmpty(an_param))
            this.group.replace(Short.valueOf(an_param), !this.group.get(Short.valueOf(an_param)));
    }
    
    public void clear(){
        this.dialog.clear();
        this.item.clear();
    }
    
    public void newItem(){
        PlanDoc rezultat = new PlanDoc();
        rezultat.setCoarea(this.coarea.getCod());
        rezultat.setHier(this.coarea.getHier());
        rezultat.setData_set(this.dataset.getId());
        rezultat.setCost_center(this.ccenter.getCod());
        rezultat.setCost_center_nume(this.ccenter.getNume());
        rezultat.setCost_center_blocat(this.ccenter.getBlocat());
        this.item.setSelected(rezultat);
        this.item.init(true);
    }
    
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null || this.selected.getData() == null){
                this.item.setSelected(null);
            } else {
                PlanDoc rezultat = new PlanDoc(((PlanDoc) this.selected.getData()).getJson(false));
                rezultat.setCost_center(this.ccenter.getCod());
                rezultat.setCost_center_nume(this.ccenter.getNume());
                rezultat.setCost_center_blocat(this.ccenter.getBlocat());
                this.item.setSelected(rezultat);
                this.item.init(initLists);
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
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
            PlanDocServ.toXlsxCCenterNCentralSumar(this.dataset.getId(), this.coarea.getHier(), this.ccenter.getCod(), Optional.of(Boolean.FALSE), cuser.getUname(), stream);
            
        }catch(Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.data.down", clocale), ex.getMessage()));
        }
        fcontext.responseComplete();
    }
    
    public Double getSumByAn(Short an, List<PlanVal> vals){
        if (vals == null || vals.isEmpty()) return null;
        double suma = vals.stream()
                .filter(x -> an.equals(x.getAn()))
                .mapToDouble(PlanVal::getValoare)
                .sum();
        return (suma == 0 ? null : suma);
    }
    
    public Double getSumByPer(Short an, String per, List<PlanVal> vals){
        if (vals == null || vals.isEmpty()) return null;
        double suma = vals.stream()
                .filter(x -> an.equals(x.getAn()) && per.equals(x.getPer()))
                .mapToDouble(PlanVal::getValoare)
                .sum();
        return (suma == 0 ? null : suma);
    }
    
    public String getInitError() {
        return initError;
    }

    @Override
    public CoArea getCoarea() {
        return coarea;
    }

    public DataSet getDataset() {
        return dataset;
    }

    public CostCenter getCcenter() {
        return ccenter;
    }

    @Override
    public List<Short> getAni() {
        return this.ani;
    }

    @Override
    public List<DataSetPer> getPerioade() {
        return perioade;
    }

    public List<DataSetPer> getPerioade(Short an) {
        return this.perioade.stream()
                .filter(x -> an.equals(x.getAn()))
                .collect(Collectors.toList());
    }

    public Map<Short, Boolean> getGroup() {
        if (this.group != null && !this.group.isEmpty()) return this.group;
        
        this.group = new HashMap<>();
        for (Short an : this.ani){
            this.group.put(an, false);
        }
        return this.group;
    }

    @Override
    public List<PlanDoc> getList() {
        return list;
    }

    public TreeNode getAggregare() {    
        return this.aggregare;
    }

    public TreeNode getSelected() {
        return selected;
    }

    public void setSelected(TreeNode selected) {
        this.selected = selected;
    }

    @Override
    public void setSelected(PlanDoc selected) {
        this.selected = new DefaultTreeNode(selected);
    }
}
