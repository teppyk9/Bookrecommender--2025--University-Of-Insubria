package bookrecommender.util;

import bookrecommender.model.Libro;
import bookrecommender.model.Token;
import bookrecommender.dao.DBManager;
import bookrecommender.service.LibInterfaceImpl;
import bookrecommender.service.LogRegInterfaceImpl;
import bookrecommender.service.MonitorInterfaceImpl;
import bookrecommender.service.SearchInterfaceImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utility singleton per la gestione centralizzata del server.
 * <p>
 * Responsabilità principali:
 * <ul>
 *   <li>gestione dello {@link Stage} principale JavaFX</li>
 *   <li>configurazione e test della connessione al database</li>
 *   <li>verifica della disponibilità delle porte TCP</li>
 *   <li>avvio del server RMI e binding dei servizi remoti</li>
 *   <li>caricamento dinamico di viste FXML</li>
 *   <li>chiusura sicura del server</li>
 * </ul>
 * È implementata come singleton thread-safe tramite holder statico.
 * </p>
 */
public final class ServerUtil {

    /** Stage principale della GUI, inizializzato una sola volta. */
    private Stage primaryStage;

    /** Gestore delle connessioni al database. */
    private DBManager dbManager;

    /** Riferimento al server di monitoraggio per notificare lo shutdown. */
    private MonitorInterfaceImpl monitorServer;

    /** Logger della classe. */
    private static final Logger logger = Logger.getLogger(ServerUtil.class.getName());

    /** Costruttore privato per pattern Singleton. */
    private ServerUtil() {
        // Nessuna inizializzazione specifica al momento
    }

    /** Holder statico per implementazione del pattern singleton thread-safe. */
    private static class Holder {
        private static final ServerUtil INSTANCE = new ServerUtil();
    }

    /**
     * Restituisce l’unica istanza del singleton {@code ServerUtil}.
     *
     * @return istanza singleton di {@code ServerUtil}
     */
    public static ServerUtil getInstance() {
        return ServerUtil.Holder.INSTANCE;
    }

    /**
     * Inizializza lo stage principale della GUI, se non già impostato.
     *
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
     *
     * @return lo stage principale, oppure {@code null} se non inizializzato
     */
    public Stage getPrimaryStage() {
        if (primaryStage == null) {
            logger.log(Level.SEVERE, "PrimaryStage non inizializzato.");
        }
        return primaryStage;
    }

    /**
     * Inizializza un nuovo oggetto {@link DBManager}, se non già presente.
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
     *
     * @param portNumber numero della porta da testare
     * @return {@code true} se la porta è disponibile; {@code false} altrimenti
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
     *
     * @param name      nome del database
     * @param port      porta del database
     * @param user      nome utente del database
     * @param password  password del database
     * @return {@code true} se la connessione ha successo; {@code false} altrimenti
     */
    public boolean tryConnectToDb(String name, String port, String user, String password) {
        return dbManager.tryConnection(name, port, user, password);
    }

    /**
     * Stabilisce una connessione permanente al database.
     *
     * @param name      nome del database
     * @param port      porta del database
     * @param user      nome utente del database
     * @param password  password del database
     * @return {@code true} se la connessione è stabilita; {@code false} altrimenti
     */
    public boolean connectToDb(String name, String port, String user, String password){
        return dbManager.connect(name, port, user, password);
    }

