package bookrecommender.client;

import bookrecommender.common.*;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Labeled;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe singleton di utilità per il client JavaFX.
 * <p>
 * Gestisce e centralizza:
 * <ul>
 *   <li>Connessione ai servizi RMI (login, ricerca, librerie, monitor)</li>
 *   <li>Caricamento e gestione delle schermate JavaFX tramite FXML</li>
 *   <li>Gestione del token utente autenticato</li>
 *   <li>Creazione di alert e finestre di conferma</li>
 *   <li>Stilizzazione delle icone e visualizzazione delle valutazioni a stelle</li>
 * </ul>
 * Implementa il pattern singleton tramite inner class statica {@code Holder}.
 * </p>
 */
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

    private Object lock = null;

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

    /**
     * Restituisce l'istanza singleton di {@code CliUtil}.
     * @return l'istanza condivisa
     */
    public static CliUtil getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Imposta lo {@link Stage} principale dell’applicazione.
     *
     * @param stage lo {@code Stage} da usare come finestra principale
     */
    public void init(Stage stage) {
        if (this.primaryStage == null) {
            this.primaryStage = stage;
        } else {
            logger.log(Level.WARNING, "PrimaryStage already initialized.");
        }
    }

    /**
     * Chiude tutte le finestre, resetta lo stato del client e riapre la schermata di connessione.
     */
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
            buildStage(FXMLtype.CONNESSIONE, null,null);
        });
    }

    /**
     * Restituisce lo {@link Stage} principale.
     *
     * @return lo stage principale
     * @throws IllegalStateException se {@code init()} non è stato chiamato
     */
    public Stage getPrimaryStage() {
        if (primaryStage == null) {
            logger.log(Level.SEVERE, "PrimaryStage has not been initialized. Call init() first.");
            throw new IllegalStateException("PrimaryStage not initialized.");
        }
        return primaryStage;
    }

    /**
     * Imposta l'host e la porta del server RMI.
     *
     * @param host indirizzo IP o nome host del server
     * @param port porta del registro RMI
     */
    public void setServer(String host, int port) {
        if (host != null && !host.isEmpty() && port > 0 && port <= 65535) {
            logger.log(Level.INFO, "Setting RMI host to: " + host + " and port to: " + port);
            RMI_HOST = host;
            RMI_PORT = port;
        } else {
            logger.log(Level.WARNING, "Invalid RMI host or port. Host: " + host + ", Port: " + port);
        }
    }

    /**
     * Verifica la connessione al registro RMI configurato.
     *
     * @return {@code true} se la connessione è avvenuta correttamente, {@code false} altrimenti
     */
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

    /**
     * Restituisce il {@link Token} dell’utente attualmente autenticato.
     *
     * @return il token corrente, o {@code null} se non autenticato
     */
    public Token getCurrentToken() {
        return currentToken;
    }

    /**
     * Imposta il {@link Token} dell’utente corrente.
     *
     * @param token nuovo token da associare alla sessione
     */
    public void setCurrentToken(Token token) {
        this.currentToken = token;
    }

    /**
     * Restituisce il servizio remoto per login e registrazione.
     *
     * @return istanza remota di {@code LogRegInterface}
     */
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

    /**
     * Restituisce il servizio remoto per la ricerca dei libri.
     *
     * @return istanza remota di {@code SearchInterface}
     */
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

    /**
     * Restituisce il servizio remoto per la gestione delle librerie.
     *
     * @return istanza remota di {@code LibInterface}
     */
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

    /**
     * Registra il client al servizio di monitoraggio remoto del server.
     */
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

    /**
     * Verifica se due liste di libri contengono elementi differenti.
     *
     * @param list1 prima lista
     * @param list2 seconda lista
     * @return {@code true} se ci sono differenze, {@code false} se sono uguali
     */
    public boolean hannoDifferenze(List<Libro> list1, List<Libro> list2) {
        if (list1.size() != list2.size()) return true;
        Set<Libro> set = new HashSet<>(list2);
        for (Libro libro : list1) if (!set.contains(libro)) return true;
        return false;
    }

    /**
     * Carica e mostra una schermata JavaFX specificata da {@link FXMLtype}.
     * Può anche passare parametri al controller corrispondente.
     *
     * @param newFxml tipo della schermata da caricare
     * @param oldFXML schermata precedente (facoltativa, usata per ritorno)
     * @param obj     parametro da passare al controller (es. {@link Libro}, {@link Valutazione}, {@link String})
     */
    public void buildStage(FXMLtype newFxml, FXMLtype oldFXML,Object obj) {
        if(lock == null)
            lock = obj;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(newFxml.getPath()));
            Parent root = loader.load();
            Stage stage;
            String msgLibro = "Failed to build stage for " + newFxml.name() + ": expected Libro object, got ";
            String msgValutazione = "Failed to build stage for " + newFxml.name() + ": expected Valutazione object, got ";
            String s = obj == null ? "null" : obj.getClass().getName();
            msgLibro = msgLibro.concat(s);
            msgValutazione = msgValutazione.concat(s);
            switch(newFxml) {
                case CONNESSIONE:
                    stage = getPrimaryStage();
                    stage.setScene(new Scene(root));
                    stage.setTitle(newFxml.getTitle());
                    stage.getIcons().setAll(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/server_connection.png"))));
                    stage.setResizable(false);
                    stage.show();
                    stage.getScene().getRoot().requestFocus();
                    return;
                case HOME, LOGIN, REGISTRAZIONE, AREARISERVATA, CERCA, CERCA_AVANZATO, GESTIONELIBRERIE, CREALIBRERIA:
                    stage = primaryStage;
                    lock = null;
                    break;
                case DETTAGLIOLIBRO:
                    DettaglioLibroController dettaglioLibroController = loader.getController();
                    stage = new Stage();
                    if(obj == null)
                        obj = lock;
                    if(obj instanceof Libro ) {
                        dettaglioLibroController.setLibro((Libro) obj);
                    } else {
                        logger.log(Level.SEVERE, msgLibro);
                        return;
                    }
                    break;
                case CREAVALUTAZIONE:
                    if(obj == null)
                        obj = lock;
                    CreaValutazioneController valutazioneController = loader.getController();
                    if(obj instanceof Libro) {
                        stage = primaryStage;
                        valutazioneController.setLibro((Libro) obj, oldFXML);
                    } else {
                        logger.log(Level.SEVERE, msgLibro);
                        return;
                    }
                    break;
                case VISUALIZZAVALUTAZIONE:
                    if(obj == null)
                        obj = lock;
                    VisualizzaValutazioneController visualizzaValutazioneController = loader.getController();
                    if(obj instanceof Valutazione) {
                        stage = new Stage();
                        visualizzaValutazioneController.setValutazione((Valutazione) obj);
                    } else {
                        logger.log(Level.SEVERE, msgValutazione);
                        return;
                    }
                    break;
                case CREACONSIGLIO:
                    if(obj == null)
                        obj = lock;
                    CreaConsiglioController aggiungiConsiglioController = loader.getController();
                    if(obj instanceof Libro) {
                        stage = primaryStage;
                        aggiungiConsiglioController.setLibro((Libro) obj, oldFXML);
                    } else {
                        logger.log(Level.SEVERE, msgLibro);
                        return;
                    }
                    break;
                case MODIFICALIBRERIA:
                    if(obj == null)
                        obj = lock;
                    ModificaLibreriaController modificaLibreriaController = loader.getController();
                    if(obj instanceof String) {
                        stage = primaryStage;
                        modificaLibreriaController.setLibreria((String) obj);
                    } else {
                        logger.log(Level.SEVERE, "Failed to build stage for " + newFxml.name() + ": expected String object, got " + s);
                        return;
                    }
                    break;
                case AGGIUNGILIBROLIBRERIA:
                    if(obj == null)
                        obj = lock;
                    AddLibroLibreriaController addLibroLibreriaController = loader.getController();
                    if(obj instanceof Libro) {
                        stage = new Stage();
                        addLibroLibreriaController.setLibro((Libro) obj);
                    } else {
                        logger.log(Level.SEVERE, msgLibro);
                        return;
                    }
                    break;
                case MODIFICACONSIGLIO:
                    if(obj == null)
                        obj = lock;
                    ModificaConsiglioController modificaConsiglioController = loader.getController();
                    if(obj instanceof Libro) {
                        stage = primaryStage;
                        modificaConsiglioController.setLibro((Libro) obj, oldFXML);
                    } else {
                        logger.log(Level.SEVERE, msgLibro);
                        return;
                    }
                    break;
                case MODIFICAVALUTAZIONE:
                    if(obj == null)
                        obj = lock;
                    ModificaValutazioneController modificaValutazioneController = loader.getController();
                    if(obj instanceof Valutazione) {
                        stage = primaryStage;
                        modificaValutazioneController.setValutazione((Valutazione) obj, oldFXML);
                    } else {
                        logger.log(Level.SEVERE, msgValutazione);
                        return;
                    }
                    break;
                default:
                    logger.log(Level.SEVERE, "Unsupported FXML type: " + newFxml.name());
                    return;
            }
            stage.setScene(new Scene(root));
            stage.setTitle(newFxml.getTitle());
            stage.getIcons().setAll(programIcon);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to build stage for " + newFxml.name(), e);
        }
    }

    /**
     * Crea un alert di errore personalizzato.
     *
     * @param titolo    titolo della finestra
     * @param messaggio contenuto del messaggio
     * @return oggetto {@link Alert} configurato
     */
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

    /**
     * Crea una finestra di conferma con bottoni personalizzati.
     *
     * @param titolo    titolo della finestra
     * @param messaggio contenuto del messaggio
     * @param binary    se {@code true}, mostra bottoni Sì/No; altrimenti solo OK
     * @return oggetto {@link Alert} configurato
     */
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

    /**
     * Restituisce l’icona di una stella piena.
     *
     * @return immagine PNG di stella piena
     */
    public Image getStarFull() {
        return starFull;
    }

    /**
     * Restituisce l’icona di una stella vuota.
     *
     * @return immagine PNG di stella vuota
     */
    public Image getStarEmpty() {
        return starEmpty;
    }


    /**
     * Imposta le immagini delle 5 stelle in base al voto espresso (float tra 0 e 5).
     *
     * @param star1 prima stella
     * @param star2 seconda stella
     * @param star3 terza stella
     * @param star4 quarta stella
     * @param star5 quinta stella
     * @param voto  valore da 0 a 5 (con frazioni)
     */
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

    /**
     * Mostra un riepilogo degli errori riscontrati durante
     * l’aggiornamento di una libreria, basandosi su codici restituiti dal server.
     *
     * @param risultati lista di interi alternati (ID libro, codice errore)
     */
    public void reviewLibUpdate(List<Integer> risultati){
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 1; i < risultati.size(); i += 2) {
                int idLibro = risultati.get(i);
                int codice = risultati.get(i + 1);
                switch (codice) {
                    case 0:
                        sb.append("Il libro con titolo ").append(searchService.getLibro(idLibro)).append(" ha valutazioni associate.");
                        break;
                    case 1:
                        sb.append("Il libro con titolo ").append(searchService.getLibro(idLibro)).append(" è stato utilizzato come consiglio.");
                        break;
                    case 2:
                        sb.append("Il libro con titolo ").append(searchService.getLibro(idLibro)).append(" ha libri consigliati ad esso associati.");
                        break;
                    default:
                        sb.append("Il libro con titolo ").append(searchService.getLibro(idLibro)).append(" ha codice errore sconosciuto: ").append(codice).append(".");
                }
                if (i + 2 < risultati.size())
                    sb.append(System.lineSeparator());
            }
            createAlert("Errore", sb.toString()).showAndWait();
        }catch (RemoteException e){
            createAlert("Errore", "Errore nel recupero dati dei libri\n" + e.getMessage()).showAndWait();
        }
    }

    /**
     * Applica uno stile trasparente a un controllo con icona (es. pulsante) e
     * aggiunge un’animazione al passaggio del mouse.
     *
     * @param control controllo {@link Labeled} da stilizzare
     */
    public void styleIconControl(Labeled control){
        control.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-border-color: transparent;"
                        + "-fx-padding: 0;"
                        + "-fx-cursor: hand;"
                        + "-fx-focus-color: transparent;"
                        + "-fx-faint-focus-color: transparent;");
        ScaleTransition enlarge = new ScaleTransition(Duration.millis(100), control);
        enlarge.setToX(1.1);
        enlarge.setToY(1.1);
        ScaleTransition shrink = new ScaleTransition(Duration.millis(100), control);
        shrink.setToX(1.0);
        shrink.setToY(1.0);
        control.setOnMouseEntered(e -> { shrink.stop(); enlarge.playFromStart(); });
        control.setOnMouseExited (e -> { enlarge.stop(); shrink.playFromStart(); });
    }
}
