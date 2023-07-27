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
public class BussLineAsg implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String ID = "id";
    private static final String COAREA = "coarea";
    private static final String BUSS_LINE = "buss_line";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //database query field names
    private static final String COAREA_NUME = "coarea_nume";
    private static final String BUSS_LINE_SEG = "buss_line_seg";
    private static final String BUSS_LINE_NUME = "buss_line_nume";
    
    private String id;
    private String coarea;
    private String coarea_nume;
    private String buss_line;
    private String buss_line_seg;
    private String buss_line_nume;
    private String mod_de;
    private Date mod_timp;
    
    public BussLineAsg(){
    }
    
    public BussLineAsg(Map<String, Object> inreg){
        this.id = (String) inreg.get(ID);
        this.coarea = (String) inreg.get(COAREA);
        this.coarea_nume = (String) inreg.get(COAREA_NUME);
        this.buss_line = (String) inreg.get(BUSS_LINE);
        this.buss_line_seg = (String) inreg.get(BUSS_LINE_SEG);
        this.buss_line_nume = (String) inreg.get(BUSS_LINE_NUME);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public BussLineAsg(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public BussLineAsg(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
     private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(COAREA) && !jsonO.isNull(COAREA)) this.coarea = jsonO.getString(COAREA);
        if (jsonO.containsKey(COAREA_NUME) && !jsonO.isNull(COAREA_NUME)) this.coarea_nume = jsonO.getString(COAREA_NUME);
        if (jsonO.containsKey(BUSS_LINE) && !jsonO.isNull(BUSS_LINE)) this.buss_line = jsonO.getString(BUSS_LINE);
        if (jsonO.containsKey(BUSS_LINE_SEG) && !jsonO.isNull(BUSS_LINE_SEG)) this.buss_line_seg = jsonO.getString(BUSS_LINE_SEG);
        if (jsonO.containsKey(BUSS_LINE_NUME) && !jsonO.isNull(BUSS_LINE_NUME)) this.buss_line_nume = jsonO.getString(BUSS_LINE_NUME);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
     }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);        
        if (this.coarea != null) jsonb.add(COAREA, this.coarea);        
        if (this.coarea_nume != null) jsonb.add(COAREA_NUME, this.coarea_nume);        
        if (this.buss_line != null) jsonb.add(BUSS_LINE, this.buss_line);        
        if (this.buss_line_seg != null) jsonb.add(BUSS_LINE_SEG, this.buss_line_seg);        
        if (this.buss_line_nume != null) jsonb.add(BUSS_LINE_NUME, this.buss_line_nume);        
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

    public String getCoarea() {
        return coarea;
    }

    public void setCoarea(String coarea) {
        this.coarea = coarea;
    }

    public String getCoarea_nume() {
        return coarea_nume;
    }

    public void setCoarea_nume(String coarea_nume) {
        this.coarea_nume = coarea_nume;
    }

    public String getBuss_line() {
        return buss_line;
    }

    public void setBuss_line(String buss_line) {
        this.buss_line = buss_line;
    }

    public String getBuss_line_seg() {
        return buss_line_seg;
    }

    public void setBuss_line_seg(String buss_line_seg) {
        this.buss_line_seg = buss_line_seg;
    }

    public String getBuss_line_nume() {
        return buss_line_nume;
    }

    public void setBuss_line_nume(String buss_line_nume) {
        this.buss_line_nume = buss_line_nume;
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
