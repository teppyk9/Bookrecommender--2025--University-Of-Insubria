package bookrecommender.server.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Classe responsabile della gestione della connessione al database, dell'esecuzione delle query e della manipolazione dei dati relativi a libri e utenti.
 * Utilizzata nel lato server dell'applicazione BookRecommender.
 */
public class DBManager {

    /** Connessione condivisa al database, usata dalle varie operazioni del DBManager. */
    private static HikariDataSource dataSource;

    /** Logger per la registrazione di eventi, errori e messaggi di debug della classe DBManager. */
    private static final Logger logger = Logger.getLogger(DBManager.class.getName());

    /**
     * Costruttore vuoto della classe DBManager.
     * Attualmente non inizializza risorse ma può essere esteso per configurazioni future.
     */
    public DBManager() {
        //al momento non è necessario alcun codice nel costruttore, poi ci penso se serve
    }

    /**
     * Prova a connettersi al database con i parametri specificati.
     * Non mantiene la connessione aperta: serve solo per verificare la validità dei parametri.
     * @param url      URL del database (es. jdbc:postgresql://localhost:5432/miodb)
     * @param user     Nome utente per accedere al database
     * @param password Password dell'utente
     * @return true se la connessione di test ha successo, false altrimenti
     */
    public boolean tryConnection(String url, String user, String password) {
        try{
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(url);
            cfg.setUsername(user);
            cfg.setPassword(password);
            cfg.setMaximumPoolSize(1);
            try (HikariDataSource testDs = new HikariDataSource(cfg);
                 Connection ignored = testDs.getConnection()) {
                logger.log(Level.INFO, "Test connessione HikariCP riuscito.");
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Test connessione fallito", e);
            return false;
        }
    }

    /**
     * Apre una connessione persistente al database se non già aperta.
     * @param url      URL del database
     * @param user     Nome utente del database
     * @param password Password dell'utente
     * @return true se la connessione è avvenuta correttamente, false altrimenti
     */
    public boolean connect(String url, String user, String password) {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.log(Level.WARNING, "Pool già inizializzato.");
            return true;
        }
        try {
            HikariConfig config = getHikariConfig(url, user, password);

            dataSource = new HikariDataSource(config);
            logger.log(Level.INFO, "HikariCP pool creato con successo.");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore inizializzazione HikariCP", e);
            return false;
        }
    }

    private static HikariConfig getHikariConfig(String url, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(60_000);
        config.setConnectionTimeout(30_000);
        config.setPoolName("BookRecommenderPool");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "100");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return config;
    }

    public Connection getConnection(){
        if (dataSource == null) {
            logger.warning("DataSource non inizializzato. Chiama connect() prima.");
        }
        try{
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante l'ottenimento della connessione al database", e);
            return null;
        }
    }

    /**
     * Chiude la connessione attualmente aperta al database, se esistente.
     */
    public void closeConnection() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.log(Level.INFO, "Pool HikariCP chiuso.");
        }
    }

    /**
     * Svuota completamente la tabella delle sessioni di login,
     * resettando anche il contatore degli ID.
     * Utile per test o per azzerare lo stato delle sessioni.
     */
    public void svuotaSessioniLogin() {
        String sql = "TRUNCATE TABLE SESSIONI_LOGIN RESTART IDENTITY";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel restart delle sessioni", e);
        }
    }
}