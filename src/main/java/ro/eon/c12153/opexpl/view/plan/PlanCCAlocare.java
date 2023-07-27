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
import ro.any.c12153.opexpl.entities.BussLineAsg;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostCenter;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.DataSetPer;
import ro.any.c12153.opexpl.entities.PlanDoc;
import ro.any.c12153.opexpl.entities.PlanVal;
import ro.any.c12153.opexpl.services.BussLineAsgServ;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostCenterGroupServ;
import ro.any.c12153.opexpl.services.CostCenterServ;
import ro.any.c12153.opexpl.services.DataSetPerServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.PlanDocServ;
import ro.any.c12153.opexpl.services.UserOxplServ;
import ro.any.c12153.opexpl.view.help.CostCenterCompoundUrlParamHelp;
import ro.any.c12153.opexpl.view.help.PlanCCAlocareReduce;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "pccaloc")
@ViewScoped
public class PlanCCAlocare implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(PlanCCAlocare.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private CostCenter ccenter;
    
    private List<Short> ani;
    private Short selAn;
    private List<BussLineAsg> busslines;
    private List<String> segmente;
    private Map<String, Boolean> group;
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
    
    private static boolean areValsInAn(Short an, List<PlanVal> valori){
        boolean rezultat = false;
        if (valori == null || valori.isEmpty()) return rezultat;
        
        for (PlanVal valoare : valori){
            if (an.equals(valoare.getAn()) && !Double.valueOf(0).equals(valoare.getValoare())){
                rezultat = true;
                break;
            }
        }
        return rezultat;
    }
    
    private void initAggregare(){
        this.aggregare = new DefaultTreeNode();
        try {
            if (this.list == null || this.list.isEmpty()) return;
            if (!this.list.stream()
                    .filter(x -> areValsInAn(this.selAn, x.getValori()))
                    .findFirst()
                    .isPresent())
                return;

            List<PlanDoc> cdrivers = PlanCCAlocareReduce.getCostDriverGroups(this.list);
            PlanDoc total = PlanCCAlocareReduce.getTotalGroup(cdrivers)
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.plan.list.nok", clocale)));

            //nodul total
            TreeNode totalNod = new DefaultTreeNode(total, this.aggregare);
            totalNod.setExpanded(true);
            totalNod.setSelectable(true);

            //noduri cost driver
            Map<String, TreeNode> noduri = new HashMap<>();
            cdrivers.forEach(x -> {
                TreeNode iterator = new DefaultTreeNode(x, totalNod);
                iterator.setExpanded(true);
                iterator.setSelectable(true);
                noduri.put(x.getCost_driver(), iterator);
            });

            //categorii opex
            final PlanDoc selectat = (this.selected == null ? null : (PlanDoc) this.selected.getData());
            this.list.stream()
                    .filter(x -> areValsInAn(this.selAn, x.getValori()))
                    .forEach(x -> {
                        TreeNode iterator = new DefaultTreeNode("ocateg", x, noduri.get(x.getCost_driver()));
                        iterator.setSelectable(true);
                        if (selectat != null && x.getOpex_categ().equals(selectat.getOpex_categ()) &&
                                ((x.getIc_part() == null && selectat.getIc_part() == null) || x.getIc_part().equals(selectat.getIc_part())))
                            iterator.setSelected(true);
                    });
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }        
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
                            if (this.ani == null || this.ani.isEmpty()){
                                this.ani = DataSetPerServ.getByDataSet(this.dataset.getId(), cuser.getUname()).stream()
                                        .map(DataSetPer::getAn)
                                        .distinct()
                                        .collect(Collectors.toList());
                                this.selAn = this.ani.get(0);
                            }
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            if (this.busslines == null || this.busslines.isEmpty()){
                                    this.busslines = BussLineAsgServ.getListAsignToCoArea(this.coarea.getCod(), cuser.getUname());
                                    this.segmente = this.busslines.stream()
                                            .map(BussLineAsg::getBuss_line_seg)
                                            .distinct()
                                            .collect(Collectors.toList());
                                }
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.list = PlanDocServ.getCCenterNCentralAlocare(this.dataset.getId(), this.coarea.getHier(), this.ccenter.getCod(), Optional.of(Boolean.FALSE), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).thenRun(this::initAggregare)
            .get(60, TimeUnit.SECONDS);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.plan.listinit", clocale), ex.getMessage()));
        }
    }
    
    public void toggleGroup(){
        String seg_param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("segment");
        if (Utils.stringNotEmpty(seg_param))
            this.group.replace(seg_param, !this.group.get(seg_param));
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
            PlanDocServ.toXlsxCCenterNCentralAlocare(this.dataset.getId(), this.coarea.getHier(), this.ccenter.getCod(), Optional.of(Boolean.FALSE), cuser.getUname(), stream);
            
        }catch(Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.data.down", clocale), ex.getMessage()));
        }
        fcontext.responseComplete();
    }
    
    public void onAnChange(){
        try {
            this.initAggregare();
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.plan.aggrinit", clocale), ex.getMessage()));
        }
    }
    
    public Double getTotalPlanValue(List<PlanVal> vals){
        if (vals == null || vals.isEmpty()) return null;
        double suma = vals.stream()
                .filter(x -> this.selAn.equals(x.getAn()) && "planificat".equals(x.getCont()))
                .mapToDouble(PlanVal::getValoare)
                .sum();
        return (suma == 0 ? null : suma);
    }
    
    public Double getTotalAlocValue(List<PlanVal> vals){
        if (vals == null || vals.isEmpty()) return null;
        double suma = vals.stream()
                .filter(x -> this.selAn.equals(x.getAn()) && "alocat".equals(x.getCont()))
                .mapToDouble(PlanVal::getValoare)
                .sum();
        return (suma == 0 ? null : suma);        
    }
    
    public Double getSumBySegm(String segment, List<PlanVal> vals){
        if (vals == null || vals.isEmpty()) return null;
        double suma = this.busslines.stream()
                .filter(x -> segment.equals(x.getBuss_line_seg()))
                .mapToDouble(x -> {
                    return vals.stream()
                            .filter(y -> this.selAn.equals(y.getAn()) && "alocat".equals(y.getCont()) && x.getBuss_line().equals(y.getPer()))
                            .mapToDouble(PlanVal::getValoare)
                            .sum();
                }).sum();
        return (suma == 0 ? null : suma);
    }
    
    public Double getSumByBussLine(String bussline, List<PlanVal> vals){
        if (vals == null || vals.isEmpty()) return null;
        double suma = vals.stream()
                .filter(x -> this.selAn.equals(x.getAn()) && "alocat".equals(x.getCont()) && bussline.equals(x.getPer()))
                .mapToDouble(PlanVal::getValoare)
                .sum();
        return (suma == 0 ? null : suma);
    }
    
    public boolean getPlanEqualAloc(List<PlanVal> vals){
        if (vals == null || vals.isEmpty()) return true;
        Double plan = this.getTotalPlanValue(vals);
        Double aloc = this.getTotalAlocValue(vals);
        if (plan == null && aloc == null){
            return true;
        } else if (plan != null && aloc == null){
            return false;
        } else if (plan == null && aloc != null){
            return false;
        } else{
            return (Math.abs(plan - aloc) <= 1);
        }
    }
    
    public String getInitError() {
        return initError;
    }
    
    public CoArea getCoarea() {
        return coarea;
    }

    public DataSet getDataset() {
        return dataset;
    }

    public CostCenter getCcenter() {
        return ccenter;
    }

    public List<Short> getAni() {
        return ani;
    }

    public Short getSelAn() {
        return selAn;
    }

    public void setSelAn(Short selAn) {
        this.selAn = selAn;
    }
    
    public List<String> getSegmente() {
        return this.segmente;
    }

    public List<BussLineAsg> getBusslines(String segment) {
        return this.busslines.stream()
                .filter(x -> segment.equals(x.getBuss_line_seg()))
                .collect(Collectors.toList());
    } 
    
    public Map<String, Boolean> getGroup() {
        if (this.group != null && !this.group.isEmpty()) return this.group;
        
        this.group = new HashMap<>();
        this.segmente.forEach(x -> this.group.put(x, Boolean.TRUE));        
        return this.group;
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
}
