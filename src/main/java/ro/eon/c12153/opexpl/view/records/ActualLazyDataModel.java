package ro.any.c12153.opexpl.view.records;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import ro.any.c12153.dbutils.JsfLazyDataModel.LazyDataModelRecords;
import ro.any.c12153.opexpl.services.RecordsActualServ;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class ActualLazyDataModel extends LazyDataModel<Map<String, Object>> implements Serializable{
    private static final Logger LOG = Logger.getLogger(ActualLazyDataModel.class.getName());

    private final Integer dataset;
    private final String coarea;
    private final String userId;
    private final Locale clocale;
    
    private Map<String, String> filter;
    private double suma;
    
    public ActualLazyDataModel(Integer dataset, String coarea, String userId, Locale clocale){
        this.dataset = dataset;
        this.coarea = coarea;
        this.userId = userId;
        this.clocale = clocale;
    }

    @Override
    public Object getRowKey(Map<String, Object> object) {
        return (String) object.get("id");
    }

    @Override
    public Map<String, Object> getRowData(String rowKey) {
        return this.getWrappedData().stream()
                .filter(x -> rowKey.equals(x.get("id")))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Map<String, Object>> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
        List<Map<String, Object>> rezultat = new ArrayList<>();
        try {
            Map<String, String> sort = null;
            if (multiSortMeta != null && !multiSortMeta.isEmpty())
                sort = multiSortMeta.stream()
                        .collect(Collectors.toMap(
                                x -> ((String) x.getSortField()).toLowerCase(),
                                x -> (x.getSortOrder().name().startsWith("ASC")? "asc": "desc")
                        ));
            
            this.filter = null;
            if (filters != null && !filters.isEmpty())
                this.filter = filters.entrySet().stream()
                        .collect(Collectors.toMap(
                                x -> ((String) x.getKey()).toLowerCase(),
                                x -> (String) x.getValue()
                        ));
            
            LazyDataModelRecords inregs = RecordsActualServ.getLazyRecords(this.coarea, this.dataset, first, pageSize,
                    Optional.ofNullable(sort), Optional.ofNullable(this.filter), this.userId);
            rezultat = inregs.getRecords();
            this.setRowCount(inregs.getPozitii());
            this.suma = inregs.getSuma();
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, this.userId, ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, App.getBeanMess("title.records.get", this.clocale), ex.getMessage()));
        }
        return rezultat;
    }
    
    public Map<String, String> getFilter() {
        return filter;
    }

    public double getSuma() {
        return this.suma;
    }
}
