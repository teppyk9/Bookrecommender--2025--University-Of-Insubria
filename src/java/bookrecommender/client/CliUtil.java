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
import javafx.stage.Window;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class CliUtil {

    private static final Logger logger = Logger.getLogger(CliUtil.class.getName());

    private Stage primaryStage;
    private Token currentToken;

    private LogRegInterface logRegService;
    private SearchInterface searchService;
    private LibInterface libService;
    private MonitorInterface monitorService;

    private static String RMI_HOST;
    private static int RMI_PORT;

    private static final Image programIcon = new Image(Objects.requireNonNull(CliUtil.class.getResourceAsStream("/bookrecommender/client/icons/program_icon.png")));
    private static final Image starFull = new Image(Objects.requireNonNull(CliUtil.class.getResourceAsStream("/bookrecommender/client/icons/star-full.png")));
    private static final Image starEmpty = new Image(Objects.requireNonNull(CliUtil.class.getResourceAsStream("/bookrecommender/client/icons/star-empty.png")));
    private static final Image starHalf = new Image(Objects.requireNonNull(CliUtil.class.getResourceAsStream("/bookrecommender/client/icons/star-half.png")));
    private static final Image starQuarter = new Image(Objects.requireNonNull(CliUtil.class.getResourceAsStream("/bookrecommender/client/icons/star-1-4.png")));
    private static final Image starThreeQuarters = new Image(Objects.requireNonNull(CliUtil.class.getResourceAsStream("/bookrecommender/client/icons/star-3-4.png")));

    private CliUtil() {
        // Al momento non è necessario alcun codice nel costruttore, poi ci penso se serve
    }

    private static class Holder {
        private static final CliUtil INSTANCE = new CliUtil();
    }

    public static CliUtil getInstance() {
        return Holder.INSTANCE;
    }

    public void init(Stage stage) {
        if (this.primaryStage == null) {
            this.primaryStage = stage;
        } else {
            logger.log(Level.WARNING, "PrimaryStage already initialized.");
        }
    }

    public void softRestart() {
        Platform.runLater(() -> {
            for (Window w : new ArrayList<>(Window.getWindows())) {
                if (w instanceof Stage s) {
                    s.close();
                }
            }
            this.currentToken = null;
            this.logRegService = null;
            this.searchService = null;
            this.libService = null;
            this.monitorService = null;
            RMI_HOST = null;
            RMI_PORT = 0;
            buildStage(FXMLtype.CONNESSIONE, null);
        });
    }

    public Stage getPrimaryStage() {
        if (primaryStage == null) {
            logger.log(Level.SEVERE, "PrimaryStage has not been initialized. Call init() first.");
            throw new IllegalStateException("PrimaryStage not initialized.");
        }
        return primaryStage;
    }

    public void setServer(String host, int port) {
        if (host != null && !host.isEmpty() && port > 0 && port <= 65535) {
            logger.log(Level.INFO, "Setting RMI host to: " + host + " and port to: " + port);
            RMI_HOST = host;
            RMI_PORT = port;
        } else {
            logger.log(Level.WARNING, "Invalid RMI host or port. Host: " + host + ", Port: " + port);
        }
    }

    public boolean testConnection() {
        try {
            Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            registry.list();
            logger.log(Level.INFO, "Connection to RMI server successful.");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to connect to RMI server at " + RMI_HOST + ":" + RMI_PORT, e);
            return false;
        }
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

    public void setMonitorService() {
        if (monitorService == null) {
            try {
                Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
                monitorService = (MonitorInterface) registry.lookup("Monitor_Interface");
                ClientListenerImpl listener = new ClientListenerImpl();
                monitorService.registerListener(listener);
                logger.log(Level.INFO, "MonitorInterface RMI service connected.");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error connecting to MonitorInterface RMI service", e);
            }
        }
    }

    public void buildStage(FXMLtype fxml, Object obj) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml.getPath()));
            Parent root = loader.load();
            Stage stage;
            switch(fxml) {
                case CONNESSIONE:
                    stage = getPrimaryStage();
                    stage.setScene(new Scene(root));
                    stage.setTitle(fxml.getTitle());
                    stage.getIcons().setAll(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/server_connection.png"))));
                    stage.setResizable(false);
                    stage.show();
                    stage.getScene().getRoot().requestFocus();
                    return;
                case HOME, LOGIN, REGISTRAZIONE, AREARISERVATA, CERCA, CERCA_AVANZATO, GESTIONELIBRERIE, CREALIBRERIA:
                    stage = primaryStage;
                    break;
                case DETTAGLIOLIBRO:
                    DettaglioLibroController dettaglioLibroController = loader.getController();
                    stage = new Stage();
                    if(obj instanceof Libro) {
                        dettaglioLibroController.setLibro((Libro) obj);
                    } else {
                        logger.log(Level.SEVERE, "Failed to build stage for " + fxml.name() + ": expected Libro object, got " + obj.getClass().getName());
                        return;
                    }
                    break;
                case CREAVALUTAZIONE:
                    CreaValutazioneController valutazioneController = loader.getController();
                    if(obj instanceof Libro) {
                        stage = new Stage();
                        valutazioneController.setLibro((Libro) obj);
                    } else {
                        logger.log(Level.SEVERE, "Failed to build stage for " + fxml.name() + ": expected Libro object, got " + obj.getClass().getName());
                        return;
                    }
                    break;
                case VISUALIZZAVALUTAZIONE:
                    VisualizzaValutazioneController visualizzaValutazioneController = loader.getController();
                    if(obj instanceof Valutazione) {
                        stage = new Stage();
                        visualizzaValutazioneController.setValutazione((Valutazione) obj);
                    } else {
                        logger.log(Level.SEVERE, "Failed to build stage for " + fxml.name() + ": expected Valutazione object, got " + obj.getClass().getName());
                        return;
                    }
                    break;
                case CREACONSIGLIO:
                    CreaConsiglioController aggiungiConsiglioController = loader.getController();
                    if(obj instanceof Libro) {
                        stage = new Stage();
                        aggiungiConsiglioController.setLibro((Libro) obj);
                    } else {
                        logger.log(Level.SEVERE, "Failed to build stage for " + fxml.name() + ": expected Libro object, got " + obj.getClass().getName());
                        return;
                    }
                    break;
                case MODIFICALIBRERIA:
                    ModificaLibreriaController modificaLibreriaController = loader.getController();
                    if(obj instanceof String) {
                        stage = primaryStage;
                        modificaLibreriaController.setLibreria((String) obj);
                    } else {
                        logger.log(Level.SEVERE, "Failed to build stage for " + fxml.name() + ": expected String object, got " + obj.getClass().getName());
                        return;
                    }
                    break;
                case AGGIUNGILIBROLIBRERIA:
                    AddLibroLibreriaController addLibroLibreriaController = loader.getController();
                    if(obj instanceof Libro) {
                        stage = new Stage();
                        addLibroLibreriaController.setLibro((Libro) obj);
                    } else {
                        logger.log(Level.SEVERE, "Failed to build stage for " + fxml.name() + ": expected Libro object, got " + obj.getClass().getName());
                        return;
                    }
                    break;
                default:
                    logger.log(Level.SEVERE, "Unsupported FXML type: " + fxml.name());
                    return;
            }
            stage.setScene(new Scene(root));
            stage.setTitle(fxml.getTitle());
            stage.getIcons().setAll(programIcon);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to build stage for " + fxml.name(), e);
        }
    }

    public Alert createAlert(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_icon.png"))));
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        alert.setGraphic(imageView);
        alert.getButtonTypes().setAll(ButtonType.OK);
        stage.getIcons().setAll(imageView.getImage());
        return alert;
    }

    public Alert createConfirmation(String titolo, String messaggio, boolean binary) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setContentText(messaggio);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_confirmation_icon.png"))));
        imageView.setFitHeight(48);
        imageView.setFitWidth(48);
        alert.setGraphic(imageView);
        stage.getIcons().setAll(imageView.getImage());
        if (binary) {
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        }else
            alert.getButtonTypes().setAll(ButtonType.OK);
        return alert;
    }

    public Image getStarFull() {
        return starFull;
    }

    public Image getStarEmpty() {
        return starEmpty;
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
    }
}
