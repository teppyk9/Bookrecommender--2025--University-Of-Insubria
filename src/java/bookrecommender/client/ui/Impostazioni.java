package bookrecommender.client.ui;

import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.util.PasswordEngine;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.rmi.RemoteException;

public class Impostazioni extends PasswordEngine{
    @FXML private Button goBackButton;
    @FXML private Button ShowP1Button;
    @FXML private Button ShowP2Button;
    @FXML private TextField VisiblePasswordField1;
    @FXML private TextField VisiblePasswordField2;
    @FXML private HBox firstPasswordField;
    @FXML private PasswordField PasswordField1;
    @FXML private HBox secondPasswordField;
    @FXML private PasswordField PasswordField2;
    @FXML private Label labelErrore;
    @FXML private Button cambiaPasswordButton;

    private boolean isChangingPassword = false;

    public void initialize() {
        goBackButton.setGraphic(IMGtype.INDIETRO.getImageView(40,40));
        firstPasswordField.setVisible(false);
        secondPasswordField.setVisible(false);
        labelErrore.setVisible(false);

        PasswordField1.textProperty().addListener((obs, old, neu) -> validatePasswords());
        PasswordField2.textProperty().addListener((obs, old, neu) -> validatePasswords());
        initP1();
        initP2();
        ShowP1Button.setVisible(false);
        ShowP2Button.setVisible(false);
        ShowP1Button.setManaged(false);
        ShowP2Button.setManaged(false);
        ShowP1Button.setDisable(true);
        ShowP2Button.setDisable(true);
    }

    @FXML private void cambiaPassword() {
        if (!isChangingPassword) {
            isChangingPassword = true;
            firstPasswordField.setVisible(true);
            secondPasswordField.setVisible(true);
            labelErrore.setVisible(false);
            cambiaPasswordButton.setDisable(true);
            ShowP1Button.setVisible(true);
            ShowP2Button.setVisible(true);
            ShowP1Button.setManaged(true);
            ShowP2Button.setManaged(true);
            ShowP1Button.setDisable(false);
            ShowP2Button.setDisable(false);
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

    @FXML private void goBackAreaRiservata() {
        CliUtil.getInstance().buildStage(FXMLtype.ACCOUNT, null, null);
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
        cambiaPasswordButton.setDisable(false);
        ShowP1Button.setVisible(false);
        ShowP2Button.setVisible(false);
        ShowP1Button.setManaged(false);
        ShowP2Button.setManaged(false);
        ShowP1Button.setDisable(true);
        ShowP2Button.setDisable(true);
    }

    @FXML private void enterControl(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            cambiaPassword();
        }
    }

    @Override
    protected PasswordField getPasswordField1() {
        return PasswordField1;
    }

    @Override
    protected PasswordField getPasswordField2() {
        return PasswordField2;
    }

    @Override
    protected TextField getVisiblePasswordField1() {
        return VisiblePasswordField1;
    }

    @Override
    protected TextField getVisiblePasswordField2() {
        return VisiblePasswordField2;
    }

    @Override
    protected Button getButton1() {
        return ShowP1Button;
    }

    @Override
    protected Button getButton2() {
        return ShowP2Button;
    }
}
