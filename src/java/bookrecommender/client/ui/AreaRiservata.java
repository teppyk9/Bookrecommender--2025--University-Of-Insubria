package bookrecommender.client.ui;

import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller JavaFX per l'area riservata dell'utente autenticato.
 * <p>
 * Consente di:
 * <ul>
 *     <li>Accedere alla ricerca avanzata dei libri</li>
 *     <li>Gestire le proprie librerie personali</li>
 *     <li>Accedere alle impostazioni dell'account</li>
 *     <li>Effettuare il logout</li>
 * </ul>
 * Alla chiusura della finestra viene automaticamente effettuata
 * la disconnessione dell'utente dal server remoto.
 * </p>
 */
public class AreaRiservata {
    @FXML private Button BottoneLogOut;
    @FXML private Button impostazioniButton;

    /**
     * Inizializza la finestra dell'area riservata.
     * <p>
     * Imposta le icone sui pulsanti e registra un handler di chiusura
     * sullo {@link Stage} corrente per effettuare il logout automatico
     * alla chiusura della finestra.
     * </p>
     */
    @FXML private void initialize() {
        impostazioniButton.setGraphic(IMGtype.IMPOSTAZIONI.getImageView(40,40));
        impostazioniButton.setAlignment(Pos.TOP_RIGHT);
        BottoneLogOut.setGraphic(IMGtype.LOGOUT.getImageView(40,40));
        BottoneLogOut.setAlignment(Pos.TOP_RIGHT);
        BottoneLogOut.setPadding(new javafx.geometry.Insets(3, 3, 3, 3));
        Platform.runLater(() -> {
            Stage stage = (Stage) BottoneLogOut.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                CliUtil.getInstance().LogOut(null);
                Platform.exit();
                System.exit(0);
            });
        });
    }

    /**
     * Apre la finestra per effettuare una ricerca avanzata dei libri.
     */
    @FXML private void OpenCercaLibroAvanzato() {
        CliUtil.getInstance().buildStage(FXMLtype.CERCA_AVANZATO, null,null);
    }

    /**
     * Apre la schermata di gestione delle librerie dell'utente.
     */
    @FXML private void OpenVisualizzaLibrerie() {
        CliUtil.getInstance().buildStage(FXMLtype.GESTIONELIBRERIE,null, null);
    }

    /**
     * Esegue il logout dell'utente corrente.
     * <p>
     * Se il logout ha successo, mostra un messaggio di conferma e reindirizza l'utente alla schermata iniziale.
     * In caso contrario, mostra un messaggio di errore.
     * </p>
     */
    @FXML private void LogOut(){
        CliUtil.getInstance().LogOut(null);
    }

    /**
     * Apre la schermata delle impostazioni dell'account utente.
     * <p>
     * Consente all'utente di modificare le proprie informazioni e preferenze.
     * </p>
     */
    @FXML private void openImpostazioni() {
        CliUtil.getInstance().buildStage(FXMLtype.ACCOUNT, null,null);
    }
}
