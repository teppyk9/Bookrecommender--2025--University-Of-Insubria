package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class CreaConsiglioController extends AbstractSearchController {
    @FXML private TextField campoRicerca;
    @FXML private TextField campoRicercaAnno;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;
    @FXML private ListView<Libro> listaLibri;

    @FXML private Button GoBackButton_MainMenu;
    @FXML private ListView<Libro> ListaConsigli;

    private String searchType = "";
    private Libro myLibro;

    @FXML
    public void initialize() {
        initCommon();
    }

    public void setLibro(Libro libro) {
        this.myLibro = libro;
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
            return CliUtil.getInstance().getSearchService().searchByName(CliUtil.getInstance().getCurrentToken(), testo);
        }catch(RemoteException e){
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    @Override
    protected List<Libro> searchByAuthor(String testo){
        try{
            return CliUtil.getInstance().getSearchService().searchByAuthor(CliUtil.getInstance().getCurrentToken(), testo);
        }catch(RemoteException e){
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    @Override
    protected List<Libro> searchByAuthorAndYear(String testo, int anno){
        try{
            return CliUtil.getInstance().getSearchService().searchByAuthorAndYear(CliUtil.getInstance().getCurrentToken(), testo, anno);
        }catch(RemoteException e){
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    @FXML
    private void getAllBooks() {
        try {
            List<Libro> libri = CliUtil.getInstance().getSearchService().getAllBooks(CliUtil.getInstance().getCurrentToken());
            libri.remove(myLibro);
            listaLibri.setItems(FXCollections.observableArrayList(libri));
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore", "Impossibile caricare i libri").showAndWait();
        }
    }

    @FXML
    private void aggiungiLibro() {
        Libro sel = listaLibri.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (sel.equals(myLibro)) {
            CliUtil.getInstance().createAlert("Errore", "Non puoi consigliare il libro corrente").showAndWait();
            return;
        }
        if (!ListaConsigli.getItems().contains(sel)) {
            if (ListaConsigli.getItems().size() >= 3) {
                CliUtil.getInstance().createAlert("Errore", "Non puoi consigliare più di 3 libri").showAndWait();
                return;
            }
            ListaConsigli.getItems().add(sel);
        }
    }

    @FXML
    private void rimuoviLibro() {
        Libro sel = ListaConsigli.getSelectionModel().getSelectedItem();
        if (sel != null)
            ListaConsigli.getItems().remove(sel);
    }

    @FXML
    private void salvaConsiglio() {
        List<Libro> cons = new ArrayList<>(ListaConsigli.getItems());
        cons.add(0, myLibro);
        if (cons.size() > 4) {
            CliUtil.getInstance().createAlert("Errore", "Non puoi consigliare più di 3 libri").showAndWait();
            return;
        }
        try {
            if (CliUtil.getInstance().getLibService().addConsiglio(CliUtil.getInstance().getCurrentToken(), cons)) {
                CliUtil.getInstance().createConfirmation("Successo", "Consiglio salvato!", true).showAndWait();
            } else {
                CliUtil.getInstance().createAlert("Errore", "Salvataggio fallito").showAndWait();
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore", e.getMessage()).showAndWait();
        }
    }
    @FXML
    private void GoToMainMenu() {
        ((Stage) GoBackButton_MainMenu.getScene().getWindow()).close();
    }

    @FXML
    private void handleListaDoppioClick_1(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Libro selezionato = listaLibri.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                mostraDettagli(selezionato);
            }
        }
    }

    @FXML
    private void handleListaDoppioClick_2(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Libro selezionato = ListaConsigli.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                mostraDettagli(selezionato);
            }
        }
    }
}
