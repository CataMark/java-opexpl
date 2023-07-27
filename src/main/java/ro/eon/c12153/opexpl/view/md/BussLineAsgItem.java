package ro.any.c12153.opexpl.view.md;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexpl.entities.BussLine;
import ro.any.c12153.opexpl.entities.BussLineAsg;
import ro.any.c12153.opexpl.services.BussLineAsgServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.SelectItemView;
import ro.any.c12153.shared.SelectTableView;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "bussasg")
@ViewScoped
public class BussLineAsgItem implements Serializable, SelectItemView<BussLineAsg>{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(BussLineAsgItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject SelectTableView<BussLineAsg> owner;
    private String initError;
    private BussLineAsg selected;    
    private List<BussLine> notAsigned;
    private String finishScript;
    
    @Override
    public void initLists(){
        try {
            this.notAsigned = BussLineAsgServ.getListNotAsignToCoArea(this.selected.getCoarea(), cuser.getUname());
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    @Override
    public void clear(){
        this.initError = null;
        this.selected = null;
        this.notAsigned = null;
        this.finishScript = null;
    }
    
    public void save(){
        try {
            if(this.selected == null || !Utils.stringNotEmpty(this.selected.getBuss_line()))
                throw new Exception(App.getBeanMess("err.bline.asg.nok", clocale));
            
            BussLineAsg rezultat = BussLineAsgServ.insert(this.selected, cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
            this.owner.getList().add(rezultat);
            this.owner.setSelected(rezultat);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.bline.asg.ins", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.bline.asg.ins", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if(this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.bline.asg.nok", clocale));
            
            if (!BussLineAsgServ.delete(this.selected.getId(), cuser.getUname()))
                throw new Exception(App.getBeanMess("err.nosuccess", clocale));            
            this.owner.getList().removeIf(x -> x.getId().equals(this.selected.getId()));
            this.owner.setSelected(null);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.bline.asg.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.bline.asg.del", clocale), ex.getMessage()));
        }
    }

    @Override
    public String getInitError() {
        return this.initError;
    }

    public List<BussLine> getNotAsigned() {
        return this.notAsigned;
    }
    
    @Override
    public BussLineAsg getSelected(){
        return this.selected;
    }
    
    @Override
    public void setSelected(BussLineAsg selected){
        this.selected = selected;
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
