package ro.any.c12153.opexpl.services;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.PlanDoc;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class PlanDocServ {
    
    public static Optional<PlanDoc> getById(String id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(id, Types.VARCHAR);
        
        Optional<Map<String, Object>> record =  App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_plandoc_get_by_id(?);", Optional.of(parametri)).stream()
                .findFirst();
        
        PlanDoc rezultat = null;
        if (record.isPresent()) rezultat = new PlanDoc(record.get());
        return Optional.ofNullable(rezultat);
    }
    
    public static List<PlanDoc> getCCenterNCentralSumar(Integer dataset, String hier, String ccenter, Optional<Boolean> cdriver_central, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("cost_centre", new ParamSql(ccenter, Types.VARCHAR));
        parametri.put("cdriver_central", new ParamSql(cdriver_central.isPresent() ? cdriver_central.get() : null, Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        List<PlanDoc> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_plandoc_get_list_ccenter_ncentr_sumar(?,?,?,?,?)}", parametri)){
            rezultat.add(new PlanDoc(record));
        }
        return rezultat;
    }
    
    public static List<PlanDoc> getCCenterNCentralPozitii(Integer dataset, String hier, String ccenter, Optional<Boolean> cdriver_central, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("cost_centre", new ParamSql(ccenter, Types.VARCHAR));
        parametri.put("cdriver_central", new ParamSql(cdriver_central.isPresent() ? cdriver_central.get() : null, Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        List<PlanDoc> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_plandoc_get_list_ccenter_ncentr_pozitii(?,?,?,?,?)}", parametri)){
            rezultat.add(new PlanDoc(record));
        }
        return rezultat;
    }
    
    public static List<PlanDoc> getCCenterNCentralCompar(Integer dataset, Optional<Integer> comparset, String hier, String ccenter, Optional<Boolean> cdriver_central, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("main_set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("compar_set", new ParamSql(comparset.isPresent() ? comparset.get() : null, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("cost_centre", new ParamSql(ccenter, Types.VARCHAR));
        parametri.put("cdriver_central", new ParamSql(cdriver_central.isPresent() ? cdriver_central.get() : null, Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        List<PlanDoc> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_plandoc_get_list_ccenter_ncentr_compar(?,?,?,?,?,?)}", parametri)){
            rezultat.add(new PlanDoc(record));
        }
        return rezultat;
    }
    
    public static List<PlanDoc> getCCenterNCentralAlocare(Integer dataset, String hier, String ccenter, Optional<Boolean> cdriver_central, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("cost_centre", new ParamSql(ccenter, Types.VARCHAR));
        parametri.put("cdriver_central", new ParamSql(cdriver_central.isPresent() ? cdriver_central.get() : null, Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        List<PlanDoc> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_plandoc_get_list_ccenter_ncentr_aloc(?,?,?,?,?)}", parametri)){
            rezultat.add(new PlanDoc(record));
        }
        return rezultat;        
    }
    
    public static List<PlanDoc> getCDriverCentralSumar(Integer dataset, String hier, String cdriver, Optional<Integer> ocateg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("cdriver", new ParamSql(cdriver, Types.CHAR));
        parametri.put("ocateg", new ParamSql((ocateg.isPresent() ? ocateg.get() : null), Types.INTEGER));
        
        List<PlanDoc> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_plandoc_get_list_cdriver_centr_sumar(?,?,?,?)}", parametri)){
            rezultat.add(new PlanDoc(record));
        }
        return rezultat;
    }
    
    public static List<PlanDoc> getCDriverCentralPozitii(Integer dataset, String hier, String cdriver, Optional<Integer> ocateg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("cdriver", new ParamSql(cdriver, Types.CHAR));
        parametri.put("ocateg", new ParamSql((ocateg.isPresent() ? ocateg.get() : null), Types.INTEGER));
        
        List<PlanDoc> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_plandoc_get_list_cdriver_centr_pozitii(?,?,?,?)}", parametri)){
            rezultat.add(new PlanDoc(record));
        }
        return rezultat;
    }
    
    public static List<PlanDoc> getCDriverCentralCompar(Integer dataset, Optional<Integer> comparset, String hier, String cdriver, Optional<Integer> ocateg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("main_set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("compar_set", new ParamSql(comparset.isPresent() ? comparset.get() : null, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("cdriver", new ParamSql(cdriver, Types.CHAR));
        parametri.put("ocateg", new ParamSql((ocateg.isPresent() ? ocateg.get() : null), Types.INTEGER));
        
        List<PlanDoc> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_plandoc_get_list_cdriver_centr_compar(?,?,?,?,?)}", parametri)){
            rezultat.add(new PlanDoc(record));
        }
        return rezultat;
    }
    
    public static List<PlanDoc> getCDriverCentralAlocare(Integer dataset, String hier, String cdriver, Optional<Integer> ocateg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("cdriver", new ParamSql(cdriver, Types.CHAR));
        parametri.put("ocateg", new ParamSql((ocateg.isPresent() ? ocateg.get() : null), Types.INTEGER));
        
        List<PlanDoc> rezultat = new ArrayList<>();
        for(Map<String, Object> record : App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_plandoc_get_list_cdriver_centr_aloc(?,?,?,?)}", parametri)){
            rezultat.add(new PlanDoc(record));
        }
        return rezultat;
    }
    
    public static Optional<PlanDoc> insert(PlanDoc inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("json", new ParamSql(inreg.getJson(true).toString(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_plandoc_insert_return(?,?)}", parametri).stream()
                .findFirst();
        
        PlanDoc rezultat = null;
        if (record.isPresent()) rezultat = new PlanDoc(record.get());
        return Optional.ofNullable(rezultat);        
    }
    
    public static Optional<PlanDoc> update(PlanDoc inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("json", new ParamSql(inreg.getJson(true).toString(), Types.NVARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        Optional<Map<String, Object>> record = App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_plandoc_update_return(?,?)}", parametri).stream()
                .findFirst();
        
        PlanDoc rezultat = null;
        if (record.isPresent()) rezultat = new PlanDoc(record.get());
        return Optional.ofNullable(rezultat); 
    }
    
    public static boolean delete(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.VARCHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_plandoc_delete_return(?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void toXlsxCCenterNCentralSumar(Integer dataset, String hier, String ccenter, Optional<Boolean> cdriver_central, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[5];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(hier, Types.CHAR);
        parametri[2] = new ParamSql(ccenter, Types.VARCHAR);
        parametri[3] = new ParamSql(cdriver_central.isPresent() ? cdriver_central.get() : null, Types.BIT);
        parametri[4] = new ParamSql(userId, Types.VARCHAR);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_plandoc_get_raw_ccenter_ncentr_sumar @set = ?, @hier = ?, @cost_centre = ?, @cdriver_central = ?, @kid = ?;", Optional.of(parametri), out);
    }
    
    public static void toXlsxCCenterNCentralPozitii(Integer dataset, String hier, String ccenter, Optional<Boolean> cdriver_central, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[5];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(hier, Types.CHAR);
        parametri[2] = new ParamSql(ccenter, Types.VARCHAR);
        parametri[3] = new ParamSql(cdriver_central.isPresent() ? cdriver_central.get() : null, Types.BIT);
        parametri[4] = new ParamSql(userId, Types.VARCHAR);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_plandoc_get_raw_ccenter_ncentr_pozitii @set = ?, @hier = ?, @cost_centre = ?, @cdriver_central = ?, @kid = ?;", Optional.of(parametri), out);
    }
    
    public static void toXlsxCCenterNCentralCompar(Integer dataset, Optional<Integer> comparset, String hier, String ccenter, Optional<Boolean> cdriver_central, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[6];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(comparset.isPresent() ? comparset.get() : null, Types.INTEGER);
        parametri[2] = new ParamSql(hier, Types.CHAR);
        parametri[3] = new ParamSql(ccenter, Types.VARCHAR);
        parametri[4] = new ParamSql(cdriver_central.isPresent() ? cdriver_central.get() : null, Types.BIT);
        parametri[5] = new ParamSql(userId, Types.VARCHAR);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_plandoc_get_raw_ccenter_ncentr_compar @main_set = ?, @compar_set = ?, @hier = ?, @cost_centre = ?, @cdriver_central = ?, @kid = ?;",
                        Optional.of(parametri), out);
    }
    
    public static void toXlsxCCenterNCentralAlocare(Integer dataset, String hier, String ccenter, Optional<Boolean> cdriver_central, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[5];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(hier, Types.CHAR);
        parametri[2] = new ParamSql(ccenter, Types.VARCHAR);
        parametri[3] = new ParamSql(cdriver_central.isPresent() ? cdriver_central.get() : null, Types.BIT);
        parametri[4] = new ParamSql(userId, Types.VARCHAR);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_plandoc_get_raw_ccenter_ncentr_aloc @set = ?, @hier = ?, @cost_centre = ?, @cdriver_central = ?, @kid = ?;", Optional.of(parametri), out);
    }
    
    public static void toCsvCCenterNCentralAlocare(Integer dataset, String hier, String ccenter, Optional<Boolean> cdriver_central, String userId, BufferedWriter out) throws Exception{
        ParamSql[] parametri = new ParamSql[5];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(hier, Types.CHAR);
        parametri[2] = new ParamSql(ccenter, Types.VARCHAR);
        parametri[3] = new ParamSql(cdriver_central.isPresent() ? cdriver_central.get() : null, Types.BIT);
        parametri[4] = new ParamSql(userId, Types.VARCHAR);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToCSV("exec oxpl.prc_plandoc_get_raw_ccenter_ncentr_aloc @set = ?, @hier = ?, @cost_centre = ?,  @cdriver_central = ?, @kid = ?;", Optional.of(parametri), out);
    }
    
    public static void toXlsxCDriverCentralSumar(Integer dataset, String hier, String cdriver, Optional<Integer> ocateg, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[4];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(hier, Types.CHAR);
        parametri[2] = new ParamSql(cdriver, Types.CHAR);
        parametri[3] = new ParamSql((ocateg.isPresent() ? ocateg.get() : null), Types.INTEGER);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_plandoc_get_raw_cdriver_centr_sumar @set = ?, @hier = ?, @cdriver = ?, @ocateg = ?;", Optional.of(parametri), out);
    }
    
    public static void toXlsxCDriverCentralPozitii(Integer dataset, String hier, String cdriver, Optional<Integer> ocateg, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[4];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(hier, Types.CHAR);
        parametri[2] = new ParamSql(cdriver, Types.CHAR);
        parametri[3] = new ParamSql((ocateg.isPresent() ? ocateg.get() : null), Types.INTEGER);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_plandoc_get_raw_cdriver_centr_pozitii @set = ?, @hier = ?, @cdriver = ?, @ocateg = ?;", Optional.of(parametri), out);
    }
    
    public static void toXlsxCDriverCentralCompar(Integer dataset, Optional<Integer> comparset, String hier, String cdriver, Optional<Integer> ocateg, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[5];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(comparset.isPresent() ? comparset.get() : null, Types.INTEGER);
        parametri[2] = new ParamSql(hier, Types.CHAR);
        parametri[3] = new ParamSql(cdriver, Types.CHAR);
        parametri[4] = new ParamSql((ocateg.isPresent() ? ocateg.get() : null), Types.INTEGER);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_plandoc_get_raw_cdriver_centr_compar @main_set = ?, @compar_set = ?, @hier = ?, @cdriver = ?, @ocateg = ?;", Optional.of(parametri), out);
    }
    
    public static void toXlsxCDriverCentralAlocare(Integer dataset, String hier, String cdriver, Optional<Integer> ocateg, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[4];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(hier, Types.CHAR);
        parametri[2] = new ParamSql(cdriver, Types.CHAR);
        parametri[3] = new ParamSql((ocateg.isPresent() ? ocateg.get() : null), Types.INTEGER);
        
        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_plandoc_get_raw_cdriver_centr_aloc @set = ?, @hier = ?, @cdriver = ?, @ocateg = ?;", Optional.of(parametri), out);
    }
}
