package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.Month;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class MonthServ {

    public static Optional<Month> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.CHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_month_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(Month::new)
                .findFirst();
    }
    
    public static List<Month> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_month_get_list_all}", Optional.empty()).stream()
                .map(Month::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<Month> insert(Month inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_month_insert_return(?,?,?)}", parametri).stream()
                .map(Month::new)
                .findFirst();
    }
    
    public static Optional<Month> update(Month inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_month_update_return(?,?,?)}", parametri).stream()
                .map(Month::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.CHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_month_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
