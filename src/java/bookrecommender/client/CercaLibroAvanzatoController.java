package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Objects;

public class CercaLibroAvanzatoController extends SearchEngine{

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

    private String searchType = "";

    public void initialize() {
        initTableColumns();
        aggiungiCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button();
            {
                btn.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/plus-circle-green.png")), 16, 16, true, true)));
                btn.setStyle("-fx-background-color: transparent;");
                btn.setOnMouseEntered(e -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
                    st.setToX(1.2);
                    st.setToY(1.2);
                    st.playFromStart();
                });
                btn.setOnMouseExited(e -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
                    st.setToX(1.0);
                    st.setToY(1.0);
                    st.playFromStart();
                });
                btn.setOnAction(evt -> {
                    Libro libro = getTableView().getItems().get(getIndex());
                    try {
                        if(CliUtil.getInstance().getLibService().isLibPresent(CliUtil.getInstance().getCurrentToken(), libro))
                            CliUtil.getInstance().buildStage(FXMLtype.CREAVALUTAZIONE, libro);
                        else{
                            CliUtil.getInstance().createAlert("Errore", "Il libro non è presente in nessuna libreria.").showAndWait();
                        }
                    } catch (RemoteException e) {
                        CliUtil.getInstance().createAlert("Errore", "Impossibile aggiungere il libro: " + e.getMessage()).showAndWait();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setAlignment(Pos.CENTER);
            }

        });
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
    protected TableView<Libro> getTableView() {
        return tableView;
    }

    @Override
    protected TableColumn<Libro, String> getTitoloCol() {
        return titoloCol;
    }

    @Override
    protected TableColumn<Libro, String> getAutoreCol() {
        return autoreCol;
    }

    @Override
    protected TableColumn<Libro, Integer> getAnnoCol() {
        return annoCol;
    }

    @Override
    protected TableColumn<Libro, Void> getRecensioniCol() {
        return recensioniCol;
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

}
