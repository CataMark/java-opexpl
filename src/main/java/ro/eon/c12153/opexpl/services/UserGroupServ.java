package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.UserGroup;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class UserGroupServ {
    
    public static List<UserGroup> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_group_get_all}", Optional.empty()).stream()
                .map(UserGroup::new)
                .collect(Collectors.toList());
    }
    
    public static List<UserGroup> getSpecificList(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_group_get_specific_list}", Optional.empty()).stream()
                .map(UserGroup::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<UserGroup> getByCod(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_group_get_by_cod(?)}", parametri).stream()
                .map(UserGroup::new)
                .findFirst();
    }
}
