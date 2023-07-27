package ro.any.c12153.shared;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 *
 * @author C12153
 */
public class EntityFactory {
    
    public static <T> List<T> get(Class<T> clasa, String jsonArray) throws Exception{
        try (StringReader sreader = new StringReader(jsonArray);
            JsonReader jreader = Json.createReader(sreader);) {            
            return get(clasa, jreader.readArray());
        }
    }
    
    public static <T> List<T> get(Class<T> clasa, JsonArray jsonArray) throws Exception{
        List<T> rezultat = new ArrayList<>();
        for (JsonValue j : jsonArray) rezultat.add(
            clasa.getConstructor(JsonObject.class).newInstance((JsonObject) j)
        );
        return rezultat;
    }
}
