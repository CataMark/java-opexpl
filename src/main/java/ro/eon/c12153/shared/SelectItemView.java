package ro.any.c12153.shared;

/**
 *
 * @author C12153
 * @param <T>
 */
public interface SelectItemView<T> {
    public void initLists();
    public void clear();
    public String getInitError();
    public T getSelected();
    public void setSelected(T selected);
    public String getFinishScript();
    public void setFinishScript(String finishScript);
}
