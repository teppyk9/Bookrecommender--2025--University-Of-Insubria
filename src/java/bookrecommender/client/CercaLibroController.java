package bookrecommender.client;

import bookrecommender.common.Libro;
import bookrecommender.common.SearchInterface;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Objects;

public class CercaLibroController {

    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;
    @FXML private TextField campoRicercaAnno;
    @FXML private TextField campoRicerca;
    @FXML private ListView<Libro> listaLibri;
    @FXML private Button bottoneCerca;

    private SearchInterface searchService;

    private String searchType;

    @FXML
    public void initialize() {
        campoRicercaAnno.setVisible(false);
        campoRicercaAnno.setDisable(true);
        searchType = "";
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            searchService = (SearchInterface) registry.lookup("Search_Interface");
            showConfirmation("Connessione stabilita", "Connessione al server RMI avvenuta con successo.");
        } catch (Exception e) {
            showAlert("Errore di connessione", "Impossibile connettersi al server RMI.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClickCerca() {
        String testo = campoRicerca.getText();
        String anno = campoRicercaAnno.getText();
        if (testo != null && testo.length() >= 2) {
            aggiornaLista(testo, anno);
        } else {
            listaLibri.setItems(FXCollections.observableArrayList());
            showAlert("Errore", "Inserire almeno 2 caratteri per la ricerca.");
        }
    }

    private void aggiornaLista(String titolo, String anno) {
        try {
            List<Libro> risultati = null;
            boolean temp = false;
            switch (searchType) {
                case "Titolo":
                    risultati = searchService.searchByName(titolo);
                    if(risultati != null) {
                        listaLibri.setItems(FXCollections.observableArrayList(risultati));
                        temp = true;
                    }
                    break;
                case "Autore":
                    risultati = searchService.searchByAuthor(titolo);
                    if(risultati != null) {
                        listaLibri.setItems(FXCollections.observableArrayList(risultati));
                        temp = true;
                    }
                    break;
                case "AutoreAnno":
                    if(anno == null || anno.trim().isEmpty())
                        showAlert("Errore", "Inserire un anno valido.");
                    else if(anno.length() > 4)
                        showAlert("Errore", "L'anno deve essere composto da massimo 4 cifre.");
                    else if(!anno.matches("\\d+"))
                        showAlert("Errore", "L'anno deve essere un numero.");
                    else {
                        risultati = searchService.searchByAuthorAndYear(titolo, Integer.parseInt(anno));
                        temp = true;
                    }
                    if(risultati != null)
                        listaLibri.setItems(FXCollections.observableArrayList(risultati));
                    break;
                default:
                    showAlert("Errore", "Tipo di ricerca non selezionato.");
                    break;
            }
            if((risultati == null || risultati.isEmpty()) && temp) {
                showAlert("Nessun risultato", "Nessun libro trovato con i criteri di ricerca specificati.");
            }
        } catch (Exception e) {
            showAlert("Errore durante la ricerca", e.getMessage());
        }
    }

    @FXML
    private void handleListaDoppioClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Libro selezionato = listaLibri.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                mostraDettagli(selezionato);
            }
        }
    }

    private void mostraDettagli(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/DettaglioLibro.fxml"));
            Parent root = loader.load();

            DettaglioLibroController controller = loader.getController();
            controller.setLibro(libro);
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
            Stage stage = new Stage();
            stage.setTitle("Dettagli del libro");
            stage.getIcons().add(icon);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Errore", "Impossibile aprire la finestra dei dettagli.");
            e.printStackTrace();
        }
    }

    private void showAlert(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Image icona = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/alert_icon.png")));
        ImageView imageView = new ImageView(icona);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        alert.setGraphic(imageView);
        stage.getIcons().add(icona);
        alert.showAndWait();
    }

    private void showConfirmation(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Image icona = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/alert_confirmation_icon.png")));
        ImageView imageView = new ImageView(icona);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        alert.setGraphic(imageView);
        stage.getIcons().add(icona);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }

    public void cercaTitolo() {
        MenuTipoRicerca.getItems().remove(MenuCercaTitolo);
        switch(searchType) {
            case "Autore":
                MenuTipoRicerca.getItems().add(MenuCercaAutore);
                break;
            case "AutoreAnno":
                MenuTipoRicerca.getItems().add(MenuCercaAutoreAnno);
                campoRicercaAnno.setDisable(true);
                campoRicercaAnno.setVisible(false);
                break;
            default:
                break;
        }
        searchType = "Titolo";
        MenuTipoRicerca.setText("Titolo");
    }

    public void cercaAutore() {
        MenuTipoRicerca.getItems().remove(MenuCercaAutore);
        switch(searchType) {
            case "Titolo":
                MenuTipoRicerca.getItems().add(MenuCercaTitolo);
                break;
            case "AutoreAnno":
                MenuTipoRicerca.getItems().add(MenuCercaAutoreAnno);
                campoRicercaAnno.setDisable(true);
                campoRicercaAnno.setVisible(false);
                break;
            default:
                break;
        }
        searchType = "Autore";
        MenuTipoRicerca.setText("Autore");
    }

    public void cercaAutoreAnno() {
        MenuTipoRicerca.getItems().remove(MenuCercaAutoreAnno);
        switch(searchType) {
            case "Titolo":
                MenuTipoRicerca.getItems().add(MenuCercaTitolo);
                break;
            case "Autore":
                MenuTipoRicerca.getItems().add(MenuCercaAutore);
                break;
            default:
                break;
        }
        searchType = "AutoreAnno";
        MenuTipoRicerca.setText("Autore e Anno");
        campoRicercaAnno.setDisable(false);
        campoRicercaAnno.setVisible(true);
    }
}
