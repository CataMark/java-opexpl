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
public class RecordsPlanDocUpdate implements Serializable{
    private static final long serialVersionUID = 1L;
    
    //campuri document
    private final ColumnUpdateValueHolder<String> uid;
    private final ColumnUpdateValueHolder<String> descriere;
    private final ColumnUpdateValueHolder<String> centru_cost;
    private final ColumnUpdateValueHolder<Integer> cheie;
    private final ColumnUpdateValueHolder<Integer> opex_categ;
    private final ColumnUpdateValueHolder<String> part_ic;
    
    public RecordsPlanDocUpdate(){
        this.uid = new ColumnUpdateValueHolder<>("doc.uid", null, false, Types.VARCHAR);
        this.descriere = new ColumnUpdateValueHolder<>("doc.descr", null, true, Types.NVARCHAR);
        this.centru_cost = new ColumnUpdateValueHolder<>("doc.cost_centre", null, true, Types.VARCHAR);
        this.cheie = new ColumnUpdateValueHolder<>("doc.cheie", null, false, Types.INTEGER);
        this.opex_categ = new ColumnUpdateValueHolder<>("doc.opex_categ", null, true, Types.INTEGER);
        this.part_ic = new ColumnUpdateValueHolder<>("doc.ic_part", null, false, Types.VARCHAR);
    }

    public ColumnUpdateValueHolder<String> getUid() {
        return uid;
    }

    public ColumnUpdateValueHolder<String> getDescriere() {
        return descriere;
    }

    public ColumnUpdateValueHolder<String> getCentru_cost() {
        return centru_cost;
    }

    public ColumnUpdateValueHolder<Integer> getCheie() {
        return cheie;
    }

    public ColumnUpdateValueHolder<Integer> getOpex_categ() {
        return opex_categ;
    }

    public ColumnUpdateValueHolder<String> getPart_ic() {
        return part_ic;
    }
    
    public boolean hasValues(){
        if (Utils.stringNotEmpty(this.uid.getValue()) || this.uid.isGoleste()) return true;
        if (Utils.stringNotEmpty(this.descriere.getValue())) return true;
        if (Utils.stringNotEmpty(this.centru_cost.getValue())) return true;
        if (this.cheie.getValue() != null || this.cheie.isGoleste()) return true;
        if (this.opex_categ.getValue() != null) return true;
        return (Utils.stringNotEmpty(this.part_ic.getValue()) || this.part_ic.isGoleste());
    }
    
    public String sqlUpdate(){
        List<Optional<String>> rezultat = new ArrayList<>();
        
        rezultat.add(sqlFieldUpdate(this.uid));
        rezultat.add(sqlFieldUpdate(this.descriere));
        rezultat.add(sqlFieldUpdate(this.centru_cost));
        rezultat.add(sqlFieldUpdate(this.cheie));
        rezultat.add(sqlFieldUpdate(this.opex_categ));
        rezultat.add(sqlFieldUpdate(this.part_ic));
        
        return rezultat.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining(", "));
    }
    
    public List<ParamSql> sqlParametri(){
        List<Optional<ParamSql>> rezultat = new ArrayList<>();
        
        rezultat.add(sqlFieldParametru(this.uid));
        rezultat.add(sqlFieldParametru(this.descriere));
        rezultat.add(sqlFieldParametru(this.centru_cost));
        rezultat.add(sqlFieldParametru(this.cheie));
        rezultat.add(sqlFieldParametru(this.opex_categ));
        rezultat.add(sqlFieldParametru(this.part_ic));
        
        return rezultat.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
