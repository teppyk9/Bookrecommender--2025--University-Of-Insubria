package bookrecommender.client.ui;

import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.util.PasswordEngine;
import bookrecommender.common.model.RegToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class Registrazione extends PasswordEngine {
    @FXML private Button GoBackButton;
    @FXML private Button ShowP1Button;
    @FXML private Button ShowP2Button;
    @FXML private TextField NomeField;
    @FXML private TextField CognomeField;
    @FXML private TextField CFFiled;
    @FXML private TextField EmailField;
    @FXML private TextField UsernameFiled;
    @FXML private PasswordField PasswordField1;
    @FXML private PasswordField PasswordField2;
    @FXML private Button AccediButton;
    @FXML private Label AccediTextField;
    @FXML private TextField VisiblePasswordField1;
    @FXML private TextField VisiblePasswordField2;

    @FXML private void initialize() {
        GoBackButton.setGraphic(IMGtype.INDIETRO.getImageView(47,47));
        AccediTextField.setOnMouseEntered(event -> {
            AccediTextField.setUnderline(true);
            AccediTextField.setCursor(Cursor.HAND);
        });
        AccediTextField.setOnMouseExited(event -> {
            AccediTextField.setUnderline(false);
        AccediTextField.setCursor(Cursor.DEFAULT);
        });
        initP1();
        initP2();
        Platform.runLater(() -> {
            Stage stage = (Stage) AccediButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    @FXML private void GoBackMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null);
    }

    @FXML private void GoToLoginPage() {
        CliUtil.getInstance().buildStage(FXMLtype.LOGIN, null, null);
    }

    @FXML private void TryReg() {
        String nome = NomeField.getText().trim();
        String cognome = CognomeField.getText().trim();
        String cf = CFFiled.getText().trim().toUpperCase();
        String email = EmailField.getText().trim().toLowerCase();
        String username = UsernameFiled.getText().trim().toLowerCase();
        String password1 = PasswordField1.getText().trim();
        String password2 = PasswordField2.getText().trim();
        Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
        Pattern CF_PATTERN = Pattern.compile("^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$", Pattern.CASE_INSENSITIVE);

        if (nome.isEmpty()) {
            CliUtil.getInstance().createAlert("Errore", "Campo 'Nome' non può essere vuoto.").showAndWait();
            return;
        }
        if (cognome.isEmpty()) {
            CliUtil.getInstance().createAlert("Errore", "Campo 'Cognome' non può essere vuoto.").showAndWait();
            return;
        }
        if (cf.isEmpty()) {
            CliUtil.getInstance().createAlert("Errore", "Campo 'Codice Fiscale' non può essere vuoto.").showAndWait();
            return;
        }
        if (email.isEmpty()) {
            CliUtil.getInstance().createAlert("Errore", "Campo 'Email' non può essere vuoto.").showAndWait();
            return;
        }
        if (username.isEmpty()) {
            CliUtil.getInstance().createAlert("Errore", "Campo 'Username' non può essere vuoto.").showAndWait();
            return;
        }
        if (password1.isEmpty()){
            CliUtil.getInstance().createAlert("Errore", "Campo 'Password' non può essere vuoto.").showAndWait();
            return;
        }
        if (password2.isEmpty()) {
            CliUtil.getInstance().createAlert("Errore", "Campo 'Conferma Password' non può essere vuoto.").showAndWait();
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            CliUtil.getInstance().createAlert("Errore", "Formato email non valido.").showAndWait();
            return;
        }
        if (!CF_PATTERN.matcher(cf).matches()) {
            CliUtil.getInstance().createAlert("Errore", "Formato Codice Fiscale non valido.").showAndWait();
            return;
        }
        if (username.length() < 5 || username.length() > 20) {
            CliUtil.getInstance().createAlert("Errore", "Username deve essere tra 5 e 20 caratteri.").showAndWait();
            return;
        }

        if (!password1.equals(password2)) {
            CliUtil.getInstance().createAlert("Errore", "Le password non corrispondono.").showAndWait();
            return;
        }
        if (password1.length() < 8) {
            CliUtil.getInstance().createAlert("Errore", "La password deve contenere almeno 8 caratteri.").showAndWait();
            return;
        }
        try {
            RegToken result = CliUtil.getInstance().getLogRegService().Register(nome, cognome, cf, email, username, password1);
            if (result.RegistrationAvailable()) {
                CliUtil.getInstance().createConfirmation("Registrazione riuscita", "Benvenuto " + username + "!", false).showAndWait();
                CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null);
            } else{
                if(result.User()) {
                    CliUtil.getInstance().createAlert("Errore", "Username già utilizzata").showAndWait();
                }
                if(result.Email()) {
                    CliUtil.getInstance().createAlert("Errore", "Email già utilizzata").showAndWait();
                }
                if(result.CF()) {
                    CliUtil.getInstance().createAlert("Errore", "Codice Fiscale già utilizzato").showAndWait();
                }
            }
        } catch (Exception e) {
            CliUtil.getInstance().LogOut(e);
        }
    }

    @FXML private void RegAction(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            TryReg();
        }
    }

    @Override
    protected PasswordField getPasswordField1() {
        return PasswordField1;
    }

    @Override
    protected PasswordField getPasswordField2() {
        return PasswordField2;
    }

    @Override
    protected TextField getVisiblePasswordField1() {
        return VisiblePasswordField1;
    }

    @Override
    protected TextField getVisiblePasswordField2() {
        return VisiblePasswordField2;
    }

    @Override
    protected Button getButton1() {
        return ShowP1Button;
    }

    @Override
    protected Button getButton2() {
        return ShowP2Button;
    }
}