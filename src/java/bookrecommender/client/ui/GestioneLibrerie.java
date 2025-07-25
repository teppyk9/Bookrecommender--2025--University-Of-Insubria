package bookrecommender.client.ui;

import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.util.TreeTableEngine;
import bookrecommender.common.model.Libro;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controller JavaFX per la schermata di gestione delle librerie personali dell’utente.
 * <p>
 * Estende {@link TreeTableEngine} per presentare una struttura ad albero in cui ogni nodo radice
 * rappresenta una libreria e i nodi figli rappresentano i libri contenuti.
 * Permette di:
 * <ul>
 *     <li>Visualizzare metadati e stato di valutazioni/consigli per ogni libro</li>
 *     <li>Modificare o eliminare librerie</li>
 *     <li>Valutare, consigliare, rimuovere o modificare libri</li>
 *     <li>Rinominare librerie tramite form e pulsante</li>
 * </ul>
 * La comunicazione con il server avviene tramite RMI.
 */
public class GestioneLibrerie extends TreeTableEngine {

    @FXML private Button ExitButton;
    /** Colonna che mostra il nome della libreria o il titolo del libro. */
    @FXML private TreeTableColumn<Object, String> nameColumn;

    /** Colonna che mostra il numero di libri contenuti nella libreria. */
    @FXML private TreeTableColumn<Object, Integer> countColumn;

    /** Colonna che mostra la data di creazione della libreria. */
    @FXML private TreeTableColumn<Object, LocalDate> dateColumn;

    /** Colonna che contiene i pulsanti di azione (modifica, elimina, valuta, ecc.). */
    @FXML private TreeTableColumn<Object, Void> azioniColumn;

    /** Colonna che indica se un libro ha almeno una valutazione. */
    @FXML private TreeTableColumn<Object, Boolean> isValColumn;

    /** Colonna che indica se un libro ha almeno un consiglio. */
    @FXML private TreeTableColumn<Object, Boolean> isConsColumn;

    /** Colonna che mostra la data dell’ultima valutazione del libro. */
    @FXML private TreeTableColumn<Object, LocalDate> lastValColumn;

    /** Colonna che mostra la data dell’ultimo consiglio associato al libro. */
    @FXML private TreeTableColumn<Object, LocalDate> lastConsColumn;

    /** Campo di testo per inserire il nuovo nome della libreria selezionata. */
    @FXML private TextField NomeLibreria;

    /** Pulsante che conferma la modifica del nome della libreria. */
    @FXML private Button BottoneCambiaNome;

    /** Mappa che associa a ogni nome di libreria il numero di libri in essa contenuti. */
    private final Map<String, Integer> libCounts = new HashMap<>();

