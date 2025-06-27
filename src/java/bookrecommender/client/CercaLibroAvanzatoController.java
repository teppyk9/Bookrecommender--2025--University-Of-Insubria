package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

public class CercaLibroAvanzatoController extends AbstractSearchController{

    @FXML private TextField campoRicerca;
    @FXML private TextField campoRicercaAnno;
    @FXML private ListView<Libro> listaLibri;
    @FXML private ListView<String> ListaLibrerie;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;
    @FXML private Button BottoneCreaLibreria;

    private String searchType = "";

    @FXML
    public void initialize() {
        initCommon();
        aggiornaListaLibrerie();
        Platform.runLater(() -> {
            Stage stage = (Stage) BottoneCreaLibreria.getScene().getWindow();
            stage.setOnCloseRequest(evt -> {
                try {
                    CliUtil.getInstance().getLogRegService().LogOut(CliUtil.getInstance().getCurrentToken());
                } catch(RemoteException ignored) {}
                Platform.exit(); System.exit(0);
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
    private void aggiornaListaLibrerie() {
        try {
            List<String> libs = CliUtil.getInstance().getLibService().getLibs(CliUtil.getInstance().getCurrentToken());
            if (libs == null || libs.isEmpty()) {
                CliUtil.getInstance().createAlert("Errore", "Nessuna libreria trovata.").showAndWait();
            } else {
                ListaLibrerie.setItems(FXCollections.observableArrayList(libs));
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore durante il caricamento delle librerie", e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleListaDoppioClick_1(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Libro sel = listaLibri.getSelectionModel().getSelectedItem();
            if (sel != null) mostraDettagli(sel);
        }
    }

    @FXML
    private void handleListaDoppioClick_2(MouseEvent e) {
        if (e.getClickCount() == 2) ApriLibreria();
    }

    @FXML
    private void GoToMainMenu(){
        CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null);
    }

    @FXML
    private void ApriLibreria() {
        String nome = ListaLibrerie.getSelectionModel().getSelectedItem();
        if (nome != null) CliUtil.getInstance().buildStage(FXMLtype.VISUALIZZALIBRERIA, nome);
    }

    @FXML
    private void CreaLibreria(){
        CliUtil.getInstance().buildStage(FXMLtype.CREALIBRERIA, null);
    }
}
