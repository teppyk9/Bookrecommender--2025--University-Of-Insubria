package bookrecommender.client;

import bookrecommender.common.RegToken;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class RegistrazioneController {
    public TextField NomeField;
    public TextField CognomeField;
    public TextField CFFiled;
    public TextField EmailField;
    public TextField UsernameFiled;
    public PasswordField PasswordField1;
    public PasswordField PasswordField2;
    public Button AccediButton;
    public Label AccediTextField;
    public Button GoBackButton;

    public void initialize() {
        AccediTextField.setOnMouseEntered(event -> {
            AccediTextField.setUnderline(true);
            AccediTextField.setCursor(Cursor.HAND);
        });
        AccediTextField.setOnMouseExited(event -> {
            AccediTextField.setUnderline(false);
        AccediTextField.setCursor(Cursor.DEFAULT);
        });
        Platform.runLater(() -> {
            Stage stage = (Stage) AccediButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    public void GoBackMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null);
    }

    public void GoToLoginPage() {
        CliUtil.getInstance().buildStage(FXMLtype.LOGIN, null, null);
    }

    public void TryReg() {
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
                if(result.isUsernameAvailable()) {
                    CliUtil.getInstance().createAlert("Errore", "Username già utilizzata").showAndWait();
                }
                if(result.isEmailAvailable()) {
                    CliUtil.getInstance().createAlert("Errore", "Email già utilizzata").showAndWait();
                }
                if(result.isCFAvailable()) {
                    CliUtil.getInstance().createAlert("Errore", "Codice Fiscale già utilizzato").showAndWait();
                }
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore", "Impossibile completare la registrazione. Riprova più tardi.\n " + e).showAndWait();
        }
    }

    public void RegAction(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            TryReg();
        }
    }
}