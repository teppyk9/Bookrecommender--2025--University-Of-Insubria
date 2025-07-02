package bookrecommender.server;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utility singleton per la gestione centralizzata del server.
 * Questa classe fornisce funzionalità di:
 *     Gestione dello stage principale JavaFX
 *     Configurazione e test della connessione al database
 *     Verifica della disponibilità di porte TCP
 *     Avvio del server RMI con binding delle interfacce
 *     Caricamento dinamico di interfacce FXML
 *     Chiusura sicura del server
 * È implementata come singleton thread-safe con holder statico.
 */
public final class ServerUtil {

    /** Stage principale della GUI, inizializzato una sola volta. */
    private Stage primaryStage;

    /** Oggetto DBManager per la gestione della connessione al database. */
    private DBManager dbManager;

    /** Riferimento al server di monitoraggio per notificare lo shutdown. */
    private MonitorInterfaceImpl monitorServer;

    /** Logger per la classe. */
    private static final Logger logger = Logger.getLogger(ServerUtil.class.getName());

    /**
     * Costruttore privato per evitare istanziazioni esterne.
     */
    private ServerUtil() {
        // Al momento non è necessario alcun codice nel costruttore, poi ci penso se serve
    }

    /**
     * Holder statico per implementazione del pattern singleton thread-safe.
     */
    private static class Holder {
        private static final ServerUtil INSTANCE = new ServerUtil();
    }

    /**
     * Restituisce l’unica istanza del singleton {@code ServerUtil}
     * @return istanza singleton di ServerUtil
     */
    public static ServerUtil getInstance() {
        return ServerUtil.Holder.INSTANCE;
    }

    /**
     * Inizializza lo stage principale della GUI, se non già impostato.
     * @param stage stage JavaFX da associare come principale
     */
    public void init(Stage stage) {
        if (this.primaryStage == null) {
            this.primaryStage = stage;
        } else {
            logger.log(Level.WARNING, "PrimaryStage già inizializzato.");
        }
    }

    /**
     * Restituisce lo stage principale della GUI.
     * @return lo stage principale
     */
    public Stage getPrimaryStage() {
        if (primaryStage == null) {
            logger.log(Level.SEVERE, "PrimaryStage non inizializzato.");
        }
        return primaryStage;
    }

    /**
     * Inizializza un nuovo oggetto {@code DBManager}, se non già presente.
     */
    public void setDBManager() {
        if (dbManager == null) {
            dbManager = new DBManager();
        } else {
            logger.log(Level.WARNING, "DBManager già inizializzato.");
        }
    }

    /**
     * Verifica se una porta TCP è libera sul sistema.
     * @param portNumber numero della porta da testare
     * @return true se la porta è disponibile, false altrimenti
     */
    public boolean isTcpPortAvailable(int portNumber) {
        try (ServerSocket ss = new ServerSocket(portNumber)) {
            ss.setReuseAddress(true);
            logger.info("Test sulla porta TCP " + portNumber + " riuscito.");
            return true;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Test sulla porta TCP " + portNumber + " fallito: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica la validità di una connessione al database senza mantenerla attiva.
     * @param url      URL JDBC del database
     * @param user     nome utente
     * @param password password del database
     * @return true se la connessione ha successo, false altrimenti
     */
    public boolean tryConnectToDb(String url, String user, String password) {
        return dbManager.tryConnection(url, user, password);
    }

    /**
     * Stabilisce una connessione permanente al database.
     * @param url      URL JDBC del database
     * @param user     nome utente
     * @param password password del database
     * @return true se la connessione ha successo, false altrimenti
     */
    public boolean connectToDb(String url, String user, String password){
        return dbManager.connect(url, user, password);
    }

    /**
     * Avvia il server RMI sulla porta specificata e registra i servizi remoti.
     * @param port porta TCP su cui avviare il registry RMI
     * @return true se l’avvio ha avuto successo, false altrimenti
     */
    public boolean setServer(int port) {
        dbManager.svuotaSessioniLogin();
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            SearchInterfaceImpl searchServer = new SearchInterfaceImpl(dbManager);
            LogRegInterfaceImpl logRegServer = new LogRegInterfaceImpl(dbManager);
            LibInterfaceImpl libServer = new LibInterfaceImpl(dbManager);
            monitorServer = new MonitorInterfaceImpl();
            registry.rebind("Search_Interface", searchServer);
            registry.rebind("LogReg_Interface", logRegServer);
            registry.rebind("Lib_Interface", libServer);
            registry.rebind("Monitor_Interface", monitorServer);
            logger.info("Server ready");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore nell'inizializzazione del server>", e);
            return false;
        }
    }

    /**
     * Chiude la connessione al database, notifica i client remoti
     * dello shutdown e arresta il server.
     */
    public void closeServer() {
        try {
            dbManager.closeConnection();
            monitorServer.notifyShutdown();
            logger.info("Server closed successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore durante la chiusura del server: ", e);
        }
    }

    /**
     * Carica e mostra un file FXML come finestra o come stage principale.
     * @param fxmlFile percorso del file FXML da caricare
     * @param title    titolo della finestra
     * @param newWindow se true apre un nuovo Stage, altrimenti usa quello principale
     */
    public void loadFXML(String fxmlFile, String title, boolean newWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = newWindow
                    ? new Stage()
                    : primaryStage;
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.getIcons().setAll(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/server/icons/server_connection.png"))));
            stage.show();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Errore nel caricamento del file FXML: " + fxmlFile, e);
        }
    }
}
