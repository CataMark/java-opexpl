package ro.any.c12153.opexpl.services;

import java.io.OutputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.KeyHead;
import ro.any.c12153.opexpl.entities.KeyVal;
import ro.any.c12153.opexpl.entities.KeyValGroup;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class KeyG01Serv {

    public static Optional<KeyHead> getById(Integer cheie, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(cheie, Types.INTEGER));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g01_def_get_by_id(?,?)}", parametri).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if(record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
        
    }
    
    public static List<KeyHead> getHeadAll(String coarea, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        
        List<KeyHead> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g01_def_get_all(?,?)}", parametri)){
            rezultat.add(new KeyHead(record));
        }
        return rezultat;
    }
    
    public static Optional<KeyHead> insertHead(KeyHead inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("descr", new ParamSql(inreg.getDescr(), Types.NVARCHAR));
        parametri.put("coarea", new ParamSql(inreg.getCoarea(), Types.CHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        Optional<Map<String, Object>> record =  App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g01_def_insert_return(?,?,?,?,?)}", parametri).stream()
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

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g01_def_update_return(?,?,?,?,?,?)}", parametri).stream()
                .findFirst();
        
        KeyHead rezultat = null;
        if(record.isPresent()) rezultat = new KeyHead(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static void deleteHead(Integer cheie, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(cheie, Types.INTEGER));

        App.getConn(userId).executeCallableStatement("{call oxpl.prc_cheie_g01_def_delete(?)}", parametri);
    }
    
    public static List<KeyValGroup> getValsGrouped(Integer cheie, String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cheie", new ParamSql(cheie, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));

        List<KeyValGroup> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g01_val_get_grouped_by_hier_and_set(?,?,?)}", parametri)){
            rezultat.add(new KeyValGroup(record));
        }
        return rezultat;
    }
    
    public static List<KeyVal> getValsByCheiAndCCenter(Integer cheie, String hier, Integer dataset, String ccenter, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cheie", new ParamSql(cheie, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("cost_centre", new ParamSql(ccenter, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g01_val_get_by_cheie_and_ccenter(?,?,?,?)}", parametri).stream()
                .map(KeyVal::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<KeyValGroup> saveVals(Integer cheie, Integer dataset, String ccenter, List<KeyVal> inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cheie", new ParamSql(cheie, Types.INTEGER));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("cost_centre", new ParamSql(ccenter, Types.VARCHAR));
        KeyValGroup group = new KeyValGroup();
        group.setValori(inreg);
        parametri.put("json", new ParamSql(group.getJson().toString(), Types.NVARCHAR));            
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_g01_val_maintain(?,?,?,?,?)}", parametri).stream()
                .findFirst();
        
        KeyValGroup rezultat = null;
        if (record.isPresent()) rezultat = new KeyValGroup(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static String deleteValsByCheiAndSet(Integer cheie, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        parametri.put("cheie", new ParamSql(cheie, Types.INTEGER));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));

        return App.getConn(userId)
                .executeCallableStatement("{call oxpl.prc_cheie_g01_val_delete_by_cheie_and_set(?,?,?)}", parametri);
    }
    
    public static String takeOverValsByCheiAndSet(Integer cheie, Integer fromset, Integer toset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cheie", new ParamSql(cheie, Types.INTEGER));
        parametri.put("from_set", new ParamSql(fromset, Types.INTEGER));
        parametri.put("dest_set", new ParamSql(toset, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .executeCallableStatement("{call oxpl.prc_cheie_g01_val_take_over_by_cheie(?,?,?,?)}", parametri);
    }
    
    public static void valsByCoareaAndSetToXlsx(String coarea, Integer dataset, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(coarea, Types.CHAR);
        parametri[1] = new ParamSql(dataset, Types.INTEGER);

        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_cheie_g01_val_get_by_coarea_and_set @coarea = ?, @set = ?;", Optional.of(parametri), out);
    }
    
    public static void valsByCheiAndSetToXlsx(Integer cheie, Integer dataset, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(cheie, Types.INTEGER);
        parametri[1] = new ParamSql(dataset, Types.INTEGER);

        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_cheie_g01_val_get_by_cheie_and_set @cheie = ?, @set = ?;", Optional.of(parametri), out);
    }
}
