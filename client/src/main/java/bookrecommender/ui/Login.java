package bookrecommender.ui;

import bookrecommender.enums.FXMLtype;
import bookrecommender.enums.IMGtype;
import bookrecommender.util.CliUtil;
import bookrecommender.util.PasswordEngine;
import bookrecommender.model.Token;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller JavaFX per la schermata di login dell'applicazione.
 * <p>
 * Consente all'utente di autenticarsi inserendo username e password,
 * gestisce la visibilità della password tramite {@link PasswordEngine},
 * e fornisce collegamenti per la registrazione e il ritorno al menu principale.
 * </p>
 */
public class Login extends PasswordEngine{
    /** Pulsante per tornare al menu principale */
    @FXML private Button GoBackButton;
    /** Pulsante per mostrare/nascondere la password */
    @FXML private Button ShowPButton;
    /** Campo di input per la password (mascherata) */
    @FXML private PasswordField PasswordField;
    /** Pulsante per avviare il login */
    @FXML private Button AccediButton;
    /** Etichetta testuale cliccabile che rimanda alla registrazione */
    @FXML private Label NonHaiUnAccountFiled;
    /** Testo di errore mostrato in caso di problemi di login */
    @FXML private Text ErrorToLogin;
    /** Campo di input per lo username */
    @FXML private TextField UsernameField;
    /** Campo di input per la password in chiaro (sincronizzato con PasswordField) */
    @FXML private TextField VisiblePasswordField;

    /**
     * Inizializza la schermata di login.
     * <p>
     * Configura icone e stili grafici, nasconde i messaggi di errore,
     * abilita l’interazione con l’etichetta "Non hai un account",
     * e imposta il focus iniziale sul campo username.
     * Registra inoltre un handler di chiusura per terminare l’applicazione.
     * </p>
     */
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

    /**
     * Gestisce la pressione del tasto Invio nella schermata.
     * <p>
     * Se premuto, richiama il metodo {@link #TryLogin()}.
     * </p>
     *
     * @param keyEvent evento di tastiera associato alla pressione di un tasto
     */
    @FXML private void EnterController(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            TryLogin();
        }
    }

    /**
     * Tenta il login dell'utente con le credenziali fornite.
     * <p>
     * Verifica che username e password non siano vuoti,
     * invia i dati al servizio remoto e:
     * <ul>
     *   <li>Se validi: salva il token corrente, mostra un messaggio di benvenuto
     *       e apre l'area riservata</li>
     *   <li>Se non validi: mostra un messaggio di errore</li>
     * </ul>
     * In caso di eccezione, richiama {@code CliUtil.LogOut(e)}.
     * </p>
     */
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
            CliUtil.getInstance().LogOut(e);
        }
    }

    /**
     * Apre la schermata di registrazione se l’utente clicca
     * sull’etichetta "Non hai un account".
     *
     * @param mouseEvent evento di click del mouse
     */
    @FXML private void GoToRegisterPage(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 1 || mouseEvent.getClickCount() == 2) {
            CliUtil.getInstance().buildStage(FXMLtype.REGISTRAZIONE, null, null);
        }
    }

    /**
     * Torna alla schermata principale (Home).
     */
    @FXML private void GoBackMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null);
    }

    /** {@inheritDoc} */
    @Override
    protected PasswordField getPasswordField1() {
        return PasswordField;
    }

    /**
     * {@inheritDoc}
     * <p>Non utilizzato: restituisce sempre {@code null}.</p>
     */
    @Override
    protected PasswordField getPasswordField2() {
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>Non utilizzato: restituisce sempre {@code null}.</p>
     */
    @Override
    protected TextField getVisiblePasswordField1() {
        return VisiblePasswordField;
    }

    /**
     * {@inheritDoc}
     * <p>Non utilizzato: restituisce sempre {@code null}.</p>
     */
    @Override
    protected TextField getVisiblePasswordField2() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    protected Button getButton1() {
        return ShowPButton;
    }

    /**
     * {@inheritDoc}
     * <p>Non utilizzato: restituisce sempre {@code null}.</p>
     */
    @Override
    protected Button getButton2() {
        return null;
    }
}
