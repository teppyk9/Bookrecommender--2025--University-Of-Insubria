package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.util.List;

public class CercaLibroAvanzatoController extends TableViewEngine {

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
    @FXML private TableColumn<Libro, Void> aggiungiCol;

    @FXML private ListView<String> ListaLibrerie;
    @FXML private Button BottoneCreaLibreria;

    private boolean searchType = false;

    public void initialize() {
        initBasicSearch();
        initSRecensioniCol();
        initSAggiungiAdvCol();
        initTableViews();
        initAutoRefresh();
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

    @Override protected TableView<Libro> getSTableView() {
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
        return recensioniCol;
    }

    @Override
    protected TableColumn<Libro, Void> getSAggiungiAdvCol() {
        return aggiungiCol;
    }

    @Override
    protected TableColumn<Libro, Void> getSAddRemCol() {
        return null;
    }

    @Override
    protected TableView<Libro> getOTableView() {
        return null;
    }

    @Override
    protected TableColumn<Libro, String> getOTitoloCol() {
        return null;
    }

    @Override
    protected TableColumn<Libro, String> getOAutoreCol() {
        return null;
    }

    @Override
    protected TableColumn<Libro, Integer> getOAnnoCol() {
        return null;
    }

    @Override
    protected TableColumn<Libro, Void> getOActionCol() {return null;}

    @Override
    protected boolean getSearchType() {return searchType;}

    private void initAutoRefresh() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(10), evt -> aggiornaListaLibrerie()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

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
        searchType = !searchType;
    }
}
