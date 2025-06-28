package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Objects;

public class CercaLibroAvanzatoController{

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
        campoRicercaAnno.setVisible(false);
        campoRicercaAnno.setDisable(true);
        ImageView arrow = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png")), 12, 12, true, true));
        MenuTipoRicerca.setGraphic(arrow);
        Platform.runLater(() -> {
            Node a = MenuTipoRicerca.lookup(".arrow");
            if (a != null) {
                a.setVisible(false);
                a.setManaged(false);
            }
        });

        MenuCercaTitolo.setOnAction(e -> switchType("Titolo", "Titolo"));
        MenuCercaAutore.setOnAction(e -> switchType("Autore", "Autore"));
        MenuCercaAutoreAnno.setOnAction(e -> {
            switchType("AutoreAnno", "Autore e Anno");
            campoRicercaAnno.setVisible(true);
            campoRicercaAnno.setDisable(false);
        });
        titoloCol.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        autoreCol.setCellValueFactory(new PropertyValueFactory<>("autore"));
        annoCol.setCellValueFactory(new PropertyValueFactory<>("annoPubblicazione"));

        recensioniCol.setCellFactory(col -> new TableCell<>() {
            private final ImageView check = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/check-green.png")), 16, 16, true, true));
            private final ImageView noCheck = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_icon.png")), 16, 16, true, true));
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Libro libro = getTableView().getItems().get(getIndex());
                    boolean has = false;
                    try {
                        has = CliUtil.getInstance().getSearchService().hasValRec(libro);
                    } catch (RemoteException e) {
                        CliUtil.getInstance().createAlert("Errore", "Impossibile verificare le recensioni: " + e.getMessage()).showAndWait();
                    }
                    setGraphic(has ? check : noCheck);
                    setAlignment(Pos.CENTER);
                }
            }
        });

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

        tableView.setRowFactory(tv -> {
            TableRow<Libro> row = new TableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && ! row.isEmpty()) {
                    Libro sel = row.getItem();
                    CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, sel);
                }
            });
            return row;
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

    private void switchType(String key, String text) {
        MenuTipoRicerca.getItems().setAll(MenuCercaTitolo, MenuCercaAutore, MenuCercaAutoreAnno);
        campoRicercaAnno.setVisible(false);
        campoRicercaAnno.setDisable(true);
        this.searchType = key;
        MenuTipoRicerca.setText(text);
        switch (key) {
            case "Titolo":
                MenuTipoRicerca.getItems().remove(MenuCercaTitolo);
                break;
            case "Autore":
                MenuTipoRicerca.getItems().remove(MenuCercaAutore);
                break;
            case "AutoreAnno":
                MenuTipoRicerca.getItems().remove(MenuCercaAutoreAnno);
                break;
        }
    }

    @FXML
    private void handleClickCerca() {
        String testo = campoRicerca.getText();
        String anno  = campoRicercaAnno.getText();
        if (testo == null || testo.length() < 2) {
            tableView.setItems(FXCollections.emptyObservableList());
            CliUtil.getInstance().createAlert("Errore", "Inserire almeno 2 caratteri per la ricerca.").showAndWait();
            return;
        }
        List<Libro> risultati;
        try {
            switch (searchType) {
                case "Titolo":
                    risultati = CliUtil.getInstance().getSearchService().searchByName(testo);
                    break;
                case "Autore":
                    risultati = CliUtil.getInstance().getSearchService().searchByAuthor(testo);
                    break;
                case "AutoreAnno":
                    if (!validateYear(anno)) return;
                    risultati = CliUtil.getInstance().getSearchService().searchByAuthorAndYear(testo, Integer.parseInt(anno));
                    break;
                default:
                    CliUtil.getInstance().createAlert("Errore", "Tipo di ricerca non selezionato.").showAndWait();
                    return;
            }
            if (risultati != null && !risultati.isEmpty()) {
                tableView.setItems(FXCollections.observableArrayList(risultati));
            } else {
                tableView.setItems(FXCollections.emptyObservableList());
                CliUtil.getInstance().createAlert("Nessun risultato", "Nessun libro trovato.").showAndWait();
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
        }
    }

    private boolean validateYear(String anno) {
        if (anno == null || anno.trim().isEmpty() || !anno.matches("\\d{1,4}")) {
            CliUtil.getInstance().createAlert("Errore", "Inserire un anno valido (fino a 4 cifre).").showAndWait();
            return false;
        }
        return true;
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
    private void keyEnterPressed_1(KeyEvent e) {
        if ("Enter".equals(e.getCode().getName()))
            handleClickCerca();
    }

    @FXML
    private void keyEnterPressed_2(KeyEvent e) {
        if ("Enter".equals(e.getCode().getName()))
            handleClickCerca();
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
