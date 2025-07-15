package bookrecommender.server;

import bookrecommender.common.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;
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
     * Cerca libri il cui titolo contiene la stringa fornita (case-insensitive)
     * @param titolo Titolo o parte del titolo da cercare
     * @return Lista di libri che soddisfano il criterio di ricerca
     */
    public List<Libro> cercaLibriPerTitolo(String titolo) {
        List<Libro> risultati = new ArrayList<>();
        String query = "SELECT * FROM LIBRI WHERE LOWER(TITOLO) LIKE LOWER(?)";

        return selectLibro(titolo, risultati, query);
    }

    /**
     * Cerca libri scritti da autori il cui nome contiene la stringa fornita (case-insensitive)
     * @param author Nome o parte del nome dell'autore
     * @return Lista di libri scritti dall'autore specificato
     */
    public List<Libro> cercaLibriPerAutore(String author) {
        List<Libro> risultati = new ArrayList<>();
        String query = "SELECT * FROM LIBRI WHERE LOWER(AUTORE) LIKE LOWER(?)";

        return selectLibro(author, risultati, query);
    }

    /**
     * Esegue una query parametrica per cercare libri in base a un titolo/autore,
     * popolando la lista fornita.
     * @param titolo    Parametro di ricerca (titolo o autore)
     * @param risultati Lista da riempire con i risultati
     * @param query     Query SQL parametrica da eseguire
     * @return Lista di libri risultanti dalla query
     */
    public List<Libro> selectLibro(String titolo, List<Libro> risultati, String query) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + titolo + "%");

            ServerUtil.getInstance().resultStmt(risultati, stmt);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di un libro", e);
        }
        return risultati;
    }

    /**
     * Cerca libri in base all'autore e all'anno di pubblicazione.
     * @param author Nome dell'autore
     * @param year   Anno di pubblicazione come stringa (anche parziale)
     * @return Lista di libri che corrispondono ai criteri
     */
    public List<Libro> cercaLibriPerAutoreAnno(String author, String year) {
        List<Libro> risultati = new ArrayList<>();
        String query = "SELECT * FROM LIBRI WHERE LOWER(AUTORE) LIKE LOWER(?) AND CAST(ANNOPUBBLICAZIONE AS TEXT) LIKE ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + author + "%");
            stmt.setString(2, "%" + year + "%");

            ServerUtil.getInstance().resultStmt(risultati, stmt);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di un libro", e);
        }
        return risultati;
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


    /**
     * Recupera i dettagli di un libro, inclusi commenti e valutazioni da parte degli utenti e consigli correlati.
     * @param libro Libro di cui recuperare i dettagli.
     * @return Oggetto {@link Libro_Details} contenente le valutazioni e i consigli ricevuti dal libro.
     */
    public Libro_Details getDetails(Libro libro) {
        List<Valutazione> valutazioni = new ArrayList<>();
        Hashtable<String, List<Libro>> consigli = new Hashtable<>();

        String queryValutazioni = """
            SELECT u.username, v.v_stile, v.c_stile, v.v_contenuto, v.c_contenuto,
                   v.v_gradevolezza, v.c_gradevolezza, v.v_originalita, v.c_originalita,
                   v.v_edizione, v.c_edizione, v.v_finale, v.c_finale
            FROM valutazioni v
            JOIN utenti u ON v.id_utente = u.id
            WHERE v.idlibro = ?
        """;

        try (Connection conn = getConnection();
                PreparedStatement psValutazioni = conn.prepareStatement(queryValutazioni)) {
            psValutazioni.setInt(1, libro.getId());
            ResultSet rs = psValutazioni.executeQuery();
            while (rs.next()) {
                List<Float> valori = ServerUtil.getInstance().getVotiVal(rs);
                List<String> commenti = ServerUtil.getInstance().getComVal(rs);
                valutazioni.add(new Valutazione(rs.getString("username"), valori, commenti, libro));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero delle valutazioni per il libro con ID: " + libro.getId(), e);
        }

        String queryConsigli = """
            SELECT u.username, l.id, l.titolo, l.autore, l.descrizione, l.categoria,
                   l.editore, l.prezzo, l.annopubblicazione, l.mesepubblicazione
            FROM consigli c
            JOIN utenti u ON c.id_utente = u.id
            JOIN libri l ON l.id IN (c.lib_1, c.lib_2, c.lib_3)
            WHERE c.idlibro = ?
        """;

        try (Connection conn = getConnection();
                PreparedStatement psConsigli = conn.prepareStatement(queryConsigli)) {
            psConsigli.setInt(1, libro.getId());
            ResultSet rs = psConsigli.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                Libro libroC = new Libro(
                        rs.getInt("id"),
                        rs.getString("titolo"),
                        rs.getString("autore"),
                        rs.getString("descrizione"),
                        rs.getString("categoria"),
                        rs.getString("editore"),
                        rs.getFloat("prezzo"),
                        rs.getShort("annopubblicazione"),
                        rs.getShort("mesepubblicazione")
                );
                consigli.computeIfAbsent(username, k -> new ArrayList<>()).add(libroC);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero dei consigli per il libro con ID: " + libro.getId(), e);
        }

        return new Libro_Details(consigli, valutazioni);
    }


    /**
     * Cerca i libri posseduti dall'utente in base a una parola chiave contenuta nel titolo.
     * @param token Token di autenticazione dell'utente.
     * @param titolo Parola chiave da cercare nel titolo.
     * @return Lista di libri corrispondenti alla ricerca, oppure {@code null} in caso di token invalido o errore.
     */
    public List<Libro> cercaLibriPerTitolo(Token token, String titolo) {
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        List<Libro> risultati = new ArrayList<>();
        String query = """
                SELECT DISTINCT l.*
                FROM libri AS l
                JOIN libreria_libro AS ll
                ON ll.idlibro = l.id
                JOIN librerie AS lr
                ON lr.id = ll.idlibreria
                WHERE lr.id_utente = ?
                AND l.titolo ILIKE '%' || ? || '%';""";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, titolo);
            ServerUtil.getInstance().resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di un libro con titolo: " + titolo, e);
            return null;
        }
        return risultati;
    }

    /**
     * Cerca i libri posseduti dall'utente in base a una parola chiave contenuta nel nome dell'autore.
     * @param token Token di autenticazione dell'utente.
     * @param author Nome (parziale o completo) dell'autore da cercare.
     * @return Lista di libri corrispondenti, oppure {@code null} in caso di token invalido o errore.
     */
    public List<Libro> cercaLibriPerAutore(Token token, String author) {
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        List<Libro> risultati = new ArrayList<>();
        String query = """
                SELECT DISTINCT l.*
                FROM libri AS l
                JOIN libreria_libro AS ll
                ON ll.idlibro = l.id
                JOIN librerie AS lr
                ON lr.id = ll.idlibreria
                WHERE lr.id_utente = ?
                AND LOWER(l.autore) LIKE LOWER(?);""";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, "%" + author + "%");
            ServerUtil.getInstance().resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di libri per autore: " + author, e);
            return null;
        }
        return risultati;
    }

    /**
     * Cerca i libri posseduti dall'utente in base al nome dell'autore e all'anno di pubblicazione.
     * @param token Token di autenticazione dell'utente.
     * @param author Nome dell'autore.
     * @param year Anno di pubblicazione (anche parziale).
     * @return Lista di libri corrispondenti, oppure {@code null} in caso di token invalido o errore.
     */
    public List<Libro> cercaLibriPerAutoreAnno(Token token, String author, String year) {
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        List<Libro> risultati = new ArrayList<>();
        String query = """
                SELECT DISTINCT l.*
                FROM libri AS l
                JOIN libreria_libro AS ll
                ON ll.idlibro = l.id
                JOIN librerie AS lr
                ON lr.id = ll.idlibreria
                WHERE lr.id_utente = ?
                AND LOWER(l.autore) LIKE LOWER(?)
                AND CAST(l.annopubblicazione AS TEXT) LIKE ?;""";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, "%" + author + "%");
            stmt.setString(3, "%" + year + "%");
            ServerUtil.getInstance().resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di libri per autore e anno: autore=" + author + " anno=" + year, e);
            return null;
        }
        return risultati;
    }

    /**
     * Recupera tutti i libri posseduti dall'utente in qualsiasi libreria.
     * @param token Token di autenticazione dell'utente.
     * @return Lista completa dei libri posseduti, oppure {@code null} in caso di token invalido o errore.
     */
    public List<Libro> cercaTuttiLibriUtente(Token token) {
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }

        List<Libro> risultati = new ArrayList<>();
        String query = """
        SELECT DISTINCT l.*
          FROM libri AS l
          JOIN libreria_libro AS ll
            ON ll.idlibro = l.id
          JOIN librerie AS lr
            ON lr.id = ll.idlibreria
         WHERE lr.id_utente = ?;
        """;

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.getUserId());
            ServerUtil.getInstance().resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero di tutti i libri per utente id " + token.getUserId(), e);
            return null;
        }
        return risultati;
    }

    /**
     * Controlla se un libro ha almeno una valutazione o un'associazione in un consiglio.
     * @param idLibro ID del libro da controllare.
     * @return {@code true} se il libro ha valutazioni o è incluso in un consiglio, {@code false} altrimenti.
     */
    public boolean haValConsAss(int idLibro){
        String sql = """
                SELECT
                ( EXISTS (SELECT 1 FROM valutazioni WHERE idlibro = ?)
                OR EXISTS (SELECT 1 FROM consigli WHERE idlibro = ?)
                ) AS presente
                """;

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            ps.setInt(2, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("presente");
                }
            }
        }catch(SQLException e){
            logger.log(Level.SEVERE, "Errore nel controllo se il libro con ID: " + idLibro + " ha valutazioni, consigli o associazioni", e);
        }
        return false;
    }
}