package ro.any.c12153.opexpl.view.key;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
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
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostCenter;
import ro.any.c12153.opexpl.entities.CostDriver;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.IcPartener;
import ro.any.c12153.opexpl.entities.KeyHead;
import ro.any.c12153.opexpl.entities.KeyRule;
import ro.any.c12153.opexpl.entities.OpexCateg;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostCenterServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.IcPartenerServ;
import ro.any.c12153.opexpl.services.KeyC01Serv;
import ro.any.c12153.opexpl.services.KeyServ;
import ro.any.c12153.opexpl.services.OpexCategServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "kc01r")
@ViewScoped
public class KeyC01RuleView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(KeyC01RuleView.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private KeyHead cheie;
    
    private KeyRule rule;
    
    private List<KeyHead> cheiList;
    private DualListModel<KeyHead> cheiPick;
    
    private List<CostCenter> ccenterList;
    private TreeNode hier;
    private TreeNode[] selCCenters;
    
    private List<OpexCateg> categList;
    private TreeNode categs;
    private TreeNode[] selCategs;
    
    private List<IcPartener> partnList;
    private DualListModel<IcPartener> partnPick;
    
    private boolean nulPartner;
    
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
                            this.cheie = KeyC01Serv.getById(
                                    Integer.valueOf(Utils.paramDecode(ky)),
                                    Integer.valueOf(Utils.paramDecode(ds)),
                                    cuser.getUname()
                            ).orElseThrow(() -> new Exception(App.getBeanMess("err.keys.not", clocale)));
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).get(30, TimeUnit.SECONDS);
            
            this.cheiPick = new DualListModel<>();
            this.hier = new CheckboxTreeNode();
            this.categs = new CheckboxTreeNode();
            this.partnPick = new DualListModel<>();
            
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
    
    public void datainit(boolean fromDataBase){
        try {
            if (fromDataBase)
                CompletableFuture.allOf(
                        CompletableFuture.runAsync(() -> {
                            try {
                                this.rule = KeyC01Serv.getRuleByKey(this.cheie.getId(), cuser.getUname())
                                        .orElse(this.newItem());
                            } catch (Exception ex) {
                                throw new CompletionException(ex);
                            }
                        }),
                        CompletableFuture.runAsync(() -> {
                            try {
                                this.cheiList = KeyServ.getGenAll(this.coarea.getCod(), cuser.getUname());
                            } catch (Exception ex) {
                                throw new CompletionException(ex);
                            }
                        }),
                        CompletableFuture.runAsync(() -> {
                            try {
                                this.ccenterList = CostCenterServ.getAll(this.coarea.getHier(), this.dataset.getId(), cuser.getUname());
                            } catch (Exception ex) {
                                throw new CompletionException(ex);
                            }
                        }),
                        CompletableFuture.runAsync(() -> {
                            try {
                                this.categList = OpexCategServ.getAssignByCoarea(this.coarea.getCod(), cuser.getUname());
                            } catch (Exception ex) {
                                throw new CompletionException(ex);
                            }
                        }),
                        CompletableFuture.runAsync(() -> {
                            try {
                                this.partnList = IcPartenerServ.getNotCoArea(this.coarea.getCod(), cuser.getUname());
                            } catch (Exception ex) {
                                throw new CompletionException(ex);
                            }
                        })
                ).get(60, TimeUnit.SECONDS);
            
            CompletableFuture.allOf(
                    //pregatire pick list pentru cheiPick de alocare
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.cheiPick = new DualListModel<>();
                            if (this.cheiList != null && !this.cheiList.isEmpty())
                                this.cheiList.forEach(x -> {
                                    if (this.rule.getChei().contains(x.getId())){
                                        this.cheiPick.getTarget().add(x);
                                    } else {
                                        this.cheiPick.getSource().add(x);
                                    }
                                });
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    //pregatire ierarhie centre de cost
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.hier = new CheckboxTreeNode();
                            Map<String, TreeNode> noduri = new HashMap<>();
                            if (this.ccenterList != null && !this.ccenterList.isEmpty())                
                                this.ccenterList.stream()
                                    .sorted(Comparator.comparing(CostCenter::getNivel).thenComparing(CostCenter::getCod))
                                    .forEach(center -> {
                                        TreeNode iterator = new CheckboxTreeNode(center,
                                                (center.getSuperior_cod() == null ? this.hier : noduri.get(center.getSuperior_cod())));

                                        if (center.getLeaf() == null || center.getLeaf().equals(Boolean.FALSE)){
                                            noduri.put(center.getCod(), iterator);
                                            iterator.setExpanded(true);
                                        }

                                        if (this.rule.getCost_centre().contains(center.getCod()))
                                            iterator.setSelected(true);
                                    });
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    //pregatire ierarhie categorii
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.categs = new CheckboxTreeNode();
                            Map<String, TreeNode> noduri = new HashMap<>();
                            if (this.categList != null && !this.categList.isEmpty()){
                                Map<String, CostDriver> cdrivers = new HashMap<>();
                                this.categList.stream()
                                        .forEach(x -> {
                                            if (!cdrivers.containsKey(x.getCost_driver())){
                                                CostDriver y = new CostDriver();
                                                y.setCod(x.getCost_driver());
                                                y.setNume(x.getCost_driver_nume());
                                                cdrivers.put(x.getCost_driver(), y);
                                            }
                                        });

                                cdrivers.values().stream()
                                    .sorted(Comparator.comparing(CostDriver::getCod))
                                    .forEach(driver -> {
                                        CheckboxTreeNode iterator = new CheckboxTreeNode(driver, this.categs);
                                        noduri.put(driver.getCod(), iterator);
                                        iterator.setExpanded(false);
                                    });

                                this.categList.forEach(categ -> {
                                    CheckboxTreeNode iterator = new CheckboxTreeNode("categ", categ, noduri.get(categ.getCost_driver()));
                                    if (this.rule.getOpex_categ().stream()
                                            .anyMatch(x -> x.getKey().equals(categ.getCost_driver()) &&
                                                    (x.getValue() == null || x.getValue().equals(categ.getCod()))))
                                        iterator.setSelected(true);
                                });
                            }
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }),
                    //pregatire pick list partnPick
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.partnPick = new DualListModel<>();
                            if (this.partnList != null && !this.partnList.isEmpty())
                                this.partnList.forEach(x -> {
                                    if (this.rule.getIc_part().contains(x.getCod())){
                                        this.partnPick.getTarget().add(x);
                                    } else {
                                        this.partnPick.getSource().add(x);
                                    }
                                });            
                            this.nulPartner = this.rule.getIc_part().contains(null);
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })                    
            ).get(30, TimeUnit.SECONDS);

        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.keys.rule.listinit", clocale), ex.getMessage()));
        }
    }
    
    private KeyRule newItem(){
        KeyRule rezultat = new KeyRule();
        rezultat.setCheie(this.cheie.getId());
        rezultat.setMedie_pond(false);
        return rezultat;
    }
    
    private KeyRule prepareRule(){
        KeyRule rezultat = new KeyRule();
        
        rezultat.setChei(this.cheiPick.getTarget().stream()
            .map(x -> x.getId())
            .collect(Collectors.toList()));

        rezultat.setCost_centre(Arrays.asList(this.selCCenters).stream()
            .map(x -> (CostCenter) x.getData())
            .filter(x -> x.getLeaf())
            .map(x -> x.getCod())
            .collect(Collectors.toList()));

        rezultat.getOpex_categ().clear();
        Arrays.asList(this.selCategs).stream()
                .filter(x -> x.getType().equals(CheckboxTreeNode.DEFAULT_TYPE))
                .map(x -> (CostDriver) x.getData())
                .forEach(x -> rezultat.getOpex_categ().add(new SimpleEntry<>(x.getCod(), null)));

        Arrays.asList(this.selCategs).stream()
                .filter(x -> x.getType().equals("categ"))
                .map(x -> (OpexCateg) x.getData())
                .forEach(x -> {
                    if (!rezultat.getOpex_categ().stream()
                            .anyMatch(y -> y.getKey().equals(x.getCost_driver()) && y.getValue() == null))
                        rezultat.getOpex_categ().add(new SimpleEntry<>(x.getCost_driver(), x.getCod()));
                });

        rezultat.setIc_part(this.partnPick.getTarget().stream()
            .map(x -> x.getCod())
            .collect(Collectors.toList()));

        if (this.nulPartner) rezultat.getIc_part().add(null);
        
        return rezultat;
    }
    
    private void checkRule(KeyRule rule) throws Exception{
        if (rule.getChei().isEmpty())
            throw new Exception(App.getBeanMess("err.keys.rule.empty", clocale));
            
        if (!rule.getMedie_pond() && (rule.getChei().size() > 1 ||
            !rule.getOpex_categ().isEmpty() || !rule.getIc_part().isEmpty()))
            throw new Exception(App.getBeanMess("err.keys.rule.avg.nart", clocale));
    }
    
    public void checkValsExist(){
        try {
            KeyRule regula = this.prepareRule();
            regula.setMedie_pond(this.rule.getMedie_pond());
            this.checkRule(regula);
            
            boolean rezultat = KeyC01Serv.checkRuleVals(this.dataset.getId(), this.coarea.getCod(), regula, cuser.getUname());
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                (rezultat ? FacesMessage.SEVERITY_INFO : FacesMessage.SEVERITY_WARN),
                App.getBeanMess("title.keys.val.chk", clocale),
                (rezultat ? App.getBeanMess("info.keys.rule.isval", clocale) : App.getBeanMess("info.keys.rule.noval", clocale))
            ));
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.val.chk", clocale), ex.getMessage()));
        }
    }
    
    public void save(){
        try {
            if (this.rule == null) throw new Exception(App.getBeanMess("err.keys.rule.nok", clocale));
            
            //preluare valori in regula de calcul
            KeyRule prepared = this.prepareRule();
            this.rule.setChei(prepared.getChei());
            this.rule.setCost_centre(prepared.getCost_centre());
            this.rule.setOpex_categ(prepared.getOpex_categ());
            this.rule.setIc_part(prepared.getIc_part());            
            
            //verificari
            this.checkRule(this.rule);
            
            //salvare cheie
            if (this.rule.getMod_timp() == null){
                KeyRule rezultat = KeyC01Serv.insertRule(this.rule, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                this.rule = rezultat;
                this.datainit(false);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.rule.ins", clocale), App.getBeanMess("info.success",  clocale)));
            } else {
                KeyRule rezultat = KeyC01Serv.updateRule(this.rule, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                this.rule = rezultat;
                this.datainit(false);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.rule.upd", clocale), App.getBeanMess("info.success",  clocale)));
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.rule.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.rule == null || this.rule.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.keys.rule.nok", clocale));
            
            if (!KeyC01Serv.deleteRule(this.cheie.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));            
            this.rule = this.newItem();
            this.datainit(false);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.rule.del", clocale), App.getBeanMess("info.success",  clocale)));
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.rule.del", clocale), ex.getMessage()));
        }
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

    public KeyHead getCheie() {
        return cheie;
    }

    public void setCheie(KeyHead cheie) {
        this.cheie = cheie;
    }

    public KeyRule getRule() {
        return rule;
    }

    public void setRule(KeyRule rule) {
        this.rule = rule;
    }

    public DualListModel<KeyHead> getCheiPick() {
        return cheiPick;
    }

    public void setCheiPick(DualListModel<KeyHead> cheiPick) {
        this.cheiPick = cheiPick;
    }

    public TreeNode getHier() {
        return hier;
    }

    public TreeNode[] getSelCCenters() {
        return selCCenters;
    }

    public void setSelCCenters(TreeNode[] selCCenters) {
        this.selCCenters = selCCenters;
    }

    public TreeNode getCategs() {
        return categs;
    }

    public TreeNode[] getSelCategs() {
        return selCategs;
    }

    public void setSelCategs(TreeNode[] selCategs) {
        this.selCategs = selCategs;
    }

    public DualListModel<IcPartener> getPartnPick() {
        return partnPick;
    }

    public void setPartnPick(DualListModel<IcPartener> partnPick) {
        this.partnPick = partnPick;
    }

    public boolean isNulPartner() {
        return nulPartner;
    }

    public void setNulPartner(boolean nulPartner) {
        this.nulPartner = nulPartner;
    }
}
