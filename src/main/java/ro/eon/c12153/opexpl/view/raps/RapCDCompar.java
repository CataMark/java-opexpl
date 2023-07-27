package ro.any.c12153.opexpl.view.raps;

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
import ro.any.c12153.opexpl.entities.CostDriver;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.DataSetPer;
import ro.any.c12153.opexpl.entities.OpexCateg;
import ro.any.c12153.opexpl.entities.PlanDoc;
import ro.any.c12153.opexpl.entities.PlanVal;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostDriverServ;
import ro.any.c12153.opexpl.services.DataSetPerServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.OpexCategServ;
import ro.any.c12153.opexpl.services.PlanDocServ;
import ro.any.c12153.opexpl.services.UserOxplServ;
import ro.any.c12153.opexpl.view.CopyFromDataSet;
import ro.any.c12153.opexpl.view.CopyFromDataSetContract;
import ro.any.c12153.opexpl.view.DataSetType;
import ro.any.c12153.opexpl.view.help.PlanCDComparReduce;
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
@Named(value = "rcdcompar")
@ViewScoped
public class RapCDCompar implements Serializable, CopyFromDataSetContract{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RapCDCompar.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject CopyFromDataSet copyForm;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private DataSet comparset;
    private CostDriver cdriver;
    private OpexCateg ocateg;
    
    private List<Short> ani_main_set;
    private List<Short> ani_compar_set;
    private List<PlanDoc> list;
    
    private TreeNode agregare;
    private TreeNode selected;
    
    private void initAggregare(){
        this.agregare = new DefaultTreeNode();
        try {
            if (this.list == null || this.list.isEmpty()) return;        
            if (this.ocateg == null){ //agregare in caz ca nu este selectata o categorie de cheltuieli
                PlanDoc total = PlanCDComparReduce.getTotalGroup(this.list)
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.plan.list.nok", clocale)));

                //nodul total
                TreeNode totalNod = new DefaultTreeNode(total, this.agregare);
                totalNod.setExpanded(true);
                totalNod.setSelectable(true);

                //noduri categorii cheltuieli
                this.list.forEach(x -> {
                    TreeNode iterator = new DefaultTreeNode("ocateg", x, totalNod);
                    iterator.setSelectable(true);
                });
            } else { //agregare pe ierarhie de centre de cost atunci cand este selectata o categorie de cheltuieli
                final PlanDoc selectat = (this.selected == null ? null : (PlanDoc) this.selected.getData());

                Map<String, TreeNode> noduri = new HashMap<>();
                PlanCDComparReduce.setValuesForCostCenterGroup(this.list);
                this.list.forEach(x -> {
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
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.dataset = DataSetServ.getById(Integer.valueOf(Utils.paramDecode(ds)), cuser.getUname())
                                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.per.noset", clocale)));
                            if(!Boolean.TRUE.equals(this.dataset.getRaportare()))
                                throw new Exception(App.getBeanMess("err.dset.norap", clocale));
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
                    if (costDriverBound.get() && !userHasRightsonCdriver.get())
                            throw new Exception(App.getBeanMess("err.user.norights", clocale));
                } catch (Exception ex) {
                    throw new CompletionException(ex);
                }
            }).get(30, TimeUnit.SECONDS);
            
            if (this.dataset.getCompar() != null){
                this.comparset = new DataSet();
                this.comparset.setId(this.dataset.getCompar());
                this.comparset.setNume(this.dataset.getCompar_nume());
                this.comparset.setAn(this.dataset.getCompar_an());
                this.comparset.setVers(this.dataset.getCompar_vers());
            }
            
            this.agregare = new DefaultTreeNode();
            this.copyForm.init(this, DataSetType.RAPORT);
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
                            if (this.ani_main_set == null || this.ani_main_set.isEmpty()){
                                this.ani_main_set = DataSetPerServ.getByDataSet(this.dataset.getId(), cuser.getUname()).stream()
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
                            if (this.comparset != null && (this.ani_compar_set == null || this.ani_compar_set.isEmpty()))
                                this.ani_compar_set = DataSetPerServ.getByDataSet(this.comparset.getId(), cuser.getUname()).stream()
                                        .map(DataSetPer::getAn)
                                        .distinct()
                                        .collect(Collectors.toList());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.list = PlanDocServ.getCDriverCentralCompar(
                                    this.dataset.getId(),
                                    this.comparset == null ? Optional.empty() : Optional.ofNullable(this.comparset.getId()),
                                    this.coarea.getHier(),
                                    this.cdriver.getCod(),
                                    this.ocateg == null ? Optional.empty() : Optional.of(this.ocateg.getCod()),
                                    cuser.getUname()
                            );
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
    
    public void clear(){
        this.dialog.clear();
        this.copyForm.clear();
    }
    
    @Override
    public void copiaza(Integer fromDataSet, String finishScript, byte alternativa) {
        try {
            if (fromDataSet == null) throw new Exception(App.getBeanMess("err.dset.per.noset", clocale));
            this.comparset = DataSetServ.getById(fromDataSet, cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.dset.per.noset", clocale)));
            this.ani_compar_set = null;
            
            this.datainit();
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.plan.doc.set.chg", clocale), App.getBeanMess("info.success", clocale)));
            if (Utils.stringNotEmpty(finishScript)) PrimeFaces.current().executeScript(finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.plan.doc.set.chg", clocale), ex.getMessage()));
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
            PlanDocServ.toXlsxCDriverCentralCompar(
                    this.dataset.getId(),
                    this.comparset == null ? Optional.empty() : Optional.ofNullable(this.comparset.getId()),
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
    
    public Double getSumBySetAndAn(Integer set, Short an, List<PlanVal> vals){
        if (vals == null || vals.isEmpty()) return null;
        double suma = vals.stream()
                .filter(x -> set.equals(x.getData_set()) &&  an.equals(x.getAn()))
                .mapToDouble(PlanVal::getValoare)
                .sum();
        return (suma == 0 ? null : suma);
    }

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
    
    public DataSet getComparset() {
        return comparset;
    }
    
    public CostDriver getCdriver() {
        return cdriver;
    }

    public OpexCateg getOcateg() {
        return ocateg;
    }   
    
    public List<Short> getAni() {
        return this.ani_main_set;
    }

    public List<Short> getAni_main_set() {
        return ani_main_set;
    }

    public List<Short> getAni_compar_set() {
        return ani_compar_set;
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
