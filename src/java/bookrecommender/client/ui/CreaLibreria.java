package bookrecommender.client.ui;

import bookrecommender.client.util.CliUtil;
import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.util.TableViewEngine;
import bookrecommender.common.model.Libro;
import bookrecommender.common.interfaces.LibInterface;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la creazione di una nuova libreria personale da parte dell'utente.
 * Permette la ricerca dei libri e la selezione di almeno 3 libri per creare una libreria,
 * associata ad un nome valido.
 * Estende {@link TableViewEngine} per gestire le funzionalità comuni di ricerca e gestione tabelle.
 */
public class CreaLibreria extends TableViewEngine {

    @FXML private ProgressIndicator loadingCircle;
    /** Tabella dei risultati di ricerca. */
    @FXML private TableView <Libro>tableView;

    /** Colonna del titolo nella tabella dei risultati. */
    @FXML private TableColumn <Libro, String> titoloCol;

    /** Colonna dell'autore nella tabella dei risultati. */
    @FXML private TableColumn <Libro, String>autoreCol;

    /** Colonna dell'anno nella tabella dei risultati. */
    @FXML private TableColumn <Libro, Integer> annoCol;

    /** Colonna delle azioni nella tabella dei risultati. */
    @FXML private TableColumn <Libro, Void> azioniCol;

    /** Tabella dei libri selezionati per la libreria. */
    @FXML private TableView <Libro> risTableView;

    /** Colonna del titolo nella tabella dei libri selezionati. */
    @FXML private TableColumn <Libro, String> risTitoloCol;

    /** Colonna dell'autore nella tabella dei libri selezionati. */
    @FXML private TableColumn <Libro, String> risAutoreCol;

    /** Colonna dell'anno nella tabella dei libri selezionati. */
    @FXML private TableColumn <Libro, Integer> risAnnoCol;

    /** Colonna delle azioni nella tabella dei libri selezionati. */
    @FXML private TableColumn <Libro, Void> risAzioniCol;

    /** Campo per inserire il titolo del libro da cercare. */
    @FXML private TextField campoRicerca;

    /** Campo per inserire l'anno del libro da cercare. */
    @FXML private TextField campoRicercaAnno;

    /** Menù per selezionare il tipo di ricerca. */
    @FXML private MenuButton MenuTipoRicerca;

    /** Opzione per cercare per titolo. */
    @FXML private MenuItem MenuCercaTitolo;

    /** Opzione per cercare per autore. */
    @FXML private MenuItem MenuCercaAutore;

    /** Opzione per cercare per autore e anno. */
    @FXML private MenuItem MenuCercaAutoreAnno;

    /** Campo di input per il nome della nuova libreria. */
    @FXML private TextField NomeLibreria;

    /**
     * Inizializza il controller, configurando la tabella e i controlli per la ricerca e selezione dei libri.
     */
    @FXML
    public void initialize() {
        initBasicSearch();
        initSAddRemCol();
        initOActionCol();
        initOTableView();
        initTableViews();
    }

    /** @return il campo di testo per la ricerca per titolo/autore. */
    @Override protected TextField getCampoRicerca(){
        return campoRicerca;
    }

    /** @return il campo di testo per la ricerca per anno. */
    @Override protected TextField getCampoRicercaAnno(){
        return campoRicercaAnno;
    }

    /** @return la tabella dei risultati di ricerca. */
    @Override protected TableView<Libro> getSTableView() {return tableView;}

    /** @return la colonna dei titoli nella tabella di ricerca. */
    @Override protected TableColumn<Libro, String> getSTitoloCol() {return titoloCol;}

    /** @return la colonna degli autori nella tabella di ricerca. */
    @Override protected TableColumn<Libro, String> getSAutoreCol() {return autoreCol;}

    /** @return la colonna degli anni nella tabella di ricerca. */
    @Override protected TableColumn<Libro, Integer> getSAnnoCol() {return annoCol;}

    /** @return sempre null perché la colonna recensioni non è usata in questa vista. */
    @Override protected TableColumn<Libro, Void> getSRecensioniCol() {return null;}

    /** @return sempre null perché la colonna aggiungi avanzata non è usata in questa vista. */
    @Override protected TableColumn<Libro, Void> getSAggiungiAdvCol() {return null;}

    /** @return la colonna delle azioni nella tabella di ricerca. */
    @Override protected TableColumn<Libro, Void> getSAddRemCol() {return azioniCol;}

