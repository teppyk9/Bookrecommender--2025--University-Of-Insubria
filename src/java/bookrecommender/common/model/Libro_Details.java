package bookrecommender.common.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;

/**
 * Rappresenta i dettagli approfonditi di un libro, includendo le valutazioni degli utenti e i libri consigliati.
 * La classe calcola automaticamente le medie delle singole metriche valutative (stile, contenuto, ecc.)
 * al momento della creazione dell'oggetto.
 * È utilizzata tipicamente per arricchire la visualizzazione delle informazioni su un libro nel client.
 */
public class Libro_Details implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Mappa contenente consigli, raggruppati per categoria (es. "Simili", "Dello stesso autore", ecc.) */
    private final Hashtable<String, List<Libro>> consigli;

    /** Lista delle valutazioni assegnate dagli utenti al libro */
    private final List<Valutazione> valutazioni;

    private float mStile, mContenuto, mGradevolezza, mOriginalita, mEdizione, mFinale;

    /**
     * Costruttore che inizializza i dettagli del libro.
     * Calcola le medie delle singole metriche in base alla lista di valutazioni fornita.
     * @param consigli mappa dei consigli suddivisi per categoria
     * @param valutazioni lista delle valutazioni ricevute dal libro
     */
    public Libro_Details(Hashtable<String, List<Libro>> consigli, List<Valutazione> valutazioni) {
        this.consigli = consigli;
        this.valutazioni = valutazioni;
        this.mStile = 0;
        this.mContenuto = 0;
        this.mGradevolezza = 0;
        this.mOriginalita = 0;
        this.mEdizione = 0;
        this.mFinale = 0;
        for(Valutazione v : valutazioni) {
            List<Float> vList = v.getValutazioni();
            mStile += vList.get(0);
            mContenuto += vList.get(1);
            mGradevolezza += vList.get(2);
            mOriginalita += vList.get(3);
            mEdizione += vList.get(4);
            mFinale += vList.get(5);
        }
    }

    /**
     * Restituisce la mappa dei consigli associati al libro.
     * @return mappa consigliata, con chiavi descrittive e valori come liste di libri consigliati
     */
    public Hashtable<String, List<Libro>> getConsigli() {
        return consigli;
    }

    /**
     * Restituisce la lista delle valutazioni fornite dagli utenti per questo libro.
     * @return lista di oggetti {@link Valutazione}
     */
    public List<Valutazione> getValutazioni() {
        return valutazioni;
    }

    /**
     * Restituisce la media delle valutazioni per la metrica "Stile"-
     * @return media dello stile, oppure 0 se nessuna valutazione è presente
     */
    public float getmStile() {
        return mStile / valutazioni.size();
    }

    /**
     * Restituisce la media delle valutazioni per la metrica "Contenuto".
     * @return media del contenuto, oppure 0 se nessuna valutazione è presente
     */
    public float getmContenuto() {
        return mContenuto / valutazioni.size();
    }

    /**
     * Restituisce la media delle valutazioni per la metrica "Gradevolezza".
     * @return media della gradevolezza, oppure 0 se nessuna valutazione è presente
     */
    public float getmGradevolezza() {
        return mGradevolezza / valutazioni.size();
    }

    /**
     * Restituisce la media delle valutazioni per la metrica "Originalità".
     * @return media dell'originalità, oppure 0 se nessuna valutazione è presente
     */
    public float getmOriginalita() {
        return mOriginalita / valutazioni.size();
    }

    /**
     * Restituisce la media delle valutazioni per la metrica "Edizione".
     * @return media dell'edizione, oppure 0 se nessuna valutazione è presente
     */
    public float getmEdizione() {
        return mEdizione / valutazioni.size();
    }

    /**
     * Restituisce la media delle valutazioni per la metrica "Finale".
     * @return media del finale, oppure 0 se nessuna valutazione è presente
     */
    public float getmFinale() {
        return mFinale / valutazioni.size();
    }

}
