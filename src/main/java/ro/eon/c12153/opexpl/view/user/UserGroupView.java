package ro.any.c12153.opexpl.view.user;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import ro.any.c12153.opexpl.entities.UserGroup;
import ro.any.c12153.opexpl.services.UserGroupServ;
import ro.any.c12153.shared.App;
import ro.any.c12153.shared.beans.CurrentUser;
import ro.any.c12153.shared.entities.User;

/**
 *
 * @author C12153
 */
@Named(value = "ugroup")
@RequestScoped
public class UserGroupView implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(UserGroupView.class.getName());

    private @Inject @CurrentUser User cuser;
    private String initError;
    private List<UserGroup> list;
       
    public void init() {
        try {
            this.list = UserGroupServ.getAll(cuser.getUname());            
        } catch (Exception ex) {
            App.log(LOG, Level.SEVERE, cuser.getUname(), ex);
            this.initError = ex.getMessage();
        }
    }
    
    public String getInitError() {
        return initError;
    }

    public List<UserGroup> getList() {
        return list;
    }
}
