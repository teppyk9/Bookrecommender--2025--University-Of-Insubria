package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;

public class Libro implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public int id;
    public String titolo;
    public String autore;
    public String descrizione;
    public String categoria;
    public String editore;
    public float prezzo;
    public short annoPubblicazione;
    public short mesePubblicazione;

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

    public String getInfo() {
        return "ID: " + id + "\n" +
               "Titolo: " + titolo + "\n" +
               "Autore: " + autore + "\n" +
               "Descrizione: " + descrizione + "\n" +
               "Categoria: " + categoria + "\n" +
               "Editore: " + editore + "\n" +
               "Prezzo: " + prezzo + "\n" +
               "Anno di Pubblicazione: " + annoPubblicazione + "\n" +
               "Mese di Pubblicazione: " + mesePubblicazione;
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
    public short getMesePubblicazione() {
        return mesePubblicazione;
    }
}