package bookrecommender.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsabile della gestione della connessione al database, dell'esecuzione delle query
 * e della manipolazione dei dati relativi a libri e utenti.
 * <p>
 * Utilizzata nel lato server dell'applicazione BookRecommender. La classe gestisce un pool
 * di connessioni basato su HikariCP mantenuto in un campo statico condiviso.
 * </p>
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
public class DBManager {

    /** Pool di connessioni HikariCP condiviso e riutilizzabile. */
    private static HikariDataSource dataSource;

    /** Logger per la registrazione di eventi, errori e messaggi di debug della classe DBManager. */
    private static final Logger logger = Logger.getLogger(DBManager.class.getName());

    /**
     * Costruttore vuoto della classe DBManager.
     * <p>Attualmente non inizializza risorse ma può essere esteso per configurazioni future.</p>
     */
    public DBManager() {
        // al momento non è necessario alcun codice nel costruttore
    }

    /**
     * Prova a connettersi al database con i parametri specificati.
     * <p>Non mantiene la connessione aperta: serve solo per verificare la validità dei parametri.</p>
     *
     * @param name     nome del database
     * @param port     porta TCP su cui il database è in ascolto
     * @param user     nome utente per accedere al database
     * @param password password dell'utente
     * @return {@code true} se la connessione di test ha successo, {@code false} altrimenti
     */
    public boolean tryConnection(String name, String port, String user, String password) {
        try{
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl("jdbc:postgresql://localhost:" + port + "/" + name);
            cfg.setUsername(user);
            cfg.setPassword(password);
            cfg.setMaximumPoolSize(1);
            try (HikariDataSource testDs = new HikariDataSource(cfg);
                 Connection ignored = testDs.getConnection()) {
                logger.log(Level.INFO, "Test connessione HikariCP riuscito.");
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Test connessione fallito", e);
            return false;
        }
    }

    /**
     * Apre una connessione persistente al database se non già aperta.
     *
     * @param name     nome del database
     * @param port     porta TCP su cui il database è in ascolto
     * @param user     nome utente del database
     * @param password password dell'utente
     * @return {@code true} se il pool è stato inizializzato (o era già attivo), {@code false} in caso di errore
     */
    public boolean connect(String name, String port, String user, String password) {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.log(Level.WARNING, "Pool già inizializzato.");
            return true;
        }
        try {
            HikariConfig config = getHikariConfig("jdbc:postgresql://localhost:" + port + "/" + name, user, password);

            dataSource = new HikariDataSource(config);
            logger.log(Level.INFO, "HikariCP pool creato con successo.");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore inizializzazione HikariCP", e);
            return false;
        }
    }

    /**
     * Crea e configura un {@link HikariConfig} per la connessione JDBC fornita.
     *
     * @param url      URL JDBC (es. {@code jdbc:postgresql://host:porta/database})
     * @param user     nome utente del database
     * @param password password del database
     * @return configurazione HikariCP pronta per essere usata in un {@link HikariDataSource}
     */
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

    /**
     * Restituisce una connessione dal pool.
     *
     * @return una {@link Connection} attiva se disponibile; {@code null} se il pool non è inizializzato
     *         o se si verifica un errore nell'ottenimento della connessione
     */
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
     * <p>Chiude il pool HikariCP e rilascia le risorse.</p>
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
     * <p>Utile per test o per azzerare lo stato delle sessioni.</p>
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
