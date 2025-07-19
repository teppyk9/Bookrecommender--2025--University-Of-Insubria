package bookrecommender.client.ui;

import bookrecommender.client.util.CliUtil;
import bookrecommender.client.enums.FXMLtype;
import bookrecommender.common.model.Libro;
import bookrecommender.common.model.Libro_Details;
import bookrecommender.common.model.Valutazione;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Controller JavaFX per la schermata di dettaglio di un {@link Libro}.
 * <p>
 * Visualizza tutte le informazioni relative a un libro selezionato: titolo, autore, categoria,
 * editore, prezzo, anno e mese di pubblicazione, descrizione e medie delle valutazioni.
 * Mostra inoltre le valutazioni utente ricevute e i consigli di lettura correlati.
 * </p>
 * <p>
 * L’interfaccia consente di accedere al dettaglio di ogni valutazione o libro consigliato tramite doppio clic.
 * </p>
 */
public class DettaglioLibro {
    // Etichette per i metadati testuali del libro
    public Label titoloLabel;          // Mostra il titolo del libro
    public Label autoreLabel;          // Mostra l'autore del libro
    public Label categoriaLabel;       // Mostra la categoria (genere) del libro
    public Label editoreLabel;         // Mostra la casa editrice
    public Label prezzoLabel;          // Mostra il prezzo
    public Label annoLabel;            // Mostra l'anno di pubblicazione
    public Label meseLabel;            // Mostra il mese di pubblicazione

    // Area per la descrizione del libro
    public Label descrizioneArea;

    // Stelle e voto medio complessivo
    public ImageView starTotal1;
    public ImageView starTotal2;
    public ImageView starTotal3;
    public ImageView starTotal4;
    public ImageView starTotal5;
    public Label votoTotal;

    // Stelle grafiche e voto medio per lo "Stile" del libro
    public ImageView starStile1;
    public ImageView starStile2;
    public ImageView starStile3;
    public ImageView starStile4;
    public ImageView starStile5;
    public Label votoStile;

    // Stelle grafiche e voto medio per il "Contenuto"
    public ImageView starContenuto1;
    public ImageView starContenuto2;
    public ImageView starContenuto3;
    public ImageView starContenuto4;
    public ImageView starContenuto5;
    public Label votoContenuto;

    // Stelle grafiche e voto medio sulla "Gradevolezza"
    public ImageView starGradevolezza1;
    public ImageView starGradevolezza2;
    public ImageView starGradevolezza3;
    public ImageView starGradevolezza4;
    public ImageView starGradevolezza5;
    public Label votoGradevolezza;

    // Stelle grafiche e voto medio sull'"Originalità"
    public ImageView starOriginalita1;
    public ImageView starOriginalita2;
    public ImageView starOriginalita3;
    public ImageView starOriginalita4;
    public ImageView starOriginalita5;
    public Label votoOriginalita;

    // Stelle grafiche e voto medio sull'"Edizione"
    public ImageView starEdizione1;
    public ImageView starEdizione2;
    public ImageView starEdizione3;
    public ImageView starEdizione4;
    public ImageView starEdizione5;
    public Label votoEdizione;

    // Liste delle valutazioni e dei libri consigliati
    public ListView<Valutazione> listaValutazioni;
    public ListView<Libro> listaConsigli;

    /** Dettagli del libro ottenuti dal server (valutazioni e consigli)*/
    private Libro_Details details;

