package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Valutazione implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private final Libro libro;
    private final List<Float> valutazioni;
    private final List<String> commenti;

    public Valutazione(String u, List<Float> v, List<String> c, Libro l) {
        this.username = u;
        this.valutazioni = v;
        this.commenti = c;
        this.libro = l;
    }

    public String getUsername() {
        return username;
    }
    public List<Float> getValutazioni() {
        return valutazioni;
    }
    public List<String> getCommenti() {
        return commenti;
    }

    @Override
    public String toString() {
        return ("Recensione di " + username);
    }

    public int getIdLibro() {
        return libro.getId();
    }
    public Libro getLibro() {
        return libro;
    }
}
