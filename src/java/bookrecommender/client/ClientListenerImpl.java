package bookrecommender.client;

import bookrecommender.common.ServerListener;
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

public class ClientListenerImpl extends UnicastRemoteObject implements ServerListener {
    public ClientListenerImpl() throws RemoteException {
        super();
    }

    @Override
    public void serverWillStop() throws RemoteException {
        Platform.runLater(() -> {
            new ArrayList<>(Window.getWindows()).forEach(w -> {
                if (w instanceof Stage s) {
                    s.close();
                }
            });
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Il server sta per chiudere.\nVuoi ritentare la connessione?", ButtonType.OK, ButtonType.CLOSE);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_info_icon.png"))));
            imageView.setFitHeight(48);
            imageView.setFitWidth(48);
            alert.setGraphic(imageView);
            stage.getIcons().setAll(imageView.getImage());
            alert.setTitle("Server Shutdown");
            alert.showAndWait();
            if(alert.getResult() == ButtonType.OK) {
                CliUtil.getInstance().softRestart();
            } else {
                Platform.exit();
                System.exit(0);
            }
        });
    }
}