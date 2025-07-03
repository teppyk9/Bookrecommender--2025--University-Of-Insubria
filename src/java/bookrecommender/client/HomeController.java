package bookrecommender.client;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomeController {
    public Button LoginButton;
    public Button SignUpButton;
    public Button CercaLibroBaseButton;

    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) LoginButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    public void OpenLoginPage() {
        CliUtil.getInstance().buildStage(FXMLtype.LOGIN, null, null);
    }

    public void OpenSignUpPage() {
        CliUtil.getInstance().buildStage(FXMLtype.REGISTRAZIONE, null, null);
    }

    public void OpenCercaLibroBasePage() {
        CliUtil.getInstance().buildStage(FXMLtype.CERCA, null, null);
    }
}
