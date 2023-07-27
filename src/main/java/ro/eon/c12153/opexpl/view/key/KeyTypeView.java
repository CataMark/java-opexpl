package ro.any.c12153.opexpl.view.key;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexpl.entities.KeyType;
import ro.any.c12153.opexpl.services.KeyTypeServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "keyType")
@RequestScoped
public class KeyTypeView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(KeyTypeView.class.getName());

    private @Inject @CurrentUser User cuser;
    private String initError;
    private List<KeyType> list;
    
    @PostConstruct
    private void init() {
        try {
            this.list = KeyTypeServ.getAll(cuser.getUname());
            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String getInitError() {
        return initError;
    }

    public List<KeyType> getList() {
        return list;
    }
}
