package ro.any.c12153.opexpl.entities;

import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author C12153
 */
public class PlanVal implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table fields names
    private static final String ID = "id";
    private static final String DOC_ID = "head_id";
    private static final String CONT = "cont";
    private static final String DATA_SET = "data_set";
    private static final String AN = "an";
    private static final String PER = "per";
    private static final String VALOARE = "valoare";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //fields not in table
    private static final String ACTUAL = "actual";
    
    private String id;
    private String doc_id;
    private String cont;
    private Integer data_set;
    private Short an;
    private String per;
    private Boolean actual;
    private Double valoare;
    private String mod_de;
    private Date mod_timp;
    
    public PlanVal(){
    }
    
    public PlanVal(Map<String, Object> inreg){
        this.id = (String) inreg.get(ID);
        this.doc_id = (String) inreg.get(DOC_ID);
        this.cont = (String) inreg.get(CONT);
        this.data_set = (Integer) inreg.get(DATA_SET);
        this.an = (Short) inreg.get(AN);
        this.per = (String) inreg.get(PER);
        this.actual = (Boolean) inreg.get(ACTUAL);
        this.valoare = (Double) inreg.get(VALOARE);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public PlanVal(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public PlanVal(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(DOC_ID) && !jsonO.isNull(DOC_ID)) this.doc_id = jsonO.getString(DOC_ID);
        if (jsonO.containsKey(CONT) && !jsonO.isNull(CONT)) this.cont = jsonO.getString(CONT);
        if (jsonO.containsKey(DATA_SET) && !jsonO.isNull(DATA_SET)) this.data_set = jsonO.getInt(DATA_SET);
        if (jsonO.containsKey(AN) && !jsonO.isNull(AN)) this.an = Short.parseShort(jsonO.getJsonNumber(AN).toString());
        if (jsonO.containsKey(PER) && !jsonO.isNull(PER)) this.per = jsonO.getString(PER);
        if (jsonO.containsKey(ACTUAL) && !jsonO.isNull(ACTUAL)) this.actual = jsonO.getBoolean(ACTUAL);
        if (jsonO.containsKey(VALOARE) && !jsonO.isNull(VALOARE))
            this.valoare = Double.parseDouble(jsonO.getJsonNumber(VALOARE).toString());            
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);        
        if (this.doc_id != null) jsonb.add(DOC_ID, this.doc_id);        
        if (this.cont != null) jsonb.add(CONT, this.cont);        
        if (this.data_set != null) jsonb.add(DATA_SET, this.data_set);        
        if (this.an != null) jsonb.add(AN, this.an);        
        if (this.per != null) jsonb.add(PER, this.per);        
        if (this.actual != null) jsonb.add(ACTUAL, this.actual);        
        if (this.valoare != null) jsonb.add(VALOARE, this.valoare);        
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);        
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        return jsonb.build();
    }
    
    public String getJsonEncoded(){
        return Base64.getEncoder().encodeToString(
                this.getJson().toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.doc_id);
        hash = 83 * hash + Objects.hashCode(this.cont);
        hash = 83 * hash + Objects.hashCode(this.data_set);
        hash = 83 * hash + Objects.hashCode(this.an);
        hash = 83 * hash + Objects.hashCode(this.per);
        hash = 83 * hash + Objects.hashCode(this.actual);
        hash = 83 * hash + Objects.hashCode(this.valoare);
        hash = 83 * hash + Objects.hashCode(this.mod_de);
        hash = 83 * hash + Objects.hashCode(this.mod_timp);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (this.getClass().isInstance(o)) return o.hashCode() == this.hashCode();
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public Integer getData_set() {
        return data_set;
    }

    public void setData_set(Integer data_set) {
        this.data_set = data_set;
    }

    public Short getAn() {
        return an;
    }

    public void setAn(Short an) {
        this.an = an;
    }

    public String getPer() {
        return per;
    }

    public void setPer(String per) {
        this.per = per;
    }

    public Boolean getActual() {
        return actual;
    }

    public void setActual(Boolean actual) {
        this.actual = actual;
    }
    
    public Double getValoare() {
        return valoare;
    }

    public void setValoare(Double valoare) {
        this.valoare = valoare;
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
