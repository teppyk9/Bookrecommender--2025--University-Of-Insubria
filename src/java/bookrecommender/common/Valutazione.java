package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Valutazione implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private final int idLibro;
    private final List<Float> valutazioni;
    private final List<String> commenti;

    public Valutazione(String u, List<Float> v, List<String> c, int idLibro) {
        this.username = u;
        this.valutazioni = v;
        this.commenti = c;
        this.idLibro = idLibro;
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
        return idLibro;
    }

}
