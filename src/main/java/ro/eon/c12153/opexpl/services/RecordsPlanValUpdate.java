package ro.any.c12153.opexpl.services;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static ro.any.c12153.dbutils.JsfLazyDataModel.ColumnUpdateUtils.sqlFieldParametru;
import static ro.any.c12153.dbutils.JsfLazyDataModel.ColumnUpdateUtils.sqlFieldUpdate;
import ro.any.c12153.dbutils.JsfLazyDataModel.ColumnUpdateValueHolder;
import ro.any.c12153.dbutils.ParamSql;
import ro.any.c12153.shared.Utils;

/**
 *
 * @author C12153
 */
public class RecordsPlanValUpdate implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //campuri valoare
    private final ColumnUpdateValueHolder<String> cont;
    private final ColumnUpdateValueHolder<Double> valoare;
    
    public RecordsPlanValUpdate(){
        this.cont = new ColumnUpdateValueHolder<>("val.cont", null, false, Types.CHAR);
        this.valoare = new ColumnUpdateValueHolder<>("val.valoare", null, true, Types.FLOAT);
    }

    public ColumnUpdateValueHolder<String> getCont() {
        return cont;
    }

    public ColumnUpdateValueHolder<Double> getValoare() {
        return valoare;
    }
    
    public boolean hasValues(){
        if (Utils.stringNotEmpty(this.cont.getValue()) || this.cont.isGoleste()) return true;
        return (this.valoare.getValue() != null);
    }
    
    public String sqlUpdate(){
        List<Optional<String>> rezultat = new ArrayList<>();
        
        rezultat.add(sqlFieldUpdate(this.cont));
        rezultat.add(sqlFieldUpdate(this.valoare));
        
        return rezultat.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining(", "));
    }
    
    public List<ParamSql> sqlParametri(){
        List<Optional<ParamSql>> rezultat = new ArrayList<>();
        
        rezultat.add(sqlFieldParametru(this.cont));
        rezultat.add(sqlFieldParametru(this.valoare));
        
        return rezultat.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
