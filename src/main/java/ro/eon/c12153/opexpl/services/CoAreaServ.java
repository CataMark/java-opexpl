package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class CoAreaServ {
    
    public static Optional<CoArea> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.CHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_coarea_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(CoArea::new)
                .findFirst();
    }
    
    public static Optional<CoArea> getByHier(String hier, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(hier, Types.CHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_coarea_get_by_hier(?);", Optional.of(parametri)).stream()
                .map(CoArea::new)
                .findFirst();
    }
    
    public static List<CoArea> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_coarea_get_list_all}", Optional.empty()).stream()
                .map(CoArea::new)
                .collect(Collectors.toList());
    }
    
    public static List<CoArea> getListAlocare(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_coarea_get_list_alocare}", Optional.empty()).stream()
                .map(CoArea::new)
                .collect(Collectors.toList());
    }
    
    public static List<CoArea> getListCcntrBound(String user, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname" ,new ParamSql(user, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_coarea_get_list_by_ccntr_bound(?)}", parametri).stream()
                .map(CoArea::new)
                .collect(Collectors.toList());
    }
    
    public static List<CoArea> getListCcntrBoundAlocare(String user, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(user, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_coarea_get_list_by_ccntr_bound_alocare(?)}", parametri).stream()
                .map(CoArea::new)
                .collect(Collectors.toList());
    }
    
    public static List<CoArea> getListCDriverBound(String user, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("uname", new ParamSql(user, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_coarea_get_list_by_cdriver_bound(?)}", parametri).stream()
                .map(CoArea::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<CoArea> insert(CoArea inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("acronim", new ParamSql(inreg.getAcronim(), Types.VARCHAR));
        parametri.put("alocare", new ParamSql(inreg.getAlocare(), Types.BIT));
        parametri.put("hier", new ParamSql(inreg.getHier(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId,Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_coarea_insert_return(?,?,?,?,?,?)}", parametri).stream()
                .map(CoArea::new)
                .findFirst();
    }
    
    public static Optional<CoArea> update(CoArea inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("acronim", new ParamSql(inreg.getAcronim(), Types.VARCHAR));
        parametri.put("alocare", new ParamSql(inreg.getAlocare(), Types.BIT));
        parametri.put("hier", new ParamSql(inreg.getHier(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_coarea_update_return(?,?,?,?,?,?)}", parametri).stream()
                .map(CoArea::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.CHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_coarea_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