    /**
     * Avvia il server RMI sulla porta specificata e registra i servizi remoti.
     *
     * @param port porta TCP su cui avviare il registry RMI
     * @return {@code true} se l’avvio e il binding hanno avuto successo; {@code false} altrimenti
     */
    public boolean setServer(int port) {
        dbManager.svuotaSessioniLogin();
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            SearchInterfaceImpl searchServer = new SearchInterfaceImpl();
            LogRegInterfaceImpl logRegServer = new LogRegInterfaceImpl();
            LibInterfaceImpl libServer = new LibInterfaceImpl();
            monitorServer = new MonitorInterfaceImpl();

            registry.rebind("Search_Interface", searchServer);
            registry.rebind("LogReg_Interface", logRegServer);
            registry.rebind("Lib_Interface", libServer);
            registry.rebind("Monitor_Interface", monitorServer);

            InetAddress localHost = InetAddress.getLocalHost();
            logger.info("Server ready on: " + localHost.getHostAddress() + " port: " + port);
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore nell'inizializzazione del server>", e);
            return false;
        }
    }

    /**
     * Restituisce la connessione corrente al database.
     *
     * @return istanza di {@link Connection} gestita da {@link DBManager}
     */
    public Connection getConnection(){
        return dbManager.getConnection();
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
     *
     * @param fxmlFile percorso del file FXML da caricare (relativo al classpath)
     * @param title    titolo della finestra
     * @param newWindow se {@code true} apre un nuovo {@link Stage}, altrimenti usa quello principale
     */
    public void loadFXML(String fxmlFile, String title, boolean newWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = newWindow ? new Stage() : primaryStage;
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.getIcons().setAll(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/server_connection.png"))));
            stage.show();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Errore nel caricamento del file FXML: " + fxmlFile, e);
        }
    }

    /**
     * Verifica se un token fornito non è più valido (non esiste o IP non corrisponde).
     *
     * @param token token da validare
     * @return {@code true} se il token è invalido o inesistente; {@code false} se è valido
     */
    public boolean isTokenNotValid(Token token) {
        String query = "SELECT 1 FROM SESSIONI_LOGIN WHERE TOKEN = ? AND IP_CLIENT = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, token.token());
            stmt.setString(2, token.ipClient());
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella validazione del token " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient(), e);
            return true;
        }
    }

    /**
     * Recupera un libro dal database a partire dal suo ID.
     *
     * @param id ID del libro da recuperare
     * @return oggetto {@link Libro} se trovato; altrimenti {@code null}
     */
    public Libro getLibro(int id) {
        String query = "SELECT * FROM LIBRI WHERE ID = ? ";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            Libro libro;
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next(); // NB: si assume che l'ID esista; in caso contrario, può generare eccezione
                libro = new Libro(
                        rs.getInt("ID"),
                        rs.getString("TITOLO"),
                        rs.getString("AUTORE"),
                        rs.getString("DESCRIZIONE"),
                        rs.getString("CATEGORIA"),
                        rs.getString("EDITORE"),
                        rs.getFloat("PREZZO"),
                        rs.getShort("ANNOPUBBLICAZIONE"),
                        rs.getShort("MESEPUBBLICAZIONE")
                );
            }
            return libro;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero del libro con ID: " + id, e);
            return null;
        }
    }

    /**
     * Estrae dal {@link ResultSet} i voti relativi a una valutazione.
     * Si aspetta colonne: {@code v_stile, v_contenuto, v_gradevolezza, v_originalita, v_edizione, v_finale}.
     *
     * @param rs result set posizionato sulla riga valida
     * @return lista immutabile di voti; {@code null} in caso di errore
     */
    public List<Float> getVotiVal(ResultSet rs){
        List<Float> Voti;
        try {
            Voti = List.of(
                    rs.getFloat("v_stile"),
                    rs.getFloat("v_contenuto"),
                    rs.getFloat("v_gradevolezza"),
                    rs.getFloat("v_originalita"),
                    rs.getFloat("v_edizione"),
                    rs.getFloat("v_finale")
            );
        } catch(SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero dei voti del libro", e);
            return null;
        }
        return Voti;
    }

    /**
     * Estrae dal {@link ResultSet} i commenti relativi a una valutazione.
     * Si aspetta colonne: {@code c_stile, c_contenuto, c_gradevolezza, c_originalita, c_edizione, c_finale}.
     *
     * @param rs result set posizionato sulla riga valida
     * @return lista immutabile di commenti; {@code null} in caso di errore
     */
    public List<String> getComVal(ResultSet rs){
        List<String> Commenti;
        try {
            Commenti = List.of(
                    rs.getString("c_stile"),
                    rs.getString("c_contenuto"),
                    rs.getString("c_gradevolezza"),
                    rs.getString("c_originalita"),
                    rs.getString("c_edizione"),
                    rs.getString("c_finale")
            );
        } catch(SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero dei commenti del libro", e);
            return null;
        }
        return Commenti;
    }

    /**
     * Verifica se l'utente (identificato dal {@link Token}) possiede il libro indicato
     * in almeno una delle sue librerie.
     *
     * @param token token dell'utente autenticato
     * @param libro libro da verificare
     * @return {@code true} se il libro è presente; {@code false} altrimenti o se il token non è valido
     */
    public boolean userHasLibro(Token token, Libro libro) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        String sql = """
                SELECT EXISTS (
                SELECT 1
                FROM librerie l
                JOIN libreria_libro ll ON l.id = ll.idlibreria
                WHERE l.id_utente = ? AND ll.idlibro = ?
                )""";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.userId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch(SQLException e){
            logger.log(Level.SEVERE, "Errore nel controllo se l'utente con ID: " + token.userId() + " contiene il libro con ID: " + libro.getId(), e);
        }
        return false;
    }

    /**
     * Esegue un {@link PreparedStatement} e popola una lista di oggetti {@link Libro}
     * con i risultati ottenuti dal {@link ResultSet}.
     *
     * @param risultati lista da riempire con i libri estratti (non {@code null})
     * @param stmt      statement SQL già preparato da eseguire (non {@code null})
     */
    public void resultStmt(List<Libro> risultati, PreparedStatement stmt){
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getInt("ID"),
                        rs.getString("TITOLO"),
                        rs.getString("AUTORE"),
                        rs.getString("DESCRIZIONE"),
                        rs.getString("CATEGORIA"),
                        rs.getString("EDITORE"),
                        rs.getFloat("PREZZO"),
                        rs.getShort("ANNOPUBBLICAZIONE"),
                        rs.getShort("MESEPUBBLICAZIONE")
                );
                risultati.add(libro);
            }
        } catch(SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'esecuzione del PreparedStatement", e);
        }
    }
}
