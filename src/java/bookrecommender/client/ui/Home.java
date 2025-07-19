package bookrecommender.client.ui;

import bookrecommender.client.util.CliUtil;
import bookrecommender.client.enums.FXMLtype;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Home {
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
