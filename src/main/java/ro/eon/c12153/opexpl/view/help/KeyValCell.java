package ro.any.c12153.opexpl.view.help;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author C12153
 */
public class KeyValCell implements Serializable{
    private static final long serialVersionUID = 1L;
        
    private String id;
    private short an;
    private double valoare;
    private Date mod_timp;

    public KeyValCell() {
    }

    public KeyValCell(String id, short an, double valoare, Date mod_de) {
        this.id = id;
        this.an = an;
        this.valoare = valoare;
        this.mod_timp = mod_de;
    }   

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + this.an;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.valoare) ^ (Double.doubleToLongBits(this.valoare) >>> 32));
        hash = 53 * hash + Objects.hashCode(this.mod_timp);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (this.getClass().isInstance(o)) return o.hashCode() == this.hashCode();
        return false;
    }
    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public short getAn() {
        return an;
    }

    public void setAn(short an) {
        this.an = an;
    }

    public double getValoare() {
        return valoare;
    }

    public void setValoare(double valoare) {
        this.valoare = valoare;
    }

    public Date getMod_timp() {
        return mod_timp;
    }

    public void setMod_timp(Date mod_timp) {
        this.mod_timp = mod_timp;
    }
}
