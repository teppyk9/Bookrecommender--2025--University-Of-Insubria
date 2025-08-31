package bookrecommender.ui;

import bookrecommender.enums.FXMLtype;
import bookrecommender.util.CliUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller JavaFX per la schermata iniziale (Home) dell'applicazione.
 * <p>
 * Consente all'utente di:
 * <ul>
 *     <li>Accedere alla schermata di login</li>
 *     <li>Accedere alla schermata di registrazione</li>
 *     <li>Accedere alla ricerca base dei libri</li>
 * </ul>
 * Gestisce inoltre la chiusura della finestra terminando l'applicazione.
 * </p>
 */
public class Home {

    /** Pulsante per accedere alla schermata di login */
    @FXML private Button LoginButton;

    /**
     * Inizializza la finestra principale.
     * <p>
     * Registra un handler sulla chiusura dello {@link Stage}
     * per terminare correttamente l'applicazione.
     * </p>
     */
    @FXML private void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) LoginButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    /**
     * Apre la schermata di login dell'applicazione.
     */
    @FXML private void OpenLoginPage() {
        CliUtil.getInstance().buildStage(FXMLtype.LOGIN, null, null);
    }

    /**
     * Apre la schermata di registrazione di un nuovo utente.
     */
    @FXML private void OpenSignUpPage() {
        CliUtil.getInstance().buildStage(FXMLtype.REGISTRAZIONE, null, null);
    }

    /**
     * Apre la schermata di ricerca base dei libri.
     */
    @FXML private void OpenCercaLibroBasePage() {
        CliUtil.getInstance().buildStage(FXMLtype.CERCA, null, null);
    }
}
