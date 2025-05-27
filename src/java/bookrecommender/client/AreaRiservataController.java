package bookrecommender.client;

import bookrecommender.common.LogRegInterface;
import bookrecommender.common.Token;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AreaRiservataController {
    public Button BottoneCercaLibroAvanzato;
    public Button BottoneVisualizzaLibrerie;
    public Button BottoneLogOut;

    private Token myToken;

    private LogRegInterface LogRegService;

    private static final Logger logger = Logger.getLogger(AreaRiservataController.class.getName());

    public void initialize() {
    }

    public void OpenCercaLibroAvanzato() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/CercaLibroAvanzato.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) BottoneLogOut.getScene().getWindow();
            CercaLibroAvanzatoController cercaLibroAvanzatoController = loader.getController();
            cercaLibroAvanzatoController.setToken(myToken);
            stage.setTitle("Cerca Libro Avanzato");
            stage.setScene(new Scene(root));
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
            stage.getIcons().add(icon);
            stage.show();
        } catch (Exception e) {
            showAlert("Errore", "Impossibile tornare al menu principale.");
            logger.log(Level.SEVERE, "Impossibile tornare al menu principale.", e);
        }
    }

    public void OpenVisualizzaLibrerie() {
        System.out.println("OpenVisualizzaLibrerie called");
    }

    public void LogOut(){
        try {
            if (LogRegService.LogOut(myToken))
                showConfirmation("Logout effettuato", "Sei stato disconnesso con successo.");
            else
                showAlert("Errore di Logout", "Si è verificato un errore durante il logout");
        }catch (RemoteException e) {
            showAlert("Errore di Logout", e.getMessage());
        }
        GoBackMainMenu();
    }

    public void setToken(Token token) {
        this.myToken = token;
    }

    public void setLogRegService(LogRegInterface logRegService) {
        this.LogRegService = logRegService;
    }

    private void createAller(Alert alert, Stage stage) {
        Image icona = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/alert_icon.png")));
        ImageView imageView = new ImageView(icona);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        alert.setGraphic(imageView);
        stage.getIcons().add(icona);
    }

    private void showAlert(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        createAller(alert, stage);
        alert.showAndWait();
    }

    private void showConfirmation(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/alert_confirmation_icon.png"))));
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        alert.setGraphic(imageView);
        stage.getIcons().add(imageView.getImage());
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }

    public void GoBackMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) BottoneLogOut.getScene().getWindow();
            stage.setTitle("BookRecommender");
            stage.setScene(new Scene(root));
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
            stage.getIcons().add(icon);
            stage.show();
        } catch (Exception e) {
            showAlert("Errore", "Impossibile tornare al menu principale.");
            logger.log(Level.SEVERE, "Impossibile tornare al menu principale.", e);
        }

    }
}
