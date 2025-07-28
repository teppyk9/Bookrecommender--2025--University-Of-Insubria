package bookrecommender.client.ui;

import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.util.TreeTableEngine;
import bookrecommender.common.model.Libro;
import bookrecommender.common.model.Token;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller JavaFX che gestisce la finestra per l'aggiunta di un {@link Libro}
 * a una libreria utente esistente.
 * <p>
 * Estende {@link TreeTableEngine}, sfruttando una {@code TreeTableView} per
 * rappresentare graficamente l'albero delle librerie dell'utente e il contenuto di ciascuna.
 * Permette la selezione di una libreria e l'aggiunta del libro selezionato a essa.
 * </p>
 *
 * <p>Colonne visualizzate nella tabella:</p>
 * <ul>
 *   <li><b>nameColumn</b>: nome della libreria o titolo del libro</li>
 *   <li><b>countColumn</b>: numero di libri contenuti nella libreria</li>
 *   <li><b>presentColumn</b>: indicazione grafica se il libro è già presente</li>
 *   <li><b>dateColumn</b>: data di creazione della libreria</li>
 * </ul>
 * @see TreeTableEngine
 */
public class AddLibroLibreria extends TreeTableEngine {
    @FXML private Button ExitButton;
    @FXML private Text titoloLibreria;
    @FXML private TreeTableColumn<Object, String> nameColumn;
    @FXML private TreeTableColumn<Object, Integer> countColumn;
    @FXML private TreeTableColumn<Object, Boolean> presentColumn;
    @FXML private TreeTableColumn<Object, LocalDate> dateColumn;
    @FXML private Button addButton;

    private Libro libro;
    private FXMLtype odlFXML;

    private final Map<String, Integer> libCounts = new HashMap<>();
    private final Map<String, Boolean> libPresent = new HashMap<>();
    private final Map<String, LocalDate> libDates = new HashMap<>();

    /**
     * Metodo di inizializzazione della GUI. Carica le librerie dell'utente,
     * imposta il comportamento delle colonne e i listener per la selezione.
     */
    @FXML private void initialize() {
        ExitButton.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        ExitButton.setAlignment(Pos.TOP_LEFT);
        titoloLibreria.setText("Le tue librerie");
        initializeTree();

        // Colonna con nomi librerie e titoli libro
        //TODO: css name column
        nameColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String s)
                return new ReadOnlyStringWrapper(s);
            if (v instanceof Libro l)
                return new ReadOnlyStringWrapper(l.getTitolo());
            return new ReadOnlyStringWrapper("");
        });

        // Caricamento colonne semplici (conteggio, date, presenza)
        loadSimple(countColumn,libCounts);
        loadSimple(dateColumn, libDates);
        loadSimple(presentColumn, libPresent);

        // Icone visive per colonna "presente"
        //TODO: presentColumn style
        presentColumn.setCellFactory(col -> new TreeTableCell<>() {
            @Override
            protected void updateItem(Boolean present, boolean empty) {
                super.updateItem(present, empty);
                if (empty || present == null)
                    setGraphic(null);
                else
                    setGraphic(present ? IMGtype.CHECK.getImageView(12,12) : IMGtype.RED_CROSS.getImageView(12,12));
            }
        });

        // Listener su selezione libreria
        treeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null && newSel.getValue() instanceof String nomeLib) {
                addButton.setDisable(libPresent.getOrDefault(nomeLib, true));
                titoloLibreria.setText(nomeLib);
            } else {
                addButton.setDisable(true);
            }
        });

        loadLibraries();
    }

    /**
     * Gestisce il doppio click su un nodo della TreeTableView per mostrare i dettagli del libro.
     *
     * @param v oggetto selezionato (di tipo {@code Libro} se valido)
     */
    @Override
    protected void handleDoubleClick(Object v) {
        if (v instanceof Libro l) {
            CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, null, l);
        }
    }

    /**
     * Restituisce una mappa con il numero di libri per ciascuna libreria.
     *
     * @return mappa nome libreria → numero di libri
     */
    @Override
    protected Map<String, Integer> getLibCounts() {
        return libCounts;
    }

    /**
     * Restituisce una mappa con la data di creazione per ciascuna libreria.
     *
     * @return mappa nome libreria → data di creazione
     */
    @Override
    protected Map<String, LocalDate> getLibDates() {
        return libDates;
    }

    @Override
    protected Map<String, Boolean> getLibPresent() {
        return libPresent;
    }

    @Override
    protected Libro getMyLibro() {
        return libro;
    }

    /**
     * Aggiunge come figli della libreria i libri corrispondenti e aggiorna lo stato di presenza.
     *
     * @param libNode  nodo padre (nome libreria)
     * @param nomeLib  nome della libreria
     */
    @Override
    protected void caricaFigliLibri(TreeItem<Object> libNode, String nomeLib) {
        try {
            Token token = CliUtil.getInstance().getCurrentToken();
            List<Libro> list = CliUtil.getInstance().getLibService().getLib(token, nomeLib);
            libPresent.put(nomeLib, list.contains(libro));
            for (Libro b : list) {
                libNode.getChildren().add(new TreeItem<>(b));
            }
            libNode.setExpanded(true);
            treeTableView.refresh();
        } catch (Exception e) {
            CliUtil.getInstance().LogOut(e);
        }
    }

    /**
     * Imposta il {@link Libro} da aggiungere alle librerie.
     *
     * @param libro libro da aggiungere
     */
    public void setLibro(Libro libro, FXMLtype odlFXML) {
        this.odlFXML = odlFXML;
        this.libro = libro;
        loadLibraries();
    }

    /**
     * Gestisce il click sul bottone "Aggiungi".
     * Aggiunge il libro alla libreria selezionata e aggiorna il server remoto.
     */
    @FXML
    private void handleAddBook() {
        TreeItem<Object> sel = treeTableView.getSelectionModel().getSelectedItem();
        if (sel == null || !(sel.getValue() instanceof String nomeLib)) return;
        try {
            List<Libro> current = CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nomeLib);
            List<Libro> updated = new ArrayList<>(current);
            updated.add(libro);
            List<Integer> risultati = CliUtil.getInstance().getLibService().updateLib(CliUtil.getInstance().getCurrentToken(), nomeLib, updated);
            if (!risultati.isEmpty() && risultati.get(0) == 1) {
                CliUtil.getInstance().createConfirmation("Aggiornamento riuscito", "Il libro è stato aggiunto correttamente alla libreria '" + nomeLib + "'.", false).showAndWait();
                CliUtil.getInstance().buildStage(odlFXML, null, null);
            } else {
                CliUtil.getInstance().reviewLibUpdate(risultati);
            }
        } catch (Exception e) {
            CliUtil.getInstance().LogOut(e);        }
    }

    /**
     * Chiude la finestra corrente senza eseguire modifiche.
     */
    @FXML
    private void ExitApplication() {
        CliUtil.getInstance().buildStage(odlFXML, null, null);
    }
}
