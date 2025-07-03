package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class CreaConsiglioController extends TableViewEngine {
    @FXML private TextField campoRicerca;
    @FXML private TextField campoRicercaAnno;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;
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
    @FXML private Button GoBackButton_MainMenu;

    private Libro myLibro;
    private FXMLtype oldFXMLType;

    @FXML
    public void initialize() {
       initForConsigli();
        Platform.runLater(() -> {
            Stage stage = (Stage) GoBackButton_MainMenu.getScene().getWindow();
            stage.setOnCloseRequest(evt -> {
                GoToMainMenu();
                evt.consume();
            });
        });
    }

    public void setLibro(Libro libro, FXMLtype oldFXMLType) {
        this.myLibro = libro;
        this.oldFXMLType = oldFXMLType;
    }

    @Override protected TextField getCampoRicerca(){
        return campoRicerca;
    }

    @Override protected TextField getCampoRicercaAnno(){
        return campoRicercaAnno;
    }

    @Override protected MenuButton getMenuTipoRicerca(){
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
        return null;
    }

    @Override
    protected TableColumn<Libro, Void> getSAggiungiAdvCol() {
        return null;
    }

    @Override
    protected TableColumn<Libro, Void> getSAddRemCol() {
        return azioniCol;
    }

    @Override
    protected TableView<Libro> getOTableView() {
        return risTableView;
    }

    @Override
    protected TableColumn<Libro, String> getOTitoloCol() {
        return risTitoloCol;
    }

    @Override
    protected TableColumn<Libro, String> getOAutoreCol() {
        return risAutoreCol;
    }

    @Override
    protected TableColumn<Libro, Integer> getOAnnoCol() {
        return risAnnoCol;
    }

    @Override
    protected TableColumn<Libro, Void> getOActionCol() {
        return risAzioniCol;
    }

    @Override
    protected boolean getSearchType() {return true;}

    @Override
    protected Libro getMyLibro() {
        return myLibro;
    }

    @Override
    protected FXMLtype getMyFXMLtype() {
        return FXMLtype.CREACONSIGLIO;
    }

    @FXML
    private void salvaConsiglio() {
        List<Libro> cons = new ArrayList<>(risTableView.getItems());
        cons.add(0, myLibro);
        if (cons.size() > 4) {
            CliUtil.getInstance().createAlert("Errore", "Non puoi consigliare più di 3 libri").showAndWait();
            return;
        }
        try {
            if (CliUtil.getInstance().getLibService().addConsiglio(CliUtil.getInstance().getCurrentToken(), cons)) {
                CliUtil.getInstance().createConfirmation("Successo", "Consiglio salvato!", false).showAndWait();
                    CliUtil.getInstance().buildStage(oldFXMLType,null, null);
            } else {
                CliUtil.getInstance().createAlert("Errore", "Salvataggio fallito").showAndWait();
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore", e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void GoToMainMenu() {
        List<Libro> cons = new ArrayList<>(risTableView.getItems());
        if(!cons.isEmpty()) {
            if (CliUtil.getInstance().createConfirmation("Attenzione", "Hai dei consigli non salvati, vuoi davvero tornare al menu principale?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
                CliUtil.getInstance().buildStage(oldFXMLType,null, null);
            }
        }else{
            CliUtil.getInstance().buildStage(oldFXMLType,null, null);
        }
    }
}
