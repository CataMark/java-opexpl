package ro.any.c12153.opexpl.services;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.opexpl.entities.DataSet;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class DataSetServ {
    
    public static Optional<DataSet> getById(Integer id, String userId) throws Exception{
        ParamSql[] parametri = new ParamSql[1];
        parametri[0] = new ParamSql(id, Types.INTEGER);

        return App.getConn(userId)
                .getFromPreparedStatement("select * from oxpl.fnc_dataset_get_by_id(?);", Optional.of(parametri)).stream()
                .map(DataSet::new)
                .findFirst();
    }
    
    public static List<DataSet> getNotBlocked(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_not_blocked}", Optional.empty()).stream()
                .map(DataSet::new)
                .collect(Collectors.toList());
    }
    
    public static List<DataSet> getPlanNotBlocked(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_plan_not_blocat}", Optional.empty()).stream()
                .map(DataSet::new)
                .collect(Collectors.toList());
    }
    
    public static List<DataSet> getPlanNotClosed(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_plan_not_incheiat}", Optional.empty()).stream()
                .map(DataSet::new)
                .collect(Collectors.toList());
    }
    
    public static List<DataSet> getActualNotClosed(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_actual_not_incheiat}", Optional.empty()).stream()
                .map(DataSet::new)
                .collect(Collectors.toList());
    }
    
    public static List<DataSet> getPlanNotClosedWithRef(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_plan_not_incheiat_with_ref}", Optional.empty()).stream()
                .map(DataSet::new)
                .collect(Collectors.toList());
    }
    
    public static List<DataSet> getAllByAn(Short an, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("an", new ParamSql(an, Types.SMALLINT));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_by_an(?)}", parametri).stream()
                .map(DataSet::new)
                .collect(Collectors.toList());
    }
    
    public static List<DataSet> getPlanByAn(Short an, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("an", new ParamSql(an, Types.SMALLINT));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_plan_by_an(?)}", parametri).stream()
                .map(DataSet::new)
                .collect(Collectors.toList());
    }
    
    public static List<DataSet> getRaportareByAn(Short an, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("an", new ParamSql(an, Types.SMALLINT));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_plan_raportare_by_an(?)}", parametri).stream()
                .map(DataSet::new)
                .collect(Collectors.toList());
    }
    
    public static List<DataSet> getRaportareAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_raportare_all}", Optional.empty()).stream()
                .map(DataSet::new)
                .collect(Collectors.toList());
    }
    
    public static List<DataSet> getChildren(Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(dataset, Types.INTEGER));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_childs(?)}", parametri).stream()
                .map(DataSet::new)
                .collect(Collectors.toList());
    }
    
    public static List<Short> getAllAni(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_ani}", Optional.empty()).stream()
                .flatMap(x -> x.values().stream())
                .map(x -> (Short) x)
                .collect(Collectors.toList());
    }
    
    public static List<Short> getPlanAni(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_plan_ani}", Optional.empty()).stream()
                .flatMap(x -> x.values().stream())
                .map(x -> (Short) x)
                .collect(Collectors.toList());
    }
    
    public static List<Short> getRaportareAni(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_get_list_raportare_ani}", Optional.empty()).stream()
                .flatMap(x -> x.values().stream())
                .map(x -> (Short) x)
                .collect(Collectors.toList());
    }
    
    public static int noOfPeriods(Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(dataset, Types.INTEGER));
            
        return (int) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_no_of_periods(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(0);
    }
    
    public static boolean hasValues(Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(dataset, Types.INTEGER));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_has_values(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
    
    public static Optional<DataSet> insert(DataSet inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.VARCHAR));
        parametri.put("an", new ParamSql(inreg.getAn(), Types.SMALLINT));
        parametri.put("vers", new ParamSql(inreg.getVers(), Types.CHAR));
        parametri.put("compar", new ParamSql(inreg.getCompar(), Types.INTEGER));
        parametri.put("actset", new ParamSql(inreg.getActual_set(), Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_insert_return(?,?,?,?,?,?)}", parametri).stream()
                .map(DataSet::new)
                .findFirst();
    }
    
    public static Optional<DataSet> update(DataSet inreg, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(inreg.getId(), Types.INTEGER));
        parametri.put("nume", new ParamSql(inreg.getNume(), Types.VARCHAR));
        parametri.put("vers", new ParamSql(inreg.getVers(), Types.CHAR));
        parametri.put("blocat", new ParamSql(inreg.getBlocat(), Types.BIT));
        parametri.put("incheiat", new ParamSql(inreg.getIncheiat(), Types.BIT));
        parametri.put("raportare", new ParamSql(inreg.getRaportare(), Types.BIT));
        parametri.put("compar", new ParamSql(inreg.getCompar(), Types.INTEGER));
        parametri.put("actset", new ParamSql(inreg.getActual_set(), Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_update_return(?,?,?,?,?,?,?,?,?)}", parametri).stream()
                .map(DataSet::new)
                .findFirst();
    }
    
    public static boolean delete (Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("id", new ParamSql(dataset, Types.INTEGER));

        return (boolean) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_dataset_delete_return(?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(false);
    }
}
