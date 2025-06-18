package bookrecommender.client;

import bookrecommender.common.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CliUtil {

    private static final Logger logger = Logger.getLogger(CliUtil.class.getName());
    private static CliUtil instance;

    private Stage primaryStage;
    private Token currentToken;

    private LogRegInterface logRegService;
    private SearchInterface searchService;
    private LibInterface libService;

    private static final String RMI_HOST = "localhost";
    private static final int RMI_PORT = 1099;

    private static final Image starFull = new Image(Objects.requireNonNull(CliUtil.class.getResourceAsStream("/bookrecommender/icons/star-full.png")));
    private static final Image starEmpty = new Image(Objects.requireNonNull(CliUtil.class.getResourceAsStream("/bookrecommender/icons/star-empty.png")));
    private static final Image starHalf = new Image(Objects.requireNonNull(CliUtil.class.getResourceAsStream("/bookrecommender/icons/star-half.png")));
    private static final Image starQuarter = new Image(Objects.requireNonNull(CliUtil.class.getResourceAsStream("/bookrecommender/icons/star-1-4.png")));
    private static final Image starThreeQuarters = new Image(Objects.requireNonNull(CliUtil.class.getResourceAsStream("/bookrecommender/icons/star-3-4.png")));

    private CliUtil() {
        // Al momento non è necessario alcun codice nel costruttore, poi ci penso se serve
    }

    public static synchronized CliUtil getInstance() {
        if (instance == null) {
            instance = new CliUtil();
        }
        return instance;
    }

    public void init(Stage stage) {
        if (this.primaryStage == null) {
            this.primaryStage = stage;
        } else {
            logger.log(Level.WARNING, "PrimaryStage already initialized.");
        }
    }

    public Stage getPrimaryStage() {
        if (primaryStage == null) {
            logger.log(Level.SEVERE, "PrimaryStage has not been initialized. Call init() first.");
            throw new IllegalStateException("PrimaryStage not initialized.");
        }
        return primaryStage;
    }

    public Token getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(Token token) {
        this.currentToken = token;
    }

    public LogRegInterface getLogRegService() {
        if (logRegService == null) {
            try {
                Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
                logRegService = (LogRegInterface) registry.lookup("LogReg_Interface");
                logger.log(Level.INFO, "LogRegInterface RMI service connected.");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error connecting to LogRegInterface RMI service", e);
            }
        }
        return logRegService;
    }

    public SearchInterface getSearchService() {
        if (searchService == null) {
            try {
                Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
                searchService = (SearchInterface) registry.lookup("Search_Interface");
                logger.log(Level.INFO, "SearchInterface RMI service connected.");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error connecting to SearchInterface RMI service", e);
            }
        }
        return searchService;
    }

    public LibInterface getLibService() {
        if (libService == null) {
            try {
                Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
                libService = (LibInterface) registry.lookup("Lib_Interface");
                logger.log(Level.INFO, "LibInterface RMI service connected.");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error connecting to LibInterface RMI service", e);
            }
        }
        return libService;
    }

    public void loadFXML(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = getPrimaryStage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);

            try {
                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
                stage.getIcons().setAll(icon);
            } catch (NullPointerException e) {
                logger.log(Level.WARNING, "Program icon not found, skipping.", e);
            }

            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load FXML: " + fxmlPath, e);
        }
    }

    public void createFXML(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.getIcons().add(icon);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create FXML: " + fxmlPath, e);
        }
    }

    public Alert createAlert(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        Image icona = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/alert_icon.png")));
        ImageView imageView = new ImageView(icona);
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        alert.setGraphic(imageView);
        stage.getIcons().add(icona);
        return alert;
    }

    public Alert createConfirmation(String titolo, String messaggio, boolean binary) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/alert_confirmation_icon.png"))));
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        alert.setGraphic(imageView);
        stage.getIcons().add(imageView.getImage());
        if (binary) {
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        }
        return alert;
    }

    public void exitApplication() {
        if (createConfirmation("Conferma uscita", "Sei sicuro di voler uscire dall'applicazione?", true).showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            if(currentToken != null) {
                try {
                    logRegService.LogOut(currentToken);
                }catch(Exception e) {
                    createAlert("Errore di Logout", e.getMessage()).showAndWait();
                }
            }
            Platform.exit();
            System.exit(0);
        }
    }

    public Image getStarFull() {
        return starFull;
    }

    public Image getStarEmpty() {
        return starEmpty;
    }

    public void showLibroAdvancedDetails(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/DettaglioLibro.fxml"));
            Parent root = loader.load();
            DettaglioLibroController controller = loader.getController();
            controller.setLibro(libro);
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
            Stage stage = new Stage();
            stage.setTitle("Dettagli Avanzati del Libro");
            stage.getIcons().add(icon);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            createAlert("Errore", "Impossibile aprire la finestra dei dettagli avanzati.").showAndWait();
            logger.log(Level.SEVERE, "Impossibile aprire la finestra dei dettagli avanzati.", e);
        }
        libro = null;
    }

    public void showCreaValutazione(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/Valutazione.fxml"));
            Parent root = loader.load();
            ValutazioneController controller = loader.getController();
            controller.setLibro(libro);
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
            Stage stage = new Stage();
            stage.setTitle("Crea valutazione del Libro");
            stage.getIcons().add(icon);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            createAlert("Errore", "Impossibile aprire la finestra di valutazione.").showAndWait();
            logger.log(Level.SEVERE, "Impossibile aprire la finestra di valutazione.", e);
        }
        libro = null;
    }

    public void setStar(ImageView star1, ImageView star2, ImageView star3, ImageView star4, ImageView star5, float voto) {
        ImageView[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            float diff = voto - i;
            if (diff >= 1) {
                stars[i].setImage(starFull);
            } else if (diff >= 0.75) {
                stars[i].setImage(starThreeQuarters);
            } else if (diff >= 0.5) {
                stars[i].setImage(starHalf);
            } else if (diff >= 0.25) {
                stars[i].setImage(starQuarter);
            } else {
                stars[i].setImage(starEmpty);
            }
        }
        star1 = null;
        star2 = null;
        star3 = null;
        star4 = null;
        star5 = null;
        stars = null;
    }

    public void showValutazione(Valutazione valutazione) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/VisualizzaValutazione.fxml"));
            Parent root = loader.load();
            VisualizzaValutazioneController controller = loader.getController();
            controller.setValutazione(valutazione);
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
            Stage stage = new Stage();
            stage.setTitle("Valutazione del Libro");
            stage.getIcons().add(icon);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            createAlert("Errore", "Impossibile aprire la finestra di valutazione.").showAndWait();
            logger.log(Level.SEVERE, "Impossibile aprire la finestra di valutazione.", e);
        }
        valutazione = null;
    }

}
