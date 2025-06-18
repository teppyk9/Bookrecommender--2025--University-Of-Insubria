package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;

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
    public int getId() {
        return id;
    }
    public String getTitolo() {
        return titolo;
    }
    public String getAutore() {
        return autore;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public String getCategoria() {
        return categoria;
    }
    public String getEditore() {
        return editore;
    }
    public float getPrezzo() {
        return prezzo;
    }
    public short getAnnoPubblicazione() {
        return annoPubblicazione;
    }
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

    @Override
    public String toString() {
        return titolo;
    }
}