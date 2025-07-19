package bookrecommender.client.listener;

import bookrecommender.client.util.CliUtil;
import bookrecommender.common.interfaces.ServerListener;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Implementazione client dell'interfaccia {@link ServerListener}, utilizzata
 * per ricevere notifiche dal server in caso di spegnimento imminente.
 * <p>
 * Quando il server sta per spegnersi, questa classe chiude tutte le finestre
 * aperte dell'applicazione client e mostra un dialogo informativo.
 * L'utente può scegliere se tentare una riconnessione oppure uscire.
 * </p>
 * <p>
 * Questa classe è registrata come oggetto remoto tramite RMI.
 */
public class ClientListener extends UnicastRemoteObject implements ServerListener {
    /**
     * Costruttore della classe {@code ClientListener}.
     * <p>
     * Registra l'oggetto come remoto tramite {@link UnicastRemoteObject}.
     * </p>
     *
     * @throws RemoteException in caso di errore nella registrazione RMI
     */
    public ClientListener() throws RemoteException {
        super();
    }

    /**
     * Metodo richiamato remotamente dal server quando sta per terminare l'esecuzione.
     * <p>
     * Chiude tutte le finestre aperte del client e mostra un avviso
     * all’utente con la possibilità di:
     * <ul>
     *   <li>Tentare una riconnessione soft con {@code CliUtil.softRestart()}</li>
     *   <li>Uscire completamente dall'applicazione</li>
     * </ul>
     * L'interfaccia utente viene aggiornata tramite il thread JavaFX.
     * </p>
     *
     * @throws RemoteException in caso di errore remoto (mai lanciata localmente)
     */
    @Override
    public void serverWillStop() throws RemoteException {
        Platform.runLater(() -> {
            new ArrayList<>(Window.getWindows()).forEach(w -> {
                if (w instanceof Stage s) {
                    s.close();
                }
            });

            // Crea dialogo informativo
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Il server sta per chiudere.\nVuoi ritentare la connessione?", ButtonType.OK, ButtonType.CLOSE);

            // Imposta icona personalizzata
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_info_icon.png"))));
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            alert.setGraphic(imageView);
            stage.getIcons().setAll(imageView.getImage());
            alert.setTitle("Server Shutdown");
            alert.showAndWait();

            // Gestione della scelta dell’utente
            if(alert.getResult() == ButtonType.OK) {
                CliUtil.getInstance().softRestart();
            } else {
                Platform.exit();
                System.exit(0);
            }
        });
    }
}