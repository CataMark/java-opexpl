package ro.any.c12153.opexpl.services;

import java.io.OutputStream;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.CostCenter;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class CostCenterServ {

    public static Optional<CostCenter> getById(String id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(id, Types.VARCHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_costcenter_get_by_id(?);", Optional.of(parametri)).stream()
                .map(CostCenter::new)
                .findFirst();
    }
    
    public static Optional<CostCenter> getByCod(String cod, String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("cod", new ParamSql(cod, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcenter_get_by_cod(?,?,?)}", parametri).stream()
                .map(CostCenter::new)
                .findFirst();
    }
    
    public static List<CostCenter> getAll(String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcenter_get_list_by_hier_and_data_set(?,?)}", parametri).stream()
                .map(CostCenter::new)
                .collect(Collectors.toList());
    }
    
    public static List<CostCenter> getAllByRights(String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcenter_get_list_by_hier_data_set_and_rights(?,?,?)}", parametri).stream()
                .map(CostCenter::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<CostCenter> insert(CostCenter inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(inreg.getHier(), Types.CHAR));
        parametri.put("set", new ParamSql(inreg.getData_set(), Types.INTEGER));
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.VARCHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("grup", new ParamSql(inreg.getSuperior_cod(), Types.VARCHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcenter_insert_return(?,?,?,?,?,?,?)}", parametri).stream()
                .map(CostCenter::new)
                .findFirst();
    }
    
    public static Optional<CostCenter> update(CostCenter inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(inreg.getId(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("grup", new ParamSql(inreg.getSuperior_cod(), Types.VARCHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcenter_update_return(?,?,?,?,?)}", parametri).stream()
                .map(CostCenter::new)
                .findFirst();
    }
    
    public static boolean delete(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcenter_delete_return(?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static boolean deleteAll(String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcenter_delete_all(?,?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void hierToXlsx(String hier, Integer dataset, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(hier, Types.CHAR);

        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_costcenter_get_list_by_hier_and_data_set @set = ?, @hier = ?;", Optional.of(parametri), out);
    }
    
    public static void takeOverFromSet(Integer dest_set, Integer from_set, String hier, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("from_set", new ParamSql(from_set, Types.INTEGER));
        parametri.put("dest_set", new ParamSql(dest_set, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        App.getConn(userId)
                .executeCallableStatement("{call oxpl.prc_costcenter_take_over(?,?,?,?)}", parametri);
    }
}
