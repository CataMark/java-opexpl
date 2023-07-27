package ro.any.c12153.opexpl.view;

import ro.any.c12153.opexpl.entities.DataSet;

/**
 *
 * @author C12153
 */
public interface CopyFromDataSetContract {
    public DataSet getDataset();
    public void copiaza(Integer fromDataSet, String finishScript, byte alternativa);
}
