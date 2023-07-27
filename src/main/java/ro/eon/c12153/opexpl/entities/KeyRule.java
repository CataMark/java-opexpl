package ro.any.c12153.opexpl.entities;

import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author C12153
 */
public class KeyRule implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String CHEIE = "cheie";
    private static final String MEDIE_POND = "medie_pond";
    private static final String CHEI = "chei_json";
    private static final String COST_CENTRE = "cost_centre_json";
    private static final String OPEX_CATEG = "opex_categ_json";
    private static final String IC_PART = "ic_part_json";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    private Integer cheie;
    private Boolean medie_pond;
    private List<Integer> chei;
    private List<String> cost_centre;
    private List<SimpleEntry<String, Integer>> opex_categ;
    private List<String> ic_part;
    private String mod_de;
    private Date mod_timp;
    
    public KeyRule(){
        this.chei = new ArrayList<>();
        this.cost_centre = new ArrayList<>();
        this.opex_categ = new ArrayList<>();
        this.ic_part = new ArrayList<>();
    }
    
    public KeyRule(Map<String, Object> inreg){
        this.cheie = (Integer) inreg.get(CHEIE);
        this.medie_pond = (Boolean) inreg.get(MEDIE_POND);
        this.setChei((String) inreg.get(CHEI));
        this.setCost_Centre((String) inreg.get(COST_CENTRE));
        this.setOpex_categ((String) inreg.get(OPEX_CATEG));
        this.setIc_part((String) inreg.get(IC_PART));
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public KeyRule(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public KeyRule(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(CHEIE) && !jsonO.isNull(CHEIE)) this.cheie = jsonO.getInt(CHEIE);
        if (jsonO.containsKey(MEDIE_POND) && !jsonO.isNull(MEDIE_POND)) this.medie_pond = jsonO.getBoolean(MEDIE_POND);
        if (jsonO.containsKey(CHEI) && !jsonO.isNull(CHEI)) this.setChei(jsonO.getJsonArray(CHEI));
        if (jsonO.containsKey(COST_CENTRE) && !jsonO.isNull(COST_CENTRE)) this.setCost_centre(jsonO.getJsonArray(COST_CENTRE));
        if (jsonO.containsKey(OPEX_CATEG) && !jsonO.isNull(OPEX_CATEG)) this.setOpex_categ(jsonO.getJsonArray(OPEX_CATEG));
        if (jsonO.containsKey(IC_PART) && !jsonO.isNull(IC_PART)) this.setIc_part(jsonO.getJsonArray(IC_PART));
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));        
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb = Json.createObjectBuilder();
        
        if (this.cheie != null) jsonb.add(CHEIE, this.cheie);        
        if (this.medie_pond != null) jsonb.add(MEDIE_POND, this.medie_pond);        
        jsonb.add(CHEI, this.getCheiJson());
        jsonb.add(COST_CENTRE, this.getCost_centreJson());
        jsonb.add(OPEX_CATEG, this.getOpex_categJson());
        jsonb.add(IC_PART, this.getIc_partJson());        
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);        
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        return jsonb.build();
    }
    
    public String getJsonEncoded(){
        return Base64.getEncoder().encodeToString(
                this.getJson().toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    public Integer getCheie() {
        return cheie;
    }

    public void setCheie(Integer cheie) {
        this.cheie = cheie;
    }

    public Boolean getMedie_pond() {
        return medie_pond;
    }

    public void setMedie_pond(Boolean medie_pond) {
        this.medie_pond = medie_pond;
    }

    public List<Integer> getChei() {
        return chei;
    }
    
    public JsonArray getCheiJson(){
        JsonArrayBuilder jsonB = Json.createArrayBuilder();        
        if (this.chei == null || this.chei.isEmpty()) return jsonB.build();
        
        this.chei.forEach(x -> jsonB.add(x));
        return jsonB.build();
    }

    public void setChei(List<Integer> chei) {
        this.chei = chei;
    }
    
    public final void setChei(String chei){
        if (this.chei == null || !this.chei.isEmpty()) this.chei = new ArrayList<>();
        if (chei == null || chei.isEmpty()) return;
        
        try(StringReader sReader = new StringReader(chei);
            JsonReader jReader = Json.createReader(sReader);){
            
            this.setChei(jReader.readArray());
        }
    }
    
    public final void setChei(JsonArray chei){
        if (this.chei == null || !this.chei.isEmpty()) this.chei = new ArrayList<>();
        if (chei == null || chei.isEmpty()) return;
            
        for (int i = 0; i < chei.size(); i++){
            this.chei.add(chei.getInt(i));
        }
    }

    public List<String> getCost_centre() {
        return cost_centre;
    }
    
    public JsonArray getCost_centreJson(){
        JsonArrayBuilder jsonB = Json.createArrayBuilder();
        if (this.cost_centre == null || this.cost_centre.isEmpty()) return jsonB.build();
        
        this.cost_centre.forEach(x -> jsonB.add(x));
        return jsonB.build();
    }

    public void setCost_centre(List<String> cost_centre) {
        this.cost_centre = cost_centre;
    }
    
    public final void setCost_Centre(String cost_centre){
        if (this.cost_centre == null || !this.cost_centre.isEmpty()) this.cost_centre = new ArrayList<>();
        if (cost_centre == null || cost_centre.isEmpty()) return;
        
        try(StringReader sReader = new StringReader(cost_centre);
            JsonReader jReader = Json.createReader(sReader);){
            
            this.setCost_centre(jReader.readArray());
        }
    }
    
    public final void setCost_centre(JsonArray cost_centre){
        if (this.cost_centre == null || !this.cost_centre.isEmpty()) this.cost_centre = new ArrayList<>();
        if (cost_centre == null || cost_centre.isEmpty()) return;
            
        for (int i = 0; i < cost_centre.size(); i++){
            this.cost_centre.add(cost_centre.getString(i));
        }
    }

    public List<SimpleEntry<String, Integer>> getOpex_categ() {
        return opex_categ;
    }
    
    public JsonArray getOpex_categJson(){
        JsonArrayBuilder jsonB = Json.createArrayBuilder();
        if (this.opex_categ == null || this.opex_categ.isEmpty()) return jsonB.build();
        
        this.opex_categ.forEach(x -> {
            JsonObjectBuilder jsonO = Json.createObjectBuilder();
            
            if (x.getKey() == null || x.getKey().isEmpty()){
                jsonO.addNull("cost_driver");
            } else {
                jsonO.add("cost_driver", x.getKey());
            }
            
            if (x.getValue() == null){
                jsonO.addNull("opex_categ");
            } else {
                jsonO.add("opex_categ", x.getValue());
            }
            
            jsonB.add(jsonO.build());
        });
        return jsonB.build();
    }

    public void setOpex_categ(List<SimpleEntry<String, Integer>> opex_categ) {
        this.opex_categ = opex_categ;
    }
    
    public final void setOpex_categ(String opex_categ){
        if (this.opex_categ == null || !this.opex_categ.isEmpty()) this.opex_categ = new ArrayList<>();
        if (opex_categ == null || opex_categ.isEmpty()) return;
        
        try(StringReader sReader = new StringReader(opex_categ);
            JsonReader jReader = Json.createReader(sReader);){
            
            this.setOpex_categ(jReader.readArray());
        }
    }
    
    public final void setOpex_categ(JsonArray opex_categ){
        if (this.opex_categ == null || !this.opex_categ.isEmpty()) this.opex_categ = new ArrayList<>();
        if (opex_categ == null || opex_categ.isEmpty()) return;
            
        for (int i = 0; i < opex_categ.size(); i++){
            JsonObject jsonO = opex_categ.getJsonObject(i);                
            this.opex_categ.add(new SimpleEntry<>(
                    (jsonO.containsKey("cost_driver") && !jsonO.isNull("cost_driver") ? jsonO.getString("cost_driver") : null),
                    (jsonO.containsKey("opex_categ") && !jsonO.isNull("opex_categ") ? jsonO.getInt("opex_categ") : null)
            ));
        }
    }

    public List<String> getIc_part() {
        return ic_part;
    }
    
    public JsonArray getIc_partJson(){
        JsonArrayBuilder jsonB = Json.createArrayBuilder();
        if (this.ic_part == null || this.ic_part.isEmpty()) return jsonB.build();
        
        this.ic_part.forEach(x -> {
            if (x == null || x.isEmpty()){
                jsonB.addNull();
            } else {
                jsonB.add(x);
            }
        });
        return jsonB.build();
    }

    public void setIc_part(List<String> ic_part) {
        this.ic_part = ic_part;
    }
    
    public final void setIc_part(String ic_part){
        if(this.ic_part == null || !this.ic_part.isEmpty()) this.ic_part = new ArrayList<>();
        if (ic_part == null || ic_part.isEmpty()) return;
        
        try(StringReader sReader = new StringReader(ic_part);
            JsonReader jReader = Json.createReader(sReader);){
            
            this.setIc_part(jReader.readArray());
        }
    }
    
    public final void setIc_part(JsonArray ic_part){
        if(this.ic_part == null || !this.ic_part.isEmpty()) this.ic_part = new ArrayList<>();
        if (ic_part == null || ic_part.isEmpty()) return;
            
        for (int i = 0; i < ic_part.size(); i++){
            if (ic_part.get(i) == JsonValue.NULL){
                this.ic_part.add(null);
            } else {
                this.ic_part.add(ic_part.getString(i));
            }
        }
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
