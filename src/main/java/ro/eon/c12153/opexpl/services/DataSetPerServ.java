package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.DataSetPer;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class DataSetPerServ {

    public static Optional<DataSetPer> getById(String id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(id, Types.VARCHAR);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_datasetper_get_by_id(?);", Optional.of(parametri)).stream()
                .map(DataSetPer::new)
                .findFirst();
    }
    
    public static List<DataSetPer> getByDataSet(Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_datasetper_get_list_by_data_set(?)}", parametri).stream()
                .map(DataSetPer::new)
                .collect(Collectors.toList());
    }
    
    public static Optional<DataSetPer> insert (DataSetPer inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(inreg.getData_set(), Types.INTEGER));
        parametri.put("an", new ParamSql(inreg.getAn(), Types.SMALLINT));
        parametri.put("per", new ParamSql(inreg.getPer(), Types.CHAR));
        parametri.put("actual", new ParamSql(inreg.getActual(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_datasetper_insert_return(?,?,?,?,?)}", parametri).stream()
                .map(DataSetPer::new)
                .findFirst();
    }
    
    public static Optional<DataSetPer> update(DataSetPer inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(inreg.getId(), Types.VARCHAR));
        parametri.put("actual", new ParamSql(inreg.getActual(), Types.BIT));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_datasetper_update_return(?,?,?)}", parametri).stream()
                .map(DataSetPer::new)
                .findFirst();
    }
    
    public static boolean deleteById(String id, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(id, Types.VARCHAR));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_datasetper_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static String deleteAllByDataSet(Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        
        return App.getConn(userId)
                .executeCallableStatement("{call oxpl.prc_datasetper_delete_all_by_data_set(?)}", parametri);
    }
    
    public static String takeOverFromSet(Integer dest_set, Integer from_set, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("dest_set", new ParamSql(dest_set, Types.INTEGER));
        parametri.put("from_set", new ParamSql(from_set, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .executeCallableStatement("{call oxpl.prc_datasetper_take_over(?,?,?)}", parametri);
    }
}
