package bookrecommender.client;

import bookrecommender.common.Libro;
import bookrecommender.common.SearchInterface;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Objects;

public class MainController {

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
        searchType = null;
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            searchService = (SearchInterface) registry.lookup("BookRecommender");
            showConfirmation("Connessione stabilita", "Connessione al server RMI avvenuta con successo.");
        } catch (Exception e) {
            showAlert("Errore di connessione", "Impossibile connettersi al server RMI.");
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
        }
    }

    private void aggiornaLista(String titolo, String anno) {
        try {
            List<Libro> risultati;
            switch (searchType) {
                case "Titolo":
                    risultati = searchService.searchByName(titolo);
                    listaLibri.setItems(FXCollections.observableArrayList(risultati));
                    break;
                case "Autore":
                    risultati = searchService.searchByAuthor(titolo);
                    listaLibri.setItems(FXCollections.observableArrayList(risultati));
                    break;
                case "AutoreAnno":
                    risultati = searchService.searchByAuthorAndYear(titolo, Integer.parseInt(anno));
                    listaLibri.setItems(FXCollections.observableArrayList(risultati));
                    break;
                default:
                    showAlert("Errore", "Tipo di ricerca non selezionato.");
                    break;
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
        alert.showAndWait();
    }

    private void showConfirmation(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    public void cercaTitolo() {
        searchType = "Titolo";
        MenuTipoRicerca.setText("Titolo");
        campoRicercaAnno.setVisible(false);

        MenuCercaTitolo.setDisable(true);
        MenuCercaTitolo.setVisible(false);

        MenuCercaAutore.setDisable(false);
        MenuCercaAutore.setVisible(true);

        MenuCercaAutoreAnno.setDisable(false);
        MenuCercaAutoreAnno.setVisible(true);
    }

    public void cercaAutore() {
        searchType = "Autore";
        MenuTipoRicerca.setText("Autore");
        campoRicercaAnno.setVisible(false);

        MenuCercaAutore.setDisable(true);
        MenuCercaAutore.setDisable(false);

        MenuCercaTitolo.setDisable(false);
        MenuCercaTitolo.setVisible(true);

        MenuCercaAutoreAnno.setDisable(false);
        MenuCercaAutoreAnno.setVisible(true);
    }

    public void cercaAutoreAnno() {
        searchType = "AutoreAnno";
        MenuTipoRicerca.setText("Autore e Anno");
        campoRicercaAnno.setVisible(true);

        MenuCercaAutoreAnno.setDisable(true);
        MenuCercaAutoreAnno.setVisible(false);

        MenuCercaTitolo.setDisable(false);
        MenuCercaTitolo.setVisible(true);

        MenuCercaAutore.setDisable(false);
        MenuCercaAutore.setVisible(true);
    }

}
