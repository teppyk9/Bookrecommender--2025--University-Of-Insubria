package bookrecommender.ui;

import bookrecommender.util.ServerUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Controller dell'interfaccia grafica di configurazione del server.
 * <p>
 * Questa classe gestisce l'interazione con il pannello FXML associato, permettendo all'utente di:
 * <ul>
 *   <li>testare la disponibilità di una porta TCP</li>
 *   <li>verificare la connessione al database</li>
 *   <li>confermare e avviare la configurazione del server</li>
 * </ul>
 * Utilizza il singleton {@link ServerUtil} per accedere alla logica di backend.
 * </p>
 */
public class ServerConfig {

    /** Campo di testo per l'inserimento del numero di porta TCP. */
    public TextField portField;

    /** Bottone per testare la disponibilità della porta. */
    public Button testPortButton;

    /** Campo di testo per l'inserimento dello username del database. */
    public TextField dbUserField;

    /** Campo per l'inserimento della password del database. */
    public PasswordField dbPasswordField;

    /** Bottone per testare la connessione al database. */
    public Button testDbButton;

    /** Bottone per confermare la configurazione. */
    public Button confirmButton;

    /** Etichetta per visualizzare messaggi di feedback all'utente. */
    public Label testLabel;

    /** Campo di testo per l'inserimento della porta del database. */
    public TextField dbPortField;

    /** Campo di testo per l'inserimento del nome del database. */
    public TextField dbNameField;

    /**
     * Metodo di inizializzazione del controller.
     * <p>
     * Pulisce l’etichetta di stato, inizializza il {@code DBManager} e imposta
     * un comportamento personalizzato alla chiusura della finestra (uscita completa).
     * Questo metodo viene invocato automaticamente dal FXMLLoader dopo
     * l'iniezione dei campi FXML.
     * </p>
     */
    public void initialize() {
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

    /**
     * Verifica se la porta TCP inserita è valida e libera.
     * <p>
     * Mostra un messaggio informativo nell'etichetta {@code testLabel},
     * colorato in base all'esito.
     * </p>
     */
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

    /**
     * Testa la connessione al database utilizzando i dati inseriti.
     * <p>
     * Verifica che i campi nome DB, porta DB e utente non siano vuoti e poi chiama
     * {@code tryConnectToDb(name, port, user, pass)} su {@link ServerUtil}.
     * </p>
     */
    @FXML
    private void testDB() {
        String name = dbNameField.getText().trim();
        String port = dbPortField.getText().trim();
        String user = dbUserField.getText().trim();
        String pass = dbPasswordField.getText();

        if (name.isEmpty() || user.isEmpty() || port.isEmpty()) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("URL e utente DB obbligatori");
            return;
        }
        if (ServerUtil.getInstance().tryConnectToDb(name, port, user, pass)) {
            testLabel.setStyle("-fx-text-fill: green;");
            testLabel.setText("Connessione al DB riuscita");
        } else {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Connessione al DB fallita");
        }
    }

    /**
     * Esegue la configurazione finale del server.
     * <p>
     * Controlla la validità di tutti i campi (porta, credenziali DB),
     * verifica le connessioni e, in caso di successo, avvia il server e chiude la finestra.
     * In caso di errore mostra un {@link Alert} o un messaggio in {@code testLabel}.
     * </p>
     */
    @FXML
    private void confirm() {
        String name = dbNameField.getText().trim();
        String portdb = dbPortField.getText().trim();
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
        if (name.isEmpty() || user.isEmpty() || portdb.isEmpty()) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("URL e utente DB obbligatori");
            return;
        }
        if (!ServerUtil.getInstance().tryConnectToDb(name, portdb, user, pass) || !ServerUtil.getInstance().isTcpPortAvailable(port)) {
            testLabel.setStyle("-fx-text-fill: red;");
            testLabel.setText("Testa prima porta e DB");
            return;
        }
        if(ServerUtil.getInstance().connectToDb(name, portdb, user, pass)) {
            if(ServerUtil.getInstance().setServer(port)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Successo");
                alert.setContentText("Database e server configurati correttamente");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/alert_confirmation_icon.png"))));
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

    /**
     * Ripristina lo stato iniziale dell’etichetta di feedback,
     * utile per cancellare eventuali messaggi di errore precedenti.
     */
    public void resetLabel() {
        testLabel.setText("");
    }
}
