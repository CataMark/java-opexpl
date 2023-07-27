package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.PlanVers;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class PlanVersServ {

    public static Optional<PlanVers> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.CHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_planvers_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(PlanVers::new)
                .findFirst();
    }
    
    public static final List<PlanVers> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_planvers_get_list_all}", Optional.empty()).stream()
                .map(PlanVers::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<PlanVers> insert(PlanVers inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_planvers_insert_return(?,?,?)}", parametri).stream()
                .map(PlanVers::new)
                .findFirst();
    }
    
    public static Optional<PlanVers> update(PlanVers inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_planvers_update_return(?,?,?)}", parametri).stream()
                .map(PlanVers::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.CHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_planvers_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
