package bookrecommender.client;

import bookrecommender.common.Libro;
import bookrecommender.common.Token;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GestioneLibrerieController extends TreeTableEngine {
    @FXML private TreeTableColumn<Object, String> nameColumn;
    @FXML private TreeTableColumn<Object, Integer> countColumn;
    @FXML private TreeTableColumn<Object, LocalDate> dateColumn;
    @FXML private TreeTableColumn<Object, Void> azioniColumn;
    @FXML private TreeTableColumn<Object, Boolean> isValColumn;
    @FXML private TreeTableColumn<Object, Boolean> isConsColumn;
    @FXML private TreeTableColumn<Object, LocalDate> lastValColumn;
    @FXML private TreeTableColumn<Object, LocalDate> lastConsColumn;
    @FXML private TextField NomeLibreria;
    @FXML private Button BottoneCambiaNome;

    private final Map<String, Integer> libCounts = new HashMap<>();
    private final Map<String, LocalDate> libDates = new HashMap<>();

    @FXML
    public void initialize() {
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

    private void setupCheckColumn(TreeTableColumn<Object, Boolean> col, java.util.function.Function<LibroRow, javafx.beans.property.BooleanProperty> prop) {
        col.setStyle("-fx-alignment: CENTER;");
        col.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof LibroRow r) return prop.apply(r);
            return new ReadOnlyObjectWrapper<>(null);
        });
        col.setCellFactory(c -> new TreeTableCell<>() {
            private final ImageView check = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/check-green.png")),16,16,true,true));
            private final ImageView noCheck = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_icon.png")),16,16,true,true));
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setGraphic(null);
                else {
                    setGraphic(item ? check : noCheck);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    @Override
    protected void handleDoubleClick(Object v) {
        if (v instanceof String s)
            CliUtil.getInstance().buildStage(FXMLtype.MODIFICALIBRERIA, null, s);
        else if (v instanceof LibroRow lr)
            CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, null, lr.getLibro());
    }

    @Override
    protected List<bookrecommender.common.Libro> fetchLibraryContents(Token token, String nomeLib) throws RemoteException {
        return CliUtil.getInstance().getLibService().getLib(token, nomeLib);
    }

    @Override
    protected Map<String, Integer> getLibCounts() {
        return libCounts;
    }

    @Override
    protected Map<String, LocalDate> getLibDates() {
        return libDates;
    }

    @Override
    protected void caricaFigliLibri(TreeItem<Object> libNode, String nomeLib) {
        try {
            Token token = CliUtil.getInstance().getCurrentToken();
            List<bookrecommender.common.Libro> list = CliUtil.getInstance().getLibService().getLib(token, nomeLib);
            for (bookrecommender.common.Libro l : list) {
                boolean hasVal = CliUtil.getInstance().getLibService().existVal(token, l);
                boolean hasCons = CliUtil.getInstance().getLibService().existCon(token, l);
                LocalDate valD = CliUtil.getInstance().getLibService().getValDate(token, l);
                LocalDate consD = CliUtil.getInstance().getLibService().getConDate(token, l);
                LibroRow row = new LibroRow(l, hasVal, hasCons, valD, consD);
                libNode.getChildren().add(new TreeItem<>(row));
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore caricamento libri", e.getMessage()).showAndWait();
        }
    }

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
                    CliUtil.getInstance().createAlert("Errore", "Connessione").showAndWait();
                }
            }
        } else {
            CliUtil.getInstance().createAlert("Errore", "Seleziona una libreria").showAndWait();
        }
    }

    @FXML
    private void ExitApplication() {
        CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null, null);
    }

    @FXML
    void creaLibreria() {
        CliUtil.getInstance().buildStage(FXMLtype.CREALIBRERIA, null, null);
    }

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
                        CliUtil.getInstance().createAlert("Failed to delete", e.getMessage()).showAndWait();
                    }
                }
            } else {
                CliUtil.getInstance().createAlert("Errore", "Elimina button on a " + o.getClass()).showAndWait();
            }
        });
        return mb;
    }

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
                    List<bookrecommender.common.Libro> modList = libri.stream().map(LibroRow::getLibro).collect(Collectors.toList());
                    List<Integer> results = CliUtil.getInstance().getLibService().updateLib(CliUtil.getInstance().getCurrentToken(), nomeLib, modList);
                    if (!results.isEmpty() && results.get(0) == 1) {
                        CliUtil.getInstance().createConfirmation("Aggiornato", "Rimosso correttamente", false).showAndWait();
                        loadLibraries();
                    } else {
                        CliUtil.getInstance().reviewLibUpdate(results);
                    }
                } catch (RemoteException e) {
                    CliUtil.getInstance().createAlert("Errore", e.getMessage()).showAndWait();
                }
            }
        });

        modVal.setOnAction(evt -> {
            Object o = cell.getTableRow().getItem();
            if (o instanceof LibroRow lr) {
                try {
                    CliUtil.getInstance().buildStage(FXMLtype.MODIFICAVALUTAZIONE, FXMLtype.GESTIONELIBRERIE, CliUtil.getInstance().getLibService().getValutazione(CliUtil.getInstance().getCurrentToken(), lr.getLibro()));
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
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

    private void setMenuButtonStyle(MenuButton mb) {
        mb.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png")), 16, 16, true, true)));
        mb.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-border-color: transparent;"
                        + "-fx-padding: 0;"
                        + "-fx-cursor: hand;"
                        + "-fx-focus-color: transparent;"
                        + "-fx-faint-focus-color: transparent;");
        ScaleTransition enlarge = new ScaleTransition(Duration.millis(100), mb);
        enlarge.setToX(1.1);
        enlarge.setToY(1.1);
        ScaleTransition shrink = new ScaleTransition(Duration.millis(100), mb);
        shrink.setToX(1.0);
        shrink.setToY(1.0);
        mb.setOnMouseEntered(e -> { shrink.stop(); enlarge.playFromStart(); });
        mb.setOnMouseExited (e -> { enlarge.stop(); shrink.playFromStart(); });
    }

    public static class LibroRow {
        private final Libro libro;
        private final StringProperty titolo;
        private final BooleanProperty hasValutazione;
        private final boolean boolVal;
        private final BooleanProperty hasConsiglio;
        private final boolean boolCons;
        private final ObjectProperty<LocalDate> lastValDate;
        private final ObjectProperty<LocalDate> lastConsDate;

        public LibroRow(Libro l, boolean val, boolean cons, LocalDate valD, LocalDate consD) {
            this.libro = l;
            this.titolo = new SimpleStringProperty(l.getTitolo());
            this.hasValutazione = new SimpleBooleanProperty(val);
            this.boolVal = val;
            this.hasConsiglio = new SimpleBooleanProperty(cons);
            this.boolCons = cons;
            this.lastValDate = new SimpleObjectProperty<>(valD);
            this.lastConsDate = new SimpleObjectProperty<>(consD);
        }

        public Libro getLibro() { return libro; }
        public StringProperty titoloProperty() { return titolo; }
        public BooleanProperty hasValutazioneProperty() { return hasValutazione; }
        public boolean hasValutazione() { return boolVal; }
        public BooleanProperty hasConsiglioProperty() { return hasConsiglio; }
        public boolean hasConsiglio() { return boolCons; }
        public ObjectProperty<LocalDate> lastValDateProperty() { return lastValDate; }
        public ObjectProperty<LocalDate> lastConsDateProperty() { return lastConsDate; }
    }
}
