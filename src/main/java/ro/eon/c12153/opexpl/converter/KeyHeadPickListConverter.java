package ro.any.c12153.opexpl.converter;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;
import ro.any.c12153.opexpl.entities.KeyHead;

/**
 *
 * @author C12153
 */
@FacesConverter("keyHeadPickListConverter")
public class KeyHeadPickListConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Object rezultat = null;
        if (component instanceof PickList){
            DualListModel<KeyHead> list = (DualListModel<KeyHead>) ((PickList) component).getValue();
            for(KeyHead poz : Stream.concat(list.getSource().stream(), list.getTarget().stream()).collect(Collectors.toList())){
                if (value.equals(poz.getTip() + " " + poz.getId() + " " + poz.getNume())){
                    rezultat = poz;
                    break;
                }
            }
        }
        return rezultat;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String rezultat = "";
        if (component instanceof PickList && value instanceof KeyHead)
            rezultat = ((KeyHead) value).getTip() + " " + ((KeyHead) value).getId() + " " + ((KeyHead) value).getNume();
        return rezultat;
    }
    
}
