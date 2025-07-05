package bookrecommender.client;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.rmi.RemoteException;

/**
 * Controller JavaFX per l'area riservata dell'utente autenticato.
 * <p>
 * Consente di:
 * <ul>
 *     <li>Accedere alla ricerca avanzata dei libri</li>
 *     <li>Gestire le proprie librerie personali</li>
 *     <li>Effettuare il logout</li>
 * </ul>
 * In fase di chiusura della finestra, viene effettuata automaticamente la disconnessione dell'utente dal server remoto.
 * </p>
 */
public class AreaRiservataController {
    /** Bottone per accedere alla ricerca avanzata dei libri */
    public Button BottoneCercaLibroAvanzato;

    /** Bottone per aprire la sezione di gestione delle librerie */
    public Button BottoneVisualizzaLibrerie;

    /** Bottone per effettuare il logout e tornare alla schermata principale */
    public Button BottoneLogOut;

    /**
     * Inizializza il comportamento della finestra.
     * <p>
     * Registra un handler sulla chiusura del {@link Stage} principale per
     * effettuare correttamente il logout e terminare l'applicazione.
     * </p>
     */
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

    /**
     * Apre la finestra per effettuare una ricerca avanzata dei libri.
     */
    public void OpenCercaLibroAvanzato() {
        CliUtil.getInstance().buildStage(FXMLtype.CERCA_AVANZATO, null,null);
    }

    /**
     * Apre la schermata di gestione delle librerie dell'utente.
     */
    public void OpenVisualizzaLibrerie() {
        CliUtil.getInstance().buildStage(FXMLtype.GESTIONELIBRERIE,null, null);
    }

    /**
     * Esegue il logout dell'utente corrente.
     * <p>
     * Se il logout ha successo, mostra un messaggio di conferma e reindirizza l'utente alla schermata iniziale.
     * In caso contrario, mostra un messaggio di errore.
     * </p>
     */
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