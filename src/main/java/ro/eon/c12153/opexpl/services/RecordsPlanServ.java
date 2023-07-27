package ro.any.c12153.opexpl.services;

import java.io.OutputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ro.any.c12153.dbutils.JsfLazyDataModel.LazyDataModelRecords;
import static ro.any.c12153.dbutils.JsfLazyDataModel.LazyRecordsUtils.getFilterSql;
import static ro.any.c12153.dbutils.JsfLazyDataModel.LazyRecordsUtils.getSortSql;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class RecordsPlanServ {
        
    public static LazyDataModelRecords getLazyRecords(String coarea, Integer dataset, int after, int size,
            Optional<Map<String, String>> sort, Optional<Map<String, String>> filter, String userId) throws Exception{
        
        //pregatire baza sql
        String sqlBase = String.format(App.getSql("recs_plan_lazy_base"),
                (sort.isPresent() ? getSortSql(sort.get()) : ""), (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] paramBase = new ParamSql[]{new ParamSql(dataset, Types.INTEGER), new ParamSql(coarea, Types.CHAR)};

        //obtine inregistrari
        String sqlRecs = String.format(App.getSql("recs_lazy_get_list"), sqlBase, size);
        ParamSql[] paramRecs = Stream.concat(Arrays.stream(paramBase), Arrays.stream(new ParamSql[]{new ParamSql(after, Types.INTEGER)}))
                .toArray(ParamSql[]::new);
        CompletableFuture<List<Map<String, Object>>> fRecords = CompletableFuture.supplyAsync(() -> {
            List<Map<String, Object>> result = new ArrayList<>();
            try {
                result = App.getConn(userId).getFromPreparedStatement(sqlRecs, Optional.of(paramRecs));
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
            return result;
        });        
            
        //obtine numar pozitii
        String sqlPoz = String.format(App.getSql("recs_lazy_get_count"), sqlBase);
        CompletableFuture<Integer> fPozitii = CompletableFuture.supplyAsync(() -> {
            int result = 0;
            try {
                result = (int) App.getConn(userId)
                        .getFromPreparedStatement(sqlPoz, Optional.of(paramBase)).stream()
                        .flatMap(x -> x.values().stream())
                        .findFirst()
                        .orElse(0);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
            return result;
        });        

        //obtine suma
        String sqlSum = String.format(App.getSql("recs_lazy_get_sum"), sqlBase);
        CompletableFuture<Double> fSuma = CompletableFuture.supplyAsync(() -> {
            double result = 0;
            try {
                result = (double) App.getConn(userId)
                        .getFromPreparedStatement(sqlSum, Optional.of(paramBase)).stream()
                        .flatMap(x -> x.values().stream())
                        .findFirst()
                        .orElse(0);
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
            return result;
        });        
        
        LazyDataModelRecords rezultat = new LazyDataModelRecords();
        rezultat.setRecords(fRecords.get());
        rezultat.setPozitii(fPozitii.get());
        rezultat.setSuma(fSuma.get());
        return rezultat;
    }
    
    public static String deleteDocByFilter(String coarea, Integer dataset, Optional<Map<String, String>> filter, String userId) throws Exception{
        String sql = String.format(App.getSql("recs_plan_lazy_delete_doc_by_filter"), (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] parametri = new ParamSql[3];
        parametri[0] = new ParamSql(userId, Types.VARCHAR);
        parametri[1] = new ParamSql(dataset, Types.INTEGER);
        parametri[2] = new ParamSql(coarea, Types.CHAR);

        return App.getConn(userId).executePreparedStatement(sql, Optional.of(parametri));
    }
    
    public static String deleteDocById(List<String> pozitii, String userId) throws Exception{
        List<Optional<ParamSql[]>> parametri = pozitii.stream()
                .map(x -> Optional.of(new ParamSql[]{new ParamSql(userId, Types.VARCHAR), new ParamSql(x, Types.VARCHAR)}))
                .collect(Collectors.toList());

        return App.getConn(userId).executePreparedStatement("exec oxpl.prc_recs_plan_delete_doc_by_id @kid = ?, @id = ?;", parametri);
    }
    
    public static String updateDocByFilter(String coarea, Integer dataset, Optional<Map<String, String>> filter,
            RecordsPlanDocUpdate updateValues, String userId) throws Exception{
        
        String sqlUpdate = updateValues.sqlUpdate();
        List<ParamSql> paramsUpdateValues = updateValues.sqlParametri();
        ParamSql[] parametri;

        if (paramsUpdateValues.isEmpty()){
            parametri = new ParamSql[3];
            parametri[0] = new ParamSql(userId, Types.VARCHAR);
            parametri[1] = new ParamSql(dataset, Types.INTEGER);
            parametri[2] = new ParamSql(coarea, Types.CHAR);
        } else {
            parametri = new ParamSql[paramsUpdateValues.size() + 3];

            parametri[0] = new ParamSql(userId, Types.VARCHAR);
            parametri[1] = new ParamSql(dataset, Types.INTEGER);
            parametri[2] = new ParamSql(coarea, Types.CHAR);
            for (int i = 0; i < paramsUpdateValues.size(); i++){
                parametri[i + 3] = paramsUpdateValues.get(i);
            }
        }

        String sql = String.format(App.getSql("recs_plan_lazy_update_doc_by_filter"), sqlUpdate, (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        return App.getConn(userId).executePreparedStatement(sql, Optional.of(parametri));
    }
    
    public static String updateDocById(List<String> pozitii, RecordsPlanDocUpdate updateValues, String userId) throws Exception{
        String sqlUpdate = updateValues.sqlUpdate();
        List<ParamSql> paramsUpdateValues = updateValues.sqlParametri();
        List<Optional<ParamSql[]>> parametri;

        if (paramsUpdateValues.isEmpty()){
            parametri = pozitii.stream()
                    .map(x -> Optional.of(new ParamSql[]{new ParamSql(userId, Types.VARCHAR), new ParamSql(x, Types.VARCHAR)}))
                    .collect(Collectors.toList());
        } else {
            parametri = pozitii.stream()
                .map(x -> {
                    ParamSql[] params = new ParamSql[paramsUpdateValues.size() + 2];

                    params[0] = new ParamSql(userId, Types.VARCHAR);
                    params[1] = new ParamSql(x, Types.VARCHAR);
                    for (int i = 0; i < paramsUpdateValues.size(); i++){
                        params[i + 2] = paramsUpdateValues.get(i);
                    }
                    return Optional.of(params);
                })
                .collect(Collectors.toList());
        }
        String sql = String.format(App.getSql("recs_plan_lazy_update_doc_by_id"), sqlUpdate);
        return App.getConn(userId).executePreparedStatement(sql, parametri);
    }
    
    public static String deleteValByFilter(String coarea, Integer dataset, Optional<Map<String, String>> filter, String userId) throws Exception{
        String sql = String.format(App.getSql("recs_plan_lazy_delete_val_by_filter"), (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] parametri = new ParamSql[3];
        parametri[0] = new ParamSql(userId, Types.VARCHAR);
        parametri[1] = new ParamSql(dataset, Types.INTEGER);
        parametri[2] = new ParamSql(coarea, Types.CHAR);

        return App.getConn(userId).executePreparedStatement(sql, Optional.of(parametri));
    }
    
    public static String deleteValById(List<String> pozitii, String userId) throws Exception{
        List<Optional<ParamSql[]>> parametri = pozitii.stream()
                .map(x -> Optional.of(new ParamSql[]{new ParamSql(userId, Types.VARCHAR), new ParamSql(x, Types.VARCHAR)}))
                .collect(Collectors.toList());
        return App.getConn(userId).executePreparedStatement("exec oxpl.prc_recs_plan_delete_val_by_id @kid = ?, @id = ?;", parametri);
    }
    
    public static String updateValByFilter(String coarea, Integer dataset, Optional<Map<String, String>> filter,
            RecordsPlanValUpdate updateValues, String userId) throws Exception{
        
        String sqlUpdate = updateValues.sqlUpdate();
        List<ParamSql> paramsUpdateValues = updateValues.sqlParametri();
        ParamSql[] parametri;

        if (paramsUpdateValues.isEmpty()){
            parametri = new ParamSql[3];
            parametri[0] = new ParamSql(userId, Types.VARCHAR);
            parametri[2] = new ParamSql(dataset, Types.INTEGER);
            parametri[3] = new ParamSql(coarea, Types.CHAR);
        } else {
            parametri = new ParamSql[paramsUpdateValues.size() + 3];

            parametri[0] = new ParamSql(userId, Types.VARCHAR);
            parametri[1] = new ParamSql(dataset, Types.INTEGER);
            parametri[2] = new ParamSql(coarea, Types.CHAR);
            for (int i = 0; i < paramsUpdateValues.size(); i++){
                parametri[i + 3] = paramsUpdateValues.get(i);
            }
        }
        String sql = String.format(App.getSql("recs_plan_lazy_update_val_by_filter"), sqlUpdate, (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        return App.getConn(userId).executePreparedStatement(sql, Optional.of(parametri));
    }
    
    public static String updateValById(List<String> pozitii, RecordsPlanValUpdate updateValues, String userId) throws Exception{
        String sqlUpdate = updateValues.sqlUpdate();
        List<ParamSql> paramsUpdateValues = updateValues.sqlParametri();
        List<Optional<ParamSql[]>> parametri;

        if (paramsUpdateValues.isEmpty()){
            parametri = pozitii.stream()
                    .map(x -> Optional.of(new ParamSql[]{new ParamSql(userId, Types.VARCHAR), new ParamSql(x, Types.VARCHAR)}))
                    .collect(Collectors.toList());
        } else {
            parametri = pozitii.stream()
                .map(x -> {
                    ParamSql[] params = new ParamSql[paramsUpdateValues.size() + 2];

                    params[0] = new ParamSql(userId, Types.VARCHAR);
                    params[1] = new ParamSql(x, Types.VARCHAR);
                    for (int i = 0; i < paramsUpdateValues.size(); i++){
                        params[i + 2] = paramsUpdateValues.get(i);
                    }
                    return Optional.of(params);
                })
                .collect(Collectors.toList());
        }
        String sql = String.format(App.getSql("recs_plan_lazy_update_val_by_id"), sqlUpdate);
        return App.getConn(userId).executePreparedStatement(sql, parametri);
    }
    
    public static double takeOverActual(String hier, Integer dataset, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("hier", new ParamSql(hier, Types.CHAR));
        parametri.put("set", new ParamSql(dataset, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));

        return (double) App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_recs_plan_take_over_actuals(?,?,?)}", parametri).stream()
                .flatMap(x -> x.values().stream())
                .findFirst()
                .orElse(0);
    }
    
    public static String takeOverAssign(String coarea, Integer fromSet, Integer toSet, String userId) throws Exception{
        Map<String, ParamSql> parametri = new HashMap<>();
        parametri.put("coarea", new ParamSql(coarea, Types.CHAR));
        parametri.put("from_set", new ParamSql(fromSet, Types.INTEGER));
        parametri.put("dest_set", new ParamSql(toSet, Types.INTEGER));
        parametri.put("kid", new ParamSql(userId, Types.VARCHAR));
        
        return App.getConn(userId)
                .executeCallableStatement("{call oxpl.prc_recs_plan_take_over_asign(?,?,?,?)}", parametri);
    }
    
    public static void recordsToXlsx(String coarea, Integer dataset, Optional<Map<String, String>> filter, String userId, OutputStream out) throws Exception{
        String sql = String.format(App.getSql("recs_plan_lazy_get_list_all"), (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(coarea, Types.CHAR);

        App.getConn(userId).downloadFromPreparedStmtToXLSX(sql, Optional.of(parametri), out);
    }
}
