package bookrecommender.client;

import javafx.scene.control.Button;

public class HomeController {
    public Button LoginButton;
    public Button SignUpButton;
    public Button CercaLibroBaseButton;

    public void OpenLoginPage() {
        CliUtil.getInstance().loadFXML("/bookrecommender/client/Login.fxml", "Login");
    }

    public void OpenSignUpPage() {
        CliUtil.getInstance().loadFXML("/bookrecommender/client/Registrazione.fxml", "Registrazione");
    }

    public void OpenCercaLibroBasePage() {
        CliUtil.getInstance().loadFXML("/bookrecommender/client/CercaLibro.fxml", "Cerca Libro");
    }
}
