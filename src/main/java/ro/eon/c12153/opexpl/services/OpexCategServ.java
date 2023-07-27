package ro.any.c12153.opexpl.services;

import java.io.OutputStream;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.OpexCateg;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class OpexCategServ {
    
    public static Optional<OpexCateg> getByCod(Integer cod, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cod, Types.INTEGER);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_opexcateg_get_by_cod(?);", Optional.of(parametri)).stream()
                .map(OpexCateg::new)
                .findFirst();
    }
    
    public static List<OpexCateg> getByCostDriver(String cdriver, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cost_driver", new ParamSql(cdriver, Types.CHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_opexcateg_get_list_by_cost_driver(?)}", parametri).stream()
                .map(OpexCateg::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<OpexCateg> insert(OpexCateg inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("cost_driver", new ParamSql(inreg.getCost_driver(), Types.CHAR));
        parametri.put("cont_ccoa", new ParamSql(inreg.getCont_ccoa(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_opexcateg_insert_return(?,?,?,?)}", parametri).stream()
                .map(OpexCateg::new)
                .findFirst();
    }
    
    public static Optional<OpexCateg> update(OpexCateg inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(inreg.getCod(), Types.INTEGER));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.NVARCHAR));
        parametri.put("cont_ccoa", new ParamSql(inreg.getCont_ccoa(), Types.CHAR));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_opexcateg_update_return(?,?,?,?)}", parametri).stream()
                .map(OpexCateg::new)
                .findFirst();
    }
    
    public static boolean delete(Integer cod, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.INTEGER));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_opexcateg_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void listByCostDriverToXlsx(String cdriver, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(cdriver, Types.CHAR);

        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_opexcateg_get_list_by_cost_driver @cost_driver = ?;", Optional.of(parametri), out);
    }
    
    public static void allToXlsx(String userId, OutputStream out) throws Exception{
        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_opexcateg_get_list_all;", Optional.empty(), out);
    }
    
    public static Optional<OpexCateg> getAssignByCod(Integer cod, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("cod", new ParamSql(cod, Types.INTEGER));
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_opexcateg_asign_get_by_cod_and_coarea(?,?)}", parametri).stream()
                .map(OpexCateg::new)
                .findFirst();
    }
    
    public static List<OpexCateg> getAssignByCostDriver(String coarea, String cdriver, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("cost_driver", new ParamSql(cdriver, Types.CHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_opexcateg_asign_get_list_by_coarea_and_driver(?,?)}", parametri).stream()
                .map(OpexCateg::new)
                .collect(Collectors.toList());
    }
    
    public static List<OpexCateg> getAssignByCoarea(String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_opexcateg_asign_get_list_by_coarea(?)}", parametri).stream()
                .map(OpexCateg::new)
                .collect(Collectors.toList());
    }
    
    public static List<OpexCateg> getNotAssign(String coarea, String cdriver, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("cost_driver", new ParamSql(cdriver, Types.CHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_opexcateg_asign_get_list_not_asign(?,?)}", parametri).stream()
                .map(OpexCateg::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<OpexCateg> insertAssign(OpexCateg inreg, String coarea, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("opex_categ", new ParamSql(inreg.getCod(), Types.INTEGER));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_opexcateg_asign_insert_return(?,?,?,?)}", parametri).stream()
                .map(OpexCateg::new)
                .findFirst();
    }
    
    public static Optional<OpexCateg> updateAssign(OpexCateg inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(inreg.getId(), Types.VARCHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_opexcateg_asign_update_return(?,?,?)}", parametri).stream()
                .map(OpexCateg::new)
                .findFirst();
    }
    
    public static boolean deleteAssign(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_opexcateg_asign_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static void listAssignByCoAreaToXlsx(String coarea, String userId, OutputStream out) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(coarea, Types.CHAR);

        App.getConn(userId)
                .downloadFromPreparedStmtToXLSX("exec oxpl.prc_opexcateg_asign_get_list_by_coarea @coarea = ?;", Optional.of(parametri), out);
    }
}
