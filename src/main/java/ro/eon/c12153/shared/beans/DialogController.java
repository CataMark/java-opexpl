package ro.any.c12153.shared.beans;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author C12153
 */
@Named(value = "dialog")
@ViewScoped
public class DialogController implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String address;
    private String title;
    
    public void clear(){
        this.address = null;
        this.title = null;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
