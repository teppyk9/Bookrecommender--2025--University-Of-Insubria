package bookrecommender.client.ui;

import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.util.TableViewEngine;
import bookrecommender.common.model.Libro;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller JavaFX per la schermata di creazione di un consiglio libro.
 * <p>
 * Permette di cercare libri da associare come consigli a un libro principale.
 * L’utente può selezionare fino a 3 libri da consigliare e salvarli tramite il server.
 * Estende {@link TableViewEngine} per la gestione delle due tabelle (ricerca e risultati).
 * </p>
 */
public class CreaConsiglio extends TableViewEngine {

    @FXML private Button bottoneCerca;
    @FXML private ProgressIndicator loadingCircle;
    /** Campo di testo per la ricerca del titolo o autore */
    @FXML private TextField campoRicerca;

    /** Campo di testo per la ricerca per anno */
    @FXML private TextField campoRicercaAnno;

    /** Menu a tendina per scegliere il tipo di ricerca */
    @FXML private MenuButton MenuTipoRicerca;

    /** Voce di menu per la ricerca per titolo */
    @FXML private MenuItem MenuCercaTitolo;

    /** Voce di menu per la ricerca per autore */
    @FXML private MenuItem MenuCercaAutore;

    /** Voce di menu per la ricerca per autore e anno */
    @FXML private MenuItem MenuCercaAutoreAnno;

    /** Tabella contenente i risultati della ricerca */
    @FXML private TableView <Libro>tableView;

    /** Colonna dei titoli dei libri nella tabella principale */
    @FXML private TableColumn <Libro, String> titoloCol;

    /** Colonna degli autori dei libri nella tabella principale */
    @FXML private TableColumn <Libro, String>autoreCol;

    /** Colonna degli anni dei libri nella tabella principale */
    @FXML private TableColumn <Libro, Integer> annoCol;

    /** Colonna delle azioni nella tabella principale (aggiungi consiglio) */
    @FXML private TableColumn <Libro, Void> azioniCol;

    /** Tabella contenente i libri scelti come consigli */
    @FXML private TableView <Libro> risTableView;

    /** Colonna dei titoli dei libri consigliati */
    @FXML private TableColumn <Libro, String> risTitoloCol;

    /** Colonna degli autori dei libri consigliati */
    @FXML private TableColumn <Libro, String> risAutoreCol;

    /** Colonna degli anni dei libri consigliati */
    @FXML private TableColumn <Libro, Integer> risAnnoCol;

    /** Colonna delle azioni nella tabella dei consigli (rimuovi consiglio) */
    @FXML private TableColumn <Libro, Void> risAzioniCol;

    /** Bottone per tornare al menu principale */
    @FXML private Button GoBackButton_MainMenu;

    @FXML private MenuButton limiterBox;

    /** Libro principale a cui si associano i consigli */
    private Libro myLibro;

    /** Tipo della schermata precedente, per il ritorno */
    private FXMLtype oldFXMLType;

    /**
     * Inizializza la schermata e registra il comportamento di chiusura della finestra.
     * Se l’utente chiude la finestra, viene eseguito il metodo {@code GoToMainMenu()}.
     */
    @FXML
    public void initialize() {
        GoBackButton_MainMenu.setGraphic(IMGtype.INDIETRO.getImageView(45,45));
        bottoneCerca.setGraphic(IMGtype.CERCA.getImageView(25,25));
        initForConsigli();
        initLimiter();
        Platform.runLater(() -> {
            Stage stage = (Stage) GoBackButton_MainMenu.getScene().getWindow();
            stage.setOnCloseRequest(evt -> {
                GoToMainMenu();
                evt.consume();
            });
        });
    }

    /**
     * Imposta il libro principale per cui si stanno creando i consigli e
     * salva il tipo di schermata precedente.
     *
     * @param libro libro di riferimento
     * @param oldFXMLType schermata da cui proviene l’utente
     */
    public void setLibro(Libro libro, FXMLtype oldFXMLType) {
        this.myLibro = libro;
        this.oldFXMLType = oldFXMLType;
    }

    /**
     * Restituisce il campo di input per la ricerca testuale (titolo o autore).
     * @return campo di testo principale per la ricerca
     */
    @Override protected TextField getCampoRicerca(){
        return campoRicerca;
    }

    /**
     * Restituisce il campo di input per l'anno nella ricerca.
     * @return campo di testo per l'anno
     */
    @Override protected TextField getCampoRicercaAnno(){
        return campoRicercaAnno;
    }

    /**
     * Restituisce il menu per la selezione del tipo di ricerca.
     * @return menu a tendina dei tipi di ricerca
     */
    @Override protected MenuButton getMenuTipoRicerca(){
        return MenuTipoRicerca;
    }

    /**
     * Restituisce la voce di menu per la ricerca per titolo.
     * @return {@code MenuItem} "Cerca per Titolo"
     */
    @Override
    protected MenuItem getMenuCercaTitolo() {
        return MenuCercaTitolo;
    }

    /**
     * Restituisce la voce di menu per la ricerca per autore.
     * @return {@code MenuItem} "Cerca per Autore"
     */
    @Override
    protected MenuItem getMenuCercaAutore() {
        return MenuCercaAutore;
    }

    /**
     * Restituisce la voce di menu per la ricerca per autore e anno.
     * @return {@code MenuItem} "Cerca per Autore e Anno"
     */
    @Override
    protected MenuItem getMenuCercaAutoreAnno() {
        return MenuCercaAutoreAnno;
    }

