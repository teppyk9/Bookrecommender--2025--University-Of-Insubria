package bookrecommender.client;

import bookrecommender.common.LogRegInterface;
import bookrecommender.common.Token;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    public javafx.scene.control.PasswordField PasswordField;
    public Button AccediButton;
    public Label NonHaiUnAccountFiled;
    public Text ErrorToLogin;
    public Button GoBackButton;
    public TextField UsernameField;

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    private LogRegInterface logRegService;

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
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            logRegService = (LogRegInterface) registry.lookup("LogReg_Interface");
            showConfirmation("Connessione stabilita", "Connessione al server RMI avvenuta con successo.");
        } catch (Exception e) {
            showAlert("Errore di connessione", "Impossibile connettersi al server RMI.");
            logger.log(Level.SEVERE, "Errore di connessione al server RMI", e);
        }
    }

    public void EnterController(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            TryLogin();
        }
    }

    public void TryLogin() {
        String username = UsernameField.getText();
        String password = PasswordField.getText();

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            ErrorToLogin.setText("Username e password non possono essere vuoti.");
            ErrorToLogin.setVisible(true);
            return;
        }

        try {
            Token token = logRegService.TryLogin(username, password);
            if (token != null) {
                showConfirmation("Login riuscito", "Benvenuto " + username + "!");
                GoBackMainMenu();
            } else {
                ErrorToLogin.setVisible(true);
                ErrorToLogin.setManaged(true);
                ErrorToLogin.setText("Credenziali errate. Riprova.");
            }
        } catch (Exception e) {
            showAlert("Errore", "Impossibile effettuare il login. Riprova più tardi.");
            logger.log(Level.SEVERE, "Errore durante il tentativo di login", e);
        }
    }

    public void GoToRegisterPage(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 1 || mouseEvent.getClickCount() == 2) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/Registrazione.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) AccediButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Registrazione");
                Image icon = new Image(java.util.Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
                stage.getIcons().add(icon);
                stage.show();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Impossibile caricare l'interfaccia richiesta!", e);
            }
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
