package ro.any.c12153.opexpl.services;

import java.io.OutputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
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
public class RecordsActualServ {

    public static LazyDataModelRecords getLazyRecords(String coarea, Integer dataset, int after, int size,
            Optional<Map<String, String>> sort, Optional<Map<String, String>> filter, String userId) throws Exception{
        
        //pregatire baza sql
        String sqlBase = String.format(App.getSql("recs_actual_lazy_base"),
                (sort.isPresent() ? getSortSql(sort.get()) : ""), (filter.isPresent() ? getFilterSql(filter.get()) : "")
        );
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
    
    public static String deleteByFilter(String coarea, Integer dataset, Optional<Map<String, String>> filter, String userId) throws Exception{
        String sql = String.format(App.getSql("recs_actual_lazy_delete_by_filter"), (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] parametri = new ParamSql[3];
        parametri[0] = new ParamSql(userId, Types.VARCHAR);
        parametri[1] = new ParamSql(dataset, Types.INTEGER);
        parametri[2] = new ParamSql(coarea, Types.CHAR);
            
        return App.getConn(userId).executePreparedStatement(sql, Optional.of(parametri));
    }
    
    public static String deleteById(List<String> pozitii, String userId) throws Exception{
        List<Optional<ParamSql[]>> parametri = pozitii.stream()
                .map(x -> Optional.of(new ParamSql[]{new ParamSql(userId, Types.VARCHAR), new ParamSql(x, Types.VARCHAR)}))
                .collect(Collectors.toList());
            
        return App.getConn(userId).executePreparedStatement("exec oxpl.prc_recs_actual_delete_by_id @kid = ?, @id = ?;", parametri);
    }
    
    public static String updateByFilter(String coarea, Integer dataset, Optional<Map<String, String>> filter, RecordsActualDocUpdate updateValues, String userId) throws Exception{
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
            for (int i = 0; i < paramsUpdateValues.size(); i++){
                parametri[i + 1] = paramsUpdateValues.get(i);
            }
            parametri[paramsUpdateValues.size() + 1] = new ParamSql(dataset, Types.INTEGER);
            parametri[paramsUpdateValues.size() + 2] = new ParamSql(coarea, Types.CHAR);
        }

        String sql = String.format(App.getSql("recs_actual_lazy_update_by_filter"), sqlUpdate, (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        return App.getConn(userId).executePreparedStatement(sql, Optional.of(parametri));
    }
    
    public static String updateById(List<String> pozitii, RecordsActualDocUpdate updateValues, String userId) throws Exception{
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
                    for (int i = 0; i < paramsUpdateValues.size(); i++){
                        params[i + 1] = paramsUpdateValues.get(i);
                    }
                    params[paramsUpdateValues.size() + 1] = new ParamSql(x, Types.VARCHAR);
                    return Optional.of(params);
                })
                .collect(Collectors.toList());
        }

        String sql = String.format(App.getSql("recs_actual_lazy_update_by_id"), sqlUpdate);
        return App.getConn(userId).executePreparedStatement(sql, parametri);
    }
    
    public static void recordsToXlsx(String coarea, Integer dataset, Optional<Map<String, String>> filter, String userId, OutputStream out) throws Exception{
        String sql = String.format(App.getSql("recs_actual_lazy_get_list_all"), (filter.isPresent() ? getFilterSql(filter.get()) : ""));
        ParamSql[] parametri = new ParamSql[2];
        parametri[0] = new ParamSql(dataset, Types.INTEGER);
        parametri[1] = new ParamSql(coarea, Types.CHAR);

        App.getConn(userId).downloadFromPreparedStmtToXLSX(sql, Optional.of(parametri), out);
    }
}
