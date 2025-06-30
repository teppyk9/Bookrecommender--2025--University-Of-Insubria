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

    @FXML private TreeTableView<Libro> tableView;
    @FXML private TreeTableColumn<Libro, String> titoloCol;
    @FXML private TreeTableColumn<Libro, String> autoreCol;
    @FXML private TreeTableColumn<Libro, Integer> annoCol;
    @FXML private TreeTableColumn<Libro, Void> recensioniCol;

    private String searchType = "";

    @FXML
    public void initialize() {
        initBasicSearch();
        initSRecensioniCol();
        initTreeTableViews();
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
    protected TreeTableView<Libro> getSTreeTableView() {
        return tableView;
    }

    @Override
    protected TreeTableColumn<Libro, String> getSTitoloCol() {
        return titoloCol;
    }

    @Override
    protected TreeTableColumn<Libro, String> getSAutoreCol() {
        return autoreCol;
    }

    @Override
    protected TreeTableColumn<Libro, Integer> getSAnnoCol() {
        return annoCol;
    }

    @Override
    protected TreeTableColumn<Libro, Void> getSRecensioniCol() {
        return recensioniCol;
    }

    @Override
    protected TreeTableColumn<Libro, Void> getSAggiungiAdvCol() {
        return null;
    }

    @Override
    protected TreeTableColumn<Libro, Void> getSAddRemCol() {
        return null;
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

    @Override
    protected TreeTableView<Libro> getOTreeTableView() {
        return null;
    }

    @Override
    protected TreeTableColumn<Libro, String> getOTitoloCol() {
        return null;
    }

    @Override
    protected TreeTableColumn<Libro, String> getOAutoreCol() {
        return null;
    }

    @Override
    protected TreeTableColumn<Libro, Integer> getOAnnoCol() {
        return null;
    }

    @Override
    protected TreeTableColumn<Libro, Void> getOActionCol() {return null;}

    @FXML
    private void GoToMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null);
    }
}