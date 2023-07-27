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
public class CostCenter implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String ID = "id";
    private static final String HIER = "hier";
    private static final String DATA_SET = "data_set";
    private static final String COD = "cod";
    private static final String NUME = "nume";
    private static final String SUPERIOR_COD = "grup";
    private static final String BLOCAT = "blocat";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //fields not in table
    private static final String SUPERIOR_NUME = "grup_nume";
    private static final String LEAF = "leaf";
    private static final String NIVEL = "nivel";
    
    private String id;
    private String hier;
    private Integer data_set;
    private String cod;
    private String nume;
    private String superior_cod;
    private String superior_nume;
    private Boolean blocat;
    private Boolean leaf;
    private Short nivel;
    private String mod_de;
    private Date mod_timp;
    
    public CostCenter(){
    }
    
    public CostCenter(Map<String, Object> inreg){        
        this.id = (String) inreg.get(ID);
        this.hier = (String) inreg.get(HIER);
        this.data_set = (Integer) inreg.get(DATA_SET);
        this.cod = (String) inreg.get(COD);
        this.nume = (String) inreg.get(NUME);
        this.superior_cod = (String) inreg.get(SUPERIOR_COD);
        this.superior_nume = (String) inreg.get(SUPERIOR_NUME);
        this.blocat = (Boolean) inreg.get(BLOCAT);
        this.leaf = (Boolean) inreg.get(LEAF);
        this.nivel = (Short) inreg.get(NIVEL);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public CostCenter(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public CostCenter(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(HIER) && !jsonO.isNull(HIER)) this.hier = jsonO.getString(HIER);
        if (jsonO.containsKey(DATA_SET) && !jsonO.isNull(DATA_SET)) this.data_set = jsonO.getInt(DATA_SET);
        if (jsonO.containsKey(COD) && !jsonO.isNull(COD)) this.cod = jsonO.getString(COD);
        if (jsonO.containsKey(NUME) && !jsonO.isNull(NUME)) this.nume = jsonO.getString(NUME);
        if (jsonO.containsKey(SUPERIOR_COD) && !jsonO.isNull(SUPERIOR_COD)) this.superior_cod = jsonO.getString(SUPERIOR_COD);
        if (jsonO.containsKey(SUPERIOR_NUME) && !jsonO.isNull(SUPERIOR_NUME)) this.superior_nume = jsonO.getString(SUPERIOR_NUME);
        if (jsonO.containsKey(BLOCAT) && !jsonO.isNull(BLOCAT)) this.blocat =  jsonO.getBoolean(BLOCAT);
        if (jsonO.containsKey(LEAF) && !jsonO.isNull(LEAF)) this.leaf = jsonO.getBoolean(LEAF);
        if (jsonO.containsKey(NIVEL) && !jsonO.isNull(NIVEL)) this.nivel = Short.valueOf(jsonO.getJsonNumber(NIVEL).toString());
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP)); 
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);        
        if (this.hier != null) jsonb.add(HIER, this.hier);        
        if (this.data_set != null) jsonb.add(DATA_SET, this.data_set);        
        if (this.cod != null) jsonb.add(COD, this.cod);        
        if (this.nume != null) jsonb.add(NUME, this.nume);        
        if (this.superior_cod != null) jsonb.add(SUPERIOR_COD, this.superior_cod);        
        if (this.superior_nume != null) jsonb.add(SUPERIOR_NUME, this.superior_nume);        
        if (this.blocat != null) jsonb.add(BLOCAT, this.blocat);        
        if (this.leaf != null) jsonb.add(LEAF, this.leaf);        
        if (this.nivel != null) jsonb.add(NIVEL, this.nivel);        
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
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.hier);
        hash = 53 * hash + Objects.hashCode(this.data_set);
        hash = 53 * hash + Objects.hashCode(this.cod);
        hash = 53 * hash + Objects.hashCode(this.mod_de);
        hash = 53 * hash + Objects.hashCode(this.mod_timp);
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

    public String getHier() {
        return hier;
    }

    public void setHier(String hier) {
        this.hier = hier;
    }

    public Integer getData_set() {
        return data_set;
    }

    public void setData_set(Integer data_set) {
        this.data_set = data_set;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getSuperior_cod() {
        return superior_cod;
    }

    public void setSuperior_cod(String superior_cod) {
        this.superior_cod = superior_cod;
    }

    public String getSuperior_nume() {
        return superior_nume;
    }

    public void setSuperior_nume(String superior_nume) {
        this.superior_nume = superior_nume;
    }

    public Boolean getBlocat() {
        return blocat;
    }

    public void setBlocat(Boolean blocat) {
        this.blocat = blocat;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }

    public String getMod_de() {
        return mod_de;
    }

    public void setMod_de(String mod_de) {
        this.mod_de = mod_de;
    }

    public Short getNivel() {
        return nivel;
    }

    public void setNivel(Short nivel) {
        this.nivel = nivel;
    }

    public Date getMod_timp() {
        return mod_timp;
    }

    public void setMod_timp(Date mod_timp) {
        this.mod_timp = mod_timp;
    }
}
