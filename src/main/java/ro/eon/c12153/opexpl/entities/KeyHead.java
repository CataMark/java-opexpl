package ro.any.c12153.opexpl.entities;

import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import ro.any.c12153.shared.EntityFactory;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author C12153
 */
public class KeyHead implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //datatabase table fields names
    private static final String ID = "id";
    private static final String NUME = "nume";
    private static final String DESCR = "descr";
    private static final String COAREA = "coarea";
    private static final String TIP = "ktype";
    private static final String BLOCAT = "blocat";
    private static final String DATA_SET = "data_set";
    private static final String HIER = "hier";
    private static final String COST_CENTER = "cost_centre";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //fields not in table
    private static final String COST_CENTER_NUME = "cost_centre_nume";
    private static final String JSON_VALORI = "valori";
    
    private Integer id;
    private String nume;
    private String descr;
    private String coarea;
    private String tip;
    private Boolean blocat;
    private Integer data_set;
    private String hier;
    private String cost_center;
    private String cost_center_nume;
    private String mod_de;
    private Date mod_timp;
    private List<KeyVal> valori;
    
    public KeyHead(){
    }
    
    public KeyHead(Map<String, Object> inreg) throws Exception{
        this.id = (Integer) inreg.get(ID);
        this.nume = (String) inreg.get(NUME);
        this.descr = (String) inreg.get(DESCR);
        this.coarea = (String) inreg.get(COAREA);
        this.tip = (String) inreg.get(TIP);
        this.blocat = (Boolean) inreg.get(BLOCAT);
        this.data_set = (Integer) inreg.get(DATA_SET);
        this.hier = (String) inreg.get(HIER);
        this.cost_center = (String) inreg.get(COST_CENTER);
        this.cost_center_nume = (String) inreg.get(COST_CENTER_NUME);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
        
        Optional<String> lvalori = Optional.ofNullable((String) inreg.get(JSON_VALORI));
        if (lvalori.isPresent()) this.valori = EntityFactory.get(KeyVal.class, lvalori.get());
    }
    
    public KeyHead(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public KeyHead(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getInt(ID);
        if (jsonO.containsKey(NUME) && !jsonO.isNull(NUME)) this.nume = jsonO.getString(NUME);
        if (jsonO.containsKey(DESCR) && !jsonO.isNull(DESCR)) this.descr = jsonO.getString(DESCR);
        if (jsonO.containsKey(COAREA) && !jsonO.isNull(COAREA)) this.coarea = jsonO.getString(COAREA);
        if (jsonO.containsKey(TIP) && !jsonO.isNull(TIP)) this.tip = jsonO.getString(TIP);
        if (jsonO.containsKey(BLOCAT) && !jsonO.isNull(BLOCAT)) this.blocat =  jsonO.getBoolean(BLOCAT);
        if (jsonO.containsKey(DATA_SET) && !jsonO.isNull(DATA_SET)) this.data_set = jsonO.getInt(DATA_SET);
        if (jsonO.containsKey(HIER) && !jsonO.isNull(HIER)) this.hier = jsonO.getString(HIER);
        if (jsonO.containsKey(COST_CENTER) && !jsonO.isNull(COST_CENTER)) this.cost_center = jsonO.getString(COST_CENTER);
        if (jsonO.containsKey(COST_CENTER_NUME) && !jsonO.isNull(COST_CENTER_NUME)) this.cost_center_nume = jsonO.getString(COST_CENTER_NUME);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));

        if (jsonO.containsKey(JSON_VALORI) && !jsonO.isNull(JSON_VALORI))
            this.valori = EntityFactory.get(KeyVal.class, jsonO.getJsonArray(JSON_VALORI));
    }

    public JsonObject getJson(boolean inclChilds){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);        
        if (this.nume != null) jsonb.add(NUME, this.nume);        
        if (this.descr != null) jsonb.add(DESCR, this.descr);        
        if (this.coarea != null) jsonb.add(COAREA, this.coarea);        
        if (this.tip != null) jsonb.add(TIP, this.tip);        
        if (this.blocat != null) jsonb.add(BLOCAT, this.blocat);        
        if (this.data_set != null) jsonb.add(DATA_SET, this.data_set);        
        if (this.hier != null) jsonb.add(HIER, this.hier);        
        if (this.cost_center != null) jsonb.add(COST_CENTER, this.cost_center);        
        if (this.cost_center_nume != null) jsonb.add(COST_CENTER_NUME, this.cost_center_nume);        
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);        
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        if (inclChilds) {
            JsonArrayBuilder jsonv = Json.createArrayBuilder();
            if (this.valori == null || this.valori.isEmpty()){
                jsonb.add(JSON_VALORI, jsonv.build());            
            } else {
                this.valori.forEach(x -> jsonv.add(x.getJson()));
                jsonb.add(JSON_VALORI, jsonv.build());
            }
        }
        
        return jsonb.build();
    }
    
    public String getJsonEncoded(boolean inclChilds){
        return Base64.getEncoder().encodeToString(
                this.getJson(inclChilds).toString().getBytes(StandardCharsets.UTF_8)
        );
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.nume);
        hash = 97 * hash + Objects.hashCode(this.coarea);
        hash = 97 * hash + Objects.hashCode(this.tip);
        hash = 97 * hash + Objects.hashCode(this.data_set);
        hash = 97 * hash + Objects.hashCode(this.hier);
        hash = 97 * hash + Objects.hashCode(this.cost_center);
        hash = 97 * hash + Objects.hashCode(this.mod_de);
        hash = 97 * hash + Objects.hashCode(this.mod_timp);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (this.getClass().isInstance(o)) return o.hashCode() == this.hashCode();
        return false;
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

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getCoarea() {
        return coarea;
    }

    public void setCoarea(String coarea) {
        this.coarea = coarea;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public Boolean getBlocat() {
        return blocat;
    }

    public void setBlocat(Boolean blocat) {
        this.blocat = blocat;
    }

    public Integer getData_set() {
        return data_set;
    }

    public void setData_set(Integer data_set) {
        this.data_set = data_set;
    }

    public String getHier() {
        return hier;
    }

    public void setHier(String hier) {
        this.hier = hier;
    }

    public String getCost_center() {
        return cost_center;
    }

    public void setCost_center(String cost_center) {
        this.cost_center = cost_center;
    }

    public String getCost_center_nume() {
        return cost_center_nume;
    }

    public void setCost_center_nume(String cost_center_nume) {
        this.cost_center_nume = cost_center_nume;
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

    public List<KeyVal> getValori() {
        return valori;
    }

    public void setValori(List<KeyVal> valori) {
        this.valori = valori;
    }
}
