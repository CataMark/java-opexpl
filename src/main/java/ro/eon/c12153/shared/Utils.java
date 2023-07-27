package ro.any.c12153.shared;

import java.io.StringReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletResponse;
import ro.any.c12153.dbutils.Constante;

/**
 *
 * @author C12153
 */
public class Utils {
    
    public static final String MEDIA_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MEDIA_CSV = "application/vnd.ms-excel";
    
    public static JsonArray readJsonArrayFromString(String input){
        if (!stringNotEmpty(input)) return Json.createArrayBuilder().build();
        
        try(StringReader sReader = new StringReader(input);
            JsonReader jReader = Json.createReader(sReader);){
            return jReader.readArray();
        }
    }
    
    public static JsonObject readJsonObjectFromString(String input){
        if (!stringNotEmpty(input)) return Json.createObjectBuilder().build();
        
        try(StringReader sReader = new StringReader(input);
            JsonReader jReader = Json.createReader(sReader);){
            return jReader.readObject();
        }
    }
    
    public static JsonObject mapToJson(Map<String, String> input){
        JsonObjectBuilder jsonb =  Json.createObjectBuilder();
        input.entrySet().forEach(x -> {
            if (x.getValue() == null){
                jsonb.addNull(x.getKey());
            } else {
                jsonb.add(x.getKey(), x.getValue());
            }
        });
        return jsonb.build();        
    }
    
    public static String mapToJsonString(Map<String, String> input){
        return mapToJson(input).toString();
    }
    
    public static String mapToJsonStringEncoded(Map<String, String> input){       
        return Base64.getEncoder().encodeToString(
                mapToJson(input).toString().getBytes());
    }
    
    public static Map<String, String> jsonObjectToMap(JsonObject input){
        return input.keySet().stream().collect(Collectors.toMap(
                x -> x,
                x -> input.getString(x)
        ));
    }
    
    public static Map<String, String> jsonStringToMap(String json, boolean encoded){        
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json)): json));
            JsonReader jsonR = Json.createReader(sReader);) {
            
            JsonObject jsonO = jsonR.readObject();
            return jsonObjectToMap(jsonO);
        }
    }
    
    public static JsonArray stringArrayToJsonArray(String[] array){
        JsonArrayBuilder jsonB = Json.createArrayBuilder();
        Arrays.asList(array).forEach(x -> jsonB.add(x));
        return jsonB.build();
    }
    
    public static Date castStringToDate(String dateTime) throws Exception{        
        try{
            return new SimpleDateFormat(Constante.DEFAULT_DATE_FORMAT).parse(dateTime);
        } catch (ParseException ex) {
            return new SimpleDateFormat(Constante.ZERO_MILIS_DATE_FORMAT).parse(dateTime);
        }
    }
    
    public static String castDateToString(Date dateTime){
        return new SimpleDateFormat(Constante.DEFAULT_DATE_FORMAT).format(dateTime);
    }
    
    public static boolean stringNotEmpty(String string){
        return string != null && !string.isEmpty();
    }
    
    public static Optional<String> valueOf(String string){
        if (stringNotEmpty(string)) return  Optional.of(string);
        return Optional.empty();
    }
    
    public static String paramEncode(String param) throws Exception{
        return URLEncoder.encode(
                Base64.getEncoder().encodeToString(param.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8.name()
        );
    }
    
    public static String paramDecode(String param) throws Exception{
        return new String(Base64.getDecoder().decode(
                URLDecoder.decode(param, StandardCharsets.UTF_8.name())),
                StandardCharsets.UTF_8);
    }
    
    public static void throwHttp511(){
        try {
            ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse())
                    .sendError(511);
        } catch (Exception ex) {
            App.log(Logger.getLogger(Utils.class.getName()), Level.SEVERE, null, ex);
        }
    }
    
    public static String toHexString(byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        
        final char[] hex = "0123456789abcdef".toCharArray();

        StringBuilder sb = new StringBuilder(bytes.length << 1);
        for(int i = 0; i < bytes.length; ++i) {
            sb.append(hex[(bytes[i] & 0xf0) >> 4])
                .append(hex[(bytes[i] & 0x0f)]);
        }
        return sb.toString();
    }
}
