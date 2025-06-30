package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Objects;

public abstract class SearchEngine {
    protected abstract TextField getCampoRicerca();
    protected abstract TextField getCampoRicercaAnno();
    protected abstract MenuButton getMenuTipoRicerca();
    protected abstract MenuItem getMenuCercaTitolo();
    protected abstract MenuItem getMenuCercaAutore();
    protected abstract MenuItem getMenuCercaAutoreAnno();
    protected abstract String getSearchType();
    protected abstract void setSearchType(String type);

    protected abstract TableView<Libro> getTableView();
    protected abstract TableColumn<Libro, String> getTitoloCol();
    protected abstract TableColumn<Libro, String> getAutoreCol();
    protected abstract TableColumn<Libro, Integer> getAnnoCol();
    protected abstract TableColumn<Libro, Void> getRecensioniCol();

    protected abstract List<Libro> searchByTitle(String testo);
    protected abstract List<Libro> searchByAuthor(String testo);
    protected abstract List<Libro> searchByAuthorAndYear(String testo, int anno);

    protected void initTableColumns() {
        getCampoRicercaAnno().setVisible(false);
        getCampoRicercaAnno().setDisable(true);
        ImageView arrow = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png")), 12, 12, true, true));
        getMenuTipoRicerca().setGraphic(arrow);
        Platform.runLater(() -> {
            Node a = getMenuTipoRicerca().lookup(".arrow");
            if (a != null) {
                a.setVisible(false);
                a.setManaged(false);
            }
        });

        getMenuCercaTitolo().setOnAction(e -> switchType("Titolo", "Titolo"));
        getMenuCercaAutore().setOnAction(e -> switchType("Autore", "Autore"));
        getMenuCercaAutoreAnno().setOnAction(e -> {
            switchType("AutoreAnno", "Autore e Anno");
            getCampoRicercaAnno().setVisible(true);
            getCampoRicercaAnno().setDisable(false);
        });
        getTitoloCol().setCellValueFactory(new PropertyValueFactory<>("titolo"));
        getAutoreCol().setCellValueFactory(new PropertyValueFactory<>("autore"));
        getAnnoCol().setCellValueFactory(new PropertyValueFactory<>("annoPubblicazione"));

        getRecensioniCol().setCellFactory(col -> new TableCell<>() {
            private final ImageView check = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/check-green.png")), 16, 16, true, true));
            private final ImageView noCheck = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_icon.png")), 16, 16, true, true));
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Libro libro = getTableView().getItems().get(getIndex());
                    boolean has = false;
                    try {
                        has = CliUtil.getInstance().getSearchService().hasValRec(libro);
                    } catch (RemoteException e) {
                        CliUtil.getInstance().createAlert("Errore", "Impossibile verificare le recensioni: " + e.getMessage()).showAndWait();
                    }
                    setGraphic(has ? check : noCheck);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        getTableView().setRowFactory(tv -> {
            TableRow<Libro> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && ! row.isEmpty()) {
                    Libro sel = row.getItem();
                    CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, sel);
                }
            });
            return row;
        });
    }

    private void switchType(String key, String text) {
        getMenuTipoRicerca().getItems().setAll(getMenuCercaTitolo(), getMenuCercaAutore(), getMenuCercaAutoreAnno());
        getCampoRicercaAnno().setVisible(false);
        getCampoRicercaAnno().setDisable(true);
        setSearchType(key);
        getMenuTipoRicerca().setText(text);
        switch (key) {
            case "Titolo":
                getMenuTipoRicerca().getItems().remove(getMenuCercaTitolo());
                break;
            case "Autore":
                getMenuTipoRicerca().getItems().remove(getMenuCercaAutore());
                break;
            case "AutoreAnno":
                getMenuTipoRicerca().getItems().remove(getMenuCercaAutoreAnno());
                break;
        }
    }

    @FXML
    private void handleClickCerca() {
        String testo = getCampoRicerca().getText();
        String anno  = getCampoRicercaAnno().getText();
        if (testo == null || testo.length() < 2) {
            getTableView().setItems(FXCollections.emptyObservableList());
            CliUtil.getInstance().createAlert("Errore", "Inserire almeno 2 caratteri per la ricerca.").showAndWait();
            return;
        }
        List<Libro> risultati;
        try {
            switch (getSearchType()) {
                case "Titolo":
                    risultati = searchByTitle(testo);
                    break;
                case "Autore":
                    risultati = searchByAuthor(testo);
                    break;
                case "AutoreAnno":
                    if (!validateYear(anno)) return;
                    risultati = searchByAuthorAndYear(testo, Integer.parseInt(anno));
                    break;
                default:
                    CliUtil.getInstance().createAlert("Errore", "Tipo di ricerca non selezionato.").showAndWait();
                    return;
            }
            if (risultati != null && !risultati.isEmpty()) {
                getTableView().setItems(FXCollections.observableArrayList(risultati));
            } else {
                getTableView().setItems(FXCollections.emptyObservableList());
                CliUtil.getInstance().createAlert("Nessun risultato", "Nessun libro trovato.").showAndWait();
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void keyEnterPressed_1(KeyEvent e) {
        if ("Enter".equals(e.getCode().getName()))
            handleClickCerca();
    }

    @FXML
    private void keyEnterPressed_2(KeyEvent e) {
        if ("Enter".equals(e.getCode().getName()))
            handleClickCerca();
    }


    private boolean validateYear(String anno) {
        if (anno == null || anno.trim().isEmpty() || !anno.matches("\\d{1,4}")) {
            CliUtil.getInstance().createAlert("Errore", "Inserire un anno valido (fino a 4 cifre).").showAndWait();
            return false;
        }
        return true;
    }
}
