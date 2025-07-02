package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;

/**
 * Rappresenta un libro con tutte le informazioni fondamentali per la catalogazione e la visualizzazione.
 * La classe implementa {@link Serializable} per permettere la trasmissione remota tramite RMI o il salvataggio su file.
 * Due libri sono considerati uguali se hanno lo stesso {@code id}.
 */
public class Libro implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String titolo;
    private final String autore;
    private final String descrizione;
    private final String categoria;
    private final String editore;
    private final float prezzo;
    private final short annoPubblicazione;
    private final short mesePubblicazione;


    /**
     * Costruttore completo per creare un oggetto {@code Libro}.
     * @param id identificativo univoco del libro
     * @param titolo titolo del libro
     * @param autore autore del libro
     * @param descrizione descrizione o trama del libro
     * @param categoria genere o categoria del libro
     * @param editore casa editrice
     * @param prezzo prezzo del libro
     * @param annoPubblicazione anno di pubblicazione
     * @param mesePubblicazione mese di pubblicazione (da 1 a 12)
     */
    public Libro(int id, String titolo, String autore, String descrizione, String categoria, String editore, float prezzo, short annoPubblicazione, short mesePubblicazione) {
        this.id = id;
        this.titolo = titolo;
        this.autore = autore;
        this.descrizione = descrizione;
        this.categoria = categoria;
        this.editore = editore;
        this.prezzo = prezzo;
        this.annoPubblicazione = annoPubblicazione;
        this.mesePubblicazione = mesePubblicazione;
    }

    /**
     * Restituisce l'ID univoco del libro.
     * @return ID del libro
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce il titolo del libro.
     * @return titolo del libro
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Restituisce il nome dell'autore.
     * @return autore del libro
     */
    public String getAutore() {
        return autore;
    }

    /**
     * Restituisce la descrizione o trama del libro.
     * @return descrizione del libro
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Restituisce la categoria o genere del libro.
     * @return categoria del libro
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Restituisce il nome dell'editore.
     * @return casa editrice
     */
    public String getEditore() {
        return editore;
    }

    /**
     * Restituisce il prezzo del libro.
     * @return prezzo in formato decimale
     */
    public float getPrezzo() {
        return prezzo;
    }

    /**
     * Restituisce l'anno di pubblicazione del libro.
     * @return anno di pubblicazione
     */
    public short getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    /**
     * Restituisce il nome del mese di pubblicazione.
     * @return nome del mese (es. "Gennaio", "Febbraio", ecc.) oppure "Mese sconosciuto" se il valore è fuori intervallo
     */
    public String getMesePubblicazione() {
        return switch (mesePubblicazione) {
            case 1 -> "Gennaio";
            case 2 -> "Febbraio";
            case 3 -> "Marzo";
            case 4 -> "Aprile";
            case 5 -> "Maggio";
            case 6 -> "Giugno";
            case 7 -> "Luglio";
            case 8 -> "Agosto";
            case 9 -> "Settembre";
            case 10 -> "Ottobre";
            case 11 -> "Novembre";
            case 12 -> "Dicembre";
            default -> "Mese sconosciuto";
        };
    }


    /**
     * Ritorna una rappresentazione testuale del libro, corrispondente al titolo.
     * @return titolo del libro
     */
    @Override
    public String toString() {
        return titolo;
    }


    /**
     * Confronta due libri in base al loro ID.
     * @param o altro oggetto da confrontare
     * @return true se l'oggetto è un {@code Libro} con lo stesso ID, false altrimenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Libro other)) return false;
        return this.id == other.id;
    }


    /**
     * Calcola l’hash code basato sull’ID del libro.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}