package bookrecommender.client.ui;

import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.util.PasswordEngine;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.rmi.RemoteException;

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

    public void initialize() {
        goBackButton.setGraphic(IMGtype.INDIETRO.getImageView(40,40));
        labelErrore.setVisible(false);
        cambiaPasswordButton.setDisable(true);
        PasswordField1.requestFocus();
        PasswordField1.textProperty().addListener((observable, oldValue, newValue) -> validatePasswords());
        PasswordField2.textProperty().addListener((observable, oldValue, newValue) -> validatePasswords());
        initP1();
        initP2();
    }

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
                } catch (RemoteException e) {
                    CliUtil.getInstance().createAlert("Errore di rete", "Impossibile cambiare la password").showAndWait();
                }
                CliUtil.getInstance().buildStage(FXMLtype.ACCOUNT,null,null);
            }
        } else {
            labelErrore.setText("Le password non corrispondono");
            labelErrore.setVisible(true);
        }
    }

    @FXML private void goBackAreaRiservata() {
        CliUtil.getInstance().buildStage(FXMLtype.ACCOUNT, null, null);
    }

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
