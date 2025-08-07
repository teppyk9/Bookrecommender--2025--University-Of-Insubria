package bookrecommender.client.ui;

import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.util.PasswordEngine;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

/**
 * Controller JavaFX per la schermata di modifica della password dell'utente autenticato.
 * <p>
 * Estende {@link PasswordEngine} per fornire il supporto alla visualizzazione
 * e gestione delle password in forma visibile o nascosta.
 * </p>
 * Consente all’utente di:
 * <ul>
 *     <li>Inserire e confermare una nuova password</li>
 *     <li>Validare la corrispondenza tra i due campi</li>
 *     <li>Confermare la modifica con apposito avviso</li>
 * </ul>
 */
public class CambiaPassword extends PasswordEngine{
    @FXML private Button goBackButton;
    @FXML private Button ShowP1Button;
    @FXML private Button ShowP2Button;
    @FXML private TextField VisiblePasswordField1;
    @FXML private TextField VisiblePasswordField2;
    @FXML private PasswordField PasswordField1;
    @FXML private PasswordField PasswordField2;
    @FXML private Label labelErrore;
    @FXML private Button cambiaPasswordButton;


    /**
     * Inizializza i componenti della GUI, imposta icone e listener.
     * <p>
     * Nasconde il messaggio d’errore inizialmente, disabilita il pulsante
     * di conferma e configura i controlli per la gestione della password visibile/nascosta.
     * </p>
     */
    @FXML private void initialize() {
        goBackButton.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        goBackButton.setAlignment(Pos.TOP_LEFT);
        labelErrore.setVisible(false);
        cambiaPasswordButton.setDisable(true);
        PasswordField1.requestFocus();
        PasswordField1.textProperty().addListener((observable, oldValue, newValue) -> validatePasswords());
        PasswordField2.textProperty().addListener((observable, oldValue, newValue) -> validatePasswords());
        initP1();
        initP2();
    }

    /**
     * Gestisce la logica per cambiare la password dell’utente.
     * <p>
     * Se i due campi password corrispondono, viene mostrata una finestra di conferma
     * e, in caso positivo, inviata la richiesta al server. Al termine,
     * l’utente viene reindirizzato alla schermata account.
     * </p>
     * In caso di errore o mismatch, viene mostrato un messaggio nella GUI.
     */
    @FXML private void cambiaPassword() {
        String p1 = PasswordField1.getText();
        String p2 = PasswordField2.getText();
        if (p1.equals(p2)) {
            if(CliUtil.getInstance().createConfirmation("Conferma", "Sei sicuro di voler cambiare la password?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
                cambiaPasswordButton.setDisable(true);
                try {
                    if (CliUtil.getInstance().getLogRegService().cambiaPassword(CliUtil.getInstance().getCurrentToken(), p1)) {
                        CliUtil.getInstance().createConfirmation("Successo", "Password cambiata con successo", false).showAndWait();
                    } else {
                        CliUtil.getInstance().createAlert("Errore", "Impossibile cambiare la password").showAndWait();
                    }
                } catch (Exception e) {
                    CliUtil.getInstance().LogOut(e);
                }
                CliUtil.getInstance().buildStage(FXMLtype.ACCOUNT,null,null);
            }
        } else {
            labelErrore.setText("Le password non corrispondono");
            labelErrore.setVisible(true);
        }
    }

    /**
     * Gestisce la logica per cambiare la password dell’utente.
     * <p>
     * Se i due campi password corrispondono, viene mostrata una finestra di conferma
     * e, in caso positivo, inviata la richiesta al server. Al termine,
     * l’utente viene reindirizzato alla schermata account.
     * </p>
     * In caso di errore o mismatch, viene mostrato un messaggio nella GUI.
     */
    @FXML private void goBackAreaRiservata() {
        CliUtil.getInstance().buildStage(FXMLtype.ACCOUNT, null, null);
    }

    /**
     * Verifica che le due password inserite coincidano e rispettino
     * i requisiti minimi (almeno 8 caratteri).
     * <p>
     * In base al risultato, abilita o disabilita il pulsante per il cambio password
     * e mostra eventuali messaggi d’errore.
     * </p>
     */
    private void validatePasswords() {
        String p1 = PasswordField1.getText();
        String p2 = PasswordField2.getText();
        boolean match = p1 != null && p1.equals(p2) && p2.length() >= 8;
        if (match) {
            labelErrore.setVisible(false);
            cambiaPasswordButton.setDisable(false);
        } else {
            labelErrore.setText("Le password non corrispondono");
            labelErrore.setVisible(true);
            cambiaPasswordButton.setDisable(true);
        }
    }

    /**
     * Esegue la conferma della modifica se l’utente preme il tasto Invio.
     *
     * @param keyEvent evento associato alla pressione di un tasto
     */
    @FXML private void enterControl(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            cambiaPassword();
        }
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il primo campo {@link PasswordField} (nuova password).</p>
     */
    @Override
    protected PasswordField getPasswordField1() {
        return PasswordField1;
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il secondo campo {@link PasswordField} (conferma password).</p>
     */
    @Override
    protected PasswordField getPasswordField2() {
        return PasswordField2;
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il primo campo {@link TextField} visibile (nuova password).</p>
     */
    @Override
    protected TextField getVisiblePasswordField1() {
        return VisiblePasswordField1;
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il secondo campo {@link TextField} visibile (conferma password).</p>
     */
    @Override
    protected TextField getVisiblePasswordField2() {
        return VisiblePasswordField2;
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il pulsante associato alla visibilità del primo campo password.</p>
     */
    @Override
    protected Button getButton1() {
        return ShowP1Button;
    }

    /**
     * {@inheritDoc}
     * <p>Restituisce il pulsante associato alla visibilità del secondo campo password.</p>
     */
    @Override
    protected Button getButton2() {
        return ShowP2Button;
    }
}
