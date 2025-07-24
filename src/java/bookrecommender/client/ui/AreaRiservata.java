package bookrecommender.client.ui;

import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.enums.FXMLtype;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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
public class AreaRiservata {
    /** Bottone per accedere alla ricerca avanzata dei libri */
    public Button BottoneCercaLibroAvanzato;

    /** Bottone per aprire la sezione di gestione delle librerie */
    public Button BottoneVisualizzaLibrerie;

    /** Bottone per effettuare il logout e tornare alla schermata principale */
    public Button BottoneLogOut;

    /** Bottone per accedere alle impostazioni dell'account */
    public Button impostazioniButton;
    /** Label per mostare etichetta bottone Impostazioni */
    public Label showImpostazioniLabel;
    /** Label per mostrare etichetta bottone Logout */
    public Label showLogoutLabel;


    /**
     * Inizializza il comportamento della finestra.
     * <p>
     * Registra un handler sulla chiusura del {@link Stage} principale per
     * effettuare correttamente il logout e terminare l'applicazione.
     * </p>
     */


    public void initialize() {
        impostazioniButton.setGraphic(IMGtype.IMPOSTAZIONI.getImageView(40,40));
        impostazioniButton.setAlignment(Pos.TOP_RIGHT);
        BottoneLogOut.setGraphic(IMGtype.LOGOUT.getImageView(40,40));
        BottoneLogOut.setAlignment(Pos.TOP_RIGHT);
        BottoneLogOut.setPadding(new javafx.geometry.Insets(3, 3, 3, 3));
        showImpostazioniLabel.setVisible(false);
        showLogoutLabel.setVisible(false);
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
        CliUtil.getInstance().LogOut();
    }

    public void openImpostazioni() {
        CliUtil.getInstance().buildStage(FXMLtype.ACCOUNT, null,null);
    }

    public void showLogoutLabel(MouseEvent mouseEvent) {
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
            showLogoutLabel.setVisible(true);
        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED) {
            showLogoutLabel.setVisible(false);
        }
    }

    public void showImpostazioniLabel(MouseEvent mouseEvent) {
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_ENTERED) {
            showImpostazioniLabel.setVisible(true);
        } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_EXITED) {
            showImpostazioniLabel.setVisible(false);
        }
    }

}