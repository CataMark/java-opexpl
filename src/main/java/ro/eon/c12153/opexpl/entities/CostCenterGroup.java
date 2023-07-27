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
public class CostCenterGroup implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String ID = "id";
    private static final String HIER = "hier";
    private static final String DATA_SET = "data_set";
    private static final String COD = "cod";
    private static final String NUME = "nume";
    private static final String SUPERIOR = "superior";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //fields not in table
    private static final String SUPERIOR_COD = "superior_cod";
    private static final String SUPERIOR_NUME = "superior_nume";
    private static final String NIVEL = "nivel";
    
    private String id;
    private String hier;
    private Integer data_set;
    private String cod;
    private String nume;
    private String superior;
    private String superior_cod;
    private String superior_nume;
    private Short nivel;
    private String mod_de;
    private Date mod_timp;
    
    public CostCenterGroup(){
    }
    
    public CostCenterGroup(Map<String, Object> inreg){        
        this.id = (String) inreg.get(ID);
        this.hier = (String) inreg.get(HIER);
        this.data_set = (Integer) inreg.get(DATA_SET);
        this.cod = (String) inreg.get(COD);
        this.nume = (String) inreg.get(NUME);
        this.superior = (String) inreg.get(SUPERIOR);
        this.superior_cod = (String) inreg.get(SUPERIOR_COD);
        this.superior_nume = (String) inreg.get(SUPERIOR_NUME);
        this.nivel = (Short) inreg.get(NIVEL);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public CostCenterGroup(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public CostCenterGroup(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(HIER) && !jsonO.isNull(HIER)) this.hier = jsonO.getString(HIER);
        if (jsonO.containsKey(DATA_SET) && !jsonO.isNull(DATA_SET)) this.data_set = jsonO.getInt(DATA_SET);
        if (jsonO.containsKey(COD) && !jsonO.isNull(COD)) this.cod = jsonO.getString(COD);
        if (jsonO.containsKey(NUME) && !jsonO.isNull(NUME)) this.nume = jsonO.getString(NUME);
        if (jsonO.containsKey(SUPERIOR) && !jsonO.isNull(SUPERIOR)) this.superior = jsonO.getString(SUPERIOR);
        if (jsonO.containsKey(SUPERIOR_COD) && !jsonO.isNull(SUPERIOR_COD)) this.superior_cod = jsonO.getString(SUPERIOR_COD);
        if (jsonO.containsKey(SUPERIOR_NUME) && !jsonO.isNull(SUPERIOR_NUME)) this.superior_nume = jsonO.getString(SUPERIOR_NUME);
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
        if (this.superior != null) jsonb.add(SUPERIOR, this.superior);        
        if (this.superior_cod != null) jsonb.add(SUPERIOR_COD, this.superior_cod);        
        if (this.superior_nume != null) jsonb.add(SUPERIOR_NUME, this.superior_nume);        
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
    
    public CostCenter cast(){
        CostCenter rezultat = new CostCenter();
        rezultat.setId(this.id);
        rezultat.setHier(this.hier);
        rezultat.setData_set(this.data_set);
        rezultat.setCod(this.cod);
        rezultat.setNume(this.nume);
        rezultat.setSuperior_cod(this.superior_cod);
        rezultat.setSuperior_nume(this.superior_nume);
        rezultat.setBlocat(Boolean.FALSE);
        rezultat.setLeaf(Boolean.FALSE);
        rezultat.setNivel(this.nivel);
        rezultat.setMod_de(this.mod_de);
        rezultat.setMod_timp(this.mod_timp);
        return rezultat;
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

    public String getSuperior() {
        return superior;
    }

    public void setSuperior(String superior) {
        this.superior = superior;
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

    public Short getNivel() {
        return nivel;
    }

    public void setNivel(Short nivel) {
        this.nivel = nivel;
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
