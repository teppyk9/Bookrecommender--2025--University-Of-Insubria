package bookrecommender.client;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller JavaFX per la schermata di connessione al server RMI.
 * <p>
 * Permette all'utente di specificare host e porta del server e di testare o confermare la connessione.
 * Se la connessione ha esito positivo, viene inizializzata la connessione ai servizi RMI e caricata la schermata iniziale.
 * </p>
 * <p>
 * Alla chiusura della finestra, l'applicazione viene terminata.
 */
public class ConnessioneController {

    /** Campo di testo per l'inserimento dell'indirizzo host */
    public TextField hostField;

    /** Campo di testo per l'inserimento della porta */
    public TextField portField;

    /** Bottone per confermare la connessione */
    public Button confermaButton;

    /** Bottone per testare la connessione */
    public Button testButton;

    /** Etichetta usata per mostrare messaggi di errore o successo */
    public Label testLabel;

    /**
     * Metodo di inizializzazione della schermata.
     * <ul>
     *   <li>Imposta valori predefiniti per host e porta (localhost, 1099)</li>
     *   <li>Registra un'azione sulla chiusura della finestra per terminare il programma</li>
     * </ul>
     */
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

    /**
     * Gestisce il click sul bottone "Conferma".
     * <ul>
     *   <li>Valida host e porta</li>
     *   <li>Effettua il test della connessione</li>
     *   <li>In caso di successo, mostra una conferma per continuare</li>
     *   <li>In caso di rifiuto o errore, termina l'applicazione</li>
     * </ul>
     */
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
                    CliUtil.getInstance().buildStage(FXMLtype.HOME, null, null);
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

    /**
     * Gestisce il click sul bottone "Test Connessione".
     * <ul>
     *   <li>Valida host e porta</li>
     *   <li>Effettua un test di connessione al server RMI</li>
     *   <li>Aggiorna l'etichetta {@code testLabel} con il risultato</li>
     * </ul>
     */
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

    /**
     * Ripristina lo stato iniziale dell'etichetta {@code testLabel},
     * rimuovendo eventuali messaggi e colori di errore.
     */
    public void resetLabel() {
        testLabel.setText("");
        testLabel.setStyle("-fx-text-fill: black;");
    }

}
