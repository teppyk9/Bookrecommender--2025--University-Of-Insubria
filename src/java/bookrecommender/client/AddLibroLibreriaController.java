package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.*;

public class AddLibroLibreriaController{
    @FXML private TreeTableView<Object> treeTableView;
    @FXML private TreeTableColumn<Object,String> nameColumn;
    @FXML private TreeTableColumn<Object,Integer> countColumn;
    @FXML private TreeTableColumn<Object,Boolean> presentColumn;
    @FXML private TreeTableColumn<Object,LocalDate> dateColumn;
    @FXML private Button addButton;

    private Libro libro;

    private final Map<String,Integer> libCounts = new HashMap<>();
    private final Map<String,Boolean> libPresent = new HashMap<>();
    private final Map<String,LocalDate> libDates = new HashMap<>();

    private TreeItem<Object> rootItem;

    public void initialize() {
        rootItem = new TreeItem<>();
        treeTableView.setRoot(rootItem);
        treeTableView.setShowRoot(false);

        nameColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String)   return new ReadOnlyStringWrapper((String)v);
            if (v instanceof Libro)    return new ReadOnlyStringWrapper(((Libro)v).getTitolo());
            return new ReadOnlyStringWrapper("");
        });

        countColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String) {
                Integer cnt = libCounts.get(v);
                return new ReadOnlyObjectWrapper<>(cnt);
            }
            return new ReadOnlyObjectWrapper<>(null);
        });

        presentColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String nome) {
                return new ReadOnlyObjectWrapper<>(libPresent.get(nome));
            }
            return new ReadOnlyObjectWrapper<>(null);
        });

        presentColumn.setCellFactory(col -> new TreeTableCell<>() {
            private final ImageView ivTrue  = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/check-green.png")),12,12,true,true));
            private final ImageView ivFalse = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_icon.png")),12,12,true,true));
            {
                ivTrue.setPreserveRatio(true);
                ivFalse.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(Boolean present, boolean empty) {
                super.updateItem(present, empty);
                if (empty || present == null) {
                    setGraphic(null);
                } else {
                    setGraphic(present ? ivTrue : ivFalse);
                }
            }
        });

        dateColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String nome) {
                return new ReadOnlyObjectWrapper<>(libDates.get(nome));
            }
            return new ReadOnlyObjectWrapper<>(null);
        });

        treeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null && newSel.getValue() instanceof String nomeLib) {
                addButton.setDisable(libPresent.getOrDefault(nomeLib, true));
            } else {
                addButton.setDisable(true);
            }
        });
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
        caricaLibrerie();
    }

    private void caricaLibrerie() {
        rootItem.getChildren().clear();
        try {
            var token = CliUtil.getInstance().getCurrentToken();
            List<String> libs = CliUtil.getInstance().getLibService().getLibs(token);

            for (String nome : libs) {
                List<Libro> libri = CliUtil.getInstance().getLibService().getLib(token, nome);

                libCounts.put(nome, libri.size());
                libPresent.put(nome, libri.contains(libro));
                libDates.put(nome, CliUtil.getInstance().getLibService().getCreationDate(token, nome));

                TreeItem<Object> libNode = new TreeItem<>(nome);
                libNode.getChildren().add(new TreeItem<>());
                libNode.addEventHandler(TreeItem.branchExpandedEvent(), e -> {
                    TreeItem<Object> item = e.getTreeItem();
                    if (item.getChildren().size() == 1 && item.getChildren().get(0).getValue() == null) {
                        item.getChildren().clear();
                        caricaFigliLibri(item, nome);
                    }
                });

                rootItem.getChildren().add(libNode);
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore durante il caricamento delle librerie", e.getMessage()).showAndWait();
        }
    }

    private void caricaFigliLibri(TreeItem<Object> libNode, String nomeLib) {
        try {
            List<Libro> libri = CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nomeLib);
            for (Libro b : libri) {
                libNode.getChildren().add(new TreeItem<>(b));
            }
            libNode.setExpanded(true);
            treeTableView.refresh();
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore durante il caricamento dei libri", e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleAddBook() {
        TreeItem<Object> sel = treeTableView.getSelectionModel().getSelectedItem();
        if (sel == null || !(sel.getValue() instanceof String nomeLib)) {
            return;
        }
        try {
            List<Libro> current = CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nomeLib);

            List<Libro> updated = new ArrayList<>(current);
            updated.add(libro);

            List<Integer> result = CliUtil.getInstance().getLibService().updateLib(CliUtil.getInstance().getCurrentToken(), nomeLib, updated);

            if (!result.isEmpty() && result.get(0) == 1) {
                Stage stage = (Stage) addButton.getScene().getWindow();
                stage.close();
                CliUtil.getInstance().createConfirmation("Aggiornamento riuscito", "Il libro è stato aggiunto correttamente alla libreria '" + nomeLib + "'.",false).showAndWait();
            } else {
                StringBuilder sb = new StringBuilder("Impossibile aggiornare la libreria:\n");
                for (int i = 1; i + 1 < result.size(); i += 2) {
                    int idLibroErr = result.get(i);
                    int codice   = result.get(i + 1);
                    sb.append("• Libro id ").append(idLibroErr).append(" errore codice ").append(codice).append("\n");
                }
                CliUtil.getInstance().createAlert("Errore Aggiornamento", sb.toString()).showAndWait();
            }

        } catch (RemoteException e){
            CliUtil.getInstance().createAlert("Errore di connessione", "Impossibile connettersi al server.\n" + e.getLocalizedMessage()).showAndWait();
        }
    }
}
