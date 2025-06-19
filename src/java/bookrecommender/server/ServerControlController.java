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

public class ServerControlController {
    public Button stopServerButton;
    public Button saveLogsButton;

    private final ImageView confirmation_icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/server/icons/alert_confirmation_icon.png"))));
    private final ImageView alert_icon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/server/icons/alert_icon.png"))));
    public TextFlow logFlow;
    public ScrollPane logScrollPane;


    public void initialize() {
        confirmation_icon.setFitWidth(48);
        confirmation_icon.setFitHeight(48);
        alert_icon.setFitWidth(48);
        alert_icon.setFitHeight(48);

        Logger rootLogger = Logger.getLogger("");
        Arrays.stream(rootLogger.getHandlers()).filter(h -> h instanceof ConsoleHandler).forEach(rootLogger::removeHandler);

        TextFlowHandler tfHandler = new TextFlowHandler(logFlow, logScrollPane);
        Set<String> allowed = Set.of(
                ServerUtil.class.getName(),
                DBManager.class.getName(),
                LibInterfaceImpl.class.getName(),
                LogRegInterfaceImpl.class.getName(),
                SearchInterfaceImpl.class.getName()
        );

        tfHandler.setFilter(record -> allowed.contains(record.getLoggerName()));

        rootLogger.addHandler(tfHandler);
        rootLogger.setLevel(Level.ALL);

        Logger.getLogger(ServerUtil.class.getName()).info("Pannello di controllo avviato");
        Platform.runLater(() -> {
            Stage stage = ServerUtil.getInstance().getPrimaryStage();
            stage.setOnCloseRequest(event -> {
                ServerUtil.getInstance().closeServer();
                Platform.exit();
                System.exit(0);
            });
        });
    }

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
