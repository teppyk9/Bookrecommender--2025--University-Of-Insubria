package bookrecommender.client;

import bookrecommender.common.Libro;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

public class CercaLibroController extends AbstractSearchController{

    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;
    @FXML private TextField campoRicercaAnno;
    @FXML private TextField campoRicerca;
    @FXML private ListView<Libro> listaLibri;
    @FXML private Button bottoneCerca;

    private String searchType = "";

    @FXML
    public void initialize() {
        initCommon();
        Platform.runLater(() -> {
            Stage stage = (Stage) bottoneCerca.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    @Override protected TextField getCampoRicerca(){
        return campoRicerca;
    }

    @Override protected TextField getCampoRicercaAnno(){
        return campoRicercaAnno;
    }

    @Override protected ListView<Libro> getListaRisultati(){
        return listaLibri;
    }

    @Override protected String getSearchType(){
        return searchType;
    }

    @Override protected void setSearchType(String type){
        this.searchType = type;
    }

    @Override protected MenuButton getMenuTipoRicerca(){
        return MenuTipoRicerca;
    }

    @Override protected MenuItem getItemTitolo(){
        return MenuCercaTitolo;
    }

    @Override protected MenuItem getItemAutore(){
        return MenuCercaAutore;
    }

    @Override protected MenuItem getItemAutoreAnno(){
        return MenuCercaAutoreAnno;
    }

    @Override protected void mostraDettagli(Libro libro){
        CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, libro);
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
    private void handleListaDoppioClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Libro selezionato = listaLibri.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, selezionato);
            }
        }
    }

    @FXML
    private void GoToMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null);
    }
}