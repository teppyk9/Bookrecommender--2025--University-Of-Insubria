package bookrecommender.client;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public abstract class TreeTableEngine {
    @FXML protected TreeTableView<Object> treeTableView;
    protected TreeItem<Object> rootItem;

    protected void initializeTree() {
        rootItem = new TreeItem<>();
        treeTableView.setRoot(rootItem);
        treeTableView.setShowRoot(false);
        treeTableView.setRowFactory(tv -> {
            TreeTableRow<Object> row = new TreeTableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    handleDoubleClick(row.getItem());
                }
            });
            return row;
        });
    }

    protected <T> void loadSimple(TreeTableColumn<Object, T> column, Map<String, T> map) {
        column.setStyle("-fx-alignment: CENTER;");
        column.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String s) {
                return new ReadOnlyObjectWrapper<>(map.get(s));
            }
            return new ReadOnlyObjectWrapper<>(null);
        });
    }

    protected void loadLibraries() {
        rootItem.getChildren().clear();
        getLibCounts().clear();
        getLibDates().clear();
        try {
            List<String> libs = CliUtil.getInstance().getLibService().getLibs(CliUtil.getInstance().getCurrentToken());
            for (String nome : libs) {
                List<?> list = fetchLibraryContents(nome);
                getLibCounts().put(nome, list.size());
                getLibDates().put(nome, CliUtil.getInstance().getLibService().getCreationDate(CliUtil.getInstance().getCurrentToken(), nome));
                creaFigliLibri(nome);
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore caricamento librerie", e.getMessage()).showAndWait();
        }
    }

    protected void creaFigliLibri(String nome) {
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

    protected abstract void handleDoubleClick(Object value);

    protected abstract List<?> fetchLibraryContents(String nomeLib) throws RemoteException;

    protected abstract Map<String, Integer> getLibCounts();

    protected abstract Map<String, LocalDate> getLibDates();

    protected abstract void caricaFigliLibri(TreeItem<Object> libNode, String nomeLib);

}
