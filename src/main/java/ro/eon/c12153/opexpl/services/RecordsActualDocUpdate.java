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
public class RecordsActualDocUpdate implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private final ColumnUpdateValueHolder<String> centru_cost_cod;
    private final ColumnUpdateValueHolder<String> centru_cost_nume;
    private final ColumnUpdateValueHolder<String> cont_ccoa_cod;
    private final ColumnUpdateValueHolder<String> cont_ccoa_nume;
    private final ColumnUpdateValueHolder<Integer> opex_categ_cod;
    private final ColumnUpdateValueHolder<String> text_antet;
    private final ColumnUpdateValueHolder<String> text_nume;
    private final ColumnUpdateValueHolder<String> obj_part_cod;
    private final ColumnUpdateValueHolder<String> obj_part_nume;
    private final ColumnUpdateValueHolder<String> part_ic;
    
    public RecordsActualDocUpdate(){        
        this.centru_cost_cod = new ColumnUpdateValueHolder<>("inrg.cost_cntr", null, true, Types.VARCHAR);
        this.centru_cost_nume = new ColumnUpdateValueHolder<>("inrg.cost_cntr_nume", null, false, Types.NVARCHAR);
        this.cont_ccoa_cod = new ColumnUpdateValueHolder<>("inrg.cont_ccoa", null, true, Types.CHAR);
        this.cont_ccoa_nume = new ColumnUpdateValueHolder<>("inrg.cont_ccoa_nume", null, false, Types.NVARCHAR);
        this.opex_categ_cod = new ColumnUpdateValueHolder<>("inrg.opex_categ", null, true, Types.INTEGER);
        this.text_antet = new ColumnUpdateValueHolder<>("inrg.text_antet", null, false, Types.NVARCHAR);
        this.text_nume = new ColumnUpdateValueHolder<>("inrg.text_nume", null, false, Types.NVARCHAR);
        this.obj_part_cod = new ColumnUpdateValueHolder<>("inrg.obj_part", null, false, Types.VARCHAR);
        this.obj_part_nume = new ColumnUpdateValueHolder<>("inrg.obj_part_nume", null, false, Types.NVARCHAR);
        this.part_ic = new ColumnUpdateValueHolder<>("inrg.part_ic", null, false, Types.VARCHAR);
    }

    public ColumnUpdateValueHolder<String> getCentru_cost_cod() {
        return centru_cost_cod;
    }

    public ColumnUpdateValueHolder<String> getCentru_cost_nume() {
        return centru_cost_nume;
    }

    public ColumnUpdateValueHolder<String> getCont_ccoa_cod() {
        return cont_ccoa_cod;
    }

    public ColumnUpdateValueHolder<String> getCont_ccoa_nume() {
        return cont_ccoa_nume;
    }

    public ColumnUpdateValueHolder<Integer> getOpex_categ_cod() {
        return opex_categ_cod;
    }

    public ColumnUpdateValueHolder<String> getText_antet() {
        return text_antet;
    }

    public ColumnUpdateValueHolder<String> getText_nume() {
        return text_nume;
    }

    public ColumnUpdateValueHolder<String> getObj_part_cod() {
        return obj_part_cod;
    }

    public ColumnUpdateValueHolder<String> getObj_part_nume() {
        return obj_part_nume;
    }

    public ColumnUpdateValueHolder<String> getPart_ic() {
        return part_ic;
    }

    public boolean hasValues(){
        if (Utils.stringNotEmpty(this.centru_cost_cod.getValue())) return true;
        if (Utils.stringNotEmpty(this.centru_cost_nume.getValue()) || this.centru_cost_nume.isGoleste()) return true;
        if (Utils.stringNotEmpty(this.cont_ccoa_cod.getValue())) return true;
        if (Utils.stringNotEmpty(this.cont_ccoa_nume.getValue()) || this.cont_ccoa_nume.isGoleste()) return true;
        if (this.opex_categ_cod.getValue() != null) return true;
        if (Utils.stringNotEmpty(this.text_antet.getValue()) || this.text_antet.isGoleste()) return true;
        if (Utils.stringNotEmpty(this.text_nume.getValue()) || this.text_nume.isGoleste()) return true;
        if (Utils.stringNotEmpty(this.obj_part_cod.getValue()) || this.obj_part_cod.isGoleste()) return true;
        if (Utils.stringNotEmpty(this.obj_part_nume.getValue()) || this.obj_part_nume.isGoleste()) return true;
        return (Utils.stringNotEmpty(this.part_ic.getValue()) || this.part_ic.isGoleste());
    }
    
    public String sqlUpdate(){
        List<Optional<String>> rezultat = new ArrayList<>();
            
        rezultat.add(sqlFieldUpdate(this.centru_cost_cod));        
        rezultat.add(sqlFieldUpdate(this.centru_cost_nume));      
        rezultat.add(sqlFieldUpdate(this.cont_ccoa_cod));        
        rezultat.add(sqlFieldUpdate(this.cont_ccoa_nume));               
        rezultat.add(sqlFieldUpdate(this.opex_categ_cod));
        rezultat.add(sqlFieldUpdate(this.text_antet));
        rezultat.add(sqlFieldUpdate(this.text_nume));
        rezultat.add(sqlFieldUpdate(this.obj_part_cod));
        rezultat.add(sqlFieldUpdate(this.obj_part_nume));
        rezultat.add(sqlFieldUpdate(this.part_ic));
        
        return rezultat.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining(", "));
    }
    
    public List<ParamSql> sqlParametri(){
        List<Optional<ParamSql>> rezultat = new ArrayList<>();
        
        rezultat.add(sqlFieldParametru(this.centru_cost_cod));        
        rezultat.add(sqlFieldParametru(this.centru_cost_nume));
        rezultat.add(sqlFieldParametru(this.cont_ccoa_cod));
        rezultat.add(sqlFieldParametru(this.cont_ccoa_nume));
        rezultat.add(sqlFieldParametru(this.opex_categ_cod));
        rezultat.add(sqlFieldParametru(this.text_antet));
        rezultat.add(sqlFieldParametru(this.text_nume));
        rezultat.add(sqlFieldParametru(this.obj_part_cod));
        rezultat.add(sqlFieldParametru(this.obj_part_nume));
        rezultat.add(sqlFieldParametru(this.part_ic));
        
        return rezultat.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