    /**
     * Restituisce la tabella principale con i risultati della ricerca.
     * @return {@code TableView} principale
     */
    @Override
    protected TableView<Libro> getSTableView() {
        return tableView;
    }

    /**
     * Restituisce la colonna dei titoli nella tabella principale.
     * @return colonna dei titoli
     */
    @Override
    protected TableColumn<Libro, String> getSTitoloCol() {
        return titoloCol;
    }

    /**
     * Restituisce la colonna degli autori nella tabella principale.
     * @return colonna degli autori
     */
    @Override
    protected TableColumn<Libro, String> getSAutoreCol() {
        return autoreCol;
    }

    /**
     * Restituisce la colonna degli anni nella tabella principale.
     * @return colonna degli anni
     */
    @Override
    protected TableColumn<Libro, Integer> getSAnnoCol() {
        return annoCol;
    }

    /**
     * La colonna delle recensioni non è usata in questa schermata.
     * @return {@code null}
     */
    @Override
    protected TableColumn<Libro, Boolean> getSRecensioniCol() {
        return null;
    }

    /**
     * La colonna per l'aggiunta avanzata non è usata in questa schermata.
     * @return {@code null}
     */
    @Override
    protected TableColumn<Libro, Void> getSAggiungiAdvCol() {
        return null;
    }

    /**
     * Restituisce la colonna per aggiungere o rimuovere consigli dalla tabella principale.
     * @return colonna delle azioni per i consigli
     */
    @Override
    protected TableColumn<Libro, Void> getSAddRemCol() {
        return azioniCol;
    }

    /**
     * Restituisce la tabella secondaria contenente i libri selezionati come consigli.
     * @return {@code TableView} dei libri consigliati
     */
    @Override
    protected TableView<Libro> getOTableView() {
        return risTableView;
    }

    /**
     * Restituisce la colonna dei titoli nella tabella dei consigli.
     * @return colonna dei titoli consigliati
     */
    @Override
    protected TableColumn<Libro, String> getOTitoloCol() {
        return risTitoloCol;
    }

    /**
     * Restituisce la colonna degli autori nella tabella dei consigli.
     * @return colonna degli autori consigliati
     */
    @Override
    protected TableColumn<Libro, String> getOAutoreCol() {
        return risAutoreCol;
    }

    /**
     * Restituisce la colonna degli anni nella tabella dei consigli.
     * @return colonna degli anni consigliati
     */
    @Override
    protected TableColumn<Libro, Integer> getOAnnoCol() {
        return risAnnoCol;
    }

    /**
     * Restituisce la colonna delle azioni nella tabella dei consigli.
     * @return colonna con i bottoni di rimozione consigli
     */
    @Override
    protected TableColumn<Libro, Void> getOActionCol() {
        return risAzioniCol;
    }

    /**
     * Restituisce {@code true} perché la schermata utilizza entrambe le tabelle.
     * @return {@code true}
     */
    @Override
    protected boolean getSearchType() {return true;}

    /**
     * Restituisce il libro principale per il quale si stanno selezionando i consigli.
     * @return libro di riferimento
     */
    @Override
    protected Libro getMyLibro() {
        return myLibro;
    }

    /**
     * Restituisce il tipo di schermata corrente FXML.
     * @return {@code FXMLtype.CREACONSIGLIO}
     */
    @Override
    protected FXMLtype getMyFXMLtype() {
        return FXMLtype.CREACONSIGLIO;
    }

    @Override
    protected ProgressIndicator getProgressIndicator() {
        return loadingCircle;
    }

    @Override
    protected MenuButton getLimiterBox() {
        return limiterBox;
    }

    /**
     * Salva i libri selezionati come consiglio associati al libro principale.
     * <ul>
     *   <li>Controlla che il numero massimo di consigli non superi 3</li>
     *   <li>Invia la richiesta al server</li>
     *   <li>Mostra messaggio di successo o errore</li>
     * </ul>
     */
    @FXML
    private void salvaConsiglio() {
        List<Libro> cons = new ArrayList<>(risTableView.getItems());
        cons.add(0, myLibro);
        if (cons.size() > 4) {
            CliUtil.getInstance().createAlert("Errore", "Non puoi consigliare più di 3 libri").showAndWait();
            return;
        }
        try {
            if (CliUtil.getInstance().getLibService().addConsiglio(CliUtil.getInstance().getCurrentToken(), cons)) {
                CliUtil.getInstance().createConfirmation("Successo", "Consiglio salvato!", false).showAndWait();
                    CliUtil.getInstance().buildStage(oldFXMLType,null, null);
            } else {
                CliUtil.getInstance().createAlert("Errore", "Salvataggio fallito").showAndWait();
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore", e.getMessage()).showAndWait();
        }
    }

    /**
     * Torna alla schermata precedente. Se ci sono consigli non salvati,
     * chiede conferma all’utente prima di proseguire.
     */
    @FXML
    private void GoToMainMenu() {
        List<Libro> cons = new ArrayList<>(risTableView.getItems());
        if(!cons.isEmpty()) {
            if (CliUtil.getInstance().createConfirmation("Attenzione", "Hai dei consigli non salvati, vuoi davvero tornare al menu principale?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
                CliUtil.getInstance().buildStage(oldFXMLType,null, null);
            }
        }else{
            CliUtil.getInstance().buildStage(oldFXMLType,null, null);
        }
    }
}
