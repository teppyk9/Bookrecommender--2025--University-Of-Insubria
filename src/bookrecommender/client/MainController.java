package bookrecommender.client;

import bookrecommender.common.Libro;
import bookrecommender.common.SearchInterface;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class MainController {

    @FXML private TextField campoRicerca;
    @FXML private ListView<Libro> listaLibri;

    private SearchInterface searchService;

    @FXML
    public void initialize() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            searchService = (SearchInterface) registry.lookup("BookRecommender");
        } catch (Exception e) {
            showAlert("Errore di connessione", "Impossibile connettersi al server RMI.");
        }

        campoRicerca.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 2) {
                aggiornaLista(newVal);
            } else {
                listaLibri.setItems(FXCollections.observableArrayList());
            }
        });
    }

    private void aggiornaLista(String titolo) {
        try {
            List<Libro> risultati = searchService.searchByName(titolo);
            listaLibri.setItems(FXCollections.observableArrayList(risultati));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DettaglioLibro.fxml"));
            Parent root = loader.load();

            DettaglioLibroController controller = loader.getController();
            controller.setLibro(libro);

            Stage stage = new Stage();
            stage.setTitle("Dettagli del libro");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Errore", "Impossibile aprire la finestra dei dettagli.");
        }
    }

    private void showAlert(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}
