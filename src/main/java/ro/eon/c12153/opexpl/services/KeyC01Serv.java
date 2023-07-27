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
import ro.any.c12153.opexpl.entities.KeyRule;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class KeyC01Serv {

    public static List<KeyHead> getAll(String coarea, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        
        List<KeyHead> rezultat = new ArrayList<>();
        for (Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_get_list_all(?,?)}", parametri)){
            rezultat.add(new KeyHead(record));
        }
        return rezultat;
    }
    
    public static Optional<KeyHead> getById(Integer cheie, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(cheie, Types.INTEGER));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        
        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_get_by_id(?,?)}", parametri).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if (record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static void valsToXlsx(String coarea, Integer dataset, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(coarea, Types.CHAR);
        parametri[1] = new ParamSql(dataset, Types.INTEGER);

        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_cheie_c01_get_raw_data_by_set @coarea = ?, @set = ?;", Optional.of(parametri), out);
    }
    
    public static Optional<KeyHead> insertHead(KeyHead inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("descr", new ParamSql(inreg.getDescr(), Types.NVARCHAR));
        parametri.put("coarea", new ParamSql(inreg.getCoarea(), Types.CHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_def_insert_return(?,?,?,?,?)}", parametri).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if (record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static Optional<KeyHead> updateHead(Integer dataset, KeyHead inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("id", new ParamSql(inreg.getId(), Types.INTEGER));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("descr", new ParamSql(inreg.getDescr(), Types.NVARCHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        Optional<Map<String, Object>> record =  App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_def_update_return(?,?,?,?,?,?)}", parametri).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if (record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static void deleteHead(Integer cheie, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(cheie, Types.INTEGER));

        App.getConn(userId).executeCallableStatement("{call oxpl.prc_cheie_c01_def_delete(?)}", parametri);
    }
    
    public static Optional<KeyHead> deleteValsByKey(Integer cheie, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cheie", new ParamSql(cheie, Types.INTEGER));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_val_delete_by_key(?,?,?)}", parametri).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if (record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static void deleteValsByCoareaAndSet(String coarea, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        App.getConn(userId).executeCallableStatement("{call oxpl.prc_cheie_c01_val_delete_by_coarea_and_set(?,?,?)}", parametri);
    }
    
    public static Optional<KeyRule> getRuleByKey(Integer cheie, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cheie", new ParamSql(cheie, Types.INTEGER));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_rule_get_by_key(?)}", parametri).stream()
                .map(KeyRule::new)
                .findFirst();
    }
    
    public static Optional<KeyRule> insertRule(KeyRule inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cheie", new ParamSql(inreg.getCheie(), Types.INTEGER));
        parametri.put("medie_pond", new ParamSql(inreg.getMedie_pond(), Types.BIT));
        parametri.put("chei_json", new ParamSql(inreg.getCheiJson().toString(), Types.NVARCHAR));
        parametri.put("cost_centre_json", new ParamSql(inreg.getCost_centreJson().toString(), Types.NVARCHAR));
        parametri.put("opex_categ_json", new ParamSql(inreg.getOpex_categJson().toString(), Types.NVARCHAR));
        parametri.put("ic_part_json", new ParamSql(inreg.getIc_partJson().toString(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_rule_insert_return(?,?,?,?,?,?,?)}", parametri).stream()
                .map(KeyRule::new)
                .findFirst();
    }
    
    public static Optional<KeyRule> updateRule(KeyRule inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cheie", new ParamSql(inreg.getCheie(), Types.INTEGER));
        parametri.put("medie_pond", new ParamSql(inreg.getMedie_pond(), Types.BIT));
        parametri.put("chei_json", new ParamSql(inreg.getCheiJson().toString(), Types.NVARCHAR));
        parametri.put("cost_centre_json", new ParamSql(inreg.getCost_centreJson().toString(), Types.NVARCHAR));
        parametri.put("opex_categ_json", new ParamSql(inreg.getOpex_categJson().toString(), Types.NVARCHAR));
        parametri.put("ic_part_json", new ParamSql(inreg.getIc_partJson().toString(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_rule_update_return(?,?,?,?,?,?,?)}", parametri).stream()
                .map(KeyRule::new)
                .findFirst();
    }
    
    public static boolean deleteRule(Integer cheie, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cheie", new ParamSql(cheie, Types.INTEGER));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_rule_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static boolean checkRuleVals(Integer dataset, String coarea, KeyRule inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("medie_pond", new ParamSql(inreg.getMedie_pond(), Types.BIT));
        parametri.put("chei_json", new ParamSql(inreg.getCheiJson().toString(), Types.NVARCHAR));
        parametri.put("cost_centre_json", new ParamSql(inreg.getCost_centreJson().toString(), Types.NVARCHAR));
        parametri.put("opex_categ_json", new ParamSql(inreg.getOpex_categJson().toString(), Types.NVARCHAR));
        parametri.put("ic_part_json", new ParamSql(inreg.getIc_partJson().toString(), Types.NVARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_rule_check_val_exists(?,?,?,?,?,?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static List<KeyHead> calcCheckValsByKey(Integer cheie, Integer dataset, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("cheie", new ParamSql(cheie, Types.INTEGER));
        
        List<KeyHead> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_check_val_exists(?,?,?)}", parametri)){
            rezultat.add(new KeyHead(record));
        }
        return rezultat;
    }
    
    public static List<KeyHead> calcCheckValsAllNotBlocked(Integer dataset, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));

        List<KeyHead> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_check_val_exists(?,?)}", parametri)){
            rezultat.add(new KeyHead(record));
        }
        return rezultat;
    }
    
    public static Optional<KeyHead> calcByKey(Integer cheie, Integer dataset, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("cheie", new ParamSql(cheie, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_c01_calculate(?,?,?,?)}", parametri).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if(record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static void calcAllNotBlocked(Integer dataset, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        parametri.put("cheie", new ParamSql(null, Types.CHAR));

        App.getConn(userId).executeCallableStatement("{call oxpl.prc_cheie_c01_calculate(?,?,?,?)}", parametri);
    }
}
