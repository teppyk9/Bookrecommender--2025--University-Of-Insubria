package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class CreaLibreriaController extends SearchEngine {
    @FXML private TreeTableView <Libro>tableView;
    @FXML private TreeTableColumn <Libro, String> titoloCol;
    @FXML private TreeTableColumn <Libro, String>autoreCol;
    @FXML private TreeTableColumn <Libro, Integer> annoCol;
    @FXML private TreeTableColumn <Libro, Void> azioniCol;
    @FXML private TreeTableView <Libro> risTableView;
    @FXML private TreeTableColumn <Libro, String> risTitoloCol;
    @FXML private TreeTableColumn <Libro, String> risAutoreCol;
    @FXML private TreeTableColumn <Libro, Integer> risAnnoCol;
    @FXML private TreeTableColumn <Libro, Void> risAzioniCol;
    @FXML private TextField campoRicerca;
    @FXML private TextField campoRicercaAnno;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;

    @FXML private Button BottoneSalvaLibreria;
    @FXML private Button ExitButton;
    @FXML private TextField NomeLibreria;

    private String searchType = "";
    private List<Libro> libriLibreria;

    @FXML
    public void initialize() {
        libriLibreria = new ArrayList<>();
        initBasicSearch();
        initSAddRemCol();
        initOActionCol(false);
        initOTableView();
        initTreeTableViews();
    }

    @Override protected TextField getCampoRicerca(){
        return campoRicerca;
    }

    @Override protected TextField getCampoRicercaAnno(){
        return campoRicercaAnno;
    }

    @Override protected String getSearchType(){
        return searchType;
    }

    @Override protected void setSearchType(String type){this.searchType = type;}

    @Override protected TreeTableView<Libro> getSTreeTableView() {return tableView;}

    @Override protected TreeTableColumn<Libro, String> getSTitoloCol() {return titoloCol;}

    @Override protected TreeTableColumn<Libro, String> getSAutoreCol() {return autoreCol;}

    @Override protected TreeTableColumn<Libro, Integer> getSAnnoCol() {return annoCol;}

    @Override protected TreeTableColumn<Libro, Void> getSRecensioniCol() {return null;}

    @Override protected TreeTableColumn<Libro, Void> getSAggiungiAdvCol() {return null;}

    @Override protected TreeTableColumn<Libro, Void> getSAddRemCol() {return azioniCol;}

    @Override protected MenuButton getMenuTipoRicerca(){
        return MenuTipoRicerca;
    }

    @Override protected MenuItem getMenuCercaTitolo() {return MenuCercaTitolo;}

    @Override protected MenuItem getMenuCercaAutore() {return MenuCercaAutore;}

    @Override protected MenuItem getMenuCercaAutoreAnno() {return MenuCercaAutoreAnno;}

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

    @Override protected TreeTableView<Libro> getOTreeTableView() {return risTableView;}

    @Override protected TreeTableColumn<Libro, String> getOTitoloCol() {return risTitoloCol;}

    @Override protected TreeTableColumn<Libro, String> getOAutoreCol() {return risAutoreCol;}

    @Override protected TreeTableColumn<Libro, Integer> getOAnnoCol() {return risAnnoCol;}

    @Override protected TreeTableColumn<Libro, Void> getOActionCol() {return risAzioniCol;}

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
