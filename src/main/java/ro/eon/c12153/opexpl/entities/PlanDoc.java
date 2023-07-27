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
public class PlanDoc implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table fields names
    private static final String ID = "id";
    private static final String COAREA = "coarea";
    private static final String DESCR = "descr";
    private static final String HIER = "hier";
    private static final String DATA_SET = "data_set";
    private static final String COST_CENTER = "cost_centre";
    private static final String CHEIE = "cheie";
    private static final String OPEX_CATEG = "opex_categ";
    private static final String IC_PART = "ic_part";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //fields not in table
    private static final String COST_CENTER_NUME = "cost_centre_nume";
    private static final String COST_CENTER_SUPER = "cost_centre_super";
    private static final String COST_CENTER_BLOCAT = "cost_centre_blocat";
    private static final String COST_CENTER_LEAF = "cost_centre_leaf";
    private static final String COST_CENTER_NIVEL = "cost_centre_nivel";
    private static final String CHEIE_NUME = "cheie_nume";
    private static final String CHEIE_BLOCAT = "cheie_blocat";
    private static final String COST_DRIVER = "cost_driver";
    private static final String COST_DRIVER_NUME = "cost_driver_nume";
    private static final String COST_DRIVER_CENTRAL = "cost_driver_central";
    private static final String OPEX_CATEG_NUME = "opex_categ_nume";
    private static final String OPEX_CATEG_BLOCAT = "opex_categ_blocat";
    private static final String IC_PART_NUME = "ic_part_nume";
    private static final String IC_PART_BLOCAT = "ic_part_blocat";
    private static final String JSON_VALORI = "valori";
    
    private String id;
    private String coarea;
    private String descr;
    private String hier;
    private Integer data_set;
    private String cost_center;
    private String cost_center_nume;
    private String cost_center_super;
    private Boolean cost_center_blocat;
    private Boolean cost_center_leaf;
    private Short cost_center_nivel;
    private Integer cheie;
    private String cheie_nume;
    private Boolean cheie_blocat;
    private String cost_driver;
    private String cost_driver_nume;
    private Boolean cost_driver_central;
    private Integer opex_categ;
    private String opex_categ_nume;
    private Boolean opex_categ_blocat;
    private String ic_part;
    private String ic_part_nume;
    private Boolean ic_part_blocat;
    private String mod_de;
    private Date mod_timp;
    private List<PlanVal> valori;
    
    public PlanDoc(){
    }
    
    public PlanDoc(Map<String, Object> inreg) throws Exception{
        this.id = (String) inreg.get(ID);
        this.coarea = (String) inreg.get(COAREA);
        this.descr = (String) inreg.get(DESCR);
        this.hier = (String) inreg.get(HIER);
        this.data_set = (Integer) inreg.get(DATA_SET);
        this.cost_center = (String) inreg.get(COST_CENTER);
        this.cost_center_nume = (String) inreg.get(COST_CENTER_NUME);
        this.cost_center_super = (String) inreg.get(COST_CENTER_SUPER);
        this.cost_center_blocat = (Boolean) inreg.get(COST_CENTER_BLOCAT);
        this.cost_center_leaf = (Boolean) inreg.get(COST_CENTER_LEAF);
        this.cost_center_nivel = (Short) inreg.get(COST_CENTER_NIVEL);
        this.cheie = (Integer) inreg.get(CHEIE);
        this.cheie_nume = (String) inreg.get(CHEIE_NUME);
        this.cheie_blocat = (Boolean) inreg.get(CHEIE_BLOCAT);
        this.cost_driver = (String) inreg.get(COST_DRIVER);
        this.cost_driver_nume = (String) inreg.get(COST_DRIVER_NUME);
        this.cost_driver_central = (Boolean) inreg.get(COST_DRIVER_CENTRAL);
        this.opex_categ = (Integer) inreg.get(OPEX_CATEG);
        this.opex_categ_nume = (String) inreg.get(OPEX_CATEG_NUME);
        this.opex_categ_blocat = (Boolean) inreg.get(OPEX_CATEG_BLOCAT);
        this.ic_part = (String) inreg.get(IC_PART);
        this.ic_part_nume = (String) inreg.get(IC_PART_NUME);
        this.ic_part_blocat = (Boolean) inreg.get(IC_PART_BLOCAT);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
        
        Optional<String> lvalori = Optional.ofNullable((String) inreg.get(JSON_VALORI));
        if (lvalori.isPresent()) this.valori = EntityFactory.get(PlanVal.class, lvalori.get());
    }
    
    public PlanDoc(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public PlanDoc(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(COAREA) && !jsonO.isNull(COAREA)) this.coarea = jsonO.getString(COAREA);
        if (jsonO.containsKey(DESCR) && !jsonO.isNull(DESCR)) this.descr = jsonO.getString(DESCR);
        if (jsonO.containsKey(HIER) && !jsonO.isNull(HIER)) this.hier = jsonO.getString(HIER);
        if (jsonO.containsKey(DATA_SET) && !jsonO.isNull(DATA_SET)) this.data_set = jsonO.getInt(DATA_SET);
        if (jsonO.containsKey(COST_CENTER) && !jsonO.isNull(COST_CENTER)) this.cost_center = jsonO.getString(COST_CENTER);
        if (jsonO.containsKey(COST_CENTER_NUME) && !jsonO.isNull(COST_CENTER_NUME)) this.cost_center_nume = jsonO.getString(COST_CENTER_NUME);
        if (jsonO.containsKey(COST_CENTER_SUPER) && !jsonO.isNull(COST_CENTER_SUPER)) this.cost_center_super = jsonO.getString(COST_CENTER_SUPER);
        if (jsonO.containsKey(COST_CENTER_BLOCAT) && !jsonO.isNull(COST_CENTER_BLOCAT)) this.cost_center_blocat = jsonO.getBoolean(COST_CENTER_BLOCAT);
        if (jsonO.containsKey(COST_CENTER_LEAF) && !jsonO.isNull(COST_CENTER_LEAF)) this.cost_center_leaf = jsonO.getBoolean(COST_CENTER_LEAF);
        if (jsonO.containsKey(COST_CENTER_NIVEL) && !jsonO.isNull(COST_CENTER_NIVEL)) this.cost_center_nivel = Short.valueOf(jsonO.getJsonNumber(COST_CENTER_NIVEL).toString());
        if (jsonO.containsKey(CHEIE) && !jsonO.isNull(CHEIE)) this.cheie = jsonO.getInt(CHEIE);
        if (jsonO.containsKey(CHEIE_NUME) && !jsonO.isNull(CHEIE_NUME)) this.cheie_nume = jsonO.getString(CHEIE_NUME);
        if (jsonO.containsKey(CHEIE_BLOCAT) && !jsonO.isNull(CHEIE_BLOCAT)) this.cheie_blocat = jsonO.getBoolean(CHEIE_BLOCAT);
        if (jsonO.containsKey(COST_DRIVER) && !jsonO.isNull(COST_DRIVER)) this.cost_driver = jsonO.getString(COST_DRIVER);
        if (jsonO.containsKey(COST_DRIVER_NUME) && !jsonO.isNull(COST_DRIVER_NUME)) this.cost_driver_nume = jsonO.getString(COST_DRIVER_NUME);
        if (jsonO.containsKey(COST_DRIVER_CENTRAL) && !jsonO.isNull(COST_DRIVER_CENTRAL)) this.cost_driver_central = jsonO.getBoolean(COST_DRIVER_CENTRAL);
        if (jsonO.containsKey(OPEX_CATEG) && !jsonO.isNull(OPEX_CATEG)) this.opex_categ = jsonO.getInt(OPEX_CATEG);
        if (jsonO.containsKey(OPEX_CATEG_NUME) && !jsonO.isNull(OPEX_CATEG_NUME)) this.opex_categ_nume = jsonO.getString(OPEX_CATEG_NUME);
        if (jsonO.containsKey(OPEX_CATEG_BLOCAT) && !jsonO.isNull(OPEX_CATEG_BLOCAT)) this.opex_categ_blocat = jsonO.getBoolean(OPEX_CATEG_BLOCAT);
        if (jsonO.containsKey(IC_PART) && !jsonO.isNull(IC_PART)) this.ic_part = jsonO.getString(IC_PART);
        if (jsonO.containsKey(IC_PART_NUME) && !jsonO.isNull(IC_PART_NUME)) this.ic_part_nume = jsonO.getString(IC_PART_NUME);
        if (jsonO.containsKey(IC_PART_BLOCAT) && !jsonO.isNull(IC_PART_BLOCAT)) this.ic_part_blocat = jsonO.getBoolean(IC_PART_BLOCAT);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
        
        if (jsonO.containsKey(JSON_VALORI) && !jsonO.isNull(JSON_VALORI))
            this.valori = EntityFactory.get(PlanVal.class, jsonO.getJsonArray(JSON_VALORI));
    }
    
    public JsonObject getJson(boolean inclChilds){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);        
        if (this.coarea != null) jsonb.add(COAREA, this.coarea);        
        if (this.descr != null) jsonb.add(DESCR, this.descr);        
        if (this.hier != null) jsonb.add(HIER, this.hier);
        if (this.data_set != null) jsonb.add(DATA_SET, this.data_set);
        if (this.cost_center != null) jsonb.add(COST_CENTER, this.cost_center);
        if (this.cost_center_nume != null) jsonb.add(COST_CENTER_NUME, this.cost_center_nume);
        if (this.cost_center_super != null) jsonb.add(COST_CENTER_SUPER, this.cost_center_super);
        if (this.cost_center_blocat != null) jsonb.add(COST_CENTER_BLOCAT, this.cost_center_blocat);
        if (this.cost_center_leaf != null) jsonb.add(COST_CENTER_LEAF, this.cost_center_leaf);
        if (this.cost_center_nivel != null) jsonb.add(COST_CENTER_NIVEL, this.cost_center_nivel);
        if (this.cheie != null) jsonb.add(CHEIE, this.cheie);
        if (this.cheie_nume != null) jsonb.add(CHEIE_NUME, this.cheie_nume);
        if (this.cheie_blocat != null) jsonb.add(CHEIE_BLOCAT, this.cheie_blocat);
        if (this.cost_driver != null) jsonb.add(COST_DRIVER, this.cost_driver);
        if (this.cost_driver_nume != null) jsonb.add(COST_DRIVER_NUME, this.cost_driver_nume);
        if (this.cost_driver_central != null) jsonb.add(COST_DRIVER_CENTRAL, this.cost_driver_central);
        if (this.opex_categ != null) jsonb.add(OPEX_CATEG, this.opex_categ);
        if (this.opex_categ_nume != null) jsonb.add(OPEX_CATEG_NUME, this.opex_categ_nume);
        if (this.opex_categ_blocat != null) jsonb.add(OPEX_CATEG_BLOCAT, this.opex_categ_blocat);
        if (this.ic_part != null) jsonb.add(IC_PART, this.ic_part);
        if (this.ic_part_nume != null) jsonb.add(IC_PART_NUME, this.ic_part_nume);
        if (this.ic_part_blocat != null) jsonb.add(IC_PART_BLOCAT, this.ic_part_blocat);
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
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.coarea);
        hash = 53 * hash + Objects.hashCode(this.descr);
        hash = 53 * hash + Objects.hashCode(this.hier);
        hash = 53 * hash + Objects.hashCode(this.data_set);
        hash = 53 * hash + Objects.hashCode(this.cost_center);
        hash = 53 * hash + Objects.hashCode(this.cheie);
        hash = 53 * hash + Objects.hashCode(this.cost_driver);
        hash = 53 * hash + Objects.hashCode(this.opex_categ);
        hash = 53 * hash + Objects.hashCode(this.ic_part);
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

    public String getCoarea() {
        return coarea;
    }

    public void setCoarea(String coarea) {
        this.coarea = coarea;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
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

    public String getCost_center_super() {
        return cost_center_super;
    }

    public void setCost_center_super(String cost_center_super) {
        this.cost_center_super = cost_center_super;
    }

    public Boolean getCost_center_blocat() {
        return cost_center_blocat;
    }

    public void setCost_center_blocat(Boolean cost_center_blocat) {
        this.cost_center_blocat = cost_center_blocat;
    }

    public Boolean getCost_center_leaf() {
        return cost_center_leaf;
    }

    public void setCost_center_leaf(Boolean cost_center_leaf) {
        this.cost_center_leaf = cost_center_leaf;
    }

    public Short getCost_center_nivel() {
        return cost_center_nivel;
    }

    public void setCost_center_nivel(Short cost_center_nivel) {
        this.cost_center_nivel = cost_center_nivel;
    }

    public Integer getCheie() {
        return cheie;
    }

    public void setCheie(Integer cheie) {
        this.cheie = cheie;
    }

    public String getCheie_nume() {
        return cheie_nume;
    }

    public void setCheie_nume(String cheie_nume) {
        this.cheie_nume = cheie_nume;
    }

    public Boolean getCheie_blocat() {
        return cheie_blocat;
    }

    public void setCheie_blocat(Boolean cheie_blocat) {
        this.cheie_blocat = cheie_blocat;
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

    public Boolean getCost_driver_central() {
        return cost_driver_central;
    }

    public void setCost_driver_central(Boolean cost_driver_central) {
        this.cost_driver_central = cost_driver_central;
    }

    public Integer getOpex_categ() {
        return opex_categ;
    }

    public void setOpex_categ(Integer opex_categ) {
        this.opex_categ = opex_categ;
    }

    public String getOpex_categ_nume() {
        return opex_categ_nume;
    }

    public void setOpex_categ_nume(String opex_categ_nume) {
        this.opex_categ_nume = opex_categ_nume;
    }

    public Boolean getOpex_categ_blocat() {
        return opex_categ_blocat;
    }

    public void setOpex_categ_blocat(Boolean opex_categ_blocat) {
        this.opex_categ_blocat = opex_categ_blocat;
    }

    public String getIc_part() {
        return ic_part;
    }

    public void setIc_part(String ic_part) {
        this.ic_part = ic_part;
    }

    public String getIc_part_nume() {
        return ic_part_nume;
    }

    public void setIc_part_nume(String ic_part_nume) {
        this.ic_part_nume = ic_part_nume;
    }

    public Boolean getIc_part_blocat() {
        return ic_part_blocat;
    }

    public void setIc_part_blocat(Boolean ic_part_blocat) {
        this.ic_part_blocat = ic_part_blocat;
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

    public List<PlanVal> getValori() {
        return valori;
    }

    public void setValori(List<PlanVal> valori) {
        this.valori = valori;
    }
}
