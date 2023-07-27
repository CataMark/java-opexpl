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
import org.primefaces.util.Constants;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.CostCenter;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.entities.KeyHead;
import ro.any.c12153.opexpl.entities.KeyVal;
import ro.any.c12153.opexpl.services.CoAreaServ;
import ro.any.c12153.opexpl.services.CostCenterGroupServ;
import ro.any.c12153.opexpl.services.CostCenterServ;
import ro.any.c12153.opexpl.services.DataSetPerServ;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.opexpl.services.KeyS01Serv;
import ro.any.c12153.opexpl.services.UserOxplServ;
import ro.any.c12153.opexpl.view.help.CostCenterCompoundUrlParamHelp;
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
@Named(value = "ks01list")
@ViewScoped
public class KeyS01List implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(KeyS01List.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject DialogController dialog;
    private @Inject KeyS01Item item;
    
    private String initError;
    private CoArea coarea;
    private DataSet dataset;
    private CostCenter ccenter;
    private List<Short> dataset_ani;
    
    private List<KeyHead> list;
    private KeyHead selected;
    private String[] filterValues;
    private List<KeyHead> filtered;
    
    public void clearFilters(){
        this.filterValues = new String[]{"","",""};
    }
    
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
    
    @PostConstruct
    private void init(){
        try {
            this.clearFilters();
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
                    
                    if (costCenterBound.get())
                        if(!Boolean.FALSE.equals(this.dataset.getBlocat()))
                            throw new Exception(App.getBeanMess("err.cgroup.dset.close", clocale));
                    if (!userHasRightsOnCcenter.get())
                        throw new Exception(App.getBeanMess("err.user.norights", clocale));
                } catch (Exception ex) {
                    throw new CompletionException(ex);
                }
            }).get(30, TimeUnit.SECONDS);
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
            this.clearFilters();
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
                            this.list = KeyS01Serv.getListByTipAndCCenter(this.coarea.getHier(), this.dataset.getId(), this.ccenter.getCod(), cuser.getUname());
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    })
            ).get(60, TimeUnit.SECONDS);
  
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.listinit", clocale), ex.getMessage()));
        }
    }
    
    public void newItem(){
        KeyHead rezultat = new KeyHead();
        rezultat.setCoarea(this.coarea.getCod());
        rezultat.setTip("S01");
        rezultat.setBlocat(false);
        rezultat.setData_set(this.dataset.getId());
        rezultat.setHier(this.coarea.getHier());
        rezultat.setCost_center(this.ccenter.getCod());
        rezultat.setCost_center_nume(this.ccenter.getNume());
        this.item.setSelected(rezultat);
        this.item.initLists();
    }
    
    public void passSelected(boolean initLists){
        try {
            if (this.selected == null){
                this.item.setSelected(null);
            } else {
                this.item.setSelected(new KeyHead(this.selected.getJson(false)));
                if (initLists) this.item.initLists();
            }
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.sel.init", clocale), ex.getMessage()));
        }
    }
    
    public void clear(){
        dialog.clear();
        item.clear();
    }
	
    public double getTotalByCheiAndAn(List<KeyVal> vals, short an){
        if (vals == null || vals.isEmpty()) return 0;
        return vals.stream()
            .filter(x -> x != null && x.getValoare() != null && x.getAn().equals(an))
            .mapToDouble(KeyVal::getValoare)
            .sum();
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
            KeyS01Serv.keysToXlsx(this.coarea.getHier(), this.dataset.getId(), this.ccenter.getCod(), cuser.getUname(), stream);
            
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

    public DataSet getDataset() {
        return dataset;
    }

    public CostCenter getCcenter() {
        return ccenter;
    }

    public List<Short> getDataset_ani() {
        return dataset_ani;
    }
    
    public List<KeyHead> getList() {
        return list;
    }

    public KeyHead getSelected() {
        return selected;
    }

    public void setSelected(KeyHead selected) {
        this.selected = selected;
    }

    public String[] getFilterValues() {
        return filterValues;
    }

    public void setFilterValues(String[] filterValues) {
        this.filterValues = filterValues;
    }

    public List<KeyHead> getFiltered() {
        return filtered;
    }

    public void setFiltered(List<KeyHead> filtered) {
        this.filtered = filtered;
    }
}
