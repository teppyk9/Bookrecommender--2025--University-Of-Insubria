package bookrecommender.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;

import java.rmi.RemoteException;

public class ImpostazioniController{

    //TODO: implementare anagrafica utente e cambio username + invio su password2

    @FXML private HBox firstPasswordField;
    @FXML private PasswordField PasswordField1;
    @FXML private HBox secondPasswordField;
    @FXML private PasswordField PasswordField2;
    @FXML private Label labelErrore;
    @FXML private Button eliminaAccountButton;
    @FXML private Button cambiaPasswordButton;

    private boolean isChangingPassword = false;

    public void initialize() {
        firstPasswordField.setVisible(false);
        secondPasswordField.setVisible(false);
        labelErrore.setVisible(false);

        PasswordField1.textProperty().addListener((obs, old, neu) -> validatePasswords());
        PasswordField2.textProperty().addListener((obs, old, neu) -> validatePasswords());
    }

    @FXML
    private void cambiaPassword() {
        if (!isChangingPassword) {
            isChangingPassword = true;
            eliminaAccountButton.setDisable(true);
            firstPasswordField.setVisible(true);
            secondPasswordField.setVisible(true);
            labelErrore.setVisible(false);
            cambiaPasswordButton.setDisable(true);
        }
        else {
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
                    } catch (RemoteException e) {
                        CliUtil.getInstance().createAlert("Errore di rete", "Impossibile cambiare la password").showAndWait();
                    }
                    resetPasswordUI();
                }
            } else {
                labelErrore.setText("Le password non corrispondono");
                labelErrore.setVisible(true);
            }
        }
    }

    @FXML
    private void eliminaAccount() {
        cambiaPasswordButton.setDisable(true);
        isChangingPassword = false;
        firstPasswordField.setVisible(false);
        secondPasswordField.setVisible(false);
        labelErrore.setVisible(false);
        PasswordField1.clear();
        PasswordField2.clear();

        if(CliUtil.getInstance().createConfirmation("Conferma", "Sei sicuro di voler eliminare l'account?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
            try {
                if (CliUtil.getInstance().getLogRegService().eliminaAccount(CliUtil.getInstance().getCurrentToken())) {
                    CliUtil.getInstance().setCurrentToken(null);
                    CliUtil.getInstance().createConfirmation("Successo", "Account eliminato", false).showAndWait();
                    CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null);
                } else {
                    cambiaPasswordButton.setDisable(false);
                }
            }catch (RemoteException e) {
                CliUtil.getInstance().createAlert("Errore di rete", "Impossibile eliminare l'account").showAndWait();
            }
        }
    }

    @FXML
    private void goBackAreaRiservata() {
        CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null, null);
    }

    private void validatePasswords() {
        if (!isChangingPassword) return;

        String p1 = PasswordField1.getText();
        String p2 = PasswordField2.getText();
        boolean match = p1 != null && !p1.isEmpty() && p1.equals(p2);

        if (match) {
            labelErrore.setVisible(false);
            cambiaPasswordButton.setDisable(false);
        } else {
            labelErrore.setText("Le password non corrispondono");
            labelErrore.setVisible(true);
            cambiaPasswordButton.setDisable(true);
        }
    }

    private void resetPasswordUI() {
        isChangingPassword = false;
        firstPasswordField.setVisible(false);
        secondPasswordField.setVisible(false);
        labelErrore.setVisible(false);
        PasswordField1.clear();
        PasswordField2.clear();
        eliminaAccountButton.setDisable(false);
        cambiaPasswordButton.setDisable(false);
    }
}
