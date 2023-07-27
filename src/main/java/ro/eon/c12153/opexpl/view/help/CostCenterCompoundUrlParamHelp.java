package ro.any.c12153.opexpl.view.help;

import java.io.Serializable;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

/**
 *
 * @author C12153
 */
public class CostCenterCompoundUrlParamHelp implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private static final String CCENTER_ID = "ccenter_id";
    private static final String LEAF = "leaf";
    
    private String ccenter_id;
    private Boolean leaf;

    public CostCenterCompoundUrlParamHelp() {
    }

    public CostCenterCompoundUrlParamHelp(String ccenter_id, Boolean leaf) {
        this.ccenter_id = ccenter_id;
        this.leaf = leaf;
    }
    
    public CostCenterCompoundUrlParamHelp(String json){
        try (StringReader sReader = new StringReader(json);            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    private void parseJson(JsonObject jsonO){
        if (jsonO.containsKey(CCENTER_ID) && !jsonO.isNull(CCENTER_ID)) this.ccenter_id = jsonO.getString(CCENTER_ID);
        if (jsonO.containsKey(LEAF) && !jsonO.isNull(LEAF)) this.leaf = jsonO.getBoolean(LEAF);
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb = Json.createObjectBuilder();
        if (this.ccenter_id != null) jsonb.add(CCENTER_ID, this.ccenter_id);
        if (this.leaf != null) jsonb.add(LEAF, this.leaf);
        return jsonb.build();
    }

    public String getCcenter_id() {
        return ccenter_id;
    }

    public void setCcenter_id(String ccenter_id) {
        this.ccenter_id = ccenter_id;
    }

    public Boolean getLeaf() {
        return leaf;
    }

    public void setLeaf(Boolean leaf) {
        this.leaf = leaf;
    }
}
