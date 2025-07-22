package bookrecommender.client.ui;

import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.util.PasswordEngine;
import javafx.scene.control.*;

import java.rmi.RemoteException;
import java.util.List;

public class Account extends PasswordEngine {
    public Label nomeLabel;
    public Label cognomeLabel;
    public Label CFLabel;
    public TextField emailTextField;
    public TextField usernameTextField;
    public PasswordField PasswordField1;
    public TextField VisiblePasswordField1;
    public Button ShowP1Button;

    public Button changeUButton; //DA FARE
    public Button changeEButton; //DA FARE

    public Button GoBackButton_MainMenu;
    public Button BottoneLogOut;

    public void initialize(){
        GoBackButton_MainMenu.setGraphic(IMGtype.INDIETRO.getImageView(46,46));
        BottoneLogOut.setGraphic(IMGtype.IMPOSTAZIONI.getImageView(46,46));
        List<String> userInfo = List.of();
        try {
            userInfo = CliUtil.getInstance().getLogRegService().getUserInfo(CliUtil.getInstance().getCurrentToken());
        }catch (RemoteException e){
            CliUtil.getInstance().createAlert("Errore di rete", "Impossibile recuperare le informazioni dell'utente").showAndWait();
        }
        if (userInfo.isEmpty() || userInfo.size() < 6) {
            CliUtil.getInstance().createAlert("Errore", "Impossibile recuperare le informazioni dell'utente").showAndWait();
            CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null, null);
        }
        usernameTextField.setText(userInfo.get(0));
        nomeLabel.setText(userInfo.get(1));
        cognomeLabel.setText(userInfo.get(2));
        CFLabel.setText(userInfo.get(3));
        emailTextField.setText(userInfo.get(4));
        PasswordField1.setText(userInfo.get(5));
    }

    public void eliminaAccount() {
        if(CliUtil.getInstance().createConfirmation("Conferma", "Sei sicuro di voler eliminare l'account?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
            try {
                if (CliUtil.getInstance().getLogRegService().eliminaAccount(CliUtil.getInstance().getCurrentToken())) {
                    CliUtil.getInstance().setCurrentToken(null);
                    CliUtil.getInstance().createConfirmation("Successo", "Account eliminato", false).showAndWait();
                    CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null);
                } else {
                    CliUtil.getInstance().createAlert("Errore", "Impossibile eliminare l'account").showAndWait();
                }
            }catch (RemoteException e) {
                CliUtil.getInstance().createAlert("Errore di rete", "Impossibile eliminare l'account").showAndWait();
            }
        }else{
            CliUtil.getInstance().createAlert("Operazione annullata", "Eliminazione account annullata").showAndWait();
        }
    }

    public void changePassword() {
        CliUtil.getInstance().buildStage(FXMLtype.CAMBIAPASSWORD,null,null);
    }

    public void changeUsername() {
    }

    public void changeEmail() {
    }

    public void GoToMainMenu() {
        CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA, null, null);
    }

    public void LogOut() {
        CliUtil.getInstance().LogOut();
    }


    @Override
    protected PasswordField getPasswordField1() {
        return PasswordField1;
    }

    @Override
    protected PasswordField getPasswordField2() {
        return null;
    }

    @Override
    protected TextField getVisiblePasswordField1() {
        return VisiblePasswordField1;
    }

    @Override
    protected TextField getVisiblePasswordField2() {
        return null;
    }

    @Override
    protected Button getButton1() {
        return ShowP1Button;
    }

    @Override
    protected Button getButton2() {
        return null;
    }
}
