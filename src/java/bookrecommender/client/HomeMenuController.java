package bookrecommender.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeMenuController {
    @FXML
    private Button BottoneCercaLibroHome;
    @FXML
    private Button BottoneEsciHome;
    @FXML
    private Text Titolo_Home;

    private static final Logger logger = Logger.getLogger(HomeMenuController.class.getName());

    public void OpenCercaLibroBasic() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/CercaLibro.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) BottoneCercaLibroHome.getScene().getWindow();
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

    public void ExitApplication() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma uscita");
        alert.setContentText("Sei sicuro di voler uscire dall'applicazione?");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Image icona = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/alert_icon.png")));
        ImageView imageView = new ImageView(icona);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        alert.setGraphic(imageView);
        stage.getIcons().add(icona);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            Platform.exit();
            System.exit(0);
        }
    }
}
