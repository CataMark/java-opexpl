package ro.any.c12153.opexpl.services;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class ModelServ {
    
    public static void persistAlocare(Integer dataset, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        App.getConn(userId)
                .executeCallableStatement("{call oxpl.prc_mdl_save_alocare(?,?)}", parametri);
    }
    
    public static Optional<Map<String, Object>> getSumar(Integer dataset, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_mdl_get_sumar(?,?)}", parametri).stream()
                .findFirst();
    }
    
    public static void toCsvAlocare(Integer dataset, String coarea, String userId, BufferedWriter out) throws Exception{
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(coarea, Types.CHAR);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToCSV("exec oxpl.prc_mdl_get_alocare @set = ?, @coarea = ?;", Optional.of(parametri), out);
    }
    
    public static void toCsvAlocareByTip(Integer dataset, String coarea, String tip, String userId, BufferedWriter out) throws Exception{
        ParamSql[] parametri = new ParamSql[3];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(coarea, Types.CHAR);
        parametri[2] = new ParamSql(tip, Types.VARCHAR);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToCSV("exec oxpl.prc_mdl_get_alocare_by_tip @set = ?, @coarea = ?, @tip = ?;", Optional.of(parametri), out);
    }
    
    public static void toCsvAlocareByCCenter(Integer dataset, String coarea, String tip, String ccenter, String userId, BufferedWriter out) throws Exception{
        ParamSql[] parametri = new ParamSql[4];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(coarea, Types.CHAR);
        parametri[2] = new ParamSql(tip, Types.VARCHAR);
        parametri[3] = new ParamSql(ccenter, Types.VARCHAR);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToCSV("exec oxpl.prc_mdl_get_alocare_by_ccenter @set = ?, @coarea = ?, @tip = ?, @ccenter = ?;", Optional.of(parametri), out);
    }
    
    public static void toJsonAlocare(Integer dataset, String coarea, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(coarea, Types.CHAR);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToJsonArray("exec oxpl.prc_mdl_get_alocare @set = ?, @coarea = ?;", Optional.of(parametri), out);
    }
}
