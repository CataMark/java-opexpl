package ro.any.c12153.opexpl.view.records;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ro.any.c12153.opexpl.entities.CostCenter;
import ro.any.c12153.opexpl.entities.CostCenterMap;
import ro.any.c12153.opexpl.services.CostCenterMapServ;
import ro.any.c12153.opexpl.services.CostCenterServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "cmap")
@ViewScoped
public class CostCenterMapItem implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CostCenterMapItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject CostCenterMapList owner;
    private String initError;
    private CostCenterMap selected;
    private TreeNode receiver;
    private TreeNode hier;
    private String finishScript;
    
    private TreeNode initHier(){
        TreeNode rezultat = new DefaultTreeNode();
        try {
            Map<String, TreeNode> noduri = new HashMap<>();
            CostCenterServ.getAll(this.owner.getCoarea().getHier(), this.owner.getDataset().getId(), cuser.getUname()).stream()
                    .sorted(Comparator.comparing(CostCenter::getNivel).thenComparing(CostCenter::getCod))
                    .forEach((center) -> {
                        TreeNode iterator = new DefaultTreeNode(center,
                                (center.getSuperior_cod()== null ? rezultat : noduri.get(center.getSuperior_cod())));

                        if (center.getLeaf() == null || center.getLeaf().equals(false)){
                            noduri.put(center.getCod(), iterator);
                            iterator.setExpanded(true);
                            iterator.setSelectable(false);
                        }
                        //setare pozitie selectata
                        if (this.selected != null && center.getCod().equals(this.selected.getReceiver())) iterator.setSelected(true);
                    });
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
        return rezultat;
    }
    
    public void initLists(){
        this.hier = this.initHier();
    }
    
    protected void clear(){
        this.initError = null;
        this.selected = null;
        this.receiver = null;
        this.hier = null;
        this.finishScript = null;
    }
    
    public void save(){;
        try {
            if (this.selected == null || !Utils.stringNotEmpty(this.selected.getSender()))
                throw new Exception(App.getBeanMess("err.ccenter.map.cntr.nok", clocale));            
            this.selected.setReceiver(((CostCenter) this.receiver.getData()).getCod());
            
            if (this.selected.getMod_timp() == null){
                CostCenterMap rezultat = CostCenterMapServ.insert(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                if (this.owner.getNotMapped() != null && !this.owner.getNotMapped().isEmpty()){
                    this.owner.getNotMapped().removeIf(x -> x.getSender().equals(rezultat.getSender()));
                    this.owner.setSelected(null);
                }
                
                if (this.owner.getMapped() != null){
                    if (!this.owner.getMapped().isEmpty()){
                        for(int i = 0; i < this.owner.getMapped().size(); i++){
                            if (this.owner.getMapped().get(i).getSender().equals(rezultat.getSender())){
                                rezultat.setRow_id(this.owner.getMapped().get(i).getRow_id());
                                this.owner.getMapped().set(i, rezultat);
                                break;
                            }
                        }
                    } else {
                        rezultat.setRow_id(new Long(1));
                        this.owner.getMapped().add(rezultat);
                    }
                    this.owner.setSelected(rezultat);
                    this.owner.getMapped().sort(Comparator.comparing(CostCenterMap::getSender));
                }                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.ccenter.map.ins", clocale), App.getBeanMess("info.success",  clocale)));                
            } else {
                CostCenterMap rezultat = CostCenterMapServ.update(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                
                for(int i = 0; i < this.owner.getMapped().size(); i++){
                    if (this.owner.getMapped().get(i).getId() != null && this.owner.getMapped().get(i).getId().equals(rezultat.getId())){
                        rezultat.setRow_id(this.owner.getMapped().get(i).getRow_id());
                        this.owner.getMapped().set(i, rezultat);
                        break;
                    }
                }
                this.owner.setSelected(rezultat);
                this.owner.getMapped().sort(Comparator.comparing(CostCenterMap::getSender));
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.ccenter.map.upd", clocale), App.getBeanMess("info.success",  clocale))); 
            }            
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.ccenter.map.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.ccenter.map.nok", clocale));
            
            if (!CostCenterMapServ.deleteById(this.selected.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));            
            this.owner.getMapped().removeIf(x -> x.getId() != null && x.getId().equals(this.selected.getId()));
            
            CostCenter senderExists = CostCenterServ.getByCod(this.selected.getSender(), this.owner.getCoarea().getHier(), this.owner.getDataset().getId(), cuser.getUname())
                    .orElse(null);            
            if (senderExists != null){
                CostCenterMap newImplicit = new CostCenterMap();
                newImplicit.setSender(senderExists.getCod());
                newImplicit.setSender_nume(senderExists.getNume());
                newImplicit.setReceiver(senderExists.getCod());
                newImplicit.setReceiver_nume(senderExists.getNume());
                newImplicit.setRow_id(
                    this.owner.getMapped().stream()
                        .mapToLong(CostCenterMap::getRow_id)
                        .max()
                        .orElse(0) + 1
                );               
                this.owner.getMapped().add(newImplicit);                
                this.owner.setSelected(newImplicit);
                this.owner.getMapped().sort(Comparator.comparing(CostCenterMap::getSender));
            }            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.ccenter.map.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    App.getBeanMess("title.ccenter.map.del", clocale), ex.getMessage()));
        }
    }

    public String getInitError() {
        return initError;
    }
    
    public CostCenterMap getSelected() {
        return selected;
    }

    public void setSelected(CostCenterMap selected) {
        this.selected = selected;
    }

    public TreeNode getReceiver() {
        return receiver;
    }

    public void setReceiver(TreeNode receiver) {
        this.receiver = receiver;
    }

    public TreeNode getHier() {
        return this.hier;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
