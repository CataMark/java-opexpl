package ro.any.c12153.opexpl.view.key;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import ro.any.c12153.opexpl.entities.BussLineAsg;
import ro.any.c12153.opexpl.entities.KeyVal;
import ro.any.c12153.opexpl.entities.KeyValGroup;
import ro.any.c12153.opexpl.services.BussLineAsgServ;
import ro.any.c12153.opexpl.services.KeyG01Serv;
import ro.any.c12153.opexpl.view.help.KeyG01ValReduce;
import ro.any.c12153.opexpl.view.help.KeyValCell;
import ro.any.c12153.opexpl.view.help.KeyValRow;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.beans.CurrentLocale;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "kg01v")
@ViewScoped
public class KeyG01ValItem implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(KeyG01ValItem.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject KeyG01ValList owner;
    private String initError;
    private KeyValGroup selected;
    private List<KeyValRow> rows;
    private String finishScript;
    
    private void initRows() throws Exception{
        final String coarea = this.owner.getCoarea().getCod();
        final Integer cheie = this.owner.getCheie().getId();        
        this.rows = new ArrayList<>();
        
        CompletableFuture<List<KeyVal>> fValori = CompletableFuture.supplyAsync(() -> {
            List<KeyVal> rezultat = null;
            try {
                if (this.selected != null && this.selected.getValori() != null && !this.selected.getValori().isEmpty())
                    rezultat = KeyG01Serv.getValsByCheiAndCCenter(
                            cheie,
                            this.selected.getHier(),
                            this.selected.getData_set(),
                            this.selected.getCod(),
                            cuser.getUname()
                    );
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
            return rezultat;
        });
        CompletableFuture<List<BussLineAsg>> fLines = CompletableFuture.supplyAsync(() -> {
            List<BussLineAsg> rezultat = new ArrayList<>();
            try {
                rezultat = BussLineAsgServ.getListAsignToCoArea(coarea, cuser.getUname());
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
            return rezultat;
        });
        
        List<KeyVal> valori = fValori.get();
        List<BussLineAsg> lines = fLines.get();
        boolean noValues = (valori == null || valori.isEmpty());
        
        for (BussLineAsg line : lines){
            KeyValRow r = new KeyValRow(line.getBuss_line(), line.getBuss_line_seg(), line.getBuss_line_nume(),
                    new ArrayList<>());
            for (Short an : this.owner.getDataset_ani()){
                if (noValues){
                    r.getCells().add(new KeyValCell(null, an, 0, null));
                } else {
                    r.getCells().add(
                        valori.stream()
                            .filter(x -> x.getBuss_line().equals(line.getBuss_line()) && x.getAn().equals(an))
                            .map(x -> new KeyValCell(x.getId(), x.getAn(), x.getValoare(), x.getMod_timp()))
                            .findFirst()
                            .orElse(new KeyValCell(null, an, 0, null))
                    );
                }
            }
            this.rows.add(r);
        }
    }
    
    public void initLists(){
        try {
            this.initRows();
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    protected void clear(){
        this.initError = null;
        this.selected = null;
        this.rows = null;
        this.finishScript = null;
    }
    
    public double getRowPercentByAn(double val, short an){
        if (val == 0) return 0;
        double suma = this.getRowSumByAn(an);            
        return (suma == 0 ? 0 : val / suma);
    }
    
    public double getRowSumByAn(short an){
        if (this.rows == null || this.rows.isEmpty()) return 0;
        return this.rows.stream()
                .map(x -> x.getCellByAn(an))
                .mapToDouble(x -> x.getValoare())
                .sum();
    }
    
    public void save(){
        try {
            if (this.selected == null || !this.selected.getLeaf())
                throw new Exception(App.getBeanMess("err.ccenter.nok", clocale));
                       
            List<KeyVal> valori = new ArrayList<>();            
            for (KeyValRow r : this.rows){
                for (KeyValCell c : r.getCells()){
                    if (c.getMod_timp() != null || c.getValoare() != 0){
                        KeyVal v = new KeyVal();
                        v.setId(c.getId());
                        v.setCheie(this.owner.getCheie().getId());
                        v.setCoarea(this.owner.getCoarea().getCod());
                        v.setBuss_line(r.getCod());
                        v.setHier(this.owner.getCoarea().getHier());
                        v.setData_set(this.owner.getDataset().getId());
                        v.setCost_center(this.selected.getCod());
                        v.setGen_data_set(this.owner.getDataset().getId());
                        v.setAn(c.getAn());
                        v.setValoare(c.getValoare());
                        valori.add(v);
                    }
                }
            }
            
            KeyValGroup rezultat =  KeyG01Serv.saveVals(this.owner.getCheie().getId(), this.owner.getDataset().getId(), this.selected.getCod(), valori, cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
            rezultat.setNivel(this.selected.getNivel());
            for (int i = 0; i < this.owner.getList().size(); i++){
                if (this.owner.getList().get(i).getId().equals(rezultat.getId())){
                    this.owner.getList().set(i, rezultat);
                }
            }
            this.owner.setList(KeyG01ValReduce.groupByCostCenter(this.owner.getList()));
            this.owner.setSelected(rezultat);
            this.owner.setReinit(true);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.val.save", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
                
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.val.save", clocale), ex.getMessage()));
        }
    }

    public String getInitError() {
        return initError;
    }
    
    public List<Short> getDataset_ani(){
        return this.owner.getDataset_ani();
    }
    
    public KeyValGroup getSelected() {
        return selected;
    }

    public void setSelected(KeyValGroup selected) {
        this.selected = selected;
    }

    public List<KeyValRow> getRows() {
        return this.rows;
    }
    
    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
