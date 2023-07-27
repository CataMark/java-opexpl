package ro.any.c12153.opexpl.services;

import java.io.OutputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.KeyHead;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class KeyS01Serv {

    public static List<KeyHead> getListByTipAndCCenter(String hier, Integer dataset, String ccentre, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("cost_centre", new ParamSql(ccentre, Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        List<KeyHead> rezultat = new ArrayList<>();
        for (Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_s01_get_list_by_ccenter_with_an_total(?,?,?,?)}", parametri)){
            rezultat.add(new KeyHead(record));
        }
        return rezultat;
    }
    
    public static Optional<KeyHead> getById(Integer cheie, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cheie, Types.INTEGER);

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_cheie_s01_get_by_id(?);", Optional.of(parametri)).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if (record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static Optional<KeyHead> insert(KeyHead inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("json", new ParamSql(inreg.getJson(true).toString(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_s01_insert_return(?,?)}", parametri).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if (record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static Optional<KeyHead> update(KeyHead inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("json", new ParamSql(inreg.getJson(true).toString(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_s01_update_return(?,?)}", parametri).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if (record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static boolean delete(Integer cheie, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(cheie, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_s01_delete_return(?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void keysToXlsx(String hier, Integer dataset, String ccentre, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[4];
        parametri[0] = new ParamSql(hier, Types.CHAR);
        parametri[1] = new ParamSql(dataset, Types.INTEGER);
        parametri[2] = new ParamSql(ccentre, Types.VARCHAR);
        parametri[3] = new ParamSql(userId, Types.VARCHAR);

        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_cheie_s01_get_raw_data_by_ccenter @hier = ?, @set = ?, @cost_centre = ?, @kid  = ?;", Optional.of(parametri), out);
    }
}
