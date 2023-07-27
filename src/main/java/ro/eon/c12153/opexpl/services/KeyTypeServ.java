package ro.any.c12153.opexpl.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ro.any.c12153.opexpl.entities.KeyType;
import ro.any.c12153.shared.App;

/**
 *
 * @author C12153
 */
public class KeyTypeServ {
    
    public static List<KeyType> getAll(String userId) throws Exception{
        return App.getConn(userId)
                .getFromCallableStatement("{call oxpl.prc_cheie_tip_get_list_all}", Optional.empty()).stream()
                .map(KeyType::new)
                .collect(Collectors.toList());
    }
}
