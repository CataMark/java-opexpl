package ro.any.c12153.shared.beans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.JsonArray;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "dbload")
@ViewScoped
public class DataBaseLoadView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(DataBaseLoadView.class.getName());    
    
    private @Inject @CurrentUser User cuser;
    private @Inject @CurrentLocale Locale clocale;
    
    private String tabela;
    private String delimitator;
    private String quote;
    
    /** file maximum size in bytes */
    private long maxFileSize;
    private String textLoad;
    
    /** JsonArray of strings */
    private String colNames;
    /** JsonArray of strings */
    private String colValues;
    
    private String errorFileLoadInit;
    private String errorTextLoadInit;
    private String finishScript;
    
    public void clear(){
        this.tabela = null;
        this.delimitator = null;
        this.quote = null;        
        this.maxFileSize = 0;
        this.textLoad = null;        
        this.colNames = null;
        this.colValues = null;
        this.errorFileLoadInit = null;
        this.errorTextLoadInit = null;
        this.finishScript = null;
    }
    
    private Optional<Map<String, String>> fieldCheckMap(){        
        JsonArray colNamesArray = Utils.readJsonArrayFromString(this.colNames);
        JsonArray colValuesArray = Utils.readJsonArrayFromString(this.colValues);
        
        if (colNamesArray.isEmpty() && colValuesArray.isEmpty()) return Optional.empty();
        
        Map<String, String> rezultat = new HashMap<>();
        for (int i = 0; i < colNamesArray.size(); i++){
            rezultat.put(colNamesArray.getString(i), colValuesArray.getString(i));
        }
        return Optional.of(rezultat);
    }
    
    public void loadFile(FileUploadEvent event){
        try {
            if (!Utils.stringNotEmpty(this.delimitator)) throw new Exception(App.getBeanMess("err.dload.delim.nok", clocale));            
            UploadedFile fisier = event.getFile();
            if (fisier == null) throw new Exception(App.getBeanMess("err.dload.file.nok", clocale));
            
            String rezultat;
            try(InputStreamReader iReader = new InputStreamReader(fisier.getInputstream(), StandardCharsets.UTF_8);
                BufferedReader bReader = new BufferedReader(iReader);){
                rezultat = App.getConn(cuser.getUname())
                        .loadText(this.tabela, bReader, this.delimitator, Optional.ofNullable(this.quote), this.fieldCheckMap());
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("title.dload.file", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dload.file", clocale), ex.getMessage()));
        }
    }
    
    public void loadText(){
        try {
            if (!Utils.stringNotEmpty(this.delimitator)) throw new Exception(App.getBeanMess("err.dload.delim.nok", clocale));
            if (!Utils.stringNotEmpty(this.textLoad)) throw new Exception("err.dload.text.nok");
            
            String rezultat;
            try(StringReader sReader = new StringReader(this.textLoad);
                BufferedReader bReader = new BufferedReader(sReader);){
                rezultat = App.getConn(cuser.getUname())
                        .loadText(this.tabela, bReader, this.delimitator, Optional.ofNullable(this.quote), this.fieldCheckMap());
            }
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, App.getBeanMess("title.dload.text", clocale), rezultat));
            if (Utils.stringNotEmpty(this.finishScript)) PrimeFaces.current().executeScript(this.finishScript);
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.dload.text", clocale), ex.getMessage()));
        }
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getDelimitator() {
        return delimitator;
    }

    public void setDelimitator(String delimitator) {
        this.delimitator = delimitator;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    /**
     * @return long - file maximum size in bytes
     */
    public long getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * @param maxFileSize - file maximum size in bytes
     */
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getTextLoad() {
        return textLoad;
    }

    public void setTextLoad(String textLoad) {
        this.textLoad = textLoad;
    }

    /**
     * @return String - representation of JsonArray of strings
     */
    public String getColNames() {
        return colNames;
    }

    /**
     * @param colNames - String - representation of JsonArray of strings
     */
    public void setColNames(String colNames) {
        this.colNames = colNames;
    }

    /**
     * @return String - representation of JsonArray of strings
     */
    public String getColValues() {
        return colValues;
    }

    /**
     * @param colValues - String - representation of JsonArray of strings
     */
    public void setColValues(String colValues) {
        this.colValues = colValues;
    }

    public String getErrorFileLoadInit() {
        if (Utils.stringNotEmpty(this.errorFileLoadInit)) return this.errorFileLoadInit;
        
        try {
            JsonArray colNamesArray = Utils.readJsonArrayFromString(this.colNames);
            JsonArray colValuesArray = Utils.readJsonArrayFromString(this.colValues);

            if (!(colNamesArray.isEmpty() && colValuesArray.isEmpty()))
                if (colNamesArray.isEmpty() || colValuesArray.isEmpty()) throw new Exception(App.getBeanMess("err.dload.cols.list.nok", clocale));      
            if (colNamesArray.size() != colValuesArray.size()) throw new Exception(App.getBeanMess("err.dload.cols.list.nok", clocale));
            
            if (!Utils.stringNotEmpty(this.tabela)) throw new Exception(App.getBeanMess("err.dload.tbl.nok", clocale));
            if (this.maxFileSize <= 0) throw new Exception(App.getBeanMess("err.dload.size.nok", clocale));
        } catch (Exception ex) {
            this.errorFileLoadInit = ex.getMessage();
        }
        return this.errorFileLoadInit;
    }

    public String getErrorTextLoadInit() {
        if (Utils.stringNotEmpty(this.errorTextLoadInit)) return this.errorTextLoadInit;
        
        try {
            JsonArray colNamesArray = Utils.readJsonArrayFromString(this.colNames);
            JsonArray colValuesArray = Utils.readJsonArrayFromString(this.colValues);

            if (!(colNamesArray.isEmpty() && colValuesArray.isEmpty()))
                if (colNamesArray.isEmpty() || colValuesArray.isEmpty()) throw new Exception(App.getBeanMess("err.dload.cols.list.nok", clocale));      
            if (colNamesArray.size() != colValuesArray.size()) throw new Exception(App.getBeanMess("err.dload.cols.list.nok", clocale));
            
            if (!Utils.stringNotEmpty(this.tabela)) throw new Exception(App.getBeanMess("err.dload.tbl.nok", clocale));
        } catch (Exception ex) {
            this.errorTextLoadInit = ex.getMessage();
        }        
        return this.errorTextLoadInit;
    }

    public String getFinishScript() {
        return finishScript;
    }

    public void setFinishScript(String finishScript) {
        this.finishScript = finishScript;
    }
}
