package bookrecommender.client.ui;

import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.util.PasswordEngine;
import bookrecommender.common.model.Token;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Login extends PasswordEngine{
    @FXML private Button GoBackButton;
    @FXML private Button ShowPButton;
    @FXML private PasswordField PasswordField;
    @FXML private Button AccediButton;
    @FXML private Label NonHaiUnAccountFiled;
    @FXML private Text ErrorToLogin;
    @FXML private TextField UsernameField;
    @FXML private TextField VisiblePasswordField;

    @FXML private void initialize() {
        GoBackButton.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        GoBackButton.setAlignment(Pos.TOP_LEFT);
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
        initP1();
        Platform.runLater(() -> {
            UsernameField.requestFocus();
            Stage stage = (Stage) AccediButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    @FXML private void EnterController(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            TryLogin();
        }
    }

    @FXML private void TryLogin() {
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
                CliUtil.getInstance().createConfirmation("Login riuscito", "Benvenuto " + username + "!",false).showAndWait();
                CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null, null);
            } else {
                ErrorToLogin.setVisible(true);
                ErrorToLogin.setManaged(true);
                ErrorToLogin.setText("Credenziali errate. Riprova.");
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore", "Impossibile effettuare il login. Riprova più tardi.").showAndWait();
        }
    }

    @FXML private void GoToRegisterPage(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 1 || mouseEvent.getClickCount() == 2) {
            CliUtil.getInstance().buildStage(FXMLtype.REGISTRAZIONE, null, null);
        }
    }

    @FXML private void GoBackMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null);
    }

    @Override
    protected PasswordField getPasswordField1() {
        return PasswordField;
    }

    @Override
    protected PasswordField getPasswordField2() {
        return null;
    }

    @Override
    protected TextField getVisiblePasswordField1() {
        return VisiblePasswordField;
    }

    @Override
    protected TextField getVisiblePasswordField2() {
        return null;
    }

    @Override
    protected Button getButton1() {
        return ShowPButton;
    }

    @Override
    protected Button getButton2() {
        return null;
    }
}
