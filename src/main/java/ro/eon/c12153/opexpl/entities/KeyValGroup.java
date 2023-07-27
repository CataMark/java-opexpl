package ro.any.c12153.opexpl.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import ro.any.c12153.shared.EntityFactory;

/**
 *
 * @author C12153
 */
public class KeyValGroup extends CostCenter implements Serializable{
    //fields not in table
        private static final String JSON_VALORI = "valori";
        
        private List<KeyVal> valori;

        public KeyValGroup() {
        }
        
        public KeyValGroup(Map<String, Object> inreg) throws Exception{
            super(inreg);
            
            Optional<String> lvalori = Optional.ofNullable((String) inreg.get(JSON_VALORI));
            if (lvalori.isPresent()) this.valori = EntityFactory.get(KeyVal.class, lvalori.get());
        }
        
        public KeyValGroup(JsonObject json) throws Exception{
            super(json);
            this.parseJson(json);
        }
        
        private void parseJson(JsonObject jsonO) throws Exception{
            if (jsonO.containsKey(JSON_VALORI) && !jsonO.isNull(JSON_VALORI))
                this.valori = EntityFactory.get(KeyVal.class, jsonO.getJsonArray(JSON_VALORI));
        }
        
        @Override
        public JsonObject getJson(){
            JsonObjectBuilder jsonb =  Json.createObjectBuilder();
            
            super.getJson().entrySet().forEach(x -> {
                jsonb.add(x.getKey(), x.getValue());
            });
            
            JsonArrayBuilder jsonv = Json.createArrayBuilder();
            if (this.valori == null || this.valori.isEmpty()){
                jsonb.add(JSON_VALORI, jsonv.build());            
            } else {
                this.valori.forEach(x -> jsonv.add(x.getJson()));
                jsonb.add(JSON_VALORI, jsonv.build());
            }

            return jsonb.build();
        }

        public List<KeyVal> getValori() {
            return valori;
        }

        public void setValori(List<KeyVal> valori) {
            this.valori = valori;
        }
}
