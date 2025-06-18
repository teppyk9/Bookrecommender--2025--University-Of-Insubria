package bookrecommender.client;

import javafx.scene.control.Button;

public class HomeController {
    public Button LoginButton;
    public Button SignUpButton;
    public Button CercaLibroBaseButton;

    public void OpenLoginPage() {
        CliUtil.getInstance().buildStage(FXMLtype.LOGIN, null);
    }

    public void OpenSignUpPage() {
        CliUtil.getInstance().buildStage(FXMLtype.REGISTRAZIONE, null);
    }

    public void OpenCercaLibroBasePage() {
        CliUtil.getInstance().buildStage(FXMLtype.CERCA, null);
    }
}
