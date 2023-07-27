package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.IcPartener;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class IcPartenerServ {

    public static Optional<IcPartener> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.VARCHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_icpart_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(IcPartener::new)
                .findFirst();
    }
    
    public static List<IcPartener> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_icpart_get_list_all}", Optional.empty()).stream()
                .map(IcPartener::new)
                .collect(Collectors.toList());
    }
    
    public static List<IcPartener> getNotCoArea(String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_icpart_get_list_not_refer_to_coarea(?)}", parametri).stream()
                .map(IcPartener::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<IcPartener> insert(IcPartener inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.VARCHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("coarea", new ParamSql(inreg.getCoarea(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_icpart_insert_return(?,?,?,?)}", parametri).stream()
                .map(IcPartener::new)
                .findFirst();
    }
    
    public static Optional<IcPartener> update(IcPartener inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("coarea", new ParamSql(inreg.getCoarea(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_icpart_update_return(?,?,?,?)}", parametri).stream()
                .map(IcPartener::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_icpart_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
