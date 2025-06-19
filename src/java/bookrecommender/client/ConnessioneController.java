package bookrecommender.client;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class ConnessioneController {
    public TextField hostField;
    public TextField portField;
    public Button confermaButton;
    public Button testButton;
    public Label testLabel;

    public void initialize() {
        hostField.setText("localhost");
        portField.setText("1099");
        testLabel.setText("");
        Platform.runLater(() -> {
            Stage stage = (Stage) testButton.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    public void conferma() {
        String host = hostField.getText();
        String port = portField.getText();

        if (host.isEmpty() || port.isEmpty()) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Inserire host e porta.");
            return;
        }

        try {
            int portNumber = Integer.parseInt(port);
            CliUtil.getInstance().setServer(host, portNumber);
            if(CliUtil.getInstance().testConnection()) {
                Stage stage = (Stage) confermaButton.getScene().getWindow();
                stage.close();
                if (CliUtil.getInstance().createConfirmation("Connessione riuscita", "La connessione al server è stata stabilita con successo. Continuare?", true).showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    CliUtil.getInstance().setMonitorService();
                    CliUtil.getInstance().buildStage(FXMLtype.HOME, null);
                } else {
                    Platform.exit();
                    System.exit(0);
                }
            }else{
                testLabel.setStyle("-fx-text-fill: red;");
                testLabel.setText("Connessione fallita");
            }
        } catch (NumberFormatException e) {
            testLabel.setText("Numero di porta non valido.");
        }

    }

    public void test() {
        String host = hostField.getText();
        String port = portField.getText();

        if (host.isEmpty() || port.isEmpty()) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Inserire host e porta.");
            return;
        }

        try {
            int portNumber = Integer.parseInt(port);
            CliUtil.getInstance().setServer(host, portNumber);
            if(CliUtil.getInstance().testConnection()) {
                testLabel.setStyle("-fx-text-fill: green;");
                testLabel.setText("Connessione riuscita.");
            } else {
                testLabel.setStyle("-fx-text-fill: red;");
                testLabel.setText("Connessione fallita.");
            }
        } catch (NumberFormatException e) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Numero di porta non valido.");
        }
    }

    public void resetLabel() {
        testLabel.setText("");
        testLabel.setStyle("-fx-text-fill: black;");
    }

}
