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

/**
 * Controller JavaFX per la schermata di ricerca avanzata dei libri.
 * <p>
 * Consente di cercare libri per:
 *     <li>Titolo</li>
 *     <li>Autore</li>
 *     <li>Autore e anno</li>
 * </ul>
 * Visualizza i risultati in una tabella con funzionalità aggiuntive come:
 * <ul>
 *     <li>Controllo della presenza del libro nelle proprie librerie</li>
 *     <li>Colonna per accedere alle recensioni</li>
 *     <li>Bottone per aggiungere libri</li>
 * </ul>
 * Estende {@link TableViewEngine} per ereditare la logica di gestione tabelle e ricerca.
 */
public class CercaLibroAvanzato extends TableViewEngine {

    @FXML private ProgressIndicator loadingCircle;
    @FXML private TextField campoRicerca;
    @FXML private TextField campoRicercaAnno;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;

    @FXML private TableView<Libro> tableView;
    @FXML private TableColumn<Libro, String> titoloCol;
    @FXML private TableColumn<Libro, String> autoreCol;
    @FXML private TableColumn<Libro, Integer> annoCol;
    @FXML private TableColumn<Libro, Void> recensioniCol;
    @FXML private TableColumn<Libro, Void> librerieCol;
    @FXML private TableColumn<Libro, Void> aggiungiCol;

    private boolean searchType = false;

    /**
     * Inizializza la GUI al momento del caricamento FXML.
     * <ul>
     *     <li>Imposta i menu e le colonne della tabella</li>
     *     <li>Configura il controllo per visualizzare la presenza del libro nelle librerie</li>
     *     <li>Registra il comportamento di chiusura dell'app (logout automatico)</li>
     * </ul>
     */
    public void initialize() {
        initBasicSearch();
        initSRecensioniCol();
        initSAggiungiAdvCol();
        librerieCol.setStyle("-fx-alignment: CENTER;");
        librerieCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                Libro libro = getTableView().getItems().get(getIndex());
                setGraphic(getInLib().get(libro) ? (IMGtype.CHECK.getImageView(12,12)) : IMGtype.RED_CROSS.getImageView(12,12));
            }
        });
        initTableViews();
        Platform.runLater(() -> {
            Stage stage = (Stage) MenuTipoRicerca.getScene().getWindow();
            stage.setOnCloseRequest(evt -> {
                try {
                    CliUtil.getInstance().getLogRegService().LogOut(CliUtil.getInstance().getCurrentToken());
                } catch (RemoteException ignored) {}
                Platform.exit();
                System.exit(0);
            });
        });
    }

    /** @return campo di ricerca principale (titolo o autore) */
    @Override
    protected TextField getCampoRicerca() {
        return campoRicerca;
    }

    /** @return campo di ricerca per l'anno di pubblicazione */
    @Override
    protected TextField getCampoRicercaAnno() {
        return campoRicercaAnno;
    }

    /** @return menu per la selezione del tipo di ricerca */
    @Override
    protected MenuButton getMenuTipoRicerca() {
        return MenuTipoRicerca;
    }

    /** @return opzione di ricerca per titolo */
    @Override
    protected MenuItem getMenuCercaTitolo() {
        return MenuCercaTitolo;
    }

    /** @return opzione di ricerca per autore */
    @Override
    protected MenuItem getMenuCercaAutore() {
        return MenuCercaAutore;
    }

    /** @return opzione di ricerca per autore e anno */
    @Override
    protected MenuItem getMenuCercaAutoreAnno() {
        return MenuCercaAutoreAnno;
    }

    /** @return tabella che contiene i risultati della ricerca */
    @Override protected TableView<Libro> getSTableView() {
        return tableView;
    }

    /** @return colonna dei titoli dei libri */
    @Override
    protected TableColumn<Libro, String> getSTitoloCol() {
        return titoloCol;
    }

    /** @return colonna degli autori dei libri */
    @Override
    protected TableColumn<Libro, String> getSAutoreCol() {
        return autoreCol;
    }

    /** @return colonna degli anni di pubblicazione */
    @Override
    protected TableColumn<Libro, Integer> getSAnnoCol() {
        return annoCol;
    }

    /** @return colonna con il bottone per visualizzare recensioni */
    @Override
    protected TableColumn<Libro, Void> getSRecensioniCol() {
        return recensioniCol;
    }

    /** @return colonna con il bottone per aggiungere il libro */
    @Override
    protected TableColumn<Libro, Void> getSAggiungiAdvCol() {
        return aggiungiCol;
    }

    /**
     * Metodo non usato in questa schermata, quindi restituisce {@code null}.
     * @return {@code null}
     */
    @Override
    protected TableColumn<Libro, Void> getSAddRemCol() {
        return null;
    }

    /** @return {@code null} perché la tabella secondaria non è utilizzata in questa vista */
    @Override
    protected TableView<Libro> getOTableView() {
        return null;
    }

    /** @return {@code null} perché la tabella secondaria non è utilizzata in questa vista */
    @Override
    protected TableColumn<Libro, String> getOTitoloCol() {
        return null;
    }

    /** @return {@code null} perché la tabella secondaria non è utilizzata in questa vista */
    @Override
    protected TableColumn<Libro, String> getOAutoreCol() {
        return null;
    }

    /** @return {@code null} perché la tabella secondaria non è utilizzata in questa vista */
    @Override
    protected TableColumn<Libro, Integer> getOAnnoCol() {
        return null;
    }

    /** @return {@code null} perché la tabella secondaria non è utilizzata in questa vista */
    @Override
    protected TableColumn<Libro, Void> getOActionCol() {return null;}

    /**
     * @return valore booleano che rappresenta lo stato interno della modalità di ricerca.
     * Viene usato da {@link TableViewEngine} per variare il comportamento.
     */
    @Override
    protected boolean getSearchType() {return searchType;}

    /**
     * Non usato in questa schermata.
     * @return sempre {@code null}
     */
    @Override
    protected Libro getMyLibro() {return null;}

    /**
     * Restituisce il tipo di FXML associato a questa schermata.
     * Utile per tornare correttamente alla vista corrente.
     *
     * @return {@code FXMLtype.CERCA_AVANZATO}
     */
    @Override
    protected FXMLtype getMyFXMLtype() {
        return FXMLtype.CERCA_AVANZATO;
    }

    @Override
    protected ProgressIndicator getProgressIndicator() {
        return loadingCircle;
    }

    /**
     * Torna al menu principale (area riservata) chiudendo la schermata corrente.
     */
    @FXML
    private void GoToMainMenu(){
        CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null, null);
    }

    /**
     * Inverte il valore booleano interno {@code searchType}, che distingue le modalità di ricerca.
     * Usato per aggiornare il comportamento dei metodi ereditati.
     */
    public void setRicerca() {
        searchType = !searchType;
    }
}
