package ro.any.c12153.opexpl.services;

import java.io.OutputStream;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.CostCenterMap;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class CostCenterMapServ {

    public static List<CostCenterMap> getMapped(String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentermap_get_list_all_mapped(?,?)}", parametri).stream()
                .map(CostCenterMap::new)
                .collect(Collectors.toList());
    }
    
    public static List<CostCenterMap> getNotMapped(String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentermap_get_list_not_mapped(?,?)}", parametri).stream()
                .map(CostCenterMap::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<CostCenterMap> insert(CostCenterMap inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(inreg.getHier(), Types.CHAR));
        parametri.put("set", new ParamSql(inreg.getData_set(), Types.INTEGER));
        parametri.put("receiver", new ParamSql(inreg.getReceiver(), Types.VARCHAR));
        parametri.put("sender", new ParamSql(inreg.getSender(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentermap_insert_return(?,?,?,?,?)}", parametri).stream()
                .map(CostCenterMap::new)
                .findFirst();
    }
    
    public static Optional<CostCenterMap> update(CostCenterMap inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(inreg.getId(), Types.VARCHAR));
        parametri.put("receiver", new ParamSql(inreg.getReceiver(), Types.VARCHAR));
        parametri.put("sender", new ParamSql(inreg.getSender(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentermap_update_return(?,?,?,?)}", parametri).stream()
                .map(CostCenterMap::new)
                .findFirst();
    }
    
    public static boolean deleteById(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentermap_delete_by_id_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static boolean deleteAll(String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentermap_delete_all_return(?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void mappedToXlsx(String hier, Integer dataset, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(hier, Types.CHAR);
        parametri[1] = new ParamSql(dataset, Types.INTEGER);

        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_costcentermap_get_list_all_mapped @hier = ?, @set = ?;", Optional.of(parametri), out);
    }
}
