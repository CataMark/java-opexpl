package ro.any.c12153.opexpl.converter;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;
import ro.any.c12153.opexpl.entities.IcPartener;

/**
 *
 * @author C12153
 */
@FacesConverter("icPartenerPickListConverter")
public class IcPartenerPickListConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Object rezultat = null;
        if (component instanceof PickList){
            DualListModel<IcPartener> list = (DualListModel<IcPartener>) ((PickList) component).getValue();
            for(IcPartener poz : Stream.concat(list.getSource().stream(), list.getTarget().stream()).collect(Collectors.toList())){
                if (value.equals(poz.getCod() + " " + poz.getNume())){
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
        if (component instanceof PickList && value instanceof IcPartener)
            rezultat = ((IcPartener) value).getCod() + " " + ((IcPartener) value).getNume();
        return rezultat;
    }
    
}
