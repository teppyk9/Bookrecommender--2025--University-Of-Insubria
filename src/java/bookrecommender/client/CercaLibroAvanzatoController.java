package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

public class CercaLibroAvanzatoController extends SearchEngine{

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
    @FXML private TreeTableColumn<Libro, Void> aggiungiCol;

    @FXML private ListView<String> ListaLibrerie;
    @FXML private Button BottoneCreaLibreria;

    private String searchType = "";
    private boolean privateSearch = false;

    public void initialize() {
        initBasicSearch();
        initSRecensioniCol();
        initSAggiungiAdvCol();
        initTreeTableViews();
        aggiornaListaLibrerie();
        Platform.runLater(() -> {
            Stage stage = (Stage) BottoneCreaLibreria.getScene().getWindow();
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
        return aggiungiCol;
    }

    @Override
    protected TreeTableColumn<Libro, Void> getSAddRemCol() {
        return null;
    }

    @Override
    protected List<Libro> searchByTitle(String testo){
        try {
            return privateSearch
                    ? CliUtil.getInstance().getSearchService().searchByName(CliUtil.getInstance().getCurrentToken(), testo)
                    : CliUtil.getInstance().getSearchService().searchByName(testo);
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    @Override
    protected List<Libro> searchByAuthor(String testo){
        try {
            return privateSearch
                    ? CliUtil.getInstance().getSearchService().searchByAuthor(CliUtil.getInstance().getCurrentToken(), testo)
                    : CliUtil.getInstance().getSearchService().searchByAuthor(testo);
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    @Override
    protected List<Libro> searchByAuthorAndYear(String testo, int anno){
        try {
            return privateSearch
                    ? CliUtil.getInstance().getSearchService().searchByAuthorAndYear(CliUtil.getInstance().getCurrentToken(), testo, anno)
                    : CliUtil.getInstance().getSearchService().searchByAuthorAndYear(testo, anno);
        } catch (RemoteException e) {
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
    private void aggiornaListaLibrerie() {
        try {
            List<String> libs = CliUtil.getInstance().getLibService().getLibs(CliUtil.getInstance().getCurrentToken());
            if (libs != null && !libs.isEmpty()) {
                ListaLibrerie.setItems(FXCollections.observableArrayList(libs));
            } else {
                CliUtil.getInstance().createAlert("Errore", "Nessuna libreria trovata.").showAndWait();
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore durante il caricamento delle librerie", e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void CreaLibreria() {
        CliUtil.getInstance().buildStage(FXMLtype.CREALIBRERIA, null);
    }

    @FXML
    private void handleListaDoppioClick_2(MouseEvent e) {
        if (e.getClickCount() == 2)
            ApriLibreria();
    }

    @FXML
    private void ApriLibreria() {
        String nome = ListaLibrerie.getSelectionModel().getSelectedItem();
        if (nome != null)
            CliUtil.getInstance().buildStage(FXMLtype.VISUALIZZALIBRERIA, nome);
    }

    @FXML
    private void GoToMainMenu(){
        CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null);
    }

    public void setRicerca() {
        privateSearch = !privateSearch;
    }
}
