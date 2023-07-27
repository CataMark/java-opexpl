package ro.any.c12153.opexpl.view.help;

import java.util.List;
import ro.any.c12153.opexpl.entities.CoArea;
import ro.any.c12153.opexpl.entities.DataSetPer;
import ro.any.c12153.opexpl.entities.PlanDoc;

/**
 *
 * @author C12153
 */
public interface PlanDocListContract{
    public void initAggregare(boolean newItem) throws Exception;
    public List<Short> getAni();
    public CoArea getCoarea();
    public List<DataSetPer> getPerioade();
    public List<PlanDoc> getList();
    public void setSelected(PlanDoc selected);
}
