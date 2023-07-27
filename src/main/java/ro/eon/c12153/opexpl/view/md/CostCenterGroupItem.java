package ro.any.c12153.opexpl.view.md;

import java.io.Serializable;
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
import ro.any.c12153.opexpl.entities.CostCenterGroup;
import ro.any.c12153.opexpl.services.CostCenterGroupServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "cgroup")
@ViewScoped
public class CostCenterGroupItem implements Serializable, SelectItemView<CostCenterGroup>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CostCenterGroupItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject CostCenterGroupList owner;
    private String initError;
    private CostCenterGroup selected;
    private TreeNode superior;
    
    private TreeNode hier;
    private String finishScript;
    
    public boolean showSelectHier(){
        return  this.selected != null && this.owner.getList() != null &&
                !this.owner.getList().isEmpty() &&
                !this.owner.getCoarea().getHier().equals(this.selected.getCod());
    }
    
    private TreeNode initHier(){
        TreeNode rezultat = new DefaultTreeNode();
        try {
            if (this.owner != null && this.owner.getList() != null && !this.owner.getList().isEmpty()){
                Map<String, TreeNode> noduri = new HashMap<>();
                this.owner.getList().stream()
                        .filter(x -> this.selected == null || !x.getId().equals(this.selected.getId())) //eliminare pozitie selectata pentru a nu putea fi folosita
                        .forEach((group) -> {
                            TreeNode iterator = new DefaultTreeNode(group,
                                    (group.getSuperior() == null ? rezultat : noduri.get(group.getSuperior())));
                            iterator.setExpanded(true);
                            noduri.put(group.getId(), iterator);

                            //setare superior pozitie selectata
                            if (this.selected != null && group.getId().equals(this.selected.getSuperior()))
                                iterator.setSelected(true);
                        }); 
            }            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
        return rezultat;
    }

    @Override
    public void initLists() {
        if (this.showSelectHier()) this.hier = this.initHier();
    }
    
    @Override
    public void clear(){
        this.initError = null;
        this.selected = null;
        this.superior = null;
        this.hier = null;
        this.finishScript = null;
    }
    
    public void save(){
        try {
            if (this.selected == null || !Utils.stringNotEmpty(this.selected.getCod()))
                throw new Exception(App.getBeanMess("err.cgroup.nok", clocale));
            
            //setare superior si nivel
            short nivel = -1;
            if (this.superior != null){
                CostCenterGroup parinte = (CostCenterGroup) this.superior.getData();
                if (parinte != null) {
                    this.selected.setSuperior(parinte.getId());
                    nivel = parinte.getNivel();
                }
            }
            
            if (this.selected.getMod_timp() == null){                
                CostCenterGroup rezultat = CostCenterGroupServ.insert(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
                rezultat.setNivel(++nivel);
                this.owner.getList().add(rezultat);
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.cgroup.ins", clocale), App.getBeanMess("info.success",  clocale)));                
            } else {
                CostCenterGroup rezultat = CostCenterGroupServ.update(this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                rezultat.setNivel(++nivel);
                for (int i = 0; i < this.owner.getList().size(); i++){
                    if (this.owner.getList().get(i).getId().equals(rezultat.getId())){
                        this.owner.getList().set(i, rezultat);
                        break;
                    }
                }
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.cgroup.upd", clocale), App.getBeanMess("info.success",  clocale)));
            }
            this.owner.setReinit(true); //setare flag pentru reinitializare redering ierarhii
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.cgroup.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.cgroup.nok", clocale));
            
            if (!CostCenterGroupServ.delete(this.selected.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));            
            this.owner.getList().removeIf(x -> x.getId().equals(this.selected.getId()));
            this.owner.setSelected((CostCenterGroup) null);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.cgroup.del", clocale), App.getBeanMess("info.success",  clocale)));
            this.owner.setReinit(true); //setare flag pentru reinitializare redering ierarhii
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.cgroup.del", clocale), ex.getMessage()));
        }
    }

    @Override
    public String getInitError() {
        return initError;
    }
    
    public TreeNode getHier(){
        return this.hier;
    }

    @Override
    public CostCenterGroup getSelected() {
        return selected;
    }

    @Override
    public void setSelected(CostCenterGroup selected) {
        this.selected = selected;
    }

    public TreeNode getSuperior() {
        return superior;
    }

    public void setSuperior(TreeNode superior) {
        this.superior = superior;
    }
    
    @Override
    public String getFinishScript() {
        return finishScript;
    }

    @Override
    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
