package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.KeyHead;
import ro.any.c12153.opexpl.entities.KeyVal;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class KeyServ {
    
    public static List<KeyHead> getGenAll(String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        List<KeyHead> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_get_list_gen_def_all(?)}", parametri)){
            rezultat.add(new KeyHead(record));
        }
        return rezultat;
    }
    
    public static List<KeyVal> getValsByIdAndCCenter(Integer cheie, Integer dataset, String ccenter, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(cheie, Types.INTEGER));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("cost_centre", new ParamSql(ccenter, Types.VARCHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_get_val_by_id_and_ccenter(?,?,?)}", parametri).stream()
                .map(KeyVal::new)
                .collect(Collectors.toList());
        
    }
    
    public static List<KeyHead> getAsignNoVal(Integer dataset, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        List<KeyHead> rezultat = new ArrayList<>();
        for (Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_get_list_asign_no_val(?,?)}", parametri)){
            rezultat.add(new KeyHead(record));
        }
        return rezultat;
    }
}