    /**
     * Carica nella GUI tutte le informazioni relative al {@link Libro} selezionato,
     * inclusi metadati, descrizione, valutazioni medie e consigli di lettura.
     * <p>
     * Per ogni attributo del libro, controlla che non sia nullo o vuoto,
     * e imposta un valore predefinito ("Non specificato") in caso contrario.
     * </p>
     * <p>
     * Inoltre:
     * <ul>
     *   <li>Chiama il servizio remoto per ottenere un oggetto {@link Libro_Details}
     *       contenente le medie delle valutazioni e la lista di {@link Valutazione}</li>
     *   <li>Aggiorna le stelle grafiche e le etichette di voto per ogni categoria</li>
     *   <li>Imposta la lista di valutazioni e di libri consigliati</li>
     * </ul>
     * </p>
     * <p>
     * In caso di errore di comunicazione con il server RMI, mostra un alert all’utente.
     * </p>
     *
     * @param libro Il libro di cui caricare e visualizzare i dettagli.
     */
    public void setLibro(Libro libro){
        descrizioneArea.setWrapText(true);
        titoloLabel.setText(libro.getTitolo());
        autoreLabel.setText(libro.getAutore());
        if(libro.getCategoria() == null || libro.getCategoria().isEmpty()) {
            categoriaLabel.setText("Non specificato");
        } else {
            categoriaLabel.setText(libro.getCategoria());
        }
        if(libro.getEditore() == null || libro.getEditore().isEmpty()) {
            editoreLabel.setText("Non specificato");
        } else {
            editoreLabel.setText(libro.getEditore());
        }
        if(libro.getPrezzo() == 0) {
            prezzoLabel.setText("Non specificato");
        } else {
            prezzoLabel.setText(String.valueOf(libro.getPrezzo()));
        }
        if(libro.getAnnoPubblicazione() == 0) {
            annoLabel.setText("Non specificato");
        } else {
            annoLabel.setText(String.valueOf(libro.getAnnoPubblicazione()));
        }
        if(libro.getMesePubblicazione() == null || libro.getMesePubblicazione().isEmpty()) {
            meseLabel.setText("Non specificato");
        } else {
            meseLabel.setText(libro.getMesePubblicazione());
        }
        if(libro.getDescrizione() == null || libro.getDescrizione().isEmpty()) {
            descrizioneArea.setText("Nessuna descrizione associata");
        } else {
            descrizioneArea.setText(libro.getDescrizione());
        }

        try {
            details = CliUtil.getInstance().getSearchService().getDetails(libro);
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore di connessione", "Impossibile recuperare i dettagli del libro.").showAndWait();
        }

        CliUtil.getInstance().setStar(starTotal1, starTotal2, starTotal3, starTotal4, starTotal5, details.getmFinale());
        votoTotal.setText(String.format("%.1f", details.getmFinale()));
        CliUtil.getInstance().setStar(starStile1, starStile2, starStile3, starStile4, starStile5, details.getmStile());
        votoStile.setText(String.format("%.1f", details.getmStile()));
        CliUtil.getInstance().setStar(starContenuto1, starContenuto2, starContenuto3, starContenuto4, starContenuto5, details.getmContenuto());
        votoContenuto.setText(String.format("%.1f", details.getmContenuto()));
        CliUtil.getInstance().setStar(starGradevolezza1, starGradevolezza2, starGradevolezza3, starGradevolezza4, starGradevolezza5, details.getmGradevolezza());
        votoGradevolezza.setText(String.format("%.1f", details.getmGradevolezza()));
        CliUtil.getInstance().setStar(starOriginalita1, starOriginalita2, starOriginalita3, starOriginalita4, starOriginalita5, details.getmOriginalita());
        votoOriginalita.setText(String.format("%.1f", details.getmOriginalita()));
        CliUtil.getInstance().setStar(starEdizione1, starEdizione2, starEdizione3, starEdizione4, starEdizione5, details.getmEdizione());
        votoEdizione.setText(String.format("%.1f", details.getmEdizione()));

        listaValutazioni.setItems(FXCollections.observableArrayList(details.getValutazioni()));
        listaConsigli.setItems(FXCollections.observableArrayList(details.getConsigli().values().stream().flatMap(List::stream).toList()));
    }

    /**
     * Gestisce il doppio clic su una valutazione presente nella lista {@code listaValutazioni}.
     * Se è stata selezionata una valutazione, viene aperta una nuova schermata per visualizzarla in dettaglio.
     *
     * @param mouseEvent Evento del mouse generato dal clic.
     */
    public void clickValutazione(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Valutazione selezionato = listaValutazioni.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                CliUtil.getInstance().buildStage(FXMLtype.VISUALIZZAVALUTAZIONE, null, selezionato);
            }
        }
    }

    /**
     * Gestisce il doppio clic su un libro presente nella lista dei consigli.
     * Se è stato selezionato un libro, viene aperta la relativa schermata di dettaglio.
     *
     * @param mouseEvent Evento del mouse generato dal clic.
     */
    public void clickConsiglio(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Libro selezionato = listaConsigli.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, null, selezionato);
            }
        }
    }
}
