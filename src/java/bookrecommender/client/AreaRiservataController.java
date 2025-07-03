package bookrecommender.client;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class AreaRiservataController {
    public Button BottoneCercaLibroAvanzato;
    public Button BottoneVisualizzaLibrerie;
    public Button BottoneLogOut;

    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) BottoneLogOut.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                try {
                    CliUtil.getInstance().getLogRegService().LogOut(CliUtil.getInstance().getCurrentToken());
                }catch (RemoteException ignored) {}
                Platform.exit();
                System.exit(0);
            });
        });
    }

    public void OpenCercaLibroAvanzato() {
        CliUtil.getInstance().buildStage(FXMLtype.CERCA_AVANZATO, null,null);
    }

    public void OpenVisualizzaLibrerie() {
        CliUtil.getInstance().buildStage(FXMLtype.GESTIONELIBRERIE,null, null);
    }

    public void LogOut(){
        try {
            if (CliUtil.getInstance().getLogRegService().LogOut(CliUtil.getInstance().getCurrentToken())) {
                CliUtil.getInstance().setCurrentToken(null);
                CliUtil.getInstance().createConfirmation("Logout effettuato", "Sei stato disconnesso con successo.", false).showAndWait();
            }
            else
                CliUtil.getInstance().createAlert("Errore di Logout", "Si è verificato un errore durante il logout");
        }catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore di Logout", e.getMessage());
        }
        CliUtil.getInstance().setCurrentToken(null);
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null);
    }
}