package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.CostCenterGroup;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class CostCenterGroupServ {
    
    public static Optional<CostCenterGroup> getById(String id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(id, Types.VARCHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_costcentergroup_get_by_id(?);", Optional.of(parametri)).stream()
                .map(CostCenterGroup::new)
                .findFirst();
    }
    
    public static Optional<CostCenterGroup> getByCod(String cod, String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("cod", new ParamSql(cod, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentergroup_get_by_hier_data_set_and_cod(?,?,?)}", parametri).stream()
                .map(CostCenterGroup::new)
                .findFirst();
    }
    
    public static List<CostCenterGroup> getAll(String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentergroup_get_list_by_hier_and_data_set(?,?)}", parametri).stream()
                .map(CostCenterGroup::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<CostCenterGroup> insert(CostCenterGroup inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(inreg.getHier(), Types.CHAR));
        parametri.put("set", new ParamSql(inreg.getData_set(), Types.INTEGER));
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.VARCHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("superior", new ParamSql(inreg.getSuperior(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentergroup_insert_return(?,?,?,?,?,?)}", parametri).stream()
                .map(CostCenterGroup::new)
                .findFirst();
    }
    
    public static Optional<CostCenterGroup> update(CostCenterGroup inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(inreg.getId(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("superior", new ParamSql(inreg.getSuperior(), Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentergroup_update_return(?,?,?,?)}", parametri).stream()
                .map(CostCenterGroup::new)
                .findFirst();
    }
    
    public static boolean delete(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentergroup_delete_return(?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static boolean deleteAll(String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costcentergroup_delete_by_hier_and_data_set(?,?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void takeOverFromSet(Integer dest_set, Integer from_set, String hier, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("from_set", new ParamSql(from_set, Types.INTEGER));
        parametri.put("dest_set", new ParamSql(dest_set, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        App.getConn(userId)
                .executeCallableStatement("{call oxpl.prc_costcentergroup_take_over(?,?,?,?)}", parametri);
    }
}