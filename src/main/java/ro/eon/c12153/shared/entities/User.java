package ro.any.c12153.shared.entities;

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

//TODO: document class
public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //database table field names
    private static final String UNAME = "uname";
    private static final String NUME = "nume";
    private static final String PRENUME = "prenume";
    private static final String EMAIL = "email";
    private static final String MOD_DE = "mod_de";
    private static final String MOD_TIMP = "mod_timp";
    
    private String uname;
    private String nume;
    private String prenume;
    private String email;
    private String mod_de;
    private Date mod_timp;
    
    public User(){
    }
    
    public User(Map<String, Object> inreg){
        this.uname = (String) inreg.get(UNAME);
        this.nume = (String) inreg.get(NUME);
        this.prenume = (String) inreg.get(PRENUME);
        this.email = (String) inreg.get(EMAIL);
        this.mod_de = (String) inreg.get(MOD_DE);
        this.mod_timp = (Date) inreg.get(MOD_TIMP);
    }
    
    public User(String json, boolean encoded) throws Exception{
        try (StringReader sReader = new StringReader((encoded ? new String(Base64.getDecoder().decode(json), StandardCharsets.UTF_8): json));            
            JsonReader jsonR = Json.createReader(sReader);){
            this.parseJson(jsonR.readObject());
        }
    }
    
    public User(JsonObject json) throws Exception{
        this.parseJson(json);
    }
    
    private void parseJson(JsonObject jsonO) throws Exception{
        if (jsonO.containsKey(UNAME) && !jsonO.isNull(UNAME)) this.uname = jsonO.getString(UNAME);
        if (jsonO.containsKey(NUME) && !jsonO.isNull(NUME)) this.nume = jsonO.getString(NUME);
        if (jsonO.containsKey(PRENUME) && !jsonO.isNull(PRENUME)) this.prenume = jsonO.getString(PRENUME);
        if (jsonO.containsKey(EMAIL) && !jsonO.isNull(EMAIL)) this.email = jsonO.getString(EMAIL);
        if (jsonO.containsKey(MOD_DE) && !jsonO.isNull(MOD_DE)) this.mod_de = jsonO.getString(MOD_DE);
        if (jsonO.containsKey(MOD_TIMP) && !jsonO.isNull(MOD_TIMP))
                this.mod_timp = Utils.castStringToDate(jsonO.getString(MOD_TIMP));
    }
    
    public JsonObject getJson(){
        JsonObjectBuilder jsonb = Json.createObjectBuilder();
        
        if (this.uname != null) jsonb.add(UNAME, this.uname);        
        if (this.nume != null) jsonb.add(NUME, this.nume);        
        if (this.prenume != null) jsonb.add(PRENUME, this.prenume);        
        if (this.email != null) jsonb.add(EMAIL, this.email);        
        if (this.mod_de != null) jsonb.add(MOD_DE, this.mod_de);        
        if (this.mod_timp != null) jsonb.add(MOD_TIMP, Utils.castDateToString(this.mod_timp));
        
        return jsonb.build();
    }
    
    public String getJsonEncoded(){
        return Base64.getEncoder().encodeToString(
                this.getJson().toString().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String getUname() {
        return uname;
    }
    
    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
