package bookrecommender.server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

public class ServerConfigController {
    public TextField portField;
    public Button testPortButton;
    public TextField dbUrlField;
    public TextField dbUserField;
    public PasswordField dbPasswordField;
    public Button testDbButton;
    public Button confirmButton;
    public Label testLabel;

    @FXML
    private void initialize() {
        testLabel.setText("");
        ServerUtil.getInstance().setDBManager();
        Platform.runLater(() -> {
            Stage stage = (Stage) testLabel.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        });
    }

    @FXML
    private void testPort() {
        String txt = portField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(txt);
            if (port < 0 || port > 65535) {
                testLabel.setStyle("-fx-text-fill: red;");
                testLabel.setText("Porta deve essere tra 0 e 65535!");
                return;
            }
        } catch (NumberFormatException ex) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Porta non valida!");
            return;
        }

        if (ServerUtil.getInstance().isTcpPortAvailable(port)) {
            testLabel.setStyle("-fx-text-fill: green;");
            testLabel.setText("Porta " + port + " libera");
        } else {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Porta " + port + " già in uso");
        }
    }

    @FXML
    private void testDB() {
        String url  = dbUrlField.getText().trim();
        String user = dbUserField.getText().trim();
        String pass = dbPasswordField.getText();

        if (url.isEmpty() || user.isEmpty()) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("URL e utente DB obbligatori");
            return;
        }
        if (ServerUtil.getInstance().tryConnectToDb(url, user, pass)) {
            testLabel.setStyle("-fx-text-fill: green;");
            testLabel.setText("Connessione al DB riuscita");
        } else {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Connessione al DB fallita");
        }
    }

    @FXML
    private void confirm() {
        String url  = dbUrlField.getText().trim();
        String user = dbUserField.getText().trim();
        String pass = dbPasswordField.getText();
        String portatxt = portField.getText().trim();
        int port;
        try{
            port = Integer.parseInt(portatxt);
        }catch(NumberFormatException ex){
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Porta non valida!");
            return;
        }
        if(port < 0 || port > 65535) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Porta deve essere tra 0 e 65535!");
            return;
        }
        if (url.isEmpty() || user.isEmpty()) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("URL e utente DB obbligatori");
            return;
        }
        if (!ServerUtil.getInstance().tryConnectToDb(url, user, pass) || !ServerUtil.getInstance().isTcpPortAvailable(port)) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Testa prima porta e DB");
            return;
        }
        if(ServerUtil.getInstance().connectToDb(url, user, pass)) {
            if(ServerUtil.getInstance().setServer(port)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Successo");
                alert.setContentText("Database e server configurati correttamente");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/server/icons/alert_confirmation_icon.png"))));
                imageView.setFitHeight(48);
                imageView.setFitWidth(48);
                alert.setGraphic(imageView);
                stage.getIcons().setAll(imageView.getImage());
                Stage mainStage = (Stage) confirmButton.getScene().getWindow();
                mainStage.close();
                alert.showAndWait();
            }else {
                testLabel.setStyle("-fx-text-fill: red;");
                testLabel.setText("Errore nella configurazione del server");
            }
        } else {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Errore nella configurazione del database");
        }
    }

    public void resetLabel() {
        testLabel.setText("");
    }
}