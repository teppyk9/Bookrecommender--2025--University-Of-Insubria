package bookrecommender.client;

import bookrecommender.common.Libro;
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
import java.util.ArrayList;
import java.util.List;

public class CreaLibreriaController extends AbstractSearchController {
    @FXML private TextField campoRicerca;
    @FXML private TextField campoRicercaAnno;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;
    @FXML private ListView<Libro> listaLibri;

    @FXML private Button BottoneSalvaLibreria;
    @FXML private Button ExitButton;
    @FXML private ListView<Libro> ListaLibrerie;
    @FXML private TextField NomeLibreria;

    private String searchType = "";
    private List<Libro> libriLibreria;

    @FXML
    public void initialize() {
        libriLibreria = new ArrayList<>();
        initCommon();
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
            Libro sel = ListaLibrerie.getSelectionModel().getSelectedItem();
            if (sel != null)
                listaLibri.setItems(FXCollections.observableArrayList(libriLibreria));
        }
    }

    @FXML
    private void AggiungiLibro() {
        Libro sel = listaLibri.getSelectionModel().getSelectedItem();
        if (sel != null && !libriLibreria.contains(sel)) {
            libriLibreria.add(sel);
            ListaLibrerie.setItems(FXCollections.observableArrayList(libriLibreria));
        }
    }

    @FXML
    private void RimuoviLibro() {
        Libro sel = ListaLibrerie.getSelectionModel().getSelectedItem();
        if (sel != null) {
            libriLibreria.remove(sel);
            ListaLibrerie.setItems(FXCollections.observableArrayList(libriLibreria));
        }
    }

    @FXML
    private void SalvaLibreria() {
        String nome = NomeLibreria.getText();
        if (libriLibreria.size() < 3 || nome == null || nome.trim().isEmpty() || nome.length() < 5 || nome.length() > 50) {
            CliUtil.getInstance().createAlert("Errore", "La libreria deve contenere almeno 3 libri e il nome deve essere compreso tra 5 e 50 caratteri.").showAndWait();
            return;
        }
        try {
            if (CliUtil.getInstance().getLibService().createLib(CliUtil.getInstance().getCurrentToken(), nome.trim(), libriLibreria)) {
                CliUtil.getInstance().createConfirmation("Successo", "Libreria salvata con successo!", false).showAndWait();
                ((Stage)BottoneSalvaLibreria.getScene().getWindow()).close();
            } else {
                CliUtil.getInstance().createAlert("Errore", "Impossibile salvare la libreria").showAndWait();
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore", "Impossibile salvare la libreria: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void ExitApplication() {
        ((Stage)ExitButton.getScene().getWindow()).close();
    }
}
