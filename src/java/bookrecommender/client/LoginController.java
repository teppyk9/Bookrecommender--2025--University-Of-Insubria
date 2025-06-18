package bookrecommender.client;

import bookrecommender.common.Token;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
public class LoginController {

    public javafx.scene.control.PasswordField PasswordField;
    public Button AccediButton;
    public Label NonHaiUnAccountFiled;
    public Text ErrorToLogin;
    public Button GoBackButton;
    public TextField UsernameField;


    public void initialize() {
        ErrorToLogin.setVisible(false);
        ErrorToLogin.setManaged(false);
        NonHaiUnAccountFiled.setOnMouseEntered(event -> {
            NonHaiUnAccountFiled.setUnderline(true);
            NonHaiUnAccountFiled.setCursor(Cursor.HAND);
        });
        NonHaiUnAccountFiled.setOnMouseExited(event -> {
            NonHaiUnAccountFiled.setUnderline(false);
            NonHaiUnAccountFiled.setCursor(Cursor.DEFAULT);
        });
    }

    public void EnterController(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            TryLogin();
        }
    }

    public void TryLogin() {
        String username = UsernameField.getText().toLowerCase();
        String password = PasswordField.getText();

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            ErrorToLogin.setText("Username e password non possono essere vuoti.");
            ErrorToLogin.setVisible(true);
            return;
        }

        try {
            Token token = CliUtil.getInstance().getLogRegService().TryLogin(username, password);
            if (token != null) {
                CliUtil.getInstance().setCurrentToken(token);
                token = null;
                CliUtil.getInstance().createConfirmation("Login riuscito", "Benvenuto " + username + "!",false).showAndWait();
                CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null);
            } else {
                ErrorToLogin.setVisible(true);
                ErrorToLogin.setManaged(true);
                ErrorToLogin.setText("Credenziali errate. Riprova.");
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore", "Impossibile effettuare il login. Riprova più tardi.").showAndWait();
        }
    }

    public void GoToRegisterPage(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 1 || mouseEvent.getClickCount() == 2) {
            CliUtil.getInstance().buildStage(FXMLtype.REGISTRAZIONE, null);
        }
    }

    public void GoBackMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null);
    }
}
