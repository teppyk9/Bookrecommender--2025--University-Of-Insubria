package bookrecommender.ui;

import bookrecommender.enums.FXMLtype;
import bookrecommender.enums.IMGtype;
import bookrecommender.util.CliUtil;
import bookrecommender.util.PasswordEngine;
import bookrecommender.model.RegToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.regex.Pattern;

/**
 * Schermata di registrazione utente.
 * <p>
 * Gestisce l’inserimento dei dati anagrafici (nome, cognome, CF),
 * delle credenziali (email, username, password) e l’invio della richiesta
 * di registrazione. Estende {@link PasswordEngine} per il supporto alla
 * visualizzazione/nascondimento delle password.
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA* </p>
 */
public class Registrazione extends PasswordEngine {

    /** Pulsante “indietro”: torna alla schermata principale. Non nullo dopo il caricamento FXML. */
    @FXML private Button GoBackButton;

    /** Pulsante per mostrare/nascondere il primo campo password (supporto di {@link PasswordEngine}). Non nullo dopo il caricamento FXML. */
    @FXML private Button ShowP1Button;

    /** Pulsante per mostrare/nascondere il secondo campo password (conferma). Non nullo dopo il caricamento FXML. */
    @FXML private Button ShowP2Button;

    /** Campo testo per il nome dell’utente. Non nullo dopo il caricamento FXML. */
    @FXML private TextField NomeField;

    /** Campo testo per il cognome dell’utente. Non nullo dopo il caricamento FXML. */
    @FXML private TextField CognomeField;

    /** Campo testo per il Codice Fiscale (validato da pattern). Non nullo dopo il caricamento FXML. */
    @FXML private TextField CFFiled;

    /** Campo testo per l’email (validata da pattern). Non nullo dopo il caricamento FXML. */
    @FXML private TextField EmailField;

    /** Campo testo per lo username. Non nullo dopo il caricamento FXML. */
    @FXML private TextField UsernameFiled;

    /** Primo campo password. Non nullo dopo il caricamento FXML. */
    @FXML private PasswordField PasswordField1;

    /** Secondo campo password (conferma corrispondenza). Non nullo dopo il caricamento FXML. */
    @FXML private PasswordField PasswordField2;

    /** Pulsante che avvia la procedura di registrazione. Non nullo dopo il caricamento FXML. */
    @FXML private Button AccediButton;

    /** Etichetta/collegamento per passare alla schermata di login. Non nulla dopo il caricamento FXML. */
    @FXML private Label AccediTextField;

    /** Campo testo alternativo al primo password field per la modalità “mostra password”. Non nullo dopo il caricamento FXML. */
    @FXML private TextField VisiblePasswordField1;

    /** Campo testo alternativo al secondo password field per la modalità “mostra password”. Non nullo dopo il caricamento FXML. */
    @FXML private TextField VisiblePasswordField2;

    /**
     * Inizializza la UI della schermata di registrazione.
     * <ul>
     *   <li>Imposta icona e posizionamento del pulsante “indietro”.</li>
     *   <li>Configura l’effetto hover su {@code AccediTextField} (sottolineatura/cursore).</li>
     *   <li>Inizializza i campi password/show-hide tramite i metodi ereditati ({@code initP1()}, {@code initP2()}).</li>
     *   <li>Registra l’handler di chiusura finestra (terminazione applicazione).</li>
     * </ul>
     * Eseguito automaticamente dal loader FXML sul JavaFX Application Thread.
     */
    @FXML private void initialize() {
        GoBackButton.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        GoBackButton.setAlignment(Pos.TOP_LEFT);
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

    /**
     * Torna alla schermata HOME dell’applicazione.
     * <p>Invoca {@code CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null)}.</p>
     * @see CliUtil
     * @see FXMLtype#HOME
     */
    @FXML private void GoBackMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null);
    }

    /**
     * Apre la schermata di login.
     * <p>Invoca {@code CliUtil.getInstance().buildStage(FXMLtype.LOGIN, null, null)}.</p>
     * @see CliUtil
     * @see FXMLtype#LOGIN
     */
    @FXML private void GoToLoginPage() {
        CliUtil.getInstance().buildStage(FXMLtype.LOGIN, null, null);
    }

    /**
     * Esegue la registrazione utente.
     * <p>
     * Legge e normalizza i campi (trim, maiuscole/minuscole), valida i requisiti
     * minimi (campi obbligatori, formato email, formato CF, corrispondenza/validità password,
     * vincoli su username) e invia la richiesta al servizio applicativo.
     * </p>
     * <p>
     * In caso di successo mostra una conferma e naviga alla HOME; in caso di errore
     * mostra avvisi specifici (email/username/CF già usati, campi non validi).
     * </p>
     * @implNote Usa {@link Pattern} per validare email e codice fiscale; gestisce il {@link RegToken}
     *           restituito dal servizio per distinguere i casi di conflitto.
     */
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

    /**
     * Gestisce il tasto Invio durante la compilazione del form.
     * Se l’utente preme “Enter”, invoca {@link #TryReg()}.
     *
     * @param keyEvent evento tastiera generato dal controllo attivo
     */
    @FXML private void RegAction(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            TryReg();
        }
    }

    /** @return il primo {@link PasswordField} gestito da {@link PasswordEngine}. */
    @Override
    protected PasswordField getPasswordField1() {
        return PasswordField1;
    }

    /** @return il secondo {@link PasswordField} (conferma), gestito da {@link PasswordEngine}. */
    @Override
    protected PasswordField getPasswordField2() {
        return PasswordField2;
    }

    /** @return il {@link TextField} alternativo al primo password field in modalità “mostra password”. */
    @Override
    protected TextField getVisiblePasswordField1() {
        return VisiblePasswordField1;
    }

    /** @return il {@link TextField} alternativo al secondo password field in modalità “mostra password”. */
    @Override
    protected TextField getVisiblePasswordField2() {
        return VisiblePasswordField2;
    }

    /** @return il pulsante associato al toggle mostra/nascondi del primo campo password. */
    @Override
    protected Button getButton1() {
        return ShowP1Button;
    }

    /** @return il pulsante associato al toggle mostra/nascondi del secondo campo password. */
    @Override
    protected Button getButton2() {
        return ShowP2Button;
    }
}