    /** @return il menu per selezionare il tipo di ricerca. */
    @Override protected MenuButton getMenuTipoRicerca(){
        return MenuTipoRicerca;
    }

    /** @return il menu per cercare per titolo. */
    @Override protected MenuItem getMenuCercaTitolo() {return MenuCercaTitolo;}

    /** @return il menu per cercare per autore. */
    @Override protected MenuItem getMenuCercaAutore() {return MenuCercaAutore;}

    /** @return il menu per cercare per autore e anno. */
    @Override protected MenuItem getMenuCercaAutoreAnno() {return MenuCercaAutoreAnno;}

    /** @return la tabella dei libri selezionati per la libreria. */
    @Override protected TableView<Libro> getOTableView() {return risTableView;}

    /** @return la colonna dei titoli nei libri selezionati. */
    @Override protected TableColumn<Libro, String> getOTitoloCol() {return risTitoloCol;}

    /** @return la colonna degli autori nei libri selezionati. */
    @Override protected TableColumn<Libro, String> getOAutoreCol() {return risAutoreCol;}

    /** @return la colonna degli anni nei libri selezionati. */
    @Override protected TableColumn<Libro, Integer> getOAnnoCol() {return risAnnoCol;}

    /** @return la colonna delle azioni nella tabella dei libri selezionati. */
    @Override protected TableColumn<Libro, Void> getOActionCol() {return risAzioniCol;}

    /** @return {@code false} perché la ricerca è in modalità base (non per consigli). */
    @Override
    protected boolean getSearchType() {return false;}

    /** @return {@code null} perché non è richiesto un libro specifico per questa vista. */
    @Override
    protected Libro getMyLibro() {return null;}

    /** @return il tipo di FXML associato a questa vista. */
    @Override
    protected FXMLtype getMyFXMLtype() {
        return FXMLtype.CREALIBRERIA;
    }

    @Override
    protected ProgressIndicator getProgressIndicator() {
        return loadingCircle;
    }

    /**
     * Salva una nuova libreria personale per l'utente.
     * <p>
     * Il metodo recupera il nome della libreria dall'apposito campo di input
     * e l'elenco dei libri selezionati dall'utente. Prima del salvataggio
     * effettua una validazione sui seguenti criteri:
     * <ul>
     *   <li>Il nome della libreria non deve essere nullo o vuoto</li>
     *   <li>Il nome deve avere una lunghezza compresa tra 5 e 50 caratteri</li>
     *   <li>Devono essere presenti almeno 3 libri nella libreria</li>
     * </ul>
     * Se i criteri non sono rispettati, viene mostrato un messaggio di errore.
     * Altrimenti, il metodo invia una richiesta al server per creare la libreria
     * tramite il metodo remoto {@code createLib()} del servizio {@code LibInterface}.
     * <p>
     * Se la creazione va a buon fine, viene mostrato un messaggio di conferma
     * e l'interfaccia viene reindirizzata alla schermata principale di gestione librerie.
     * In caso contrario, viene mostrato un messaggio di errore.
     * <p>
     * Ogni eccezione sollevata durante la comunicazione RMI viene gestita e
     * visualizzata tramite un messaggio d'errore all'utente.
     * @see CliUtil#getLibService()
     * @see LibInterface#createLib
     */
    @FXML
    private void SalvaLibreria() {
        String nome = NomeLibreria.getText();
        if (risTableView.getItems().size() < 3 || nome == null || nome.trim().isEmpty() || nome.length() < 5 || nome.length() > 50) {
            CliUtil.getInstance().createAlert("Errore", "La libreria deve contenere almeno 3 libri e il nome deve essere compreso tra 5 e 50 caratteri.").showAndWait();
            return;
        }
        List<Libro> libri = new ArrayList<>(risTableView.getItems());
        try {
            if (CliUtil.getInstance().getLibService().createLib(CliUtil.getInstance().getCurrentToken(), nome.trim(), libri)) {
                CliUtil.getInstance().createConfirmation("Successo", "Libreria salvata con successo!", false).showAndWait();
                ExitApplication();
            } else {
                CliUtil.getInstance().createAlert("Errore", "Impossibile salvare la libreria").showAndWait();
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore", "Impossibile salvare la libreria: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Torna alla schermata di gestione delle librerie dopo il salvataggio o l’annullamento.
     */
    @FXML
    private void ExitApplication() {
        CliUtil.getInstance().buildStage(FXMLtype.GESTIONELIBRERIE,null, null);
    }
}
