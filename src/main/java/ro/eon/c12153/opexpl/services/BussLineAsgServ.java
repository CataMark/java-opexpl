package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.BussLine;
import ro.any.c12153.opexpl.entities.BussLineAsg;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author C12153
 */
public class BussLineAsgServ {
    
    public static Optional<BussLineAsg> getById(String id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(id, Types.VARCHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_bussasg_get_by_id(?);", Optional.of(parametri)).stream()
                .map(BussLineAsg::new)
                .findFirst();
    }
    
    public static List<BussLineAsg> getListAsignToCoArea(String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_bussasg_get_list_asign_to_coarea(?)}", parametri).stream()
                .map(BussLineAsg::new)
                .collect(Collectors.toList());
    }
    
    public static List<BussLine> getListNotAsignToCoArea(String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_bussline_get_list_not_asign_to_coarea(?)}", parametri).stream()
                .map(BussLine::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<BussLineAsg> insert(BussLineAsg inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(inreg.getCoarea(), Types.CHAR));
        parametri.put("bussline", new ParamSql(inreg.getBuss_line(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_bussasg_insert_return(?,?,?)}", parametri).stream()
                .map(BussLineAsg::new)
                .findFirst();
    }
    
    public static boolean delete(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_bussasg_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
