package bookrecommender.server;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ServerConfigController {
    public TextField portField;
    public Button testPortButton;
    public TextField dbUrlField;
    public TextField dbUserField;
    public PasswordField dbPasswordField;
    public Button testDbButton;
    public Button confirmButton;
    public Label testLabel;

    private boolean portOk = false;
    private boolean dbOk   = false;

    @FXML
    private void initialize() {
        confirmButton.setDisable(true);
        testLabel.setText("Test porta e DB per iniziare");
    }

    @FXML
    private void testPort() {
        String txt = portField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(txt);
            if (port < 0 || port > 65535)
            {
                testLabel.setStyle("-fx-text-fill: red;");
                testLabel.setText("Porta deve essere tra 0 e 65535!");
                portOk = false;
                updateConfirmState();
                return;
            }
        } catch (NumberFormatException ex) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Porta non valida!");
            portOk = false;
            updateConfirmState();
            return;
        }

        ServerUtil.getInstance().setPort(port);

        if (ServerUtil.isTcpPortAvailable()) {
            testLabel.setStyle("-fx-text-fill: green;");
            testLabel.setText("Porta " + port + " libera");
            portOk = true;
        } else {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Porta " + port + " già in uso");
            portOk = false;
        }
        updateConfirmState();
    }

    @FXML
    private void testDB() {
        String url  = dbUrlField.getText().trim();
        String user = dbUserField.getText().trim();
        String pass = dbPasswordField.getText();

        if (url.isEmpty() || user.isEmpty()) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("URL e utente DB obbligatori");
            dbOk = false;
            updateConfirmState();
            return;
        }
        ServerUtil.getInstance().setDBManager();
        if (ServerUtil.getInstance().tryDBConnection(url, user, pass)) {
            testLabel.setStyle("-fx-text-fill: green;");
            testLabel.setText("Connessione al DB riuscita");
            dbOk = true;
        } else {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Connessione al DB fallita");
            dbOk = false;
        }
        updateConfirmState();
    }

    @FXML
    private void confirm() {
        if (!portOk || !dbOk) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Testa prima porta e DB");
            return;
        }
        ServerUtil.getInstance().setServer();
        ServerUtil.getInstance().loadFXML("/bookrecommender/server/fxml/ServerControl.fxml","SERVER");
    }

    private void updateConfirmState() {
        confirmButton.setDisable(!(portOk && dbOk));
    }

    public void resetLabel() {
        testLabel.setText("");
    }
}