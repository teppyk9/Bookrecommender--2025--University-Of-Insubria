package bookrecommender.client.ui;

import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.util.TableViewEngine;
import bookrecommender.common.model.Libro;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.Stage;
/**
 * Controller JavaFX per la schermata di ricerca semplice dei libri.
 * <p>
 * Permette all’utente di cercare libri per titolo, autore o anno,
 * e visualizzare i risultati in una {@link TableView}.
 * I risultati includono una colonna con pulsanti per accedere alle recensioni.
 * </p>
 * <p>
 * Estende {@link TableViewEngine} per riutilizzare la logica comune
 * alle ricerche tabellari dell'applicazione.
 * </p>
 */
public class CercaLibro extends TableViewEngine {

    @FXML private Button bottoneCerca;
    @FXML private Button GoBackButton_MainMenu;
    @FXML private ProgressIndicator loadingCircle;
    @FXML private TextField campoRicerca;
    @FXML private TextField campoRicercaAnno;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;
    @FXML private MenuButton limiterBox;

    @FXML private TableView<Libro> tableView;
    @FXML private TableColumn<Libro, String> titoloCol;
    @FXML private TableColumn<Libro, String> autoreCol;
    @FXML private TableColumn<Libro, Integer> annoCol;
    @FXML private TableColumn<Libro, Boolean> recensioniCol;

    /**
     * Inizializza la schermata e i componenti grafici.
     * <ul>
     *     <li>Configura i bottoni e le icone</li>
     *     <li>Inizializza la logica di ricerca semplice</li>
     *     <li>Prepara le colonne della tabella, comprese quelle con recensioni</li>
     *     <li>Registra un handler per la chiusura forzata dell'applicazione</li>
     * </ul>
     */
    @FXML private void initialize() {
        bottoneCerca.setGraphic(IMGtype.CERCA.getImageView(25,25));
        bottoneCerca.setAlignment(Pos.CENTER_LEFT);
        bottoneCerca.setStyle("-fx-padding: 0");
        GoBackButton_MainMenu.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        GoBackButton_MainMenu.setAlignment(Pos.TOP_LEFT);
        initBasicSearch();
        initSRecensioniCol();
        initTableViews();
        initLimiter();
        Platform.runLater(() -> {
            Stage stage = (Stage) MenuTipoRicerca.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    /** @return campo testo per inserire il titolo o l'autore */
    @Override
    protected TextField getCampoRicerca() {
        return campoRicerca;
    }

    /** @return campo testo per inserire l'anno di pubblicazione */
    @Override
    protected TextField getCampoRicercaAnno() {
        return campoRicercaAnno;
    }

    /** @return menu a tendina per selezionare il tipo di ricerca */
    @Override
    protected MenuButton getMenuTipoRicerca() {
        return MenuTipoRicerca;
    }

    /** @return voce di menu per la ricerca per titolo */
    @Override
    protected MenuItem getMenuCercaTitolo() {
        return MenuCercaTitolo;
    }

    /** @return voce di menu per la ricerca per autore */
    @Override
    protected MenuItem getMenuCercaAutore() {
        return MenuCercaAutore;
    }

    /** @return voce di menu per la ricerca per autore e anno */
    @Override
    protected MenuItem getMenuCercaAutoreAnno() {
        return MenuCercaAutoreAnno;
    }

    /** @return tabella contenente i risultati della ricerca */
    @Override
    protected TableView<Libro> getSTableView() {
        return tableView;
    }

    /** @return colonna dei titoli dei libri */
    @Override
    protected TableColumn<Libro, String> getSTitoloCol() {
        return titoloCol;
    }

    /** @return colonna dei titoli dei libri */
    @Override
    protected TableColumn<Libro, String> getSAutoreCol() {
        return autoreCol;
    }

    /** @return colonna degli anni di pubblicazione */
    @Override
    protected TableColumn<Libro, Integer> getSAnnoCol() {
        return annoCol;
    }

    /** @return colonna con il bottone per visualizzare le recensioni del libro */
    @Override
    protected TableColumn<Libro, Boolean> getSRecensioniCol() {
        return recensioniCol;
    }

    /**
     * Colonna non utilizzata in questa schermata.
     * @return {@code null}
     */
    @Override
    protected TableColumn<Libro, Void> getSAggiungiAdvCol() {
        return null;
    }

    /**
     * Colonna non utilizzata in questa schermata.
     * @return {@code null}
     */
    @Override
    protected TableColumn<Libro, Void> getSAddRemCol() {
        return null;
    }

    /**
     * Tabella secondaria non utilizzata in questa schermata.
     * @return {@code null}
     */
    @Override
    protected TableView<Libro> getOTableView() {
        return null;
    }

    /**
     * Colonna secondaria non usata in questa schermata.
     * @return {@code null}
     */
    @Override
    protected TableColumn<Libro, String> getOTitoloCol() {
        return null;
    }

    /**
     * Colonna secondaria non usata in questa schermata.
     * @return {@code null}
     */
    @Override
    protected TableColumn<Libro, String> getOAutoreCol() {
        return null;
    }

    /**
     * Colonna secondaria non usata in questa schermata.
     * @return {@code null}
     */
    @Override
    protected TableColumn<Libro, Integer> getOAnnoCol() {
        return null;
    }

    /**
     * Colonna di azioni secondarie non usata.
     * @return {@code null}
     */
    @Override
    protected TableColumn<Libro, Void> getOActionCol() {return null;}

    /**
     * Modalità di ricerca non alternabile in questa vista.
     * @return {@code false}
     */
    @Override
    protected boolean getSearchType() {return false;}

    /**
     * Nessun libro preimpostato in questa schermata.
     * @return {@code null}
     */
    @Override
    protected Libro getMyLibro() {return null;}

    /**
     * Tipo FXML non definito esplicitamente per questa schermata.
     * @return {@code null}
     */
    @Override
    protected FXMLtype getMyFXMLtype() {
        return FXMLtype.CERCA;
    }

    /** Indicatore di caricamento per operazioni asincrone.
     * @return l'indicatore di questa interfaccia*/
    @Override
    protected ProgressIndicator getProgressIndicator() {
        return loadingCircle;
    }

    /**
     * @return Menu per limitare il numero di risultati mostrati.
     */
    @Override
    protected MenuButton getLimiterBox() {
        return limiterBox;
    }

    /**
     * Torna alla schermata principale dell’applicazione.
     */
    @FXML
    private void GoToMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null,null);
    }
}
