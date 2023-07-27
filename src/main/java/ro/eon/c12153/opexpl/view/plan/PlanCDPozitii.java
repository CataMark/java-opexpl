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
@Named(value = "pcdpoz")
@ViewScoped
public class PlanCDPozitii implements Serializable, PlanDocListContract{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(PlanCDPozitii.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject PlanDocItem item;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private CostDriver cdriver;
    private OpexCateg ocateg;
    
    private List<Short> ani;
    private List<DataSetPer> perioade;
    private Map<Short, Boolean> group;
    private List<PlanDoc> list;
    
    private PlanDoc selected;
    private String[] filterValues;
    private List<PlanDoc> filtered;
    
    private void clearFilters(){
        this.filterValues = new String[]{"","","","","",""};
    }

    @Override
    public void initAggregare(boolean newItem) throws Exception {

    }
    
    @PostConstruct
    private void init(){
        try {
            this.clearFilters();
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
            
            this.item.inject(this, true);
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
                            this.list = PlanDocServ.getCDriverCentralPozitii(
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
            ).get(60, TimeUnit.SECONDS);
            
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
    
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                PlanDoc rezultat = new PlanDoc(this.selected.getJson(true));
                rezultat.setCost_driver(this.cdriver.getCod());
                rezultat.setCost_driver_nume(this.cdriver.getNume());
                if (this.ocateg != null){
                    rezultat.setOpex_categ(this.ocateg.getCod());
                    rezultat.setOpex_categ_nume(this.ocateg.getNume());
                    rezultat.setOpex_categ_blocat(this.ocateg.getBlocat());
                }                
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
            PlanDocServ.toXlsxCDriverCentralPozitii(
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
    
    public double getTotalByAn(Short an){
        List<PlanDoc> lista = (this.filtered == null ? this.list : this.filtered);
        if (lista == null || lista.isEmpty()) return 0;
        return lista.stream()
                .filter(x -> x.getValori() != null && !x.getValori().isEmpty())
                .flatMap(x -> x.getValori().stream())
                .filter(x -> x.getAn().equals(an))
                .mapToDouble(PlanVal::getValoare)
                .sum();
    }
    
    public double getTotalByPer(Short an, String per){
        if (this.list == null || this.list.isEmpty()) return 0;
        return this.list.stream()
                .filter(x -> x.getValori() != null && !x.getValori().isEmpty())
                .flatMap(x -> x.getValori().stream())
                .filter(x -> x.getAn().equals(an) && x.getPer().equals(per))
                .mapToDouble(PlanVal::getValoare)
                .sum();
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

    public CostDriver getCdriver() {
        return cdriver;
    }

    public OpexCateg getOcateg() {
        return ocateg;
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
        return this.perioade.stream().filter(x -> an.equals(x.getAn())).collect(Collectors.toList());
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

    public PlanDoc getSelected() {
        return selected;
    }

    @Override
    public void setSelected(PlanDoc selected) {
        this.selected = selected;
    }

    public List<PlanDoc> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<PlanDoc> filtered) {
        this.filtered = filtered;
    }

    public String[] getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(String[] filterValues) {
        this.filterValues = filterValues;
    }
}
