package bookrecommender.client;

import javafx.scene.control.Button;
import java.rmi.RemoteException;

public class AreaRiservataController {
    public Button BottoneCercaLibroAvanzato;
    public Button BottoneVisualizzaLibrerie;
    public Button BottoneLogOut;

    public void initialize() {
    }

    public void OpenCercaLibroAvanzato() {
        CliUtil.getInstance().buildStage(FXMLtype.CERCA_AVANZATO, null);
    }

    public void OpenVisualizzaLibrerie() {
        System.out.println("OpenVisualizzaLibrerie called");
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
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null);
    }
}