package bookrecommender.common.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * La classe {@code Valutazione} rappresenta una recensione eseguita da un utente
 * su un {@link Libro}. Ogni valutazione contiene:
 * <ul>
 *     <li>il nome utente che ha effettuato la recensione,</li>
 *     <li>una lista di voti (float),</li>
 *     <li>una lista di commenti testuali,</li>
 *     <li>il libro recensito.</li>
 * </ul>
 * <p>
 * Questa classe è serializzabile per permettere la trasmissione remota
 * (ad esempio via RMI) o il salvataggio su file.
 * </p>
 * @see Libro
 */
public class Valutazione implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private final Libro libro;
    private final List<Float> valutazioni;
    private final List<String> commenti;

    /**
     * Costruisce una nuova istanza di {@code Valutazione}.
     * @param u  lo username dell'utente che ha lasciato la valutazione
     * @param v  la lista di valutazioni numeriche (es. da 1.0 a 5.0)
     * @param c  la lista di commenti testuali associati alla valutazione
     * @param l  il libro oggetto della valutazione
     */
    public Valutazione(String u, List<Float> v, List<String> c, Libro l) {
        this.username = u;
        this.valutazioni = v;
        this.commenti = c;
        this.libro = l;
    }

    /**
     * Restituisce il nome utente che ha effettuato la valutazione.
     * @return lo username dell'utente
     */
    public String getUsername() {
        return username;
    }

    /**
     * Restituisce la lista delle valutazioni numeriche associate al libro.
     * @return la lista dei voti
     */
    public List<Float> getValutazioni() {
        return valutazioni;
    }

    /**
     * Restituisce la lista dei commenti testuali della valutazione.
     * @return la lista dei commenti
     */
    public List<String> getCommenti() {
        return commenti;
    }

    /**
     * Restituisce una rappresentazione testuale semplificata della valutazione,
     * contenente lo username del recensore.
     *
     * @return una stringa del tipo "Recensione di [username]"
     */
    @Override
    public String toString() {
        return ("Recensione di " + username);
    }

    /**
     * Restituisce l'identificatore del libro associato alla valutazione.
     *
     * @return l'ID del libro
     */
    public int getIdLibro() {
        return libro.getId();
    }

    /**
     * Restituisce l'oggetto {@link Libro} recensito.
     *
     * @return il libro valutato
     */
    public Libro getLibro() {
        return libro;
    }
}
