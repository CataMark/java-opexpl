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
public class CostDriver implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table fields names
    private static final String ID = "id";
    private static final String COD = "cod";
    private static final String NUME = "nume";
    private static final String CENTRAL = "central";
    private static final String BLOCAT = "blocat";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    private String id;
    private String cod;
    private String nume;
    private Boolean central;
    private Boolean blocat;
    private String mod_de;
    private Date mod_timp;
    
    public CostDriver(){
    }
    
    public CostDriver(Map<String, Object> inreg){
        this.id = (String) inreg.get(ID);
        this.cod = (String) inreg.get(COD);
        this.nume = (String) inreg.get(NUME);
        this.central = (Boolean) inreg.get(CENTRAL);
        this.blocat = (Boolean) inreg.get(BLOCAT);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public CostDriver(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public CostDriver(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(COD) && !jsonO.isNull(COD)) this.cod = jsonO.getString(COD);
        if (jsonO.containsKey(NUME) && !jsonO.isNull(NUME)) this.nume = jsonO.getString(NUME);
        if (jsonO.containsKey(CENTRAL) && !jsonO.isNull(CENTRAL)) this.central = jsonO.getBoolean(CENTRAL);
        if (jsonO.containsKey(BLOCAT) && !jsonO.isNull(BLOCAT)) this.blocat =  jsonO.getBoolean(BLOCAT);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);        
        if (this.cod != null) jsonb.add(COD, this.cod);        
        if (this.nume != null) jsonb.add(NUME, this.nume);        
        if (this.central != null) jsonb.add(CENTRAL, this.central);        
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

    public Boolean getCentral() {
        return central;
    }

    public void setCentral(Boolean central) {
        this.central = central;
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
