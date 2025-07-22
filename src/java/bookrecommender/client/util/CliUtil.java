package bookrecommender.client.util;

import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.listener.ClientListener;
import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.ui.*;
import bookrecommender.common.interfaces.LibInterface;
import bookrecommender.common.interfaces.LogRegInterface;
import bookrecommender.common.interfaces.MonitorInterface;
import bookrecommender.common.interfaces.SearchInterface;
import bookrecommender.common.model.Libro;
import bookrecommender.common.model.Token;
import bookrecommender.common.model.Valutazione;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Labeled;
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
                ClientListener listener = new ClientListener();
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
                    stage = primaryStage;
                    stage.setScene(new Scene(root));
                    stage.setTitle(newFxml.getTitle());
                    stage.getIcons().setAll(IMGtype.SERVER.getImage());
                    stage.setResizable(false);
                    stage.show();
                    stage.getScene().getRoot().requestFocus();
                    return;
                case HOME, LOGIN, REGISTRAZIONE, AREARISERVATA, CERCA, CERCA_AVANZATO, GESTIONELIBRERIE, CREALIBRERIA, IMPOSTAZIONI:
                    stage = primaryStage;
                    lock = null;
                    break;
                case DETTAGLIOLIBRO:
                    DettaglioLibro dettaglioLibro = loader.getController();
                    stage = new Stage();
                    if(obj == null)
                        obj = lock;
                    if(obj instanceof Libro ) {
                        dettaglioLibro.setLibro((Libro) obj);
                    } else {
                        logger.log(Level.SEVERE, msgLibro);
                        return;
                    }
                    break;
                case CREAVALUTAZIONE:
                    if(obj == null)
                        obj = lock;
                    CreaValutazione valutazioneController = loader.getController();
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
                    VisualizzaValutazione visualizzaValutazione = loader.getController();
                    if(obj instanceof Valutazione) {
                        stage = new Stage();
                        visualizzaValutazione.setValutazione((Valutazione) obj);
                    } else {
                        logger.log(Level.SEVERE, msgValutazione);
                        return;
                    }
                    break;
                case CREACONSIGLIO:
                    if(obj == null)
                        obj = lock;
                    CreaConsiglio aggiungiConsiglioController = loader.getController();
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
                    ModificaLibreria modificaLibreria = loader.getController();
                    if(obj instanceof String) {
                        stage = primaryStage;
                        modificaLibreria.setLibreria((String) obj);
                    } else {
                        logger.log(Level.SEVERE, "Failed to build stage for " + newFxml.name() + ": expected String object, got " + s);
                        return;
                    }
                    break;
                case AGGIUNGILIBROLIBRERIA:
                    if(obj == null)
                        obj = lock;
                    AddLibroLibreria addLibroLibreria = loader.getController();
                    if(obj instanceof Libro) {
                        stage = primaryStage;
                        addLibroLibreria.setLibro((Libro) obj, oldFXML);
                    } else {
                        logger.log(Level.SEVERE, msgLibro);
                        return;
                    }
                    break;
                case MODIFICACONSIGLIO:
                    if(obj == null)
                        obj = lock;
                    ModificaConsiglio modificaConsiglio = loader.getController();
                    if(obj instanceof Libro) {
                        stage = primaryStage;
                        modificaConsiglio.setLibro((Libro) obj, oldFXML);
                    } else {
                        logger.log(Level.SEVERE, msgLibro);
                        return;
                    }
                    break;
                case MODIFICAVALUTAZIONE:
                    if(obj == null)
                        obj = lock;
                    ModificaValutazione modificaValutazione = loader.getController();
                    if(obj instanceof Valutazione) {
                        stage = primaryStage;
                        modificaValutazione.setValutazione((Valutazione) obj, oldFXML);
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
            stage.getIcons().setAll(IMGtype.ICONA_PROG.getImage());
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
        alert.setGraphic(IMGtype.RED_CROSS.getImageView(48,48));
        alert.getButtonTypes().setAll(ButtonType.OK);
        stage.getIcons().setAll(IMGtype.RED_CROSS.getImage());
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
        alert.setGraphic(IMGtype.CONFIRM.getImageView(48,48));
        stage.getIcons().setAll(IMGtype.CONFIRM.getImage());
        if (binary) {
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        }else
            alert.getButtonTypes().setAll(ButtonType.OK);
        return alert;
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
    public void setStar(ImageView star1, ImageView star2, ImageView star3, ImageView star4, ImageView star5, float voto, IMGtype.STARtype type) {
        ImageView[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            float diff = voto - i;
            if (diff >= 1) {
                stars[i].setImage(type.getStar(4).getImage());
            } else if (diff >= 0.75) {
                stars[i].setImage(type.getStar(3).getImage());
            } else if (diff >= 0.5) {
                stars[i].setImage(type.getStar(2).getImage());
            } else if (diff >= 0.25) {
                stars[i].setImage(type.getStar(1).getImage());
            } else {
                stars[i].setImage(type.getStar(0).getImage());
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
