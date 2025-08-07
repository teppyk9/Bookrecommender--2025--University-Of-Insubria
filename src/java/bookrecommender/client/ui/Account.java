package bookrecommender.client.ui;

import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.util.PasswordEngine;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Controller JavaFX per la schermata di gestione dell'account utente.
 * <p>
 * Permette la visualizzazione e modifica di username, email e password.
 * Integra meccanismi di validazione e conferma per ogni modifica effettuata.
 * Estende {@link PasswordEngine} per gestire la visibilità del campo password.
 */
public class Account extends PasswordEngine {
    @FXML private Label nomeLabel;
    @FXML private Label cognomeLabel;
    @FXML private Label CFLabel;
    @FXML private TextField emailTextField;
    @FXML private TextField usernameTextField;
    @FXML private PasswordField PasswordField1;
    @FXML private TextField VisiblePasswordField1;
    @FXML private Button ShowP1Button;
    @FXML private Button changeUButton;
    @FXML private Button changeEButton;
    @FXML private Button GoBackButton_MainMenu;
    @FXML private Button BottoneLogOut;
    @FXML private Button changePButton;

    private String oldUsername = "";
    private String oldEmail = "";
    private Tooltip Uinfo;
    private Tooltip Einfo;
    private boolean isUsernameChanged = false;
    private boolean isEmailChanged = false;
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    /**
     * Inizializza la schermata caricando i dati dell'utente corrente,
     * impostando le icone e le proprietà dei campi testuali.
     * <p>
     * Registra un handler di chiusura
     * sullo {@link Stage} corrente per effettuare il logout automatico
     * alla chiusura della finestra.
     */
    @FXML private void initialize(){
        GoBackButton_MainMenu.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        GoBackButton_MainMenu.setAlignment(Pos.TOP_LEFT);
        BottoneLogOut.setGraphic(IMGtype.LOGOUT.getImageView(40,40));
        BottoneLogOut.setAlignment(Pos.TOP_RIGHT);
        BottoneLogOut.setPadding(new javafx.geometry.Insets(3, 3, 3, 3));
        changeEButton.setGraphic(IMGtype.EDIT.getImageView(25,25));
        changeUButton.setGraphic(IMGtype.EDIT.getImageView(25,25));
        changePButton.setGraphic(IMGtype.EDIT.getImageView(25,25));
        ShowP1Button.setGraphic(IMGtype.VISIBLE_PASSWORD.getImageView(20,20));
        PasswordField1.setEditable(false);
        VisiblePasswordField1.setEditable(false);
        emailTextField.setEditable(false);
        usernameTextField.setEditable(false);
        Uinfo = new Tooltip("Lo username deve essere tra 5 e 20 caratteri.");
        Uinfo.setAutoHide(true);
        Einfo = new Tooltip("L'email non è valida.");
        Einfo.setAutoHide(true);
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> validateEmail());
        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> validateUsername());
        initP1();
        List<String> userInfo = List.of();
        try {
            userInfo = CliUtil.getInstance().getLogRegService().getUserInfo(CliUtil.getInstance().getCurrentToken());
        }catch (Exception e){
            CliUtil.getInstance().LogOut(e);
        }
        if (userInfo.isEmpty() || userInfo.size() < 6) {
            CliUtil.getInstance().createAlert("Errore", "Impossibile recuperare le informazioni dell'utente").showAndWait();
            CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null, null);
        }
        usernameTextField.setText(userInfo.get(0));
        oldUsername = userInfo.get(0);
        nomeLabel.setText(userInfo.get(1));
        cognomeLabel.setText(userInfo.get(2));
        CFLabel.setText(userInfo.get(3));
        emailTextField.setText(userInfo.get(4));
        oldEmail = userInfo.get(4);
        PasswordField1.setText(userInfo.get(5));
        Platform.runLater(() -> {
            Stage stage = (Stage) BottoneLogOut.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                CliUtil.getInstance().LogOut(null);
                Platform.exit();
                System.exit(0);
            });
        });
    }

    /**
     * Elimina l'account dell'utente dopo conferma.
     * Se l'eliminazione ha successo, l'utente viene disconnesso e reindirizzato alla home.
     */
    @FXML private void eliminaAccount() {
        if(CliUtil.getInstance().createConfirmation("Conferma", "Sei sicuro di voler eliminare l'account?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
            try {
                if (CliUtil.getInstance().getLogRegService().eliminaAccount(CliUtil.getInstance().getCurrentToken())) {
                    CliUtil.getInstance().setCurrentToken(null);
                    CliUtil.getInstance().createConfirmation("Successo", "Account eliminato", false).showAndWait();
                    CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null);
                } else {
                    CliUtil.getInstance().createAlert("Errore", "Impossibile eliminare l'account").showAndWait();
                }
            }catch (Exception e) {
                CliUtil.getInstance().LogOut(e);
            }
        }else{
            CliUtil.getInstance().createAlert("Operazione annullata", "Eliminazione account annullata").showAndWait();
        }
    }

    /**
     * Reindirizza l'utente alla schermata per la modifica della password.
     */
    @FXML private void changePassword() {
        CliUtil.getInstance().buildStage(FXMLtype.CAMBIAPASSWORD,null,null);
    }

    /**
     * Abilita o conferma la modifica dello username.
     * Se lo username è valido e differente da quello attuale, tenta di aggiornare il valore nel server.
     */
    @FXML private void changeUsername() {
        if(!isUsernameChanged) {
            changeUButton.setGraphic(IMGtype.SAVE.getImageView(24,24));
            changeUButton.setDisable(true);
            usernameTextField.setEditable(true);
            isUsernameChanged = true;
        }else{
            String newUsername = usernameTextField.getText().trim().toLowerCase();
            if (newUsername.equals(oldUsername)) {
                CliUtil.getInstance().createAlert("Errore", "Lo username non è cambiato.").showAndWait();
                isUsernameChanged = false;
                usernameTextField.setEditable(false);
                changeUButton.setGraphic(IMGtype.EDIT.getImageView(24,24));
                changeUButton.setDisable(false);
                return;
            }
            try {
                if (CliUtil.getInstance().getLogRegService().cambiaUsername(CliUtil.getInstance().getCurrentToken(), newUsername)) {
                    oldUsername = newUsername;
                    CliUtil.getInstance().createConfirmation("Successo", "Username cambiato con successo", false).showAndWait();
                    isUsernameChanged = false;
                    usernameTextField.setEditable(false);
                    changeUButton.setGraphic(IMGtype.EDIT.getImageView(24,24));
                    changeUButton.setDisable(false);
                } else {
                    CliUtil.getInstance().createAlert("Errore", "Impossibile cambiare lo username").showAndWait();
                }
            } catch (Exception e) {
                CliUtil.getInstance().LogOut(e);            }
        }
    }

    /**
     * Abilita o conferma la modifica dell'email.
     * Se l'email è valida e differente da quella attuale, tenta di aggiornare il valore nel server.
     */
    @FXML private void changeEmail() {
        if(!isEmailChanged) {
            changeEButton.setGraphic(IMGtype.SAVE.getImageView(24,24));
            changeEButton.setDisable(true);
            emailTextField.setEditable(true);
            isEmailChanged = true;
        }else{
            String newEmail = emailTextField.getText().trim().toLowerCase();
            if (newEmail.equals(oldEmail)) {
                CliUtil.getInstance().createAlert("Errore", "L'email non è cambiata.").showAndWait();
                isEmailChanged = false;
                emailTextField.setEditable(false);
                changeEButton.setGraphic(IMGtype.EDIT.getImageView(24,24));
                changeEButton.setDisable(false);
                return;
            }
            if( !EMAIL_PATTERN.matcher(newEmail).matches()) {
                CliUtil.getInstance().createAlert("Errore", "L'email non è valida.").showAndWait();
                return;
            }
            try {
                if (CliUtil.getInstance().getLogRegService().cambiaEmail(CliUtil.getInstance().getCurrentToken(), newEmail)) {
                    oldEmail = newEmail;
                    CliUtil.getInstance().createConfirmation("Successo", "Email cambiata con successo", false).showAndWait();
                    isEmailChanged = false;
                    emailTextField.setEditable(false);
                    changeEButton.setGraphic(IMGtype.EDIT.getImageView(24,24));
                    changeEButton.setDisable(false);
                } else {
                    CliUtil.getInstance().createAlert("Errore", "Impossibile cambiare l'email").showAndWait();
                }
            } catch (Exception e) {
                CliUtil.getInstance().LogOut(e);            }
        }
    }

    /**
     * Reindirizza l'utente alla schermata principale dell'area riservata.
     */
    @FXML private void GoToMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null, null);
    }

    /**
     * Gestisce il logout dell'utente, chiudendo la sessione corrente e reindirizzando alla schermata home.
     * Se il logout fallisce, viene visualizzato un messaggio di errore.
     */
    @FXML private void LogOut() {
        CliUtil.getInstance().LogOut(null);
    }

    /**
     * Valida lo username inserito verificando che abbia tra 5 e 20 caratteri.
     * Se invalido, mostra un tooltip e disabilita il pulsante di conferma.
     */
    private void validateUsername(){
        String newU = usernameTextField.getText().trim().toLowerCase();
        if (newU.length() < 5 || newU.length() > 20) {
            usernameTextField.setTooltip(Uinfo);
            changeUButton.setDisable(true);
        }else{
            usernameTextField.setTooltip(null);
            changeUButton.setDisable(false);
        }
    }

    /**
     * Valida l'email inserita confrontandola con un pattern regex.
     * Se invalida, mostra un tooltip e disabilita il pulsante di conferma.
     */
    private void validateEmail() {
        String newE = emailTextField.getText().trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(newE).matches()) {
            emailTextField.setTooltip(Einfo);
            changeEButton.setDisable(true);
        } else {
            emailTextField.setTooltip(null);
            changeEButton.setDisable(false);
        }
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il campo password principale non visibile.</p>
     */
    @Override
    protected PasswordField getPasswordField1() {
        return PasswordField1;
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il campo password secondario non visibile, in questo caso null.</p>
     */
    @Override
    protected PasswordField getPasswordField2() {
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il campo password visibile principale.</p>
     */
    @Override
    protected TextField getVisiblePasswordField1() {
        return VisiblePasswordField1;
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il campo password visibile secondario, in questo caso null.</p>
     */
    @Override
    protected TextField getVisiblePasswordField2() {
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il pulsante per mostrare/nascondere la password principale.</p>
     */
    @Override
    protected Button getButton1() {
        return ShowP1Button;
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il pulsante per mostrare/nascondere la password secondaria, in questo caso null.</p>
     */
    @Override
    protected Button getButton2() {
        return null;
    }
}
