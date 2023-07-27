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
public class KeyG02Serv {

    public static List<KeyHead> getAll(String coarea, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        
        List<KeyHead> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g02_get_list_all(?,?)}", parametri)){
            rezultat.add(new KeyHead(record));
        }
        return rezultat;
    }
    
    public static Optional<KeyHead> getById(Integer cheie, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(cheie, Types.INTEGER));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g02_get_by_id(?,?)}", parametri).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if(record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static Optional<KeyHead> insert(Integer dataset, KeyHead inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("json", new ParamSql(inreg.getJson(true).toString(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g02_insert_return(?,?,?)}", parametri).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if(record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static Optional<KeyHead> update(Integer dataset, KeyHead inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("json", new ParamSql(inreg.getJson(true).toString(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g02_update_return(?,?,?)}", parametri).stream()
                .findFirst();
            
        KeyHead rezultat = null;
        if(record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static String deleteHead(Integer cheie, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(cheie, Types.INTEGER));

        return App.getConn(userId).executeCallableStatement("{call oxpl.prc_cheie_g02_def_delete(?)}", parametri);
    }
    
    public static Optional<KeyHead> valsTakeOverByCheieAndSet(Integer cheie, Integer fromset, Integer toset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cheie", new ParamSql(cheie, Types.INTEGER));
        parametri.put("from_set", new ParamSql(fromset, Types.INTEGER));
        parametri.put("dest_set", new ParamSql(toset, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g02_val_take_over_by_cheie(?,?,?,?)}", parametri).stream()
                .findFirst();

        KeyHead rezultat = null;
        if(record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static void valsToXlsx(String coarea, Integer dataset, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(coarea, Types.CHAR);
        parametri[1] = new ParamSql(dataset, Types.INTEGER);

        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_cheie_g02_get_raw_data_by_set @coarea = ?, @set = ?;", Optional.of(parametri), out);
    }
}
