package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CreaLibreriaController extends TableViewEngine {
    @FXML private TableView <Libro>tableView;
    @FXML private TableColumn <Libro, String> titoloCol;
    @FXML private TableColumn <Libro, String>autoreCol;
    @FXML private TableColumn <Libro, Integer> annoCol;
    @FXML private TableColumn <Libro, Void> azioniCol;
    @FXML private TableView <Libro> risTableView;
    @FXML private TableColumn <Libro, String> risTitoloCol;
    @FXML private TableColumn <Libro, String> risAutoreCol;
    @FXML private TableColumn <Libro, Integer> risAnnoCol;
    @FXML private TableColumn <Libro, Void> risAzioniCol;
    @FXML private TextField campoRicerca;
    @FXML private TextField campoRicercaAnno;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;

    @FXML private Button BottoneSalvaLibreria;
    @FXML private Button ExitButton;
    @FXML private TextField NomeLibreria;

    @FXML
    public void initialize() {
        initBasicSearch();
        initSAddRemCol();
        initOActionCol(false);
        initOTableView();
        initTableViews();
    }

    @Override protected TextField getCampoRicerca(){
        return campoRicerca;
    }

    @Override protected TextField getCampoRicercaAnno(){
        return campoRicercaAnno;
    }

    @Override protected TableView<Libro> getSTableView() {return tableView;}

    @Override protected TableColumn<Libro, String> getSTitoloCol() {return titoloCol;}

    @Override protected TableColumn<Libro, String> getSAutoreCol() {return autoreCol;}

    @Override protected TableColumn<Libro, Integer> getSAnnoCol() {return annoCol;}

    @Override protected TableColumn<Libro, Void> getSRecensioniCol() {return null;}

    @Override protected TableColumn<Libro, Void> getSAggiungiAdvCol() {return null;}

    @Override protected TableColumn<Libro, Void> getSAddRemCol() {return azioniCol;}

    @Override protected MenuButton getMenuTipoRicerca(){
        return MenuTipoRicerca;
    }

    @Override protected MenuItem getMenuCercaTitolo() {return MenuCercaTitolo;}

    @Override protected MenuItem getMenuCercaAutore() {return MenuCercaAutore;}

    @Override protected MenuItem getMenuCercaAutoreAnno() {return MenuCercaAutoreAnno;}

    @Override protected TableView<Libro> getOTableView() {return risTableView;}

    @Override protected TableColumn<Libro, String> getOTitoloCol() {return risTitoloCol;}

    @Override protected TableColumn<Libro, String> getOAutoreCol() {return risAutoreCol;}

    @Override protected TableColumn<Libro, Integer> getOAnnoCol() {return risAnnoCol;}

    @Override protected TableColumn<Libro, Void> getOActionCol() {return risAzioniCol;}

    @Override
    protected boolean getSearchType() {return false;}

    @FXML
    private void SalvaLibreria() {
        String nome = NomeLibreria.getText();
        if (risTableView.getItems().size() < 3 || nome == null || nome.trim().isEmpty() || nome.length() < 5 || nome.length() > 50) {
            CliUtil.getInstance().createAlert("Errore", "La libreria deve contenere almeno 3 libri e il nome deve essere compreso tra 5 e 50 caratteri.").showAndWait();
            return;
        }
        List<Libro> libri = new ArrayList<>(risTableView.getItems());
        try {
            if (CliUtil.getInstance().getLibService().createLib(CliUtil.getInstance().getCurrentToken(), nome.trim(), libri)) {
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
