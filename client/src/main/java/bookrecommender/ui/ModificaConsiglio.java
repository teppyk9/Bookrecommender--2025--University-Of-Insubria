package bookrecommender.ui;

import bookrecommender.enums.FXMLtype;
import bookrecommender.enums.IMGtype;
import bookrecommender.util.CliUtil;
import bookrecommender.util.TableViewEngine;
import bookrecommender.model.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


/**
 * Controller JavaFX per la schermata di modifica dei consigli di lettura.
 * <p>
 * Permette di cercare libri e comporre un elenco di consigli per il libro corrente.
 * Al salvataggio il libro corrente viene inserito in testa alla lista; è applicato
 * il vincolo di massimo 3 libri consigliati oltre al libro corrente (totale <= 4).
 * </p>
 * <p>
 * Estende {@link TableViewEngine} riutilizzando logica di ricerca, tabelle S/O
 * (sorgente/output) e limitazioni dei risultati.
 * </p>
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
public class ModificaConsiglio extends TableViewEngine {
    /** Pulsante per avviare la ricerca; icona e stile inizializzati in {@link #initialize()}. Non nullo dopo il caricamento FXML. */
    @FXML private Button bottoneCerca;

    /** Indicatore di caricamento mostrato durante le chiamate al servizio. Non nullo dopo il caricamento FXML. */
    @FXML private ProgressIndicator loadingCircle;

    /** Pulsante “indietro” al menù/schermata precedente. Non nullo dopo il caricamento FXML. */
    @FXML private Button GoBackButton_MainMenu;

    /** Campo di testo per i termini di ricerca (titolo/autore). Non nullo dopo il caricamento FXML. */
    @FXML private TextField campoRicerca;

    /** Campo di testo per il filtro sull'anno di pubblicazione. Non nullo dopo il caricamento FXML. */
    @FXML private TextField campoRicercaAnno;

    /** Menu principale per selezionare il tipo di ricerca. Non nullo dopo il caricamento FXML. */
    @FXML private MenuButton MenuTipoRicerca;

    /** Azione di ricerca per “Titolo”. Non nullo dopo il caricamento FXML. */
    @FXML private MenuItem MenuCercaTitolo;

    /** Azione di ricerca per “Autore”. Non nullo dopo il caricamento FXML. */
    @FXML private MenuItem MenuCercaAutore;

    /** Azione di ricerca per “Autore + Anno”. Non nullo dopo il caricamento FXML. */
    @FXML private MenuItem MenuCercaAutoreAnno;

    /** Tabella sorgente (S) con i risultati della ricerca. Non nulla dopo il caricamento FXML. */
    @FXML private TableView<Libro> tableView;

    /** Colonna titolo della tabella S. Non nulla dopo il caricamento FXML. */
    @FXML private TableColumn<Libro, String> titoloCol;

    /** Colonna autore della tabella S. Non nulla dopo il caricamento FXML. */
    @FXML private TableColumn<Libro, String> autoreCol;

    /** Colonna anno della tabella S. Non nulla dopo il caricamento FXML. */
    @FXML private TableColumn<Libro, Integer> annoCol;

    /** Colonna azioni (aggiungi/rimuovi) della tabella S. Non nulla dopo il caricamento FXML. */
    @FXML private TableColumn<Libro, Void> azioniCol;

    /** Tabella output (O) con i libri consigliati correnti. Non nulla dopo il caricamento FXML. */
    @FXML private TableView<Libro> risTableView;

    /** Colonna titolo della tabella O. Non nulla dopo il caricamento FXML. */
    @FXML private TableColumn<Libro, String> risTitoloCol;

    /** Colonna autore della tabella O. Non nulla dopo il caricamento FXML. */
    @FXML private TableColumn<Libro, String> risAutoreCol;

    /** Colonna anno della tabella O. Non nulla dopo il caricamento FXML. */
    @FXML private TableColumn<Libro, Integer> risAnnoCol;

    /** Colonna azioni (rimuovi) della tabella O. Non nulla dopo il caricamento FXML. */
    @FXML private TableColumn<Libro, Void> risAzioniCol;

    /** Menu per limitare il numero di risultati mostrati. Non nullo dopo il caricamento FXML. */
    @FXML private MenuButton limiterBox;

    /** Libro “corrente” a cui associare i consigli. */
    private Libro myLibro;

    /** Tipo FXML della schermata di provenienza, usato per la navigazione di ritorno. */
    private FXMLtype oldFXMLType;


    /**
     * Cache della lista di consigli iniziale: usata per rilevare modifiche non salvate
     * (confronto con i contenuti correnti della tabella O).
     */
    private List<Libro> oldLibri = new ArrayList<>();

    /**
     * Inizializza la UI: imposta icone e allineamenti colonne, inizializza
     * limiti/filtri e registra l’handler di chiusura finestra che richiama
     * {@link #GoToMainMenu()} per gestire eventuali modifiche non salvate.
     * <p>Viene eseguito automaticamente dal loader FXML sul JavaFX Application Thread.</p>
     */
    @FXML private void initialize() {
        GoBackButton_MainMenu.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        GoBackButton_MainMenu.setAlignment(Pos.TOP_LEFT);
        bottoneCerca.setGraphic(IMGtype.CERCA.getImageView(25,25));
        bottoneCerca.setAlignment(Pos.CENTER_LEFT);
        bottoneCerca.setStyle("-fx-padding: 0");
        titoloCol.setStyle("-fx-alignment: CENTER;");
        annoCol.setStyle("-fx-alignment: CENTER;");
        autoreCol.setStyle("-fx-alignment: CENTER;");
        azioniCol.setStyle("-fx-alignment: CENTER;");
        risTitoloCol.setStyle("-fx-alignment: CENTER;");
        risAnnoCol.setStyle("-fx-alignment: CENTER;");
        risAutoreCol.setStyle("-fx-alignment: CENTER;");
        risAzioniCol.setStyle("-fx-alignment: CENTER;");
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
     * Imposta il libro corrente e prepara lo stato della schermata.
     * <p>
     * Carica i consigli già presenti dal servizio remoto, rimuove il libro corrente
     * dall’elenco candidati, popola la tabella O e salva una copia iniziale in {@code oldLibri}
     * per il controllo modifiche.
     * </p>
     *
     * @param libro        il libro a cui associare i consigli (verrà aggiunto in testa al salvataggio)
     * @param oldFXMLType  la schermata di provenienza per la navigazione di ritorno
     */
    public void setLibro(Libro libro, FXMLtype oldFXMLType) {
        this.myLibro = libro;
        this.oldFXMLType = oldFXMLType;
        try{
            List <Libro> listaConsigli = new ArrayList<>(CliUtil.getInstance().getLibService().getConsigli(CliUtil.getInstance().getCurrentToken(), myLibro));
            listaConsigli.remove(myLibro);
            oldLibri = listaConsigli;
            risTableView.setItems(FXCollections.observableArrayList(listaConsigli));
        } catch (Exception e) {
            CliUtil.getInstance().LogOut(e);
        }

    }


    @Override protected TextField getCampoRicerca(){
        return campoRicerca;
    }

    @Override protected TextField getCampoRicercaAnno(){
        return campoRicercaAnno;
    }

    @Override protected MenuButton getMenuTipoRicerca(){
        return MenuTipoRicerca;
    }



    /** @return il MenuItem per la ricerca per titolo nella tabella S. */
    @Override
    protected MenuItem getMenuCercaTitolo() { return MenuCercaTitolo; }

    /** @return il MenuItem per la ricerca per autore nella tabella S. */
    @Override
    protected MenuItem getMenuCercaAutore() { return MenuCercaAutore; }

    /** @return il MenuItem per la ricerca per autore+anno nella tabella S. */
    @Override
    protected MenuItem getMenuCercaAutoreAnno() { return MenuCercaAutoreAnno; }

    /** @return la tabella S (risultati di ricerca). */
    @Override
    protected TableView<Libro> getSTableView() { return tableView; }

    /** @return la colonna Titolo della tabella S. */
    @Override
    protected TableColumn<Libro, String> getSTitoloCol() { return titoloCol; }

    /** @return la colonna Autore della tabella S. */
    @Override
    protected TableColumn<Libro, String> getSAutoreCol() { return autoreCol; }

    /** @return la colonna Anno della tabella S. */
    @Override
    protected TableColumn<Libro, Integer> getSAnnoCol() { return annoCol; }

    /**
     * @return la colonna Recensioni della tabella S, se presente; {@code null} se non utilizzata in questa schermata.
     */
    @Override
    protected TableColumn<Libro, Boolean> getSRecensioniCol() { return null; }

    /**
     * @return la colonna “aggiungi avanzato” della tabella S, se presente; {@code null} se non utilizzata in questa schermata.
     */
    @Override
    protected TableColumn<Libro, Void> getSAggiungiAdvCol() { return null; }

    /** @return la colonna azioni (aggiungi/rimuovi) della tabella S. */
    @Override
    protected TableColumn<Libro, Void> getSAddRemCol() { return azioniCol; }

    /** @return la tabella O (elenco consigli). */
    @Override
    protected TableView<Libro> getOTableView() { return risTableView; }

    /** @return la colonna Titolo della tabella O. */
    @Override
    protected TableColumn<Libro, String> getOTitoloCol() { return risTitoloCol; }

    /** @return la colonna Autore della tabella O. */
    @Override
    protected TableColumn<Libro, String> getOAutoreCol() { return risAutoreCol; }

    /** @return la colonna Anno della tabella O. */
    @Override
    protected TableColumn<Libro, Integer> getOAnnoCol() { return risAnnoCol; }

    /** @return la colonna azioni della tabella O. */
    @Override
    protected TableColumn<Libro, Void> getOActionCol() { return risAzioniCol; }

    /**
     * Indica la modalità di ricerca per questa schermata (interpretata da {@link TableViewEngine}).
     * @return {@code true} per la modalità prevista da ModificaConsiglio.
     */
    @Override
    protected boolean getSearchType() { return true; }

    /** @return il libro corrente a cui sono associati i consigli. */
    @Override
    protected Libro getMyLibro() { return myLibro; }

    /** @return il tipo FXML della schermata corrente. */
    @Override
    protected FXMLtype getMyFXMLtype() { return FXMLtype.MODIFICACONSIGLIO; }

    /** @return l’indicatore di progresso da mostrare durante le operazioni asincrone. */
    @Override
    protected ProgressIndicator getProgressIndicator() { return loadingCircle; }

    /** @return il menu per limitare il numero di risultati mostrati. */
    @Override
    protected MenuButton getLimiterBox() { return limiterBox; }


    /**
     * Salva i consigli correnti per il libro selezionato.
     * <p>
     * Aggiunge il libro corrente in prima posizione e verifica il vincolo:
     * al massimo 3 consigli oltre al libro corrente (dimensione totale <= 4).
     * In caso di successo mostra conferma e torna alla schermata precedente;
     * in caso di fallimento mostra un messaggio d’errore.
     * </p>
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
            if (CliUtil.getInstance().getLibService().updateCon(CliUtil.getInstance().getCurrentToken(), cons)) {
                CliUtil.getInstance().createConfirmation("Successo", "Consiglio salvato!", true).showAndWait();
                CliUtil.getInstance().buildStage(oldFXMLType,null, null);
            } else {
                CliUtil.getInstance().createAlert("Errore", "Salvataggio fallito").showAndWait();
            }
        } catch (Exception e) {
            CliUtil.getInstance().LogOut(e);
        }
    }

    /**
     * Ritorna alla schermata precedente.
     * <p>
     * Se la lista dei consigli è stata modificata rispetto a {@code oldLibri},
     * chiede conferma all’utente prima di uscire per evitare perdite di dati.
     * </p>
     */
    @FXML
    private void GoToMainMenu() {
        if(CliUtil.getInstance().hannoDifferenze(oldLibri, risTableView.getItems())){
            if(CliUtil.getInstance().createConfirmation("Modifiche non salvate", "Hai modifiche non salvate, sei sicuro di voler uscire=", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES){
                CliUtil.getInstance().buildStage(oldFXMLType,null, null);
            }
        }else{
            CliUtil.getInstance().buildStage(oldFXMLType,null, null);
        }
    }

    /**
     * Elimina tutti i consigli associati al libro corrente previa conferma.
     * <p>In caso di successo mostra conferma e torna alla schermata precedente.</p>
     */
    @FXML private void eliminaConsigli() {
        if(CliUtil.getInstance().createConfirmation("Eliminazione Consiglio", "Sei sicuro di voler eliminare questo consiglio?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES){
            try {
                if (CliUtil.getInstance().getLibService().deleteCon(CliUtil.getInstance().getCurrentToken(), myLibro)) {
                    CliUtil.getInstance().createConfirmation("Successo", "Consiglio eliminato!", true).showAndWait();
                    CliUtil.getInstance().buildStage(oldFXMLType,null, null);
                } else {
                    CliUtil.getInstance().createAlert("Errore", "Eliminazione fallita").showAndWait();
                }
            } catch (Exception e) {
                CliUtil.getInstance().LogOut(e);
            }
        }
    }
}
