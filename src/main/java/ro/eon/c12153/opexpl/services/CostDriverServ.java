package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.CostDriver;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class CostDriverServ {
    
    public static Optional<CostDriver> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.CHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_costdriver_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(CostDriver::new)
                .findFirst();
    }
    
    public static List<CostDriver> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_get_list_all}", Optional.empty()).stream()
                .map(CostDriver::new)
                .collect(Collectors.toList());
    }
    
    public static List<CostDriver> getByCentral(boolean central, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("central", new ParamSql(central, Types.BIT));
            
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_get_list_by_central(?)}", parametri).stream()
                .map(CostDriver::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<CostDriver> insert(CostDriver inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.VARCHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("central", new ParamSql(inreg.getCentral(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_insert_return(?,?,?,?)}", parametri).stream()
                .map(CostDriver::new)
                .findFirst();
    }
    
    public static Optional<CostDriver> update(CostDriver inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.VARCHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("central", new ParamSql(inreg.getCentral(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_update_return(?,?,?,?)}", parametri).stream()
                .map(CostDriver::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.CHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static Optional<CostDriver> getAssignByCod(String cod, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.CHAR));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_asign_get_by_cod_and_coarea(?,?)}", parametri).stream()
                .map(CostDriver::new)
                .findFirst();
    }
    
    public static List<CostDriver> getAssignAll(String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_asign_get_list_all(?)}", parametri).stream()
                .map(CostDriver::new)
                .collect(Collectors.toList());
    }
    
    public static List<CostDriver> getAssignByCentral(String coarea, boolean central, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("central", new ParamSql(central, Types.BIT));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_asign_get_list_by_central(?,?)}", parametri).stream()
                .map(CostDriver::new)
                .collect(Collectors.toList());
    }
    
    public static List<CostDriver> getNotAssign(String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_asign_get_list_not_asign_to_coarea(?)}", parametri).stream()
                .map(CostDriver::new)
                .collect(Collectors.toList());
    }
    
    public static List<CostDriver> getAssignByRights(String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_asign_get_list_by_rights(?,?)}", parametri).stream()
                .map(CostDriver::new)
                .collect(Collectors.toList());
    }
    
    public static boolean checkIsAssigned(String coarea, String cdriver, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("cdriver", new ParamSql(cdriver, Types.CHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_asign_check_is_assigned(?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);        
    }
    
    public static Optional<CostDriver> insertAssign(CostDriver inreg, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("cost_driver", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_asign_insert_return(?,?,?,?)}", parametri).stream()
                .map(CostDriver::new)
                .findFirst();
    }
    
    public static Optional<CostDriver> updateAssign(CostDriver inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(inreg.getId(), Types.VARCHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_asign_update_return(?,?,?)}", parametri).stream()
                .map(CostDriver::new)
                .findFirst();
    }
    
    public static boolean deleteAssign(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_costdriver_asign_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
