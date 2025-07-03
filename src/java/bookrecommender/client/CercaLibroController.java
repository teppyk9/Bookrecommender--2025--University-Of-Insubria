package bookrecommender.client;

import bookrecommender.common.Libro;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CercaLibroController extends TableViewEngine {

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

    @FXML
    public void initialize() {
        initBasicSearch();
        initSRecensioniCol();
        initTableViews();
        Platform.runLater(() -> {
            Stage stage = (Stage) MenuTipoRicerca.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
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

    @Override
    protected TableView<Libro> getSTableView() {
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
        return null;
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
    protected boolean getSearchType() {return false;}

    @Override
    protected Libro getMyLibro() {return null;}

    @FXML
    private void GoToMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null);
    }
}