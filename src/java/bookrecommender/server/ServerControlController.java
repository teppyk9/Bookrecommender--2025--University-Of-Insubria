package bookrecommender.server;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller JavaFX del pannello di controllo del server. Questa classe gestisce:
 *   L'inizializzazione del logging verso l'interfaccia grafica
 *   L'interruzione del server tramite bottone dedicato
 *   Il salvataggio dei log su file locale
 * È pensata per consentire il monitoraggio in tempo reale del server e la gestione interattiva
 * da parte dell'utente tramite interfaccia grafica.
 */
public class ServerControlController {
    /** Bottone per arrestare il server manualmente. */
    public Button stopServerButton;
    /** Bottone per salvare su file il contenuto dei log. */
    public Button saveLogsButton;

    /** Icona grafica usata nei popup di conferma. */
    private final ImageView confirmation_icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/server/icons/alert_confirmation_icon.png"))));
    /** Icona grafica usata nei popup di errore. */
    private final ImageView alert_icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/server/icons/alert_icon.png"))));
    /** Area in cui vengono visualizzati i log del server in tempo reale. */
    public TextFlow logFlow;
    /** ScrollPane contenente l’area di log. */
    public ScrollPane logScrollPane;


    /**
     * Inizializza il pannello di controllo del server.
     * - Imposta le dimensioni delle icone
     * - Reindirizza il logging Java verso il {@link TextFlow} con un handler personalizzato
     * - Applica un filtro per visualizzare solo log rilevanti
     * - Imposta un comportamento di chiusura sicuro per la finestra
     */
    public void initialize() {
        confirmation_icon.setFitWidth(48);
        confirmation_icon.setFitHeight(48);
        alert_icon.setFitWidth(48);
        alert_icon.setFitHeight(48);

        /* Rimuove gli handler console predefiniti */
        Logger rootLogger = Logger.getLogger("");
        Arrays.stream(rootLogger.getHandlers()).filter(h -> h instanceof ConsoleHandler).forEach(rootLogger::removeHandler);


        // Crea e configura handler personalizzato per i log grafici
        TextFlowHandler tfHandler = new TextFlowHandler(logFlow, logScrollPane);
        //Definisce i package/classi da cui accettare log
        Set<String> allowed = Set.of(
                ServerUtil.class.getName(),
                DBManager.class.getName(),
                LibInterfaceImpl.class.getName(),
                LogRegInterfaceImpl.class.getName(),
                SearchInterfaceImpl.class.getName()
        );

        tfHandler.setFilter(record -> allowed.contains(record.getLoggerName()));

        // Aggiunge handler al root logger
        rootLogger.addHandler(tfHandler);
        rootLogger.setLevel(Level.ALL);

        Logger.getLogger(ServerUtil.class.getName()).info("Pannello di controllo avviato");
        // Comportamento alla chiusura della finestra
        Platform.runLater(() -> {
            Stage stage = ServerUtil.getInstance().getPrimaryStage();
            stage.setOnCloseRequest(event -> {
                ServerUtil.getInstance().closeServer();
                Platform.exit();
                System.exit(0);
            });
        });
    }


    /**
     * Arresta il server in modo sicuro, mostrando prima una finestra di conferma.
     * Se l’utente conferma, il metodo:
     *     Chiama {@link ServerUtil#closeServer()}
     *     Chiude JavaFX con {@code Platform.exit()}
     *     Termina il processo con {@code System.exit(0)}
     */
    public void stopServer() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Uscita");
        alert.setContentText("Sei sicuro di voler fermare il server?");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        alert.setGraphic(confirmation_icon);
        stage.getIcons().setAll(confirmation_icon.getImage());
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                ServerUtil.getInstance().closeServer();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    /**
     * Salva su file il contenuto corrente dei log visualizzati nell'interfaccia.
     * L'utente sceglie il file tramite un {@link FileChooser}, con suggerimento
     * di salvataggio sul desktop. I log sono salvati in formato UTF-8.
     * In caso di successo, viene mostrata una finestra di conferma con il percorso.
     * In caso di errore I/O, viene mostrata una finestra di errore dettagliata.
     */
    public void saveLogs() {
        FileChooser fileChooser = new FileChooser();
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        if (desktop.exists() && desktop.isDirectory()) {
            fileChooser.setInitialDirectory(desktop);
        }
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("Log Files (*.log)", "*.log"),
                new FileChooser.ExtensionFilter("Markdown Files (*.md)", "*.md"),
                new FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
        );
        fileChooser.setInitialFileName("server-log.txt");
        Window owner = logFlow.getScene().getWindow();
        File file = fileChooser.showSaveDialog(owner);
        if (file != null) {
            try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
                StringBuilder sb = new StringBuilder();
                for (Node node : logFlow.getChildren()) {
                    if (node instanceof Text textNode) {
                        sb.append(textNode.getText());
                    }
                }

                writer.write(sb.toString());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Log salvati con successo");
                alert.setContentText("Log salvati in: " + file.getAbsolutePath());
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                alert.setGraphic(confirmation_icon);
                stage.getIcons().setAll(confirmation_icon.getImage());
                alert.getButtonTypes().setAll(ButtonType.OK);
                alert.showAndWait();
                writer.flush();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Errore nel salvataggio dei log:\n" + e.getMessage(), ButtonType.OK).showAndWait();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore nel salvataggio dei log");
                alert.setContentText("Errore nel salvataggio dei log:\n" + e.getMessage());
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                alert.setGraphic(alert_icon);
                stage.getIcons().setAll(alert_icon.getImage());
                alert.getButtonTypes().setAll(ButtonType.OK);
                alert.showAndWait();
            }
        }
    }
}
