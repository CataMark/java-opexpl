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
public class OpexCateg implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String ID = "id";
    private static final String COD = "cod";
    private static final String NUME = "nume";
    private static final String COST_DRIVER = "cost_driver";
    private static final String CONT_CCOA = "cont_ccoa";
    private static final String BLOCAT = "blocat";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //fields not in table
    private static final String COST_DRIVER_NUME = "cost_driver_nume";
    
    private String id;
    private Integer cod;
    private String nume;
    private String cost_driver;
    private String cost_driver_nume;
    private String cont_ccoa;
    private Boolean blocat;
    private String mod_de;
    private Date mod_timp;
    
    public OpexCateg(){
    }
    
    public OpexCateg(Map<String, Object> inreg){
        this.id = (String) inreg.get(ID);
        this.cod = (Integer) inreg.get(COD);
        this.nume = (String) inreg.get(NUME);
        this.cost_driver = (String) inreg.get(COST_DRIVER);
        this.cost_driver_nume = (String) inreg.get(COST_DRIVER_NUME);
        this.cont_ccoa = (String) inreg.get(CONT_CCOA);
        this.blocat = (Boolean) inreg.get(BLOCAT);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public OpexCateg(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public OpexCateg(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(COD) && !jsonO.isNull(COD)) this.cod = jsonO.getInt(COD);
        if (jsonO.containsKey(NUME) && !jsonO.isNull(NUME)) this.nume = jsonO.getString(NUME);
        if (jsonO.containsKey(COST_DRIVER) && !jsonO.isNull(COST_DRIVER)) this.cost_driver = jsonO.getString(COST_DRIVER);
        if (jsonO.containsKey(COST_DRIVER_NUME) && !jsonO.isNull(COST_DRIVER_NUME)) this.cost_driver_nume = jsonO.getString(COST_DRIVER_NUME);
        if (jsonO.containsKey(CONT_CCOA) && !jsonO.isNull(CONT_CCOA)) this.cont_ccoa = jsonO.getString(CONT_CCOA);
        if (jsonO.containsKey(BLOCAT) && !jsonO.isNull(BLOCAT)) this.blocat = jsonO.getBoolean(BLOCAT);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);        
        if (this.cod != null) jsonb.add(COD, this.cod);        
        if (this.nume != null) jsonb.add(NUME, this.nume);        
        if (this.cost_driver != null) jsonb.add(COST_DRIVER, this.cost_driver);        
        if (this.cost_driver_nume != null) jsonb.add(COST_DRIVER_NUME, this.cost_driver_nume);        
        if (this.cont_ccoa != null) jsonb.add(CONT_CCOA, this.cont_ccoa);        
        if (this.blocat != null) jsonb.add(BLOCAT, this.blocat);        
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);        
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        return jsonb.build();
    }
    
    public String getJsonEncoded(){
        return Base64.getEncoder().encodeToString(
                this.getJson().toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getCost_driver() {
        return cost_driver;
    }

    public void setCost_driver(String cost_driver) {
        this.cost_driver = cost_driver;
    }

    public String getCost_driver_nume() {
        return cost_driver_nume;
    }

    public void setCost_driver_nume(String cost_driver_nume) {
        this.cost_driver_nume = cost_driver_nume;
    }

    public String getCont_ccoa() {
        return cont_ccoa;
    }

    public void setCont_ccoa(String cont_ccoa) {
        this.cont_ccoa = cont_ccoa;
    }

    public Boolean getBlocat() {
        return blocat;
    }

    public void setBlocat(Boolean blocat) {
        this.blocat = blocat;
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
