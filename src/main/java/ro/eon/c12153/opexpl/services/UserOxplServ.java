package ro.any.c12153.opexpl.services;

import java.io.OutputStream;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.entities.User;

public class UserOxplServ {

    public static boolean checkUserCostCenterBound(String uname, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(uname, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_check_cost_center_bound(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(true);
    }
    
    public static boolean checkUserCostDriverBound(String uname, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(uname, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_check_cost_driver_bound(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(true);
    }
    
    public static boolean checkUserHasRightsOnCcenter(String hier, Integer dataset, String ccenter, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("ccenter", new ParamSql(ccenter, Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_has_rights_on_ccenter(?,?,?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(true);
    }
    
    public static boolean checkUserHasRightsOnCdriver(String coarea, String cdriver, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("cdriver", new ParamSql(cdriver, Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_has_rights_on_cdriver(?,?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(true);
    }
        
    public static List<User> getByGroup(String group, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("group", new ParamSql(group, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_get_list_by_group(?)}", parametri).stream()
                .map(User::new)
                .collect(Collectors.toList());
    }
    
    public static List<User> getByCostCenter(String hier, Integer dataset, String ccenter, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("cost_centre", new ParamSql(ccenter, Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_get_list_by_cost_center(?,?,?,?)}", parametri).stream()
                .map(User::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<User> addToCCenter(String user, String hier, Integer dataset, String ccenter, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(user, Types.VARCHAR));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("cost_centre", new ParamSql(ccenter, Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_add_rights_by_cost_center(?,?,?,?,?)}", parametri).stream()
                .map(User::new)
                .findFirst();
    }
    
    public static boolean deleteFromCCenter(String user, String hier, Integer dataset, String ccenter, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(user, Types.VARCHAR));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("cost_centre", new ParamSql(ccenter, Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_del_rights_by_cost_center(?,?,?,?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void ccenterRightsToXlsx(String hier, Integer dataset, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(hier, Types.CHAR);
        parametri[1] = new ParamSql(dataset, Types.INTEGER);

        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_user_get_list_by_hier @hier = ?, @set = ?;", Optional.of(parametri), out);
    }
    
    public static List<User> getByCostDriver(String coarea, String cdriver, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("cost_driver", new ParamSql(cdriver, Types.CHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_get_list_by_cost_driver(?,?)}", parametri).stream()
                .map(User::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<User> addToCDriver(String user, String coarea, String cdriver, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(user, Types.VARCHAR));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("cost_driver", new ParamSql(cdriver, Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_add_rights_by_cost_driver(?,?,?,?)}", parametri).stream()
                .map(User::new)
                .findFirst();
    }
    
    public static boolean deleteFromCDriver(String user, String coarea, String cdriver, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(user, Types.VARCHAR));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("cost_driver", new ParamSql(cdriver, Types.CHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_user_del_rights_by_cost_driver(?,?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
