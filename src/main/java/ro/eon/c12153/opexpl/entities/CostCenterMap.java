
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
public class CostCenterMap implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String ID = "id";
    private static final String HIER = "hier";
    private static final String DATA_SET = "data_set";
    private static final String RECEIVER = "receiver";
    private static final String SENDER = "sender";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    //fields not in table
    private static final String ROW_ID = "row_id";
    private static final String RECEIVER_NUME = "receiver_nume";
    private static final String SENDER_NUME = "sender_nume";
    private static final String VAL_COMPAR_SET = "val_compar_set";
    private static final String VAL_ACTUAL_SET = "val_actual_set";
    
    private Long row_id;
    private String id;
    private String hier;
    private Integer data_set;
    private String receiver;
    private String receiver_nume;
    private String sender;
    private String sender_nume;
    private Double val_compar_set;
    private Double val_actual_set;
    private String mod_de;
    private Date mod_timp;
    
    public CostCenterMap(){
    }
    
    public CostCenterMap(Map<String, Object> inreg){
        this.row_id = (Long) inreg.get(ROW_ID);
        this.id = (String) inreg.get(ID);
        this.hier = (String) inreg.get(HIER);
        this.data_set = (Integer) inreg.get(DATA_SET);
        this.receiver = (String) inreg.get(RECEIVER);
        this.receiver_nume = (String) inreg.get(RECEIVER_NUME);
        this.sender = (String) inreg.get(SENDER);
        this.sender_nume = (String) inreg.get(SENDER_NUME);
        this.val_compar_set = (Double) inreg.get(VAL_COMPAR_SET);
        this.val_actual_set = (Double) inreg.get(VAL_ACTUAL_SET);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public CostCenterMap(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public CostCenterMap(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(ROW_ID) && !jsonO.isNull(ROW_ID)) this.row_id = Long.valueOf(jsonO.getJsonNumber(ROW_ID).toString());
        if (jsonO.containsKey(ID) && !jsonO.isNull(ID)) this.id = jsonO.getString(ID);
        if (jsonO.containsKey(HIER) && !jsonO.isNull(HIER)) this.hier = jsonO.getString(HIER);
        if (jsonO.containsKey(DATA_SET) && !jsonO.isNull(DATA_SET)) this.data_set = jsonO.getInt(DATA_SET);
        if (jsonO.containsKey(RECEIVER) && !jsonO.isNull(RECEIVER)) this.receiver = jsonO.getString(RECEIVER);
        if (jsonO.containsKey(RECEIVER_NUME) && !jsonO.isNull(RECEIVER_NUME)) this.receiver_nume = jsonO.getString(RECEIVER_NUME);
        if (jsonO.containsKey(SENDER) && !jsonO.isNull(SENDER)) this.sender = jsonO.getString(SENDER);
        if (jsonO.containsKey(SENDER_NUME) && !jsonO.isNull(SENDER_NUME)) this.sender_nume = jsonO.getString(SENDER_NUME);
        if (jsonO.containsKey(VAL_COMPAR_SET) && !jsonO.isNull(VAL_COMPAR_SET))
            this.val_compar_set = Double.parseDouble(jsonO.getJsonNumber(VAL_COMPAR_SET).toString());
        if (jsonO.containsKey(VAL_ACTUAL_SET) && !jsonO.isNull(VAL_ACTUAL_SET))
            this.val_actual_set = Double.parseDouble(jsonO.getJsonNumber(VAL_ACTUAL_SET).toString());
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        
        if (this.row_id != null) jsonb.add(ROW_ID, this.row_id);
        if (this.id != null) jsonb.add(ID, this.id);
        if (this.hier != null) jsonb.add(HIER, this.hier);
        if (this.data_set != null) jsonb.add(DATA_SET, this.data_set);
        if (this.receiver != null) jsonb.add(RECEIVER, this.receiver);
        if (this.receiver_nume != null) jsonb.add(RECEIVER_NUME, this.receiver_nume);
        if (this.sender != null) jsonb.add(SENDER, this.sender);
        if (this.sender_nume != null) jsonb.add(SENDER_NUME, this.sender_nume);
        if (this.val_compar_set != null) jsonb.add(VAL_COMPAR_SET, this.val_compar_set);
        if (this.val_actual_set != null) jsonb.add(VAL_ACTUAL_SET, this.val_actual_set);
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        return jsonb.build();
    }
    
    public String getJsonEncoded(){
        return Base64.getEncoder().encodeToString(
                this.getJson().toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    public Long getRow_id() {
        return row_id;
    }

    public void setRow_id(Long row_id) {
        this.row_id = row_id;
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

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver_nume() {
        return receiver_nume;
    }

    public void setReceiver_nume(String receiver_nume) {
        this.receiver_nume = receiver_nume;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender_nume() {
        return sender_nume;
    }

    public void setSender_nume(String sender_nume) {
        this.sender_nume = sender_nume;
    }

    public Double getVal_compar_set() {
        return val_compar_set;
    }

    public void setVal_compar_set(Double val_compar_set) {
        this.val_compar_set = val_compar_set;
    }

    public Double getVal_actual_set() {
        return val_actual_set;
    }

    public void setVal_actual_set(Double val_actual_set) {
        this.val_actual_set = val_actual_set;
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
