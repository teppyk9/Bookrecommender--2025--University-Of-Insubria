package bookrecommender.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeController {
    public Button LoginButton;
    public Button SignUpButton;
    public Button CercaLibroBaseButton;

    private static final Logger logger = Logger.getLogger(HomeController.class.getName());

    public void OpenLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) CercaLibroBaseButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            Image icon = new Image(java.util.Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
            stage.getIcons().add(icon);
            stage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Impossibile caricare l'interfaccia richiesta!", e);
        }
    }

    public void OpenSignUpPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/Registrazione.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) CercaLibroBaseButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Registrazione");
            Image icon = new Image(java.util.Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
            stage.getIcons().add(icon);
            stage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Impossibile caricare l'interfaccia richiesta!", e);
        }
    }

    public void OpenCercaLibroBasePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/CercaLibro.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) CercaLibroBaseButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Cerca Libro");
            Image icon = new Image(java.util.Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
            stage.getIcons().add(icon);
            stage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Impossibile caricare l'interfaccia richiesta!", e);
        }
    }
}
