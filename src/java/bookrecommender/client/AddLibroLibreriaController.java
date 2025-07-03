package bookrecommender.client;

import bookrecommender.common.Libro;
import bookrecommender.common.Token;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.*;

public class AddLibroLibreriaController extends TreeTableEngine {
    @FXML private Text titoloLibreria;
    @FXML private TreeTableColumn<Object, String> nameColumn;
    @FXML private TreeTableColumn<Object, Integer> countColumn;
    @FXML private TreeTableColumn<Object, Boolean> presentColumn;
    @FXML private TreeTableColumn<Object, LocalDate> dateColumn;
    @FXML private Button addButton;

    private Libro libro;
    private final Map<String, Integer> libCounts = new HashMap<>();
    private final Map<String, Boolean> libPresent = new HashMap<>();
    private final Map<String, LocalDate> libDates = new HashMap<>();

    @FXML
    public void initialize() {
        titoloLibreria.setText("Le tue librerie");
        initializeTree();

        nameColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String s)
                return new ReadOnlyStringWrapper(s);
            if (v instanceof Libro l)
                return new ReadOnlyStringWrapper(l.getTitolo());
            return new ReadOnlyStringWrapper("");
        });

        loadSimple(countColumn,libCounts);
        loadSimple(dateColumn, libDates);
        loadSimple(presentColumn, libPresent);

        presentColumn.setCellFactory(col -> new TreeTableCell<>() {
            private final ImageView ivTrue  = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/check-green.png")), 12,12,true,true));
            private final ImageView ivFalse = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_icon.png")), 12,12,true,true));
            @Override
            protected void updateItem(Boolean present, boolean empty) {
                super.updateItem(present, empty);
                if (empty || present == null)
                    setGraphic(null);
                else
                    setGraphic(present ? ivTrue : ivFalse);
            }
        });

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

    @Override
    protected void loadLibraries() {
        rootItem.getChildren().clear();
        libCounts.clear();
        libDates.clear();
        libPresent.clear();
        try {
            Token token = CliUtil.getInstance().getCurrentToken();
            List<String> libs = CliUtil.getInstance().getLibService().getLibs(token);
            for (String nome : libs) {
                List<Libro> libri = CliUtil.getInstance().getLibService().getLib(token, nome);
                libCounts.put(nome, libri.size());
                libDates.put(nome, CliUtil.getInstance().getLibService().getCreationDate(token, nome));
                libPresent.put(nome, libri.contains(libro));
                creaFigliLibri(nome);
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore durante il caricamento delle librerie", e.getMessage()).showAndWait();
        }
    }

    @Override
    protected void handleDoubleClick(Object v) {
        if (v instanceof Libro l) {
            CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, null, l);
        }
    }

    @Override
    protected List<Libro> fetchLibraryContents(Token token, String nomeLib) throws RemoteException {
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
            List<Libro> list = CliUtil.getInstance().getLibService().getLib(token, nomeLib);
            libPresent.put(nomeLib, list.contains(libro));
            for (Libro b : list) {
                libNode.getChildren().add(new TreeItem<>(b));
            }
            libNode.setExpanded(true);
            treeTableView.refresh();
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore durante il caricamento dei libri", e.getMessage()).showAndWait();
        }
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
        loadLibraries();
    }

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
                ((Stage)addButton.getScene().getWindow()).close();
                CliUtil.getInstance().createConfirmation("Aggiornamento riuscito", "Il libro è stato aggiunto correttamente alla libreria '" + nomeLib + "'.", false).showAndWait();
            } else {
                CliUtil.getInstance().reviewLibUpdate(risultati);
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore di connessione", "Impossibile connettersi al server.\n" + e.getLocalizedMessage()).showAndWait();
        }
    }

    @FXML
    private void ExitApplication() {
        ((Stage)addButton.getScene().getWindow()).close();
    }
}