    /** Mappa che associa a ogni nome di libreria la sua data di creazione. */
    private final Map<String, LocalDate> libDates = new HashMap<>();

    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX dopo il caricamento dell’FXML.
     * Configura le colonne della tabella, associa le proprietà visive, imposta i listener degli eventi,
     * e carica inizialmente tutte le librerie dell’utente tramite invocazione remota al server.
     * <p>
     * Le colonne vengono popolate con proprietà specifiche degli oggetti:
     * <ul>
     *     <li>{@code nameColumn} mostra il titolo della libreria o del libro</li>
     *     <li>{@code countColumn} e {@code dateColumn} sono collegate alle mappe {@code libCounts} e {@code libDates}</li>
     *     <li>{@code isValColumn} e {@code isConsColumn} mostrano check/icon in base alla presenza di valutazioni/consigli</li>
     *     <li>{@code lastValColumn} e {@code lastConsColumn} mostrano la data dell’ultima valutazione/consiglio</li>
     *     <li>{@code azioniColumn} visualizza menu dinamici contestuali (per librerie o libri)</li>
     * </ul>
     * Viene anche registrato un evento per la chiusura della finestra e viene disabilitato il pulsante
     * {@code BottoneCambiaNome} se non è selezionata una libreria.
     */
    @FXML private void initialize() {
        ExitButton.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        initializeTree();

        nameColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String s) return new ReadOnlyStringWrapper(s);
            if (v instanceof LibroRow row) return row.titoloProperty();
            return new ReadOnlyStringWrapper("");
        });

        loadSimple(countColumn,libCounts);
        loadSimple(dateColumn, libDates);

        setupCheckColumn(isValColumn, LibroRow::hasValutazioneProperty);

        lastValColumn.setStyle("-fx-alignment: CENTER;");
        lastValColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof LibroRow r) return r.lastValDateProperty();
            return new ReadOnlyObjectWrapper<>(null);
        });

        setupCheckColumn(isConsColumn, LibroRow::hasConsiglioProperty);

        lastConsColumn.setStyle("-fx-alignment: CENTER;");
        lastConsColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof LibroRow r) return r.lastConsDateProperty();
            return new ReadOnlyObjectWrapper<>(null);
        });

        azioniColumn.setCellFactory(col -> new TreeTableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                Object v = getTableRow().getItem();
                if (v instanceof String) setGraphic(createMenuLibrerieActions(this));
                else if (v instanceof LibroRow) setGraphic(createMenuBookActions(this));
                else setGraphic(null);
                setAlignment(Pos.CENTER);
            }
        });

        BottoneCambiaNome.setDisable(true);

        treeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> BottoneCambiaNome.setDisable(newSel == null || !(newSel.getValue() instanceof String)));

        loadLibraries();

        Platform.runLater(() -> {
            Stage stage = (Stage) BottoneCambiaNome.getScene().getWindow();
            stage.setOnCloseRequest(e -> { Platform.exit(); System.exit(0); });
        });
    }

    /**
     * Configura una colonna booleana per visualizzare un'icona (check o alert)
     * in base alla proprietà restituita da una funzione applicata a {@link LibroRow}.
     *
     * @param col  La colonna da configurare.
     * @param prop La funzione che restituisce la proprietà booleana da visualizzare.
     */
    private void setupCheckColumn(TreeTableColumn<Object, Boolean> col, Function<LibroRow, BooleanProperty> prop) {
        col.setStyle("-fx-alignment: CENTER;");
        col.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof LibroRow r) return prop.apply(r);
            return new ReadOnlyObjectWrapper<>(null);
        });
        col.setCellFactory(c -> new TreeTableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setGraphic(null);
                else {
                    setGraphic(item ? IMGtype.CHECK.getImageView(12,12) : IMGtype.RED_CROSS.getImageView(12,12));
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    /**
     * Gestisce il doppio clic su un elemento della tabella.
     * Se si tratta di una libreria, apre la schermata di modifica della libreria.
     * Se si tratta di un libro, apre la schermata di dettaglio del libro.
     *
     * @param v Oggetto selezionato dall'utente.
     */
    @Override
    protected void handleDoubleClick(Object v) {
        if (v instanceof String s)
            CliUtil.getInstance().buildStage(FXMLtype.MODIFICALIBRERIA, null, s);
        else if (v instanceof LibroRow lr)
            CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, null, lr.getLibro());
    }

    /**
     * Restituisce la mappa con i conteggi dei libri per ciascuna libreria.
     *
     * @return Mappa nome libreria → numero di libri.
     */
    @Override
    protected Map<String, Integer> getLibCounts() {
        return libCounts;
    }

    /**
     * Restituisce la mappa con le date di creazione delle librerie.
     *
     * @return Mappa nome libreria → data di creazione.
     */
    @Override
    protected Map<String, LocalDate> getLibDates() {
        return libDates;
    }

    @Override
    protected Map<String, Boolean> getLibPresent() {
        return null;
    }

    @Override
    protected Libro getMyLibro() {
        return null;
    }

    /**
     * Carica i libri associati a una libreria specifica e li inserisce come figli
     * del nodo della TreeTable. Per ogni libro, recupera dal server informazioni
     * su valutazioni, consigli e relative date.
     * <p>
     * In caso di errore durante la comunicazione, viene mostrato un alert.
     *
     * @param libNode Nodo della libreria nella TreeTable.
     * @param nomeLib Nome della libreria.
     */
    @Override
    protected void caricaFigliLibri(TreeItem<Object> libNode, String nomeLib) {
        try {
            List<Libro> list = CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nomeLib);
            for (Libro l : list) {
                boolean hasVal = CliUtil.getInstance().getLibService().existVal(CliUtil.getInstance().getCurrentToken(), l);
                boolean hasCons = CliUtil.getInstance().getLibService().existCon(CliUtil.getInstance().getCurrentToken(), l);
                LocalDate valD = CliUtil.getInstance().getLibService().getValDate(CliUtil.getInstance().getCurrentToken(), l);
                LocalDate consD = CliUtil.getInstance().getLibService().getConDate(CliUtil.getInstance().getCurrentToken(), l);
                LibroRow row = new LibroRow(l, hasVal, hasCons, valD, consD);
                libNode.getChildren().add(new TreeItem<>(row));
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().LogOut(e);        }
    }

    /**
     * Gestisce la modifica del nome di una libreria selezionata.
     * <p>
     * Il metodo verifica che:
     * <ul>
     *     <li>Sia selezionata una libreria</li>
     *     <li>Il nuovo nome abbia lunghezza compresa tra 5 e 50 caratteri</li>
     *     <li>Il nuovo nome sia diverso da quello attuale</li>
     * </ul>
     * Se tutti i controlli sono superati, invia la richiesta di modifica al server tramite RMI.
     * In base all'esito della modifica, mostra un messaggio di successo o errore.
     */
    @FXML
    private void cambiaNome() {
        Object o = treeTableView.getSelectionModel().getSelectedItem().getValue();
        if (o instanceof String oldName) {
            String newName = NomeLibreria.getText().trim();
            if (newName.length() < 5 || newName.length() > 50) {
                CliUtil.getInstance().createAlert("Errore", "Lunghezza compresa tra 5 e 50").showAndWait();
            } else if (newName.equals(oldName)) {
                CliUtil.getInstance().createAlert("Errore", "Nome uguale").showAndWait();
            } else {
                try {
                    if (CliUtil.getInstance().getLibService().modifyLibName(CliUtil.getInstance().getCurrentToken(), oldName, newName)) {
                        CliUtil.getInstance().createConfirmation("Successo", "Nome modificato", false).showAndWait();
                        NomeLibreria.clear();
                        loadLibraries();
                    } else {
                        CliUtil.getInstance().createAlert("Errore", "Modifica fallita").showAndWait();
                    }
                } catch (RemoteException e) {
                    CliUtil.getInstance().LogOut(e);
                }
            }
        } else {
            CliUtil.getInstance().createAlert("Errore", "Seleziona una libreria").showAndWait();
        }
    }

    /**
     * Ritorna alla schermata dell'area riservata dell'utente.
     */
    @FXML
    private void ExitApplication() {
        CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null, null);
    }

    /**
     * Apre la schermata per la creazione di una nuova libreria.
     */
    @FXML private void creaLibreria() {
        CliUtil.getInstance().buildStage(FXMLtype.CREALIBRERIA, null, null);
    }

    /**
     * Crea e restituisce un {@link MenuButton} contenente le azioni disponibili per una libreria,
     * come "Modifica" e "Elimina". Il comportamento di ogni voce è gestito tramite listener.
     * Se viene selezionata l’opzione "Elimina", viene chiesta conferma e, in caso positivo,
     * viene inviata la richiesta di rimozione della libreria al server.
     *
     * @param cell La cella di tabella contenente la libreria selezionata.
     * @return MenuButton con azioni contestuali per la libreria.
     */
    private MenuButton createMenuLibrerieActions(TreeTableCell<Object, Void> cell) {
        MenuButton mb = new MenuButton();
        MenuItem modifica = new MenuItem("Modifica Libreria");
        MenuItem rimuovi = new MenuItem("Elimina Libreria");
        mb.getItems().addAll(modifica, rimuovi);
        setMenuButtonStyle(mb);
        modifica.setOnAction(evt -> {
            Object o = cell.getTableRow().getItem();
            if (o instanceof String nome) {
                CliUtil.getInstance().buildStage(FXMLtype.MODIFICALIBRERIA, null, nome);
            } else {
                CliUtil.getInstance().createAlert("Errore", "Modifica button on a " + o.getClass()).showAndWait();
            }
        });
        rimuovi.setOnAction(evt -> {
            Object o = cell.getTableRow().getItem();
            if (o instanceof String nome) {
                if (CliUtil.getInstance().createConfirmation("Conferma", "Sei sicuro di voler eliminare la libreria " + nome + "?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
                    try {
                        if (CliUtil.getInstance().getLibService().deleteLib(CliUtil.getInstance().getCurrentToken(), nome)) {
                            CliUtil.getInstance().createConfirmation("Successo", "Libreria eliminata con successo!", false).showAndWait();
                            loadLibraries();
                        } else {
                            CliUtil.getInstance().createAlert("Errore", "Errore nell'eliminazione").showAndWait();
                        }
                    } catch (RemoteException e) {
                        CliUtil.getInstance().LogOut(e);
                    }
                }
            } else {
                CliUtil.getInstance().createAlert("Errore", "Elimina button on a " + o.getClass()).showAndWait();
            }
        });
        return mb;
    }

    /**
     * Crea e restituisce un {@link MenuButton} contenente le azioni disponibili per un libro.
     * Le azioni includono:
     * <ul>
     *     <li>Aggiungi o modifica valutazione</li>
     *     <li>Aggiungi o modifica consiglio</li>
     *     <li>Rimuovi il libro dalla libreria</li>
     * </ul>
     * Se il libro ha meno di 3 elementi dopo la rimozione, la libreria non può essere aggiornata.
     * Tutte le modifiche sono eseguite tramite chiamate RMI e l'utente è notificato con alert o conferme.
     *
     * @param cell La cella di tabella contenente il libro selezionato.
     * @return MenuButton con azioni contestuali per il libro.
     */
    private MenuButton createMenuBookActions(TreeTableCell<Object, Void> cell) {
        MenuButton mb = new MenuButton();
        MenuItem valuta = new MenuItem("Aggiungi Valutazione");
        MenuItem consiglia = new MenuItem("Aggiungi Consigli");
        MenuItem rimuovi = new MenuItem("Rimuovi dalla libreria");
        MenuItem modVal = new MenuItem("Modifica Valutazione");
        MenuItem modCons = new MenuItem("Modifica Consiglio");
        setMenuButtonStyle(mb);

        valuta.setOnAction(evt -> {
            Object o = cell.getTableRow().getItem();
            if (o instanceof LibroRow lr) {
                CliUtil.getInstance().buildStage(FXMLtype.CREAVALUTAZIONE, FXMLtype.GESTIONELIBRERIE, lr.getLibro());
            } else {
                CliUtil.getInstance().createAlert("Errore", "Valuta needs Libro, got " + o.getClass()).showAndWait();
            }
        });

        consiglia.setOnAction(evt -> {
            Object o = cell.getTableRow().getItem();
            if (o instanceof LibroRow lr) {
                CliUtil.getInstance().buildStage(FXMLtype.CREACONSIGLIO, FXMLtype.GESTIONELIBRERIE, lr.getLibro());
            } else {
                CliUtil.getInstance().createAlert("Errore", "Consiglia needs Libro, got " + o.getClass()).showAndWait();
            }
        });

        rimuovi.setOnAction(evt -> {
            TreeItem<Object> thisItem = cell.getTableRow().getTreeItem();
            if (thisItem == null) return;
            TreeItem<Object> libNode = thisItem.getParent();
            if (libNode == null) return;
            Object libV = libNode.getValue();
            if (!(libV instanceof String nomeLib)) return;
            List<LibroRow> libri = libNode.getChildren().stream().map(TreeItem::getValue).filter(v -> v instanceof LibroRow).map(v -> (LibroRow) v).collect(Collectors.toList());
            LibroRow lr = (LibroRow) thisItem.getValue();
            libri.removeIf(rw -> rw.getLibro().equals(lr.getLibro()));
            if (libri.size() < 3) {
                CliUtil.getInstance().createAlert("Errore", "La libreria deve contenere almeno 3 libri").showAndWait();
                return;
            }
            if (CliUtil.getInstance().createConfirmation("Conferma", "Sei sicuro?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
                try {
                    List<Libro> modList = libri.stream().map(LibroRow::getLibro).collect(Collectors.toList());
                    List<Integer> results = CliUtil.getInstance().getLibService().updateLib(CliUtil.getInstance().getCurrentToken(), nomeLib, modList);
                    if (!results.isEmpty() && results.get(0) == 1) {
                        CliUtil.getInstance().createConfirmation("Aggiornato", "Rimosso correttamente", false).showAndWait();
                        loadLibraries();
                    } else {
                        CliUtil.getInstance().reviewLibUpdate(results);
                    }
                } catch (RemoteException e) {
                    CliUtil.getInstance().LogOut(e);
                }
            }
        });

        modVal.setOnAction(evt -> {
            Object o = cell.getTableRow().getItem();
            if (o instanceof LibroRow lr) {
                try {
                    CliUtil.getInstance().buildStage(FXMLtype.MODIFICAVALUTAZIONE, FXMLtype.GESTIONELIBRERIE, CliUtil.getInstance().getLibService().getValutazione(CliUtil.getInstance().getCurrentToken(), lr.getLibro()));
                } catch (RemoteException e) {
                    CliUtil.getInstance().LogOut(e);
                }
            } else {
                CliUtil.getInstance().createAlert("Errore", "Consiglia needs Libro, got " + o.getClass()).showAndWait();
            }
        });

        modCons.setOnAction(evt -> {
            Object o = cell.getTableRow().getItem();
            if (o instanceof LibroRow lr) {
                CliUtil.getInstance().buildStage(FXMLtype.MODIFICACONSIGLIO, FXMLtype.GESTIONELIBRERIE, lr.getLibro());
            } else {
                CliUtil.getInstance().createAlert("Errore", "Consiglia needs Libro, got " + o.getClass()).showAndWait();
            }
        });

        Object o = cell.getTableRow().getItem();
        if (o instanceof LibroRow lr) {
            mb.getItems().addAll((lr.hasValutazione() ? modVal : valuta), (lr.hasConsiglio() ? modCons : consiglia), rimuovi);
        }

        return mb;
    }

    /**
     * Applica uno stile grafico al {@link MenuButton} inserendo un'icona a discesa e applicando
     * lo stile definito da {@link CliUtil}.
     *
     * @param mb Il pulsante a cui applicare lo stile.
     */
    private void setMenuButtonStyle(MenuButton mb) {
        mb.setGraphic(IMGtype.ARROW_DOWN.getImageView(12,12));
        CliUtil.getInstance().styleIconControl(mb);
        mb.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Node arrow = mb.lookup(".arrow");
                if (arrow != null) {
                    arrow.setVisible(false);
                    arrow.setManaged(false);
                }
            }
        });
    }

    /**
     * Classe di supporto per rappresentare un libro all’interno della TreeTableView.
     * <p>
     * Ogni oggetto {@code LibroRow} contiene:
     * <ul>
     *     <li>Il riferimento al {@link Libro} originale</li>
     *     <li>Il titolo come proprietà osservabile per l’interfaccia</li>
     *     <li>Due booleani per sapere se esistono valutazioni e consigli</li>
     *     <li>Le date dell’ultima valutazione e dell’ultimo consiglio</li>
     * </ul>
     * Viene usata come wrapper per facilitare il binding tra dati e colonne nella tabella.
     */
    public static class LibroRow {

        /** Riferimento al libro rappresentato da questa riga. */
        private final Libro libro;

        /** Titolo del libro, usato come proprietà osservabile. */
        private final StringProperty titolo;

        /** Proprietà booleana che indica se il libro ha almeno una valutazione. */
        private final BooleanProperty hasValutazione;

        /** Proprietà booleana che indica se il libro ha almeno una valutazione. */
        private final boolean boolVal;

        /** Proprietà booleana che indica se il libro ha almeno un consiglio. */
        private final BooleanProperty hasConsiglio;

        /** Valore booleano statico che indica la presenza di consigli (non osservabile). */
        private final boolean boolCons;

        /** Data dell'ultima valutazione, come proprietà osservabile. */
        private final ObjectProperty<LocalDate> lastValDate;

        /** Data dell'ultimo consiglio, come proprietà osservabile. */
        private final ObjectProperty<LocalDate> lastConsDate;


        /**
         * Costruttore che inizializza tutte le proprietà della riga libro.
         *
         * @param l      Il libro da rappresentare.
         * @param val    True se il libro ha almeno una valutazione.
         * @param cons   True se il libro ha almeno un consiglio.
         * @param valD   Data dell’ultima valutazione del libro.
         * @param consD  Data dell’ultimo consiglio per il libro.
         */
        public LibroRow(Libro l, boolean val, boolean cons, LocalDate valD, LocalDate consD) {
            this.libro = l;
            this.titolo = new SimpleStringProperty(l.   getTitolo());
            this.hasValutazione = new SimpleBooleanProperty(val);
            this.boolVal = val;
            this.hasConsiglio = new SimpleBooleanProperty(cons);
            this.boolCons = cons;
            this.lastValDate = new SimpleObjectProperty<>(valD);
            this.lastConsDate = new SimpleObjectProperty<>(consD);
        }

        /**
         * Restituisce il libro originale associato a questa riga.
         *
         * @return Il libro rappresentato.
         */
        public Libro getLibro() { return libro; }

        /**
         * Restituisce la proprietà osservabile del titolo del libro,
         * utile per collegarla a una colonna della TreeTableView.
         *
         * @return Titolo del libro come {@code StringProperty}.
         */
        public StringProperty titoloProperty() { return titolo; }

        /**
         * Restituisce la proprietà osservabile che indica
         * se il libro ha almeno una valutazione.
         *
         * @return {@code BooleanProperty} true se il libro ha valutazioni.
         */
        public BooleanProperty hasValutazioneProperty() { return hasValutazione; }

        /**
         * Restituisce il valore statico (non osservabile) che indica
         * se il libro ha almeno una valutazione.
         *
         * @return true se è presente almeno una valutazione.
         */
        public boolean hasValutazione() { return boolVal; }

        /**
         * Restituisce la proprietà osservabile che indica
         * se il libro ha almeno un consiglio.
         *
         * @return {@code BooleanProperty} true se il libro ha consigli.
         */
        public BooleanProperty hasConsiglioProperty() { return hasConsiglio; }

        /**
         * Restituisce il valore statico (non osservabile) che indica
         * se il libro ha almeno un consiglio.
         *
         * @return true se è presente almeno un consiglio.
         */
        public boolean hasConsiglio() { return boolCons; }

        /**
         * Restituisce la proprietà osservabile della data dell'ultima valutazione del libro.
         *
         * @return {@code ObjectProperty<LocalDate>} con la data dell’ultima valutazione.
         */
        public ObjectProperty<LocalDate> lastValDateProperty() { return lastValDate; }

        /**
         * Restituisce la proprietà osservabile della data dell'ultimo consiglio per il libro.
         *
         * @return {@code ObjectProperty<LocalDate>} con la data dell’ultimo consiglio.
         */
        public ObjectProperty<LocalDate> lastConsDateProperty() { return lastConsDate; }
    }
}
