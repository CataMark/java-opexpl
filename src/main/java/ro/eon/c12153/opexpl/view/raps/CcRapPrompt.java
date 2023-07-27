package ro.any.c12153.opexpl.view.raps;

import java.io.Serializable;
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
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostCenter;
import ro.any.c12153.opexpl.entities.CostCenterGroup;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostCenterGroupServ;
import ro.any.c12153.opexpl.services.CostCenterServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.UserOxplServ;
import ro.any.c12153.opexpl.view.help.CostCenterCompoundUrlParamHelp;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "ccrapprompt")
@ViewScoped
public class CcRapPrompt implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CcRapPrompt.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private List<CoArea> arii;
    private List<DataSet> seturi;
    private TreeNode hier;
    private boolean userCCentreBound;
    
    private String initError;
    private String arie_sel;
    private String set_sel;
    private TreeNode hier_sel;
    
    private void initArii(){
        try {
            if (this.userCCentreBound){
                this.arii = CoAreaServ.getListCcntrBound(cuser.getUname(), cuser.getUname());
            } else {
                this.arii = CoAreaServ.getAll(cuser.getUname());
            }
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    private void initSeturi(){
        try {
            this.seturi = DataSetServ.getRaportareAll(cuser.getUname());
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    private void initHier(){
        this.hier = new DefaultTreeNode();
        try {
            if (Utils.stringNotEmpty(this.arie_sel) && Utils.stringNotEmpty(this.set_sel)){
                //obtine lista centre de cost
                List<CostCenter> ccenters;
                if (this.userCCentreBound){
                    ccenters = CostCenterServ.getAllByRights(this.arie_sel, Integer.valueOf(this.set_sel), cuser.getUname());
                } else {
                    ccenters = CostCenterServ.getAll(this.arie_sel, Integer.valueOf(this.set_sel), cuser.getUname());
                }
                if (ccenters == null || ccenters.isEmpty()) return;

                //stabilire pozitie selectata
                final CostCenter selectat = (this.hier_sel == null ? null: (CostCenter) this.hier_sel.getData());

                Map<String, TreeNode> noduri = new HashMap<>();
                ccenters.sort(Comparator.comparing(CostCenter::getNivel).thenComparing(CostCenter::getCod));
                ccenters.forEach((center) -> {
                    TreeNode iterator = new DefaultTreeNode(center,
                            (center.getSuperior_cod()== null? this.hier: noduri.get(center.getSuperior_cod())));

                    if (center.getLeaf() == null || center.getLeaf().equals(Boolean.FALSE)){
                        noduri.put(center.getCod(), iterator);
                        iterator.setExpanded(true);
                        //iterator.setSelectable(false);
                    }

                    //setare pozitie selectata
                    if (selectat != null && center.getId().equals(selectat.getId()))
                        iterator.setSelected(true);
                });
            }
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    private void initHier_sel(String cc_param){
        try {
            CostCenter rezultat = null;
            CostCenterCompoundUrlParamHelp param = new CostCenterCompoundUrlParamHelp(Utils.paramDecode(cc_param));
            
            if (Boolean.TRUE.equals(param.getLeaf())){
                Optional<CostCenter> item = CostCenterServ.getById(param.getCcenter_id(), cuser.getUname());
                if (item.isPresent()){
                    rezultat = item.get();
                    rezultat.setLeaf(Boolean.TRUE);
                }
            } else {
                Optional<CostCenterGroup> item = CostCenterGroupServ.getById(param.getCcenter_id(), cuser.getUname());
                if (item.isPresent()){
                    rezultat = item.get().cast();
                    rezultat.setLeaf(Boolean.FALSE);
                }
            }
            this.hier_sel = (rezultat == null ? null : new DefaultTreeNode(rezultat));
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }
    
    @PostConstruct
    private void init(){
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            Optional<String> hr = Optional.ofNullable(params.get("hr"));
            if (hr.isPresent()) this.arie_sel = Utils.paramDecode(hr.get());
            Optional<String> ds = Optional.ofNullable(params.get("ds"));
            if (ds.isPresent()) this.set_sel = Utils.paramDecode(ds.get());
            Optional<String> cc = Optional.ofNullable(params.get("cc"));
            
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(this::initSeturi),
                    CompletableFuture.runAsync(() -> {if (cc.isPresent()) this.initHier_sel(cc.get());}),
                    CompletableFuture.runAsync(() -> {
                        try {
                            this.userCCentreBound = UserOxplServ.checkUserCostCenterBound(cuser.getUname(), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).thenRun(() -> {
                try {
                    CompletableFuture.allOf(
                                CompletableFuture.runAsync(this::initArii),
                                CompletableFuture.runAsync(this::initHier)
                    ).get(60, TimeUnit.SECONDS);
                } catch(Exception ex){
                    throw new CompletionException(ex);
                }
            }).get(90, TimeUnit.SECONDS);
     
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public void onChange(){
        try {
            this.hier_sel = null;
            this.initHier();
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.ccenter.hier.get", clocale), ex.getMessage()));
        }
    }
    
    public String navigate(String page){
        String rezultat = page + "?faces-redirect=true";
        try {
            CostCenterCompoundUrlParamHelp ccenter_param = null;
            if (this.hier_sel != null && this.hier_sel.getData() != null){
                CostCenter ccenter = (CostCenter) this.hier_sel.getData();
                ccenter_param = new CostCenterCompoundUrlParamHelp(ccenter.getId(), ccenter.getLeaf());
            }
            rezultat += (this.arie_sel == null ? "" : "&hr=" + Utils.paramEncode(this.arie_sel)) +
                        (this.set_sel == null ? "" : "&ds=" + Utils.paramEncode(this.set_sel)) +
                        (ccenter_param == null ? "" : "&cc=" + Utils.paramEncode(ccenter_param.getJson().toString()));
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
        }
        return rezultat;
    }

    public String getInitError() {
        return initError;
    }

    public List<CoArea> getArii() {
        return this.arii;
    }

    public List<DataSet> getSeturi() {
        return this.seturi;
    }

    public TreeNode getHier() {
        return this.hier;
    }
    
    public String getArie_sel() {
        return arie_sel;
    }

    public void setArie_sel(String arie_sel) {
        this.arie_sel = arie_sel;
    }

    public String getSet_sel() {
        return set_sel;
    }

    public void setSet_sel(String set_sel) {
        this.set_sel = set_sel;
    }

    public TreeNode getHier_sel() {
        return hier_sel;
    }

    public void setHier_sel(TreeNode hier_sel) {
        this.hier_sel = hier_sel;
    }
}
