package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestioneLibrerieController {
    @FXML private TreeTableView<Object> treeTableView;
    @FXML private TreeTableColumn<Object,String> nameColumn;
    @FXML private TreeTableColumn<Object,Integer> countColumn;
    @FXML private TreeTableColumn<Object, LocalDate> dateColumn;
    @FXML private TextField NomeLibreria;
    @FXML private Button BottoneCambiaNome;
    @FXML private Button BottoneSalvaLibreria;
    @FXML private Button ExitButton;

    private TreeItem<Object> rootItem;
    private final Map<String,Integer> libCounts = new HashMap<>();
    private final Map<String,LocalDate> libDates = new HashMap<>();

    @FXML
    public void initialize() {
        rootItem = new TreeItem<>();
        treeTableView.setRoot(rootItem);
        treeTableView.setShowRoot(false);

        nameColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String) {
                return new ReadOnlyStringWrapper((String)v);
            }
            if (v instanceof Libro) {
                return new ReadOnlyStringWrapper(((Libro)v).getTitolo());
            }
            return new ReadOnlyStringWrapper("");
        });

        countColumn.setStyle("-fx-alignment: CENTER;");
        countColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String nome) {
                Integer cnt = libCounts.get(nome);
                return new ReadOnlyObjectWrapper<>(cnt);
            }
            return new ReadOnlyObjectWrapper<>(null);
        });

        dateColumn.setStyle("-fx-alignment: CENTER;");
        dateColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String nome) {
                return new ReadOnlyObjectWrapper<>(libDates.get(nome));
            }
            return new ReadOnlyObjectWrapper<>(null);
        });

        treeTableView.setRowFactory(tv -> {
            TreeTableRow<Object> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Object v = row.getItem();
                    if (v instanceof String nomeLib) {
                        CliUtil.getInstance().buildStage(FXMLtype.VISUALIZZALIBRERIA, nomeLib);
                    }
                }
            });
            return row;
        });
        caricaLibrerie();
    }

    private void caricaLibrerie() {
        rootItem.getChildren().clear();
        try {
            List<String> libs = CliUtil.getInstance().getLibService().getLibs(CliUtil.getInstance().getCurrentToken());
            for (String nome : libs) {
                List<Libro> libri = CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nome);
                libCounts.put(nome, libri.size());
                libDates.put(nome, CliUtil.getInstance().getLibService().getCreationDate(CliUtil.getInstance().getCurrentToken(), nome));

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
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore caricamento librerie", e.getMessage()).showAndWait();
        }
    }

    private void caricaFigliLibri(TreeItem<Object> libNode, String nomeLib) {
        try {
            List<Libro> libri = CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nomeLib);
            for (Libro l : libri) {
                TreeItem<Object> libroNode = new TreeItem<>(l);
                libNode.getChildren().add(libroNode);
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore caricamento libri", e.getMessage()).showAndWait();
        }
    }

    @FXML private void cambiaNome(ActionEvent actionEvent) {
    }

    @FXML private void SalvaLibreria(ActionEvent actionEvent) {
    }

    @FXML private void ExitApplication() {
        CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA,null);
    }
}
