package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.SegmentInd;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class SegmentIndServ {

    public static Optional<SegmentInd> getByCod(String cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.CHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_segind_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(SegmentInd::new)
                .findFirst();
    }
    
    public static List<SegmentInd> getAll (String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_segind_get_list_all}", Optional.empty()).stream()
                .map(SegmentInd::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<SegmentInd> insert(SegmentInd inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_segind_insert_return(?,?,?)}", parametri).stream()
                .map(SegmentInd::new)
                .findFirst();
    }
    
    public static Optional<SegmentInd> update(SegmentInd inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.CHAR));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_segind_update_return(?,?,?)}", parametri).stream()
                .map(SegmentInd::new)
                .findFirst();
    }
    
    public static boolean delete(String cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.CHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_segind_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
