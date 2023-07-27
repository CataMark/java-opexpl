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
public class KeyVal implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table fields names
    private static final String ID = "id";
    private static final String CHEIE = "cheie";
    private static final String COAREA = "coarea";
    private static final String BUSS_LINE = "buss_line";
    private static final String HIER = "hier";
    private static final String DATA_SET = "data_set";
    private static final String COST_CENTER = "cost_centre";
    private static final String GEN_DATA_SET  = "gen_data_set";
    private static final String AN = "an";
    private static final String VALOARE = "valoare";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //fields not in database
    private static final String BUSS_LINE_SEG = "buss_line_seg";
    private static final String BUSS_LINE_NUME = "buss_line_nume";
    private static final String COST_CENTER_NUME = "cost_centre_nume";
    
    private String id;
    private Integer cheie;
    private String coarea;
    private String buss_line;
    private String buss_line_seg;
    private String buss_line_nume;
    private String hier;
    private Integer data_set;
    private String cost_center;
    private String cost_center_nume;
    private Integer gen_data_set;
    private Short an;
    private Double valoare;
    private String mod_de;
    private Date mod_timp;
    
    public KeyVal(){
    }
    
    public KeyVal(Map<String, Object> inreg){
        this.id = (String) inreg.get(ID);
        this.cheie = (Integer) inreg.get(CHEIE);
        this.coarea = (String) inreg.get(COAREA);
        this.buss_line = (String) inreg.get(BUSS_LINE);
        this.buss_line_seg = (String) inreg.get(BUSS_LINE_SEG);
        this.buss_line_nume = (String) inreg.get(BUSS_LINE_NUME);
        this.hier = (String) inreg.get(HIER);
        this.data_set = (Integer) inreg.get(DATA_SET);
        this.cost_center = (String) inreg.get(COST_CENTER);
        this.cost_center_nume = (String) inreg.get(COST_CENTER_NUME);
        this.gen_data_set = (Integer) inreg.get(GEN_DATA_SET);
        this.an = (Short) inreg.get(AN);
        this.valoare = (Double) inreg.get(VALOARE);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    
    public KeyVal(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public KeyVal(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(CHEIE) && !jsonO.isNull(CHEIE)) this.cheie = jsonO.getInt(CHEIE);
        if (jsonO.containsKey(COAREA) && !jsonO.isNull(COAREA)) this.coarea = jsonO.getString(COAREA);
        if (jsonO.containsKey(BUSS_LINE) && !jsonO.isNull(BUSS_LINE)) this.buss_line = jsonO.getString(BUSS_LINE);
        if (jsonO.containsKey(BUSS_LINE_SEG) && !jsonO.isNull(BUSS_LINE_SEG)) this.buss_line_seg = jsonO.getString(BUSS_LINE_SEG);
        if (jsonO.containsKey(BUSS_LINE_NUME) && !jsonO.isNull(BUSS_LINE_NUME)) this.buss_line_nume = jsonO.getString(BUSS_LINE_NUME);
        if (jsonO.containsKey(HIER) && !jsonO.isNull(HIER)) this.hier = jsonO.getString(HIER);
        if (jsonO.containsKey(DATA_SET) && !jsonO.isNull(DATA_SET)) this.data_set = jsonO.getInt(DATA_SET);
        if (jsonO.containsKey(COST_CENTER) && !jsonO.isNull(COST_CENTER)) this.cost_center = jsonO.getString(COST_CENTER);
        if (jsonO.containsKey(COST_CENTER_NUME) && !jsonO.isNull(COST_CENTER_NUME)) this.cost_center_nume = jsonO.getString(COST_CENTER_NUME);
        if (jsonO.containsKey(GEN_DATA_SET) && !jsonO.isNull(GEN_DATA_SET)) this.gen_data_set = jsonO.getInt(GEN_DATA_SET);
        if (jsonO.containsKey(AN) && !jsonO.isNull(AN)) this.an = Short.parseShort(jsonO.getJsonNumber(AN).toString());
        if (jsonO.containsKey(VALOARE) && !jsonO.isNull(VALOARE))
            this.valoare = Double.parseDouble(jsonO.getJsonNumber(VALOARE).toString());            
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.id != null) jsonb.add(ID, this.id);        
        if (this.cheie != null) jsonb.add(CHEIE, this.cheie);        
        if (this.coarea != null) jsonb.add(COAREA, this.coarea);        
        if (this.buss_line != null) jsonb.add(BUSS_LINE, this.buss_line);        
        if (this.buss_line_seg != null) jsonb.add(BUSS_LINE_SEG, this.buss_line_seg);        
        if (this.buss_line_nume != null) jsonb.add(BUSS_LINE_NUME, this.buss_line_nume);    
        if (this.hier != null) jsonb.add(HIER, this.hier);        
        if (this.data_set != null) jsonb.add(DATA_SET, this.data_set);        
        if (this.cost_center != null) jsonb.add(COST_CENTER, this.cost_center);        
        if (this.cost_center_nume != null) jsonb.add(COST_CENTER_NUME, this.cost_center_nume);        
        if (this.gen_data_set != null) jsonb.add(GEN_DATA_SET, this.gen_data_set);        
        if (this.an != null) jsonb.add(AN, this.an);        
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
        hash = 29 * hash + Objects.hashCode(this.id);
        hash = 29 * hash + Objects.hashCode(this.cheie);
        hash = 29 * hash + Objects.hashCode(this.coarea);
        hash = 29 * hash + Objects.hashCode(this.buss_line);
        hash = 29 * hash + Objects.hashCode(this.hier);
        hash = 29 * hash + Objects.hashCode(this.cost_center);
        hash = 29 * hash + Objects.hashCode(this.gen_data_set);
        hash = 29 * hash + Objects.hashCode(this.an);
        hash = 29 * hash + Objects.hashCode(this.valoare);
        hash = 29 * hash + Objects.hashCode(this.mod_de);
        hash = 29 * hash + Objects.hashCode(this.mod_timp);
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

    public Integer getCheie() {
        return cheie;
    }

    public void setCheie(Integer cheie) {
        this.cheie = cheie;
    }

    public String getCoarea() {
        return coarea;
    }

    public void setCoarea(String coarea) {
        this.coarea = coarea;
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

    public Integer getGen_data_set() {
        return gen_data_set;
    }

    public void setGen_data_set(Integer gen_data_set) {
        this.gen_data_set = gen_data_set;
    }

    public Short getAn() {
        return an;
    }

    public void setAn(Short an) {
        this.an = an;
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
