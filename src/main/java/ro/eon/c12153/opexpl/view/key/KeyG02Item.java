package ro.any.c12153.opexpl.view.key;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
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
import ro.any.c12153.opexpl.entities.KeyHead;
import ro.any.c12153.opexpl.entities.KeyVal;
import ro.any.c12153.opexpl.services.BussLineAsgServ;
import ro.any.c12153.opexpl.services.KeyG02Serv;
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
@Named(value = "kg02")
@ViewScoped
public class KeyG02Item implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(KeyG02Item.class.getName());
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private @Inject KeyG02List owner;
    private String initError;
    private KeyHead selected;
    private List<KeyValRow> rows;
    private String finishScript;
    
    private void initRows() throws Exception{
        final Integer dataset = this.owner.getDataset().getId();
        final String coarea = this.owner.getCoarea().getCod();
        this.rows = new ArrayList<>();
        
        CompletableFuture<List<KeyVal>> fValori = CompletableFuture.supplyAsync(() -> {
            List<KeyVal> rezultat = null;
            try {
                if (this.selected != null && this.selected.getMod_timp() != null)
                    rezultat = KeyG02Serv.getById(this.selected.getId(), dataset, cuser.getUname())
                            .orElseThrow(() -> new Exception(App.getBeanMess("err.keys.nok", clocale))).getValori();
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
            if (this.selected == null) throw new Exception(App.getBeanMess("err.keys.nok", clocale));
            
            this.selected.setValori(new ArrayList<>());
            for (KeyValRow r : this.rows){
                for (KeyValCell c : r.getCells()){
                    if (c.getMod_timp() != null || c.getValoare() != 0){
                        KeyVal v = new KeyVal();
                        v.setId(c.getId());
                        v.setBuss_line(r.getCod());
                        v.setGen_data_set(this.owner.getDataset().getId());
                        v.setAn(c.getAn());
                        v.setValoare(c.getValoare());
                        this.selected.getValori().add(v);
                    }
                }
            }
            
            if (this.selected.getMod_timp() == null){
                KeyHead rezultat = KeyG02Serv.insert(this.owner.getDataset().getId(), this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                this.owner.getList().add(rezultat);
                this.owner.setSelected(rezultat);
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.ins", clocale), App.getBeanMess("info.success",  clocale)));
            } else {
                KeyHead rezultat = KeyG02Serv.update(this.owner.getDataset().getId(), this.selected, cuser.getUname())
                        .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));                
                for (int i = 0; i < this.owner.getList().size(); i++){
                    if (this.owner.getList().get(i).getId().equals(rezultat.getId())){
                        this.owner.getList().set(i, rezultat);
                        break;
                    }
                }
                this.owner.setSelected(rezultat);
                this.owner.getList().sort(Comparator.comparing(KeyHead::getId));
                
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.upd", clocale), App.getBeanMess("info.success",  clocale)));
            }            
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);

        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.save", clocale), ex.getMessage()));
        }
    }
    
    public void delete(){
        try {
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.keys.nok", clocale));
            
            KeyG02Serv.deleteHead(this.selected.getId(), cuser.getUname());            
            this.owner.getList().removeIf(x -> x.getId().equals(this.selected.getId()));
            this.owner.setSelected(null);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.del", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.del", clocale), ex.getMessage()));
        }
    }
    
    protected void copiaza(Integer fromset, String finishScript){
        try {
            if (fromset == null) throw new Exception(App.getBeanMess("err.dset.per.noset", clocale));
            if (this.selected == null || this.selected.getMod_timp() == null)
                throw new Exception(App.getBeanMess("err.keys.nok", clocale));
            
            KeyHead rezultat = KeyG02Serv.valsTakeOverByCheieAndSet(this.selected.getId(), fromset, this.owner.getDataset().getId(), cuser.getUname())
                    .orElseThrow(() -> new Exception(App.getBeanMess("err.nosuccess", clocale)));
            for (int i = 0; i < this.owner.getList().size(); i++){
                if (this.owner.getList().get(i).getId().equals(rezultat.getId())){
                    this.owner.getList().set(i, rezultat);
                    break;
                }
            }
            this.owner.setSelected(rezultat);
            //this.owner.getList().sort(Comparator.comparing(KeyHead::getId));

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    App.getBeanMess("title.keys.vals.copy", clocale), App.getBeanMess("info.success",  clocale)));
            if (Utils.stringNotEmpty(finishScript)) PrimeFaces.current().executeScript(finishScript);            
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.keys.vals.copy", clocale), ex.getMessage()));
        }
    }

    public String getInitError() {
        return initError;
    }
    
    public List<Short> getDataset_ani(){
        return this.owner.getDataset_ani();
    }
    
    public KeyHead getSelected() {
        return selected;
    }

    public void setSelected(KeyHead selected) {
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
