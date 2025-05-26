package bookrecommender.client;

import bookrecommender.common.LogRegInterface;
import bookrecommender.common.RegToken;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final Logger logger = Logger.getLogger(RegistrazioneController.class.getName());

    private LogRegInterface logRegService;

    public void initialize() {
        AccediTextField.setOnMouseEntered(event -> {
            AccediTextField.setUnderline(true);
            AccediTextField.setCursor(Cursor.HAND);
        });
        AccediTextField.setOnMouseExited(event -> {
            AccediTextField.setUnderline(false);
        AccediTextField.setCursor(Cursor.DEFAULT);
        });
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            logRegService = (LogRegInterface) registry.lookup("LogReg_Interface");
        } catch (Exception e) {
            showAlert("Errore di connessione", "Impossibile connettersi al server RMI.");
            logger.log(Level.SEVERE, "Errore di connessione al server RMI", e);
        }
    }

    public void GoBackMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) GoBackButton.getScene().getWindow();
            stage.setTitle("BookRecommender");
            stage.setScene(new Scene(root));
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
            stage.getIcons().add(icon);
            stage.show();
        } catch (Exception e) {
            showAlert("Errore", "Impossibile tornare al menu principale.");
            logger.log(Level.SEVERE, "Impossibile tornare al menu principale.", e);
        }
    }

    public void GoToLoginPage(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 1 || mouseEvent.getClickCount() == 2) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) AccediButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Login");
                Image icon = new Image(java.util.Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
                stage.getIcons().add(icon);
                stage.show();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Impossibile caricare l'interfaccia richiesta!", e);
            }
        }
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
            showAlert("Errore", "Campo 'Nome' non può essere vuoto.");
            return;
        }
        if (cognome.isEmpty()) {
            showAlert("Errore", "Campo 'Cognome' non può essere vuoto.");
            return;
        }
        if (cf.isEmpty()) {
            showAlert("Errore", "Campo 'Codice Fiscale' non può essere vuoto.");
            return;
        }
        if (email.isEmpty()) {
            showAlert("Errore", "Campo 'Email' non può essere vuoto.");
            return;
        }
        if (username.isEmpty()) {
            showAlert("Errore", "Campo 'Username' non può essere vuoto.");
            return;
        }
        if (password1.isEmpty()){
            showAlert("Errore", "Campo 'Password' non può essere vuoto.");
            return;
        }
        if (password2.isEmpty()) {
            showAlert("Errore", "Campo 'Conferma Password' non può essere vuoto.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showAlert("Errore", "Formato email non valido.");
            return;
        }
        if (!CF_PATTERN.matcher(cf).matches()) {
            showAlert("Errore", "Formato Codice Fiscale non valido.");
            return;
        }
        if (username.length() < 5 || username.length() > 20) {
            showAlert("Errore", "Username deve essere tra 5 e 20 caratteri.");
            return;
        }

        if (!password1.equals(password2)) {
            showAlert("Errore", "Le password non corrispondono.");
            return;
        }
        if (password1.length() < 8) {
            showAlert("Errore", "La password deve contenere almeno 8 caratteri.");
            return;
        }
        try {
            RegToken result = logRegService.Register(nome, cognome, cf, email, username, password1);
            if (result.RegistrationAvailable()) {
                showConfirmation("Registrazione riuscita", "Benvenuto " + username + "!");
                GoBackMainMenu();
            } else{
                if(result.isUsernameAvailable()) {
                    showAlert("Errore", "Username già utilizzata");
                }
                if(result.isEmailAvailable()) {
                    showAlert("Errore", "Email già utilizzata");
                }
                if(result.isCFAvailable()) {
                    showAlert("Errore", "Codice Fiscale già utilizzato");
                }
            }
        } catch (Exception e) {
            showAlert("Errore", "Impossibile completare la registrazione. Riprova più tardi.");
            logger.log(Level.SEVERE, "Errore durante il tentativo di registrazione", e);
        }
    }

    public void RegAction(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
        TryReg();
        }
    }

    private void createAller(Alert alert, Stage stage) {
        Image icona = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/alert_icon.png")));
        ImageView imageView = new ImageView(icona);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        alert.setGraphic(imageView);
        stage.getIcons().add(icona);
    }

    private void showAlert(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        createAller(alert, stage);
        alert.showAndWait();
    }

    private void showConfirmation(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/alert_confirmation_icon.png"))));
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        alert.setGraphic(imageView);
        stage.getIcons().add(imageView.getImage());
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }
}
