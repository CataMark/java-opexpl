package ro.any.c12153.shared;

import java.util.List;

/**
 *
 * @author C12153
 * @param <T>
 */
public interface SelectTableView<T> {
    public void newItem();
    public void passSelected(boolean initLists);
    public void clear();
    public String getInitError();
    public List<T> getList();
    public void setSelected(T selected);
}
