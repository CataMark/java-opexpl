package ro.any.c12153.opexpl.view.help;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author C12153
 */
public class KeyValRow implements Serializable{
    private static final long serialVersionUID = 1L;
        
    private String cod;
    private String segment;
    private String nume;
    private List<KeyValCell> cells;

    public KeyValRow() {
    }

    public KeyValRow(String cod, String segment, String nume, List<KeyValCell> cells) {
        this.cod = cod;
        this.segment = segment;
        this.nume = nume;
        this.cells = cells;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.cod);
        hash = 23 * hash + Objects.hashCode(this.segment);
        hash = 23 * hash + Objects.hashCode(this.nume);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (this.getClass().isInstance(o)) return o.hashCode() == this.hashCode();
        return false;
    }
    
    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public List<KeyValCell> getCells() {
        return cells;
    }

    public void setCells(List<KeyValCell> cells) {
        this.cells = cells;
    }

    public KeyValCell getCellByAn(short an){
        KeyValCell rezultat = null;
        for (KeyValCell cell : this.cells){
            if (cell.getAn() == an) {
                rezultat = cell;
                break;
            }
        }
        return rezultat;
    }
}
