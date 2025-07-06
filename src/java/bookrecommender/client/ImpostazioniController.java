package bookrecommender.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ImpostazioniController{

    @FXML private HBox firstPasswordField;
    @FXML private PasswordField PasswordField1;
    @FXML private HBox secondPasswordField;
    @FXML private PasswordField PasswordField2;
    @FXML private Label labelErrore;
    @FXML private Button eliminaAccountButton;
    @FXML private Button cambiaPasswordButton;
    @FXML private Button goBackButton;

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
                // TODO: metodo per cambiare la password
                resetPasswordUI();
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

        // TODO: metodo per eliminare l’account
        boolean success = true;

        if (success) {
            Stage stage = (Stage) eliminaAccountButton.getScene().getWindow();
            stage.close();
        } else {
            cambiaPasswordButton.setDisable(false);
        }
    }

    @FXML
    private void goBackAreaRiservata() {
        // TODO: implementa navigazione indietro
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
