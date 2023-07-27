package ro.any.c12153.opexpl.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.opexpl.services.DataSetServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "copyfromdset")
@ViewScoped
public class CopyFromDataSet implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CopyFromDataSet.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;

    private CopyFromDataSetContract owner;
    private DataSetType setType;
    
    private List<Short> ani;
    private List<DataSet> seturi;
    
    private Short an;
    private Integer from_set;
    private Byte alternativa;
    private String finishScript;
    private boolean okFlag = false;
    private String nokMessage;
    
    public void init(CopyFromDataSetContract owner, DataSetType setType) {
        this.owner = owner;
        this.setType = setType;
    }
    
    public void clear(){
        this.ani = null;
        this.seturi = null;
        this.an = null;
        this.from_set = null;
        this.alternativa = null;
        this.finishScript = null;
        this.okFlag = false;
        this.nokMessage = null;
    }
    
    public void clearSeturi(){
        this.from_set = null;
        this.seturi = null;
    }

    public void execute(){
        this.owner.copiaza(this.from_set, this.finishScript, (this.alternativa == null ? -1 : this.alternativa));
    }

    public List<Short> getAni() {
        try {
            if (this.ani == null || this.ani.isEmpty())
                switch (this.setType){
                    case ALL:
                    case ACTUAL:
                        this.ani = DataSetServ.getAllAni(cuser.getUname());
                        break;
                    case PLAN:
                        this.ani = DataSetServ.getPlanAni(cuser.getUname());
                        break;
                    case RAPORT:
                        this.ani = DataSetServ.getRaportareAni(cuser.getUname());
                        break;
                    default:
                        throw new Exception(App.getBeanMess("err.dset.notip", clocale));
                }        
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dset.getani", clocale), ex.getMessage()));
            this.ani = new ArrayList<>(); 
        }
        return this.ani;
    }

    public List<DataSet> getSeturi() {
        try{
            if (this.seturi == null || this.seturi.isEmpty())
                if (this.an != null)
                    switch (this.setType){
                        case ALL:
                            this.seturi = DataSetServ.getAllByAn(this.an, cuser.getUname()).stream()
                                .filter(x -> !x.getId().equals(this.owner.getDataset().getId()))
                                .collect(Collectors.toList());
                            break;
                        case ACTUAL:
                            this.seturi = DataSetServ.getAllByAn(this.an, cuser.getUname()).stream()
                                .filter(x -> x.getActual().equals(true))
                                .collect(Collectors.toList());
                            break;
                        case PLAN:
                            this.seturi = DataSetServ.getPlanByAn(this.an, cuser.getUname()).stream()
                                .filter(x -> !x.getId().equals(this.owner.getDataset().getId()))
                                .collect(Collectors.toList());
                            break;
                        case RAPORT:
                            this.seturi = DataSetServ.getRaportareByAn(this.an, cuser.getUname()).stream()
                                .filter(x -> !x.getId().equals(this.owner.getDataset().getId()))
                                .collect(Collectors.toList());
                            break;
                        default:
                            throw new Exception(App.getBeanMess("err.dset.per.noset", clocale));
                    }
        } catch (Exception ex){
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dset.get", clocale), ex.getMessage()));
            this.seturi = new ArrayList<>();
        }
        return this.seturi;
    }

    public Short getAn() {
        return an;
    }

    public void setAn(Short an) {
        this.an = an;
    }

    public DataSet getDest_set() {
        return this.owner.getDataset();
    }

    public Integer getFrom_set() {
        return from_set;
    }

    public void setFrom_set(Integer from_set) {
        this.from_set = from_set;
    }

    public Byte getAlternativa() {
        return alternativa;
    }

    public void setAlternativa(Byte alternativa) {
        this.alternativa = alternativa;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }

    public boolean getOkFlag() {
        return okFlag;
    }

    public void setOkFlag(boolean okFlag) {
        this.okFlag = okFlag;
    }

    public String getNokMessage() {
        return nokMessage;
    }

    public void setNokMessage(String nokMessage) {
        this.nokMessage = nokMessage;
    }
}
