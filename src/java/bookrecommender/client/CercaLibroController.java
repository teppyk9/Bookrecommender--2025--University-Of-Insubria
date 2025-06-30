package bookrecommender.client;

import bookrecommender.common.Libro;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

public class CercaLibroController extends SearchEngine{

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

    private String searchType = "";

    @FXML
    public void initialize() {
        initTableColumns();
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
    protected String getSearchType() {
        return searchType;
    }

    @Override
    protected void setSearchType(String type) {
        searchType = type;
    }

    @Override
    protected TableView<Libro> getTableView() {
        return tableView;
    }

    @Override
    protected TableColumn<Libro, String> getTitoloCol() {
        return titoloCol;
    }

    @Override
    protected TableColumn<Libro, String> getAutoreCol() {
        return autoreCol;
    }

    @Override
    protected TableColumn<Libro, Integer> getAnnoCol() {
        return annoCol;
    }

    @Override
    protected TableColumn<Libro, Void> getRecensioniCol() {
        return recensioniCol;
    }

    @Override
    protected List<Libro> searchByTitle(String testo){
        try{
            return CliUtil.getInstance().getSearchService().searchByName(testo);
        }catch(RemoteException e){
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    @Override
    protected List<Libro> searchByAuthor(String testo){
        try{
            return CliUtil.getInstance().getSearchService().searchByAuthor(testo);
        }catch(RemoteException e){
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    @Override
    protected List<Libro> searchByAuthorAndYear(String testo, int anno){
        try{
            return CliUtil.getInstance().getSearchService().searchByAuthorAndYear(testo, anno);
        }catch(RemoteException e){
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    @FXML
    private void GoToMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null);
    }
}