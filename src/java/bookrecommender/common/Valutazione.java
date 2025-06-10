package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Valutazione implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String username;
    private List<Float> valutazioni;
    private List<String> commenti;

    public Valutazione(String u, List<Float> v, List<String> c) {
        this.username = u;
        this.valutazioni = v;
        this.commenti = c;
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
}
