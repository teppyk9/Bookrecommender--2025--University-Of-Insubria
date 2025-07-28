package bookrecommender.client.ui;

import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.util.CliUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Home {
    @FXML private Button LoginButton;

    @FXML private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) LoginButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    @FXML private void OpenLoginPage() {
        CliUtil.getInstance().buildStage(FXMLtype.LOGIN, null, null);
    }

    @FXML private void OpenSignUpPage() {
        CliUtil.getInstance().buildStage(FXMLtype.REGISTRAZIONE, null, null);
    }

    @FXML private void OpenCercaLibroBasePage() {
        CliUtil.getInstance().buildStage(FXMLtype.CERCA, null, null);
    }
}
