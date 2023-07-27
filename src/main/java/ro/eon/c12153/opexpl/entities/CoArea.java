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
public class CoArea implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table fields name
    private static final String COD = "cod";
    private static final String NUME = "nume";
    private static final String ACRONIM = "acronim";
    private static final String ALOCARE = "alocare";
    private static final String HIER = "cc_hier";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    private String cod;
    private String nume;
    private String acronim;
    private Boolean alocare;
    private String hier;
    private String mod_de;
    private Date mod_timp;
    
    public CoArea(){
    }
    
    public CoArea(Map<String, Object> inreg){
        this.cod = (String) inreg.get(COD);
        this.nume = (String) inreg.get(NUME);
        this.acronim = (String) inreg.get(ACRONIM);
        this.alocare = (Boolean) inreg.get(ALOCARE);
        this.hier = (String) inreg.get(HIER);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public CoArea(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public CoArea(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(COD) && !jsonO.isNull(COD)) this.cod = jsonO.getString(COD);
        if (jsonO.containsKey(NUME) && !jsonO.isNull(NUME)) this.nume = jsonO.getString(NUME);
        if (jsonO.containsKey(ACRONIM) && !jsonO.isNull(ACRONIM)) this.acronim = jsonO.getString(ACRONIM);
        if (jsonO.containsKey(ALOCARE) && !jsonO.isNull(ALOCARE)) this.alocare = jsonO.getBoolean(ALOCARE);
        if (jsonO.containsKey(HIER) && !jsonO.isNull(HIER)) this.hier = jsonO.getString(HIER);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.cod != null) jsonb.add(COD, this.cod);        
        if (this.nume != null) jsonb.add(NUME, this.nume);        
        if (this.acronim != null) jsonb.add(ACRONIM, this.acronim);        
        if(this.alocare != null) jsonb.add(ALOCARE, this.alocare);        
        if (this.hier != null) jsonb.add(HIER, this.hier);        
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);        
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        return jsonb.build();
    }
    
    public String getJsonEncoded(){
        return Base64.getEncoder().encodeToString(
                    this.getJson().toString().getBytes(StandardCharsets.UTF_8)
        );
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

    public String getAcronim() {
        return acronim;
    }

    public void setAcronim(String acronim) {
        this.acronim = acronim;
    }

    public Boolean getAlocare() {
        return alocare;
    }

    public void setAlocare(Boolean alocare) {
        this.alocare = alocare;
    }

    public String getHier() {
        return hier;
    }

    public void setHier(String hier) {
        this.hier = hier;
    }

    public String getMod_de() {
        return mod_de;
    }

    public void setMod_de(String mod_de) {
        this.mod_de = mod_de;
    }

    public Date getMod_timp() {
        return this.mod_timp;
    }

    public void setMod_timp(Date mod_timp) {
        this.mod_timp = mod_timp;
    }
}
