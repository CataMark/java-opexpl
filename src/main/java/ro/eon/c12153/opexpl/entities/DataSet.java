package ro.any.c12153.opexpl.entities;

import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author C12153
 */
public class DataSet implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String ID = "id";
    private static final String NUME = "nume";
    private static final String AN = "an";
    private static final String VERS = "vers";
    private static final String BLOCAT = "blocat";
    private static final String INCHEIAT = "incheiat";
    private static final String RAPORTARE = "raportare";
    private static final String COMPAR = "impl_compare";
    private static final String ACTUAL_SET = "actual_set";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //fields not in table
    private static final String ACTUAL = "actual";
    private static final String COMPAR_NUME = "impl_compare_nume";
    private static final String COMPAR_AN = "impl_compare_an";
    private static final String COMPAR_VERS = "impl_compare_vers";
    private static final String ACTUAL_SET_NUME = "actual_set_nume";
    private static final String ACTUAL_SET_AN = "actual_set_an";
    private static final String ACTUAL_SET_VERS = "actual_set_vers";
    
    private Integer id;
    private String nume;
    private Short an;
    private String vers;
    private Boolean actual;
    private Boolean blocat;
    private Boolean incheiat;
    private Boolean raportare;
    private Integer compar;
    private String compar_nume;
    private Short compar_an;
    private String compar_vers;
    private Integer actual_set;
    private String actual_set_nume;
    private Short actual_set_an;
    private String actual_set_vers;
    private String mod_de;
    private Date mod_timp;
    
    public DataSet(){
    }
    
    public DataSet(Map<String,Object> inreg){
        this.id = (Integer) inreg.get(ID);
        this.nume = (String) inreg.get(NUME);
        this.an = (Short) inreg.get(AN);
        this.vers = (String) inreg.get(VERS);
        this.actual = (Boolean) inreg.get(ACTUAL);
        this.blocat = (Boolean) inreg.get(BLOCAT);
        this.incheiat = (Boolean) inreg.get(INCHEIAT);
        this.raportare = (Boolean) inreg.get(RAPORTARE);
        this.compar = (Integer) inreg.get(COMPAR);
        this.compar_nume = (String) inreg.get(COMPAR_NUME);
        this.compar_an = (Short) inreg.get(COMPAR_AN);
        this.compar_vers = (String) inreg.get(COMPAR_VERS);
        this.actual_set = (Integer) inreg.get(ACTUAL_SET);
        this.actual_set_nume = (String) inreg.get(ACTUAL_SET_NUME);
        this.actual_set_an = (Short) inreg.get(ACTUAL_SET_AN);
        this.actual_set_vers = (String) inreg.get(ACTUAL_SET_VERS);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public DataSet(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public DataSet(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getInt(ID);
        if (jsonO.containsKey(NUME) && !jsonO.isNull(NUME)) this.nume = jsonO.getString(NUME);
        if (jsonO.containsKey(AN) && !jsonO.isNull(AN)) this.an = Short.valueOf(jsonO.getJsonNumber(AN).toString());
        if (jsonO.containsKey(VERS) && !jsonO.isNull(VERS)) this.vers = jsonO.getString(VERS);
        if (jsonO.containsKey(ACTUAL) && !jsonO.isNull(ACTUAL)) this.actual = jsonO.getBoolean(ACTUAL);
        if (jsonO.containsKey(BLOCAT) && !jsonO.isNull(BLOCAT)) this.blocat =  jsonO.getBoolean(BLOCAT);
        if (jsonO.containsKey(INCHEIAT) && !jsonO.isNull(INCHEIAT)) this.incheiat = jsonO.getBoolean(INCHEIAT);
        if (jsonO.containsKey(RAPORTARE) && !jsonO.isNull(RAPORTARE)) this.raportare = jsonO.getBoolean(RAPORTARE);
        if (jsonO.containsKey(COMPAR) && !jsonO.isNull(COMPAR)) this.compar = jsonO.getInt(COMPAR);
        if (jsonO.containsKey(COMPAR_NUME) && !jsonO.isNull(COMPAR_NUME)) this.compar_nume = jsonO.getString(COMPAR_NUME);
        if (jsonO.containsKey(COMPAR_AN) && !jsonO.isNull(COMPAR_AN))
            this.compar_an = Short.valueOf(jsonO.getJsonNumber(COMPAR_AN).toString());
        if (jsonO.containsKey(COMPAR_VERS) && !jsonO.isNull(COMPAR_VERS)) this.compar_vers = jsonO.getString(COMPAR_VERS);
        if (jsonO.containsKey(ACTUAL_SET) && !jsonO.isNull(ACTUAL_SET)) this.actual_set = jsonO.getInt(ACTUAL_SET);
        if (jsonO.containsKey(ACTUAL_SET_NUME) && !jsonO.isNull(ACTUAL_SET_NUME)) this.actual_set_nume = jsonO.getString(ACTUAL_SET_NUME);
        if (jsonO.containsKey(ACTUAL_SET_AN) && !jsonO.isNull(ACTUAL_SET_AN))
            this.actual_set_an = Short.valueOf(jsonO.getJsonNumber(ACTUAL_SET_AN).toString());
        if (jsonO.containsKey(ACTUAL_SET_VERS) && !jsonO.isNull(ACTUAL_SET_VERS)) this.actual_set_vers = jsonO.getString(ACTUAL_SET_VERS);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));        
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);        
        if (this.nume != null) jsonb.add(NUME, this.nume);        
        if (this.an != null) jsonb.add(AN, this.an);        
        if (this.vers != null) jsonb.add(VERS, this.vers);        
        if (this.actual != null) jsonb.add(ACTUAL, this.actual);        
        if (this.blocat != null) jsonb.add(BLOCAT, this.blocat);        
        if (this.incheiat != null) jsonb.add(INCHEIAT, this.incheiat);        
        if (this.raportare != null) jsonb.add(RAPORTARE, this.raportare);        
        if (this.compar != null) jsonb.add(COMPAR, this.compar);        
        if (this.compar_nume != null) jsonb.add(COMPAR_NUME, this.compar_nume);        
        if (this.compar_an != null) jsonb.add(COMPAR_AN, this.compar_an);        
        if (this.compar_vers != null) jsonb.add(COMPAR_VERS, this.compar_vers);        
        if (this.actual_set != null) jsonb.add(ACTUAL_SET, this.actual_set);        
        if (this.actual_set_nume != null) jsonb.add(ACTUAL_SET_NUME, this.actual_set_nume);        
        if (this.actual_set_an != null) jsonb.add(ACTUAL_SET_AN, this.actual_set_an);        
        if (this.actual_set_vers != null) jsonb.add(ACTUAL_SET_VERS, this.actual_set_vers);        
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);        
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        return jsonb.build();
    }
    
    public String getJsonEncoded(){
        return Base64.getEncoder().encodeToString(
                this.getJson().toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Short getAn() {
        return an;
    }

    public void setAn(Short an) {
        this.an = an;
    }

    public String getVers() {
        return vers;
    }

    public void setVers(String vers) {
        this.vers = vers;
    }

    public Boolean getActual() {
        return actual;
    }

    public void setActual(Boolean actual) {
        this.actual = actual;
    }

    public Boolean getBlocat() {
        return blocat;
    }

    public void setBlocat(Boolean blocat) {
        this.blocat = blocat;
    }

    public Boolean getIncheiat() {
        return incheiat;
    }

    public void setIncheiat(Boolean incheiat) {
        this.incheiat = incheiat;
    }

    public Boolean getRaportare() {
        return raportare;
    }

    public void setRaportare(Boolean raportare) {
        this.raportare = raportare;
    }

    public Integer getCompar() {
        return compar;
    }

    public void setCompar(Integer compar) {
        this.compar = compar;
    }

    public String getCompar_nume() {
        return compar_nume;
    }

    public void setCompar_nume(String compar_nume) {
        this.compar_nume = compar_nume;
    }

    public Short getCompar_an() {
        return compar_an;
    }

    public void setCompar_an(Short compar_an) {
        this.compar_an = compar_an;
    }

    public String getCompar_vers() {
        return compar_vers;
    }

    public void setCompar_vers(String compar_vers) {
        this.compar_vers = compar_vers;
    }

    public Integer getActual_set() {
        return actual_set;
    }

    public void setActual_set(Integer actual_set) {
        this.actual_set = actual_set;
    }

    public String getActual_set_nume() {
        return actual_set_nume;
    }

    public void setActual_set_nume(String actual_set_nume) {
        this.actual_set_nume = actual_set_nume;
    }

    public Short getActual_set_an() {
        return actual_set_an;
    }

    public void setActual_set_an(Short actual_set_an) {
        this.actual_set_an = actual_set_an;
    }

    public String getActual_set_vers() {
        return actual_set_vers;
    }

    public void setActual_set_vers(String actual_set_vers) {
        this.actual_set_vers = actual_set_vers;
    }

    public String getMod_de() {
        return mod_de;
    }

    public void setMod_de(String mod_de) {
        this.mod_de = mod_de;
    }

    public Date getMod_timp() {
        return mod_timp;
    }

    public void setMod_timp(Date mod_timp) {
        this.mod_timp = mod_timp;
    }
}
