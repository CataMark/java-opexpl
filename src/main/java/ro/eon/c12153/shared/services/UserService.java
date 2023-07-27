package ro.any.c12153.shared.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Types;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
public class UserService {
    
    public static Optional<User> getByUname(String uname, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(uname, Types.VARCHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from portal.fnc_user_get_by_uname(?);", Optional.of(parametri)).stream()
                .map(User::new)
                .findFirst();
    }
    
    private static String hashPass(String pass, Optional<Integer> runs) throws Exception{
        int times = 1;
        if (runs.isPresent() && runs.get() > 1) times = runs.get();
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = pass.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < times; i++){
            digest = md.digest(digest);
        }
        
        return Utils.toHexString(digest);
    }
    
    public static String pseudoPassGenerator(String userId, String passPhrase) throws Exception{
        String key = userId.concat(passPhrase)
                .concat(Base64.getEncoder().encodeToString(new StringBuilder().append(userId).reverse().toString().getBytes(StandardCharsets.UTF_8)));
        return hashPass(key, Optional.of((int) 2));
    }
    
    public static String changePass(String changeKid, String pass, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("upass", new ParamSql(hashPass(pass, Optional.empty()), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        parametri.put("uname", new ParamSql(changeKid, Types.VARCHAR));

        return App.getConn(userId)
                .executeCallableStatement("{call portal.prc_user_update_pass(?,?,?)}", parametri);
    }
    
    public static void newSessionRecord(String sessid, String userId, String path) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("sess", new ParamSql(sessid, Types.VARCHAR));
        parametri.put("uname", new ParamSql(userId, Types.VARCHAR));
        parametri.put("app_path", new ParamSql(path, Types.VARCHAR));

        App.getConn(userId)
                .executeCallableStatement("{call portal.prc_user_insert_new_session(?,?,?)}", parametri);
    }
    
    public static List<User> getLastSessionsByApp(String path, String userId) throws Exception{            
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("app_path", new ParamSql(path, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call portal.prc_user_get_last_session_for_app(?)}", parametri).stream()
                .map(User::new)
                .collect(Collectors.toList());
    }
    
    public static List<User> getByGroup(String group, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("ugroup", new ParamSql(group, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call portal.prc_user_get_list_by_group(?)}", parametri).stream()
                .map(User::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<User> insert(User inreg, String userId, String pass) throws Exception{            
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(inreg.getUname(), Types.VARCHAR));
        parametri.put("upass", new ParamSql(hashPass(pass, Optional.empty()), Types.VARCHAR));
        parametri.put("nume", new ParamSql(inreg.getNume().toUpperCase(), Types.NVARCHAR));
        parametri.put("prenume", new ParamSql(inreg.getPrenume().toUpperCase(), Types.NVARCHAR));
        parametri.put("email", new ParamSql(inreg.getEmail().toLowerCase(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call portal.prc_user_insert_return(?,?,?,?,?,?)}", parametri).stream()
                .map(User::new)
                .findFirst();
    }
    
    public static Optional<User> update(User inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(inreg.getUname(), Types.VARCHAR));
        parametri.put("nume", new ParamSql(inreg.getNume().toUpperCase(), Types.NVARCHAR));
        parametri.put("prenume", new ParamSql(inreg.getPrenume().toUpperCase(), Types.NVARCHAR));
        parametri.put("email", new ParamSql(inreg.getEmail().toLowerCase(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call portal.prc_user_update_return(?,?,?,?,?)}", parametri).stream()
                .map(User::new)
                .findFirst();
    }
    
    public static boolean addToGroup(String uname, String group, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(uname, Types.VARCHAR));
        parametri.put("ugroup", new ParamSql(group, Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call portal.prc_user_add_to_group(?,?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static boolean removeFromGroup(String uname, String group, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(uname, Types.VARCHAR));
        parametri.put("ugroup", new ParamSql(group, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call portal.prc_user_remove_from_group(?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
