package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.Objects;

public class CercaLibroAvanzatoController extends TableViewEngine {

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

    public void initialize() {
        initBasicSearch();
        initSRecensioniCol();
        initSAggiungiAdvCol();
        librerieCol.setStyle("-fx-alignment: CENTER;");
        librerieCol.setCellFactory(col -> new TableCell<>() {
            private final ImageView ivTrue  = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/check-green.png")),12,12,true,true));
            private final ImageView ivFalse = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_icon.png")),12,12,true,true));
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                Libro libro = getTableView().getItems().get(getIndex());
                boolean has = false;
                try {
                    has = CliUtil.getInstance().getLibService().isLibPresent(CliUtil.getInstance().getCurrentToken(), libro);
                } catch (RemoteException e) {
                    CliUtil.getInstance().createAlert("Errore di connessione", "Impossibile verificare la presenza del libro in libreria.").showAndWait();
                }
                setGraphic(has ? ivTrue : ivFalse);
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

    @Override
    protected TextField getCampoRicerca() {
        return campoRicerca;
    }

    @Override
    protected TextField getCampoRicercaAnno() {
        return campoRicercaAnno;
    }

    @Override
    protected MenuButton getMenuTipoRicerca() {
        return MenuTipoRicerca;
    }

    @Override
    protected MenuItem getMenuCercaTitolo() {
        return MenuCercaTitolo;
    }

    @Override
    protected MenuItem getMenuCercaAutore() {
        return MenuCercaAutore;
    }

    @Override
    protected MenuItem getMenuCercaAutoreAnno() {
        return MenuCercaAutoreAnno;
    }

    @Override protected TableView<Libro> getSTableView() {
        return tableView;
    }

    @Override
    protected TableColumn<Libro, String> getSTitoloCol() {
        return titoloCol;
    }

    @Override
    protected TableColumn<Libro, String> getSAutoreCol() {
        return autoreCol;
    }

    @Override
    protected TableColumn<Libro, Integer> getSAnnoCol() {
        return annoCol;
    }

    @Override
    protected TableColumn<Libro, Void> getSRecensioniCol() {
        return recensioniCol;
    }

    @Override
    protected TableColumn<Libro, Void> getSAggiungiAdvCol() {
        return aggiungiCol;
    }

    @Override
    protected TableColumn<Libro, Void> getSAddRemCol() {
        return null;
    }

    @Override
    protected TableView<Libro> getOTableView() {
        return null;
    }

    @Override
    protected TableColumn<Libro, String> getOTitoloCol() {
        return null;
    }

    @Override
    protected TableColumn<Libro, String> getOAutoreCol() {
        return null;
    }

    @Override
    protected TableColumn<Libro, Integer> getOAnnoCol() {
        return null;
    }

    @Override
    protected TableColumn<Libro, Void> getOActionCol() {return null;}

    @Override
    protected boolean getSearchType() {return searchType;}

    @Override
    protected Libro getMyLibro() {return null;}

    @FXML
    private void GoToMainMenu(){
        CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null, null);
    }

    public void setRicerca() {
        searchType = !searchType;
    }
}
