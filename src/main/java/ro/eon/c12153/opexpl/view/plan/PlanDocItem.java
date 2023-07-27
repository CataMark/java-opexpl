package ro.any.c12153.opexpl.view.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
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
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostDriver;
import ro.any.c12153.opexpl.entities.DataSetPer;
import ro.any.c12153.opexpl.entities.IcPartener;
import ro.any.c12153.opexpl.entities.OpexCateg;
import ro.any.c12153.opexpl.entities.PlanDoc;
import ro.any.c12153.opexpl.entities.PlanVal;
import ro.any.c12153.opexpl.services.CostDriverServ;
import ro.any.c12153.opexpl.services.IcPartenerServ;
import ro.any.c12153.opexpl.services.KeyS01Serv;
import ro.any.c12153.opexpl.services.KeyServ;
import ro.any.c12153.opexpl.services.OpexCategServ;
import ro.any.c12153.opexpl.services.PlanDocServ;
import ro.any.c12153.opexpl.view.help.KeyValCell;
import ro.any.c12153.opexpl.view.help.KeyValRow;
import ro.any.c12153.opexpl.view.help.PlanDocListContract;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "plandoc")
@ViewScoped
public class PlanDocItem implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(PlanDocItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private PlanDocListContract owner;
    private String initError;
    private PlanDoc selected;
    
    private List<CostDriver> cdrivers;
    private List<OpexCateg> ocategs;
    private List<IcPartener> partners;
    private List<SelectItem> chei;
    private List<KeyValRow> cheie_vals;
    private List<PlanVal> valori;
    private String finishScript;
    
    private boolean planCDriver;
    private boolean hasActual;
    private boolean renderCdriverSel;
    private boolean renderOcategSel;
    private boolean renderIcPartSel;
    private boolean renderCheieSel;
    private boolean renderSaveBtn;
    
    private void initFlags(boolean withLists){
        if (this.selected == null) return;
        if (this.selected.getValori() != null)
            this.hasActual = this.selected.getValori().stream()
                    .filter(x -> Boolean.TRUE.equals(x.getActual()) && !x.getValoare().equals(Double.valueOf(0)))
                    .map(PlanVal::getActual)
                    .findFirst()
                    .orElse(false);

        this.renderSaveBtn = !Boolean.TRUE.equals(this.selected.getCost_center_blocat()) &&
                ((!this.hasActual && !this.planCDriver) ||
                    (!Boolean.TRUE.equals(this.selected.getOpex_categ_blocat()) &&
                        !Boolean.TRUE.equals(this.selected.getIc_part_blocat())));

        if (!withLists) return;
        this.renderCdriverSel = this.renderSaveBtn && !this.hasActual && !this.planCDriver;
        this.renderOcategSel = this.renderSaveBtn && !this.hasActual && !this.planCDriver;
        this.renderIcPartSel = this.renderSaveBtn && !this.hasActual;
        this.renderCheieSel = this.renderSaveBtn && Boolean.TRUE.equals(this.getCoarea().getAlocare());

        if (this.renderOcategSel && Boolean.TRUE.equals(this.selected.getOpex_categ_blocat())){
            this.selected.setOpex_categ(null);
            this.selected.setOpex_categ_nume(null);
            this.selected.setOpex_categ_blocat(null);
            this.selected.setIc_part(null);
            this.selected.setIc_part_nume(null);
            this.selected.setIc_part_blocat(null);
        }

        if (this.renderCheieSel && Boolean.TRUE.equals(this.selected.getCheie_blocat())){
            this.selected.setCheie(null);
            this.selected.setCheie_nume(null);
            this.selected.setCheie_blocat(null);
        }
    }
    
    private void initOcategs() throws Exception{
        if (this.renderOcategSel && this.selected.getCost_driver() != null)
            this.ocategs = OpexCategServ.getAssignByCostDriver(this.selected.getCoarea(), this.selected.getCost_driver(), cuser.getUname()).stream()
                    .filter(x -> x.getBlocat().equals(false))
                    .collect(Collectors.toList());
    }
    
    private void initKeyList() throws Exception{
        SelectItemGroup spf = new SelectItemGroup(App.getBeanMess("label.plan.specifice", clocale));
        SelectItemGroup gen = new SelectItemGroup(App.getBeanMess("label.plan.generale", clocale));

        CompletableFuture<SelectItem[]> fSpf = CompletableFuture.supplyAsync(() -> {
            SelectItem[] rezultat = new SelectItem[0];
            try {
                rezultat = KeyS01Serv.getListByTipAndCCenter(this.selected.getHier(), this.selected.getData_set(),
                        this.selected.getCost_center(), cuser.getUname()).stream()
                        .map(x -> new SelectItem(x.getId(), x.getNume()))
                        .toArray(SelectItem[]::new);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
            return rezultat;
        });
        CompletableFuture<SelectItem[]> fGen = CompletableFuture.supplyAsync(() -> {
            SelectItem[] rezultat = new SelectItem[0];
            try {
                rezultat = KeyServ.getGenAll(this.selected.getCoarea(), cuser.getUname()).stream()
                        .filter(x -> x.getBlocat().equals(false))
                        .map(x -> new SelectItem(x.getId(), x.getNume()))
                        .toArray(SelectItem[]::new);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
            return rezultat;
        });
        spf.setSelectItems(fSpf.get());
        gen.setSelectItems(fGen.get());

        this.chei = new ArrayList<>();
        if (spf.getSelectItems().length > 0) this.chei.add(spf);
        if (gen.getSelectItems().length > 0) this.chei.add(gen);
    }
    
    private void initKeyVals(){
        try {
            if (this.selected.getCheie() != null){            
                this.cheie_vals = KeyServ.getValsByIdAndCCenter(this.selected.getCheie(), this.selected.getData_set(), this.selected.getCost_center(), cuser.getUname()).stream()
                        .collect(Collectors.groupingBy(x -> new KeyValRow(x.getBuss_line(), x.getBuss_line_seg(), x.getBuss_line_nume(), new ArrayList<>()),
                            Collectors.mapping(x -> new KeyValCell(null, x.getAn(), x.getValoare(), null), Collectors.toList())
                        ))
                        .entrySet().stream()
                        .map(x -> {x.getKey().setCells(x.getValue()); return x.getKey(); })
                        .sorted(Comparator.comparing(KeyValRow::getCod))
                        .collect(Collectors.toList());
            }            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.keys.val.get", clocale), ex.getMessage()));
        }
    }
    
    private static PlanVal mapPerioada(DataSetPer per, String docId, Integer dataset){
        PlanVal rezultat = new PlanVal();
        rezultat.setDoc_id(docId);
        rezultat.setAn(per.getAn());
        rezultat.setPer(per.getPer());
        rezultat.setActual(per.getActual());
        rezultat.setValoare(Double.valueOf(0));
        rezultat.setData_set(dataset);
        return rezultat;
    }
    
    private void initValori() throws Exception{
        this.valori = new ArrayList<>();
        boolean hasValues = this.selected.getValori() != null && !this.selected.getValori().isEmpty();

        for (DataSetPer per : this.owner.getPerioade()){
            Optional<PlanVal> optVal = Optional.empty();
            if (hasValues)
                optVal = this.selected.getValori().stream()
                        .filter(x -> x.getData_set().equals(per.getData_set()) && x.getAn().equals(per.getAn()) && x.getPer().equals(per.getPer()))
                        .findFirst();

            valori.add(
                (optVal.isPresent() ? new PlanVal(optVal.get().getJson()) : mapPerioada(per, this.selected.getId(), this.selected.getData_set()))
            );
        }
    }
    
    public void inject(PlanDocListContract owner, boolean planCDriver){
        this.owner = owner;
        this.planCDriver = planCDriver;
    }
    
    public void init(boolean withLists){
        try {
            this.initFlags(withLists);
            if (withLists)
                CompletableFuture.allOf(
                        //initializare lista cost driveri
                        CompletableFuture.runAsync(() -> {
                            try {
                                if (this.renderCdriverSel)
                                    this.cdrivers = CostDriverServ.getAssignByCentral(this.selected.getCoarea(), false, cuser.getUname()).stream()
                                            .filter(x -> x.getBlocat().equals(false))
                                            .collect(Collectors.toList());
                            } catch (Exception ex) {
                                throw new CompletionException(ex);
                            }
                        }),
                        //initializare lista categorii de cheltuieli
                        CompletableFuture.runAsync(() -> {
                            try {
                                this.initOcategs();
                            } catch (Exception ex) {
                                throw new CompletionException(ex);
                            }
                        }),
                        //initializare lista parteneri
                        CompletableFuture.runAsync(() -> {
                            try {
                                if (this.renderIcPartSel)
                                    this.partners = IcPartenerServ.getNotCoArea(this.selected.getCoarea(), cuser.getUname());
                            } catch (Exception ex) {
                                throw new CompletionException(ex);
                            }
                        }),
                        //initializare lista chei de alocare
                        CompletableFuture.runAsync(() -> {
                            try {
                                if (this.renderCheieSel) this.initKeyList();
                            } catch (Exception ex) {
                                throw new CompletionException(ex);
                            }
                        }),
                        //initializare valori
                        CompletableFuture.runAsync(() -> {
                            try {
                                this.initValori();
                            } catch (Exception ex) {
                                throw new CompletionException(ex);
                            }
                        })
                ).get(90, TimeUnit.SECONDS);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public void clear(){
        this.initError = null;
        this.selected = null;        
        this.cdrivers = null;
        this.ocategs = null;
        this.partners = null;
        this.chei = null;
        this.cheie_vals = null;
        this.valori = null;
        this.finishScript = null;
        
        this.hasActual = false;
        this.renderCdriverSel = false;
        this.renderOcategSel = false;
        this.renderIcPartSel = false;
        this.renderCheieSel = false;
        this.renderSaveBtn = false;
    }
    
    public void onCdriverChange(){
        try {
            this.initOcategs();
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.opexcat.get", clocale), ex.getMessage()));
        }
    }
    
    public void onKeyChange(){
        this.cheie_vals = null;
    }
    
    public void onTabChange(TabChangeEvent event){
        switch (event.getTab().getId()){
            case "key":
                if (this.cheie_vals == null){
                    this.initKeyVals();
                    PrimeFaces.current().executeScript("updateKeyVals();");
                }
                break;
            case "vals":
                break;
            default:
                break;
        }
    }
    
    public void save(){
        try {
            if (this.selected == null) throw new Exception(App.getBeanMess("err.plan.doc.nok", clocale));
            if (this.valori == null || this.valori.isEmpty()) throw new Exception(App.getBeanMess("err.plan.doc.valnok", clocale));
            this.valori.stream()
                    .filter(x -> !x.getValoare().equals(Double.valueOf(0)))
                    .findFirst()
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.plan.doc.valnok", clocale)));
         
            this.selected.setValori(
                this.valori.stream()
                    .filter(x -> x.getActual().equals(false) && !x.getValoare().equals(Double.valueOf(0)))
                    .collect(Collectors.toList())
            );
            
            if (this.selected.getMod_timp() == null){
                PlanDoc rezultat = PlanDocServ.insert(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                rezultat.setCost_center_super(this.selected.getCost_center_super());
                rezultat.setCost_center_blocat(this.selected.getCost_center_blocat());
                rezultat.setCost_center_leaf(this.selected.getCost_center_leaf());
                rezultat.setCost_center_nivel(this.selected.getCost_center_nivel());

                this.owner.getList().add(rezultat);
                this.owner.setSelected(rezultat);
                this.owner.initAggregare(true);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        App.getBeanMess("title.plan.doc.ins", clocale), App.getBeanMess("info.success", clocale)));
            } else {
                PlanDoc rezultat = PlanDocServ.update(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                rezultat.setCost_center_super(this.selected.getCost_center_super());
                rezultat.setCost_center_blocat(this.selected.getCost_center_blocat());
                rezultat.setCost_center_leaf(this.selected.getCost_center_leaf());
                rezultat.setCost_center_nivel(this.selected.getCost_center_nivel());
                
                for (int i = 0; i < this.owner.getList().size(); i++){
                    if (this.owner.getList().get(i).getId().equals(rezultat.getId())){
                        this.owner.getList().set(i, rezultat);
                        break;
                    }
                }
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        App.getBeanMess("title.plan.doc.upd", clocale), App.getBeanMess("info.success", clocale)));
            }
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.plan.doc.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.plan.doc.nok", clocale));
            if (!PlanDocServ.delete(this.selected.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));
            this.owner.getList().removeIf(x -> x.getId().equals(this.selected.getId()));
            this.owner.setSelected(null);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                        App.getBeanMess("title.plan.doc.del", clocale), App.getBeanMess("info.success", clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.plan.doc.del", clocale), ex.getMessage()));
        }
    }
    
    public double getKeyValPercentByAn(double val, short an){
        if (val == 0) return 0;
        double suma = this.cheie_vals.stream()
                .map(x -> x.getCellByAn(an))
                .mapToDouble(x -> x.getValoare())
                .sum();
        return (suma == 0 ? 0 : val / suma);
    }
    
    public double getValSumByAn(short an){
        return this.valori.stream()
                .filter(x -> x.getAn().equals(an))
                .mapToDouble(PlanVal::getValoare)
                .sum();
    }

    public String getInitError() {
        return initError;
    }
    
    public CoArea getCoarea(){
        return this.owner.getCoarea();
    }

    public PlanDoc getSelected() {
        return selected;
    }

    public void setSelected(PlanDoc selected) {
        this.selected = selected;
    }
    
    public List<DataSetPer> getPerioade(){
        return this.owner.getPerioade();
    }
    
    public List<Short> getAni(){
        return this.owner.getPerioade().stream()
                .map(DataSetPer::getAn)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<CostDriver> getCdrivers() {
        return this.cdrivers;
    }    
    
    public List<OpexCateg> getOcategs() {
        return this.ocategs;
    }

    public List<IcPartener> getPartners() {
        return this.partners;
    }

    public List<SelectItem> getChei(){
        return this.chei;
    }

    public List<KeyValRow> getCheie_vals() {
        return this.cheie_vals;
    }

    public List<PlanVal> getValori() {
        return this.valori;
    }

    public void setValori(List<PlanVal> valori) {
        this.valori = valori;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }

    public boolean isHasActual() {
        return hasActual;
    }

    public boolean isRenderCdriverSel() {
        return renderCdriverSel;
    }

    public boolean isRenderOcategSel() {
        return renderOcategSel;
    }

    public boolean isRenderIcPartSel() {
        return renderIcPartSel;
    }

    public boolean isRenderCheieSel() {
        return renderCheieSel;
    }

    public boolean isRenderSaveBtn() {
        return renderSaveBtn;
    }
}
