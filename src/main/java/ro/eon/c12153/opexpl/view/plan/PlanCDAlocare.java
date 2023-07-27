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
import ro.any.c12153.opexpl.entities.CostDriver;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.DataSetPer;
import ro.any.c12153.opexpl.entities.OpexCateg;
import ro.any.c12153.opexpl.entities.PlanDoc;
import ro.any.c12153.opexpl.entities.PlanVal;
import ro.any.c12153.opexpl.services.BussLineAsgServ;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostDriverServ;
import ro.any.c12153.opexpl.services.DataSetPerServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.OpexCategServ;
import ro.any.c12153.opexpl.services.PlanDocServ;
import ro.any.c12153.opexpl.services.UserOxplServ;
import ro.any.c12153.opexpl.view.help.PlanCDAlocareReduce;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "pcdaloc")
@ViewScoped
public class PlanCDAlocare implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(PlanCDAlocare.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private CostDriver cdriver;
    private OpexCateg ocateg;
    
    private List<Short> ani;
    private Short selAn;
    private List<BussLineAsg> busslines;
    private List<String> segmente;
    private Map<String, Boolean> group;
    private List<PlanDoc> list;
    
    private TreeNode agregare;
    private TreeNode selected;
    
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
        this.agregare = new DefaultTreeNode();
        try {
            if (this.list == null || this.list.isEmpty()) return;
            if (!this.list.stream()
                    .filter(x -> areValsInAn(this.selAn, x.getValori()))
                    .findFirst()
                    .isPresent())
                return;

            final PlanDoc selectat = (this.selected == null ? null : (PlanDoc) this.selected.getData());            
            if (this.ocateg == null){ //agregare in caz ca nu este selectata o categorie de cheltuieli
                PlanDoc total = PlanCDAlocareReduce.getTotalGroup(this.list)
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.plan.list.nok", clocale)));

                //nodul total
                TreeNode totalNod = new DefaultTreeNode(total, this.agregare);
                totalNod.setExpanded(true);
                totalNod.setSelectable(true);

                //noduri categorii cheltuieli

                this.list.stream()
                    .filter(x -> areValsInAn(this.selAn, x.getValori()))
                    .forEach(x -> {
                        TreeNode iterator = new DefaultTreeNode("ocateg", x, totalNod);
                        iterator.setSelectable(true);
                        if (selectat != null && x.getOpex_categ().equals(selectat.getOpex_categ()) &&
                                ((x.getIc_part() == null && selectat.getIc_part() == null) || x.getIc_part().equals(selectat.getIc_part())))
                            iterator.setSelected(true);
                    });                
            } else { //agregare pe ierarhie de centre de cost atunci cand este selectata o categorie de cheltuieli
                Map<String, TreeNode> noduri = new HashMap<>();
                this.list.stream()
                    .filter(x -> areValsInAn(this.selAn, x.getValori()))
                    .forEach(x -> {
                        TreeNode iterator = new DefaultTreeNode(x, (x.getCost_center_super() == null ? this.agregare : noduri.get(x.getCost_center_super())));
                        if (!Boolean.TRUE.equals(x.getCost_center_leaf())){
                            noduri.put(x.getCost_center(), iterator);
                            iterator.setExpanded(true);
                            iterator.setSelectable(true);
                        }
                        //setare pozitie selectata
                        if (selectat != null && selectat.getCost_center_leaf() != null && x.getCost_center().equals(selectat.getCost_center()) &&
                                ((x.getIc_part() == null && selectat.getIc_part() == null) || (x.getIc_part() != null && x.getIc_part().equals(selectat.getIc_part()))))
                            iterator.setSelected(true);
                    });
            }
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }        
    }
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String co = Optional.ofNullable(params.get("co"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("title.coarea.not", clocale)));
            String ds = Optional.ofNullable(params.get("ds"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.per.noset", clocale)));
            String cd = Optional.ofNullable(params.get("cd"))
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.cdriver.not", clocale)));
            Optional<String> oc = Optional.ofNullable(params.get("oc"));
            
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
                            this.cdriver = CostDriverServ.getAssignByCod(Utils.paramDecode(cd), Utils.paramDecode(co), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("err.cdriver.not", clocale)));                                    
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            if (oc.isPresent())
                                this.ocateg = OpexCategServ.getAssignByCod(Integer.valueOf(Utils.paramDecode(oc.get())), Utils.paramDecode(co), cuser.getUname())
                                        .orElse(null);
                            if (this.ocateg != null && !Utils.paramDecode(cd).equals(this.ocateg.getCost_driver()))
                                throw new Exception(App.getBeanMess("err.plan.opexcat.nok", clocale));
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).thenRun(() -> {
                try {
                    CompletableFuture<Boolean> costDriverBound = CompletableFuture.supplyAsync(() -> {
                        try {
                            return UserOxplServ.checkUserCostDriverBound(cuser.getUname(), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    });
                    CompletableFuture<Boolean> userHasRightsonCdriver = CompletableFuture.supplyAsync(() -> {
                        try {
                            return UserOxplServ.checkUserHasRightsOnCdriver(this.coarea.getCod(), this.cdriver.getCod(), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    });
                    if (costDriverBound.get()){
                        if (!Boolean.FALSE.equals(this.dataset.getBlocat()))
                            throw new Exception(App.getBeanMess("err.cgroup.dset.close", clocale));
                        if (!userHasRightsonCdriver.get())
                            throw new Exception(App.getBeanMess("err.user.norights", clocale));
                    }
                } catch (Exception ex) {
                    throw new CompletionException(ex);
                }
            }).get(30, TimeUnit.SECONDS);
            this.agregare = new DefaultTreeNode();
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
                        (this.cdriver == null ? "" : "&cd=" + Utils.paramEncode(this.cdriver.getCod())) +
                        (this.ocateg == null ? "" : "&oc=" + Utils.paramEncode(this.ocateg.getCod().toString()));
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
                            this.list = PlanDocServ.getCDriverCentralAlocare(
                                    this.dataset.getId(),
                                    this.coarea.getHier(),
                                    this.cdriver.getCod(),
                                    this.ocateg == null ? Optional.empty() : Optional.of(this.ocateg.getCod()),
                                    cuser.getUname()
                            );
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).thenRun(() -> {
                if (this.ocateg != null) PlanCDAlocareReduce.setValuesForCostCenterGroup(this.list);
                this.initAggregare();
            }).get(60, TimeUnit.SECONDS);
            
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
            PlanDocServ.toXlsxCDriverCentralAlocare(
                    this.dataset.getId(),
                    this.coarea.getHier(),
                    this.cdriver.getCod(),
                    this.ocateg == null ? Optional.empty() : Optional.of(this.ocateg.getCod()),
                    cuser.getUname(),
                    stream
            );
            
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

    public CostDriver getCdriver() {
        return cdriver;
    }

    public OpexCateg getOcateg() {
        return ocateg;
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
    
    public TreeNode getAgregare() {
        return this.agregare;
    }
    
    public TreeNode getSelected() {
        return selected;
    }

    public void setSelected(TreeNode selected) {
        this.selected = selected;
    }
}
