package bookrecommender.server;

import bookrecommender.common.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

            dataSource = new HikariDataSource(config);
            logger.log(Level.INFO, "HikariCP pool creato con successo.");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore inizializzazione HikariCP", e);
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource non inizializzato. Chiama connect() prima.");
        }
        return dataSource.getConnection();
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
     * Esegue un PreparedStatement e popola una lista di oggetti {@link Libro}
     * con i risultati ottenuti dal ResultSet.
     * @param risultati Lista da riempire con i libri estratti
     * @param stmt      Statement SQL già preparato da eseguire
     * @throws SQLException In caso di errore nell'esecuzione del PreparedStatement
     */
    public void resultStmt(List<Libro> risultati, PreparedStatement stmt) throws SQLException {
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

            resultStmt(risultati, stmt);

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

            resultStmt(risultati, stmt);

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
     * Esegue il login di un utente verificando username e password.
     * In caso di successo, genera un token di sessione e lo salva nel database.
     * @param username  Nome utente dell'utente
     * @param password  Password dell'utente (in chiaro o da confrontare con hash)
     * @param ipClient  Indirizzo IP del client che effettua il login
     * @return {@link Token} valido se il login è riuscito, altrimenti null
     */
    public Token loginUtente(String username, String password, String ipClient) {
        String query = "SELECT ID, PASSWORD FROM UTENTI WHERE USERNAME = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("ID");
                    String storedPassword = rs.getString("PASSWORD");
                    int rows;
                    if (password.equals(storedPassword)) {  // Puoi sostituire con hash per maggiore sicurezza
                        String token = generaToken();

                        // Salva token nel DB
                        String insert = "INSERT INTO SESSIONI_LOGIN (IDUTENTE, IP_CLIENT, TOKEN) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insert)) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setString(2, ipClient);
                            insertStmt.setString(3, token);
                            rows = insertStmt.executeUpdate();
                        }
                        if (rows > 0) {
                            return new Token(token, userId, ipClient);
                        } else {
                            return null;
                        }
                    } else return null;
                } else return null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel login di un utente", e);
        }
        return null;
    }

    /**
     * Genera un token sicuro e univoco utilizzato per identificare una sessione di login.
     * Il token è codificato in Base64 URL-safe e privo di padding.
     * @return Token generato come stringa.
     */
    private String generaToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[24];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Registra un nuovo utente nel database dopo aver verificato che username, codice fiscale ed email siano unici.
     * @param nome      Nome dell'utente
     * @param cognome   Cognome dell'utente
     * @param CF        Codice fiscale dell'utente
     * @param email     Indirizzo email dell'utente
     * @param username  Nome utente scelto
     * @param password  Password scelta (attualmente in chiaro)
     * @return {@link RegToken} contenente lo stato della registrazione per ciascun campo e l'esito complessivo
     */
    public RegToken Register(String nome, String cognome, String CF, String email, String username, String password) {
        String checkUsername = "SELECT 1 FROM UTENTI WHERE USERNAME = ?";
        String checkCF = "SELECT 1 FROM UTENTI WHERE CODICE_FISCALE = ?";
        String checkEmail = "SELECT 1 FROM UTENTI WHERE EMAIL = ?";
        String insertQuery = "INSERT INTO UTENTI (USERNAME, NOME, COGNOME, CODICE_FISCALE, EMAIL, PASSWORD) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = getConnection();
                PreparedStatement checkUStmt = conn.prepareStatement(checkUsername);
                PreparedStatement checkCFStmt = conn.prepareStatement(checkCF);
                PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmail);
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            // Verifica se esiste già un utente con username, codice fiscale o email
            checkUStmt.setString(1, username);
            checkCFStmt.setString(1, CF);
            checkEmailStmt.setString(1, email);


            try (ResultSet rsU = checkUStmt.executeQuery();
                 ResultSet rsCF = checkCFStmt.executeQuery();
                 ResultSet rsEmail = checkEmailStmt.executeQuery()) {

                boolean existsUsername = rsU.next();
                boolean existsCF = rsCF.next();
                boolean existsEmail = rsEmail.next();
                if (existsUsername || existsCF || existsEmail) {
                    return new RegToken(existsUsername, existsCF, existsEmail, false);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore nella registrazione di un utente CASE: ResultSet not valid", e);
                return new RegToken(false, false, false, false);
            }

            insertStmt.setString(1, username);
            insertStmt.setString(2, nome);
            insertStmt.setString(3, cognome);
            insertStmt.setString(4, CF);
            insertStmt.setString(5, email);
            insertStmt.setString(6, password);

            int rows = insertStmt.executeUpdate();
            if (rows > 0) {
                return new RegToken(true, true, true, true); // Registrazione avvenuta con successo
            } else {
                return new RegToken(false, false, false, false); // Errore durante la registrazione
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella registrazione di un utente CASE: InsertStatement not valid", e);
            return new RegToken(false, false, false, false);
        }
    }

    /**
     * Invalida un token eliminandolo dalla tabella delle sessioni di login.
     * @param token Token di sessione da invalidare
     * @return true se il logout ha avuto successo (token eliminato), false altrimenti
     */
    public boolean LogOut(Token token) {
        String deleteQuery = "DELETE FROM SESSIONI_LOGIN WHERE TOKEN = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setString(1, token.getToken());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il logout dell'utente", e);
            return false;
        }
    }

    /**
     * Verifica se un token fornito non è più valido (non esiste o IP non corrisponde).
     * @param token Token da validare
     * @return true se il token è invalido o inesistente, false se è valido
     */
    public boolean isTokenNotValid(Token token) {
        String query = "SELECT 1 FROM SESSIONI_LOGIN WHERE TOKEN = ? AND IP_CLIENT = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, token.getToken());
            stmt.setString(2, token.getIpClient());
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella validazione del token " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient(), e);
            return true;
        }
    }

    /**
     * Recupera un libro dal database a partire dal suo ID.
     * @param id ID del libro da recuperare
     * @return Oggetto {@link Libro} se trovato, altrimenti null
     */
    public Libro getLibro(int id) {
        String query = "SELECT * FROM LIBRI WHERE ID = ? ";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            Libro libro;
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
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
     * Crea una nuova libreria per l'utente specificato da token, associando un elenco di libri.
     * Se il token non è valido o la libreria esiste già con lo stesso nome per quell'utente, l'operazione fallisce.
     * @param token Token di sessione valido dell'utente
     * @param nome  Nome della nuova libreria
     * @param libri Lista di libri da inserire nella libreria
     * @return true se la creazione della libreria ha avuto successo, false altrimenti
     */
    public boolean creaLibreria(Token token, String nome, List<Libro> libri) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        try {
            String checkQuery = "SELECT 1 FROM LIBRERIE WHERE ID_UTENTE = ? AND TITOLO_LIBRERIA = ?";
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(checkQuery);
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, nome);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                logger.log(Level.WARNING, "Libreria con nome " + nome + " già esistente per l'utente con ID: " + token.getUserId());
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        String insertLibQuery = "INSERT INTO LIBRERIE (ID_UTENTE, TITOLO_LIBRERIA) VALUES (?, ?)";
        String insertLibroQuery = "INSERT INTO LIBRERIA_LIBRO (IDLIBRERIA, IDLIBRO) VALUES (?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement insertLibStmt = conn.prepareStatement(insertLibQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertLibStmt.setInt(1, token.getUserId());
            insertLibStmt.setString(2, nome);

            int rows = insertLibStmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = insertLibStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idLibreria = generatedKeys.getInt(1);
                        try (PreparedStatement insertLibroStmt = conn.prepareStatement(insertLibroQuery)) {
                            for (Libro libro : libri) {
                                insertLibroStmt.setInt(1, idLibreria);
                                insertLibroStmt.setInt(2, libro.getId());
                                insertLibroStmt.addBatch();
                            }
                            insertLibroStmt.executeBatch();
                        } catch (SQLException e) {
                            logger.log(Level.SEVERE, "Errore nell'inserimento dei libri nella libreria", e);
                            return false;
                        }
                        return true;
                    }
                }
            } else {
                logger.log(Level.WARNING, "Nessuna riga inserita nella libreria per l'utente con ID: " + token.getUserId());
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella creazione della libreria", e);
        }
        return false;
    }

    /**
     * Elimina una libreria per un determinato utente, identificato dal token.
     * @param token Token di sessione valido dell'utente
     * @param nome  Nome della libreria da eliminare
     * @return true se la libreria è stata eliminata correttamente, false altrimenti
     */
    public boolean eliminaLibreria(Token token, String nome) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        String deleteQuery = "DELETE FROM LIBRERIE WHERE ID_UTENTE = ? AND TITOLO_LIBRERIA = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, nome);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'eliminazione della libreria", e);
            return false;
        }
    }

    /**
     * Aggiorna il contenuto di una libreria specifica associata all'utente, aggiungendo e rimuovendo libri.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria da aggiornare.
     * @param libriAggiornati Lista aggiornata di libri che devono essere presenti nella libreria.
     * @return Una lista contenente:
     *         - [1] se l'operazione è andata a buon fine;
     *         - [0] se si è verificato un errore o se il token è invalido;
     *         - [0, idLibro, motivo, ...] se alcuni libri non possono essere eliminati, con motivo:
     *           0 → libro valutato, 1 → libro consigliato, 2 → libro presente in consigli da altri utenti.
     */
    public List<Integer> aggiornaLibreria(Token token, String nome, List<Libro> libriAggiornati) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return List.of(0);
        }
        int idLibreria;
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT id FROM librerie WHERE id_utente = ? AND titolo_libreria = ?")) {
            ps.setInt(1, token.getUserId());
            ps.setString(2, nome);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idLibreria = rs.getInt("id");
                } else {
                    return List.of(0);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero dell'id libreria per utente id " + token.getUserId(), e);
            return List.of(0);
        }
        Set<Integer> correnti = new HashSet<>();
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT idlibro FROM libreria_libro WHERE idlibreria = ?")) {
            ps.setInt(1, idLibreria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    correnti.add(rs.getInt("idlibro"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel caricamento dei libri correnti per libreria id " + idLibreria, e);
            return List.of(0);
        }
        Set<Integer> aggiornati = libriAggiornati.stream().map(Libro::getId).collect(Collectors.toSet());
        Set<Integer> daEliminare = new HashSet<>(correnti);
        daEliminare.removeAll(aggiornati);
        Set<Integer> daAggiungere = new HashSet<>(aggiornati);
        daAggiungere.removeAll(correnti);
        List<Integer> errors = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement valStmt = conn.prepareStatement("SELECT 1 FROM valutazioni WHERE id_utente = ? AND idlibro = ? LIMIT 1");
             PreparedStatement useStmt = conn.prepareStatement("SELECT 1 FROM consigli WHERE id_utente = ? AND idlibro = ? LIMIT 1");
             PreparedStatement recStmt = conn.prepareStatement("SELECT 1 FROM consigli WHERE id_utente = ? AND (lib_1 = ? OR lib_2 = ? OR lib_3 = ?) LIMIT 1")) {
            for (int id : daEliminare) {
                valStmt.setInt(1, token.getUserId());
                valStmt.setInt(2, id);
                if (valStmt.executeQuery().next()) {
                    errors.add(id);
                    errors.add(0);
                    continue;
                }
                useStmt.setInt(1, token.getUserId());
                useStmt.setInt(2, id);
                if (useStmt.executeQuery().next()) {
                    errors.add(id);
                    errors.add(1);
                    continue;
                }
                recStmt.setInt(1, token.getUserId());
                recStmt.setInt(2, id);
                recStmt.setInt(3, id);
                recStmt.setInt(4, id);
                if (recStmt.executeQuery().next()) {
                    errors.add(id);
                    errors.add(2);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nei controlli pre-eliminazione per libreria id " + idLibreria, e);
            return List.of(0);
        }
        if (!errors.isEmpty()) {
            List<Integer> result = new ArrayList<>();
            result.add(0);
            result.addAll(errors);
            return result;
        }
        String delQ = "DELETE FROM libreria_libro WHERE idlibreria = ? AND idlibro = ?";
        String insQ = "INSERT INTO libreria_libro (idlibreria, idlibro) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement delStmt = conn.prepareStatement(delQ);
             PreparedStatement insStmt = conn.prepareStatement(insQ)) {
            for (int id : daEliminare) {
                delStmt.setInt(1, idLibreria);
                delStmt.setInt(2, id);
                delStmt.addBatch();
            }
            for (int id : daAggiungere) {
                insStmt.setInt(1, idLibreria);
                insStmt.setInt(2, id);
                insStmt.addBatch();
            }
            delStmt.executeBatch();
            insStmt.executeBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'aggiornamento della libreria id " + idLibreria, e);
            return List.of(0);
        }
        return List.of(1);
    }

    /**
     * Recupera la lista di libri contenuti in una libreria specifica dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria di cui recuperare i libri.
     * @return Lista di libri presenti nella libreria, oppure {@code null} se il token non è valido o si verifica un errore.
     */
    public List<Libro> getLibreria(Token token, String nome) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        List<Libro> libri = new ArrayList<>();
        String query = "SELECT L.* FROM LIBRERIE R JOIN LIBRERIA_LIBRO LL ON R.ID = LL.IDLIBRERIA JOIN LIBRI L ON LL.IDLIBRO = L.ID WHERE R.ID_UTENTE = ? AND R.TITOLO_LIBRERIA = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, nome);
            resultStmt(libri, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero della libreria", e);
        }
        return libri;
    }

    /**
     * Recupera i nomi di tutte le librerie associate all'utente.
     * @param token Token di autenticazione dell'utente.
     * @return Lista di nomi delle librerie dell'utente, oppure {@code null} se il token è invalido o si verifica un errore.
     */
    public List<String> getLibrerie(Token token) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        List<String> librerie = new ArrayList<>();
        String query = "SELECT TITOLO_LIBRERIA FROM LIBRERIE WHERE ID_UTENTE = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    librerie.add(rs.getString("TITOLO_LIBRERIA"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero delle librerie", e);
        }
        return librerie;
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
                List<Float> valori = List.of(
                        rs.getFloat("v_stile"),
                        rs.getFloat("v_contenuto"),
                        rs.getFloat("v_gradevolezza"),
                        rs.getFloat("v_originalita"),
                        rs.getFloat("v_edizione"),
                        rs.getFloat("v_finale")
                );
                List<String> commenti = List.of(
                        rs.getString("c_stile"),
                        rs.getString("c_contenuto"),
                        rs.getString("c_gradevolezza"),
                        rs.getString("c_originalita"),
                        rs.getString("c_edizione"),
                        rs.getString("c_finale")
                );
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
     * Aggiunge una valutazione per un libro da parte dell'utente autenticato.
     * @param token Token di autenticazione dell'utente.
     * @param valutazione Oggetto contenente le valutazioni numeriche e i commenti dell'utente.
     * @return {@code true} se la valutazione è stata inserita correttamente, {@code false} in caso di token non valido, libro non posseduto o errore SQL.
     */
    public boolean addValutazione(Token token, Valutazione valutazione) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        if(!utenteContieneLibro(token.getUserId(), valutazione.getIdLibro())) {
            logger.log(Level.WARNING, "L'utente con ID: " + token.getUserId() + " non ha il libro con ID: " + valutazione.getIdLibro() + " nelle sue librerie.");
            return false;
        }
        List<String> commenti = valutazione.getCommenti();
        List<Float> valutazioni = valutazione.getValutazioni();
        String insertQuery = """
                INSERT INTO valutazioni (idlibro, id_utente, v_stile, c_stile, v_contenuto, c_contenuto,
                                         v_gradevolezza, c_gradevolezza, v_originalita, c_originalita,
                                         v_edizione, c_edizione, c_finale)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)""";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setInt(1, valutazione.getIdLibro());
            stmt.setInt(2, token.getUserId());
            stmt.setFloat(3, valutazioni.get(0));
            stmt.setString(4, commenti.get(0));
            stmt.setFloat(5, valutazioni.get(1));
            stmt.setString(6, commenti.get(1));
            stmt.setFloat(7, valutazioni.get(2));
            stmt.setString(8, commenti.get(2));
            stmt.setFloat(9, valutazioni.get(3));
            stmt.setString(10, commenti.get(3));
            stmt.setFloat(11, valutazioni.get(4));
            stmt.setString(12, commenti.get(4));
            stmt.setString(13, commenti.get(5));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.log(Level.INFO, "Valutazione aggiunta con successo per il libro con ID: " + valutazione.getIdLibro() + " da parte dell'utente con ID: " + token.getUserId());
                return true;
            } else {
                logger.log(Level.WARNING, "Nessuna riga inserita nella tabella delle valutazioni per il libro con ID: " + valutazione.getIdLibro() + " da parte dell'utente con ID: " + token.getUserId());
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'aggiunta della valutazione per il libro con ID: " + valutazione.getIdLibro(), e);
            return false;
        }
    }

    /**
     * Aggiunge un consiglio per un libro, associandolo ad altri 2-4 libri ritenuti simili o consigliabili.
     * @param token Token di autenticazione dell'utente.
     * @param libri Lista di libri dove:
     *              - il primo è quello a cui si vuole associare il consiglio;
     *              - gli altri sono i libri consigliati (almeno 2, massimo 4, null inclusi se mancanti).
     * @return {@code true} se il consiglio è stato aggiunto correttamente, {@code false} altrimenti.
     */
    public boolean addConsiglio(Token token, List<Libro> libri) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        if (libri.size() < 2) {
            logger.log(Level.WARNING, "Numero di libri per il consiglio inferiore a 2");
            return false;
        }
        if (libri.size() > 4) {
            logger.log(Level.WARNING, "Numero di libri per il consiglio superiore a 4, verranno considerati solo i primi 4");
            libri = libri.subList(0, 4);
        }
        if (libri.size() < 4) {
            for (int i = libri.size(); i < 4; i++) {
                libri.add(null);
            }
        }
        String insertQuery = """
                INSERT INTO consigli (idlibro, id_utente, lib_1, lib_2, lib_3)
                VALUES (?, ?, ?, ?, ?)""";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setInt(1, libri.get(0).getId());
            stmt.setInt(2, token.getUserId());
            stmt.setInt(3, libri.get(1).getId());
            if(libri.get(2) == null)
                stmt.setNull(4, Types.INTEGER);
            else
                stmt.setInt(4, libri.get(2).getId());
            if(libri.get(3) == null)
                stmt.setNull(5, Types.INTEGER);
            else
                stmt.setInt(5, libri.get(3).getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.log(Level.INFO, "Consiglio aggiunto con successo per il libro con ID: " + libri.get(0).getId() + " da parte dell'utente con ID: " + token.getUserId());
                return true;
            } else {
                logger.log(Level.WARNING, "Nessuna riga inserita nella tabella dei consigli per il libro con ID: " + libri.get(0).getId() + " da parte dell'utente con ID: " + token.getUserId());
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'aggiunta del consiglio per il libro con ID: " + libri.get(0).getId(), e);
            return false;
        }
    }

    /**
     * Cerca i libri posseduti dall'utente in base a una parola chiave contenuta nel titolo.
     * @param token Token di autenticazione dell'utente.
     * @param titolo Parola chiave da cercare nel titolo.
     * @return Lista di libri corrispondenti alla ricerca, oppure {@code null} in caso di token invalido o errore.
     */
    public List<Libro> cercaLibriPerTitolo(Token token, String titolo) {
        if (isTokenNotValid(token)) {
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
            resultStmt(risultati, stmt);
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
        if (isTokenNotValid(token)) {
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
            resultStmt(risultati, stmt);
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
        if (isTokenNotValid(token)) {
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
            resultStmt(risultati, stmt);
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
        if (isTokenNotValid(token)) {
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
            resultStmt(risultati, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero di tutti i libri per utente id " + token.getUserId(), e);
            return null;
        }
        return risultati;
    }

    /**
     * Modifica il nome di una libreria specifica dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param nomeAttuale Nome corrente della libreria.
     * @param nuovoNome Nuovo nome da assegnare alla libreria.
     * @return {@code true} se l'aggiornamento è avvenuto con successo, {@code false} altrimenti.
     */
    public boolean modificaNomeLibreria(Token token, String nomeAttuale, String nuovoNome) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }

        String sql = """
                     UPDATE librerie
                     SET titolo_libreria = ?
                     WHERE id_utente = ?
                     AND titolo_libreria = ?;""";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuovoNome);
            ps.setInt(2, token.getUserId());
            ps.setString(3, nomeAttuale);

            int righe = ps.executeUpdate();
            if (righe == 1) {
                logger.log(Level.INFO, "Rinominata libreria \"" + nomeAttuale + "\" in \"" + nuovoNome + "\" per utente id " + token.getUserId());
                return true;
            } else {
                logger.log(Level.WARNING, "Nessuna libreria aggiornata: non esiste una libreria \"" + nomeAttuale + "\" per utente id " + token.getUserId());
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante la modifica del nome della libreria \"" + nomeAttuale + "\" per utente id " + token.getUserId(), e);
            return false;
        }
    }

    /**
     * Verifica se l'utente possiede un determinato libro in almeno una delle sue librerie.
     * @param userId ID dell'utente.
     * @param bookId ID del libro.
     * @return {@code true} se il libro è presente, {@code false} altrimenti o in caso di errore.
     */
    public boolean utenteContieneLibro(int userId, int bookId){
        String sql = """
                SELECT EXISTS (
                SELECT 1
                FROM librerie l
                JOIN libreria_libro ll ON l.id = ll.idlibreria
                WHERE l.id_utente = ? AND ll.idlibro = ?
                )""";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        }catch(SQLException e){
            logger.log(Level.SEVERE, "Errore nel controllo se l'utente con ID: " + userId + " contiene il libro con ID: " + bookId, e);
        }
        return false;
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

    /**
     * Restituisce la data di creazione di una specifica libreria dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria.
     * @return {@link LocalDate} della creazione della libreria, oppure {@code null} se non trovata o in caso di errore.
     */
    public LocalDate dataCreazioneLibreria(Token token, String nome){
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        final String SQL =
                "SELECT libreria_time " +
                        "  FROM librerie " +
                        " WHERE id_utente = ? " +
                        "   AND titolo_libreria = ?";

        int userId = token.getUserId();

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt (1, userId);
            ps.setString(2, nome);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("libreria_time");
                    return ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.warning("Libreria non trovata per utente id " + userId + " con nome: " + nome + " - " + e.getMessage());
            return null;
        }
    }

    public boolean existVal(Token token, Libro libro) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        String sql = "SELECT 1 FROM valutazioni WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.getUserId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in existVal", e);
            return false;
        }
    }

    public boolean existCon(Token token, Libro libro) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        String sql = "SELECT 1 FROM consigli WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.getUserId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in existCon", e);
            return false;
        }
    }

    public boolean updateVal(Token token, Valutazione val) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        String sql = """
            UPDATE valutazioni
               SET c_stile = ?,  v_stile = ?,
                   c_contenuto = ?, v_contenuto = ?,
                   c_gradevolezza = ?, v_gradevolezza = ?,
                   c_originalita = ?,  v_originalita = ?,
                   c_edizione = ?,  v_edizione = ?,
                   c_finale = ?,
                   valutazione_time = CURRENT_TIMESTAMP
             WHERE id_utente = ? AND idlibro = ?
            """;
        List<Float> ratings = val.getValutazioni();
        List<String> comments = val.getCommenti();
        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  comments.get(0));
            ps.setInt(   2,  Math.round(ratings.get(0)));
            ps.setString(3,  comments.get(1));
            ps.setInt(   4,  Math.round(ratings.get(1)));
            ps.setString(5,  comments.get(2));
            ps.setInt(   6,  Math.round(ratings.get(2)));
            ps.setString(7,  comments.get(3));
            ps.setInt(   8,  Math.round(ratings.get(3)));
            ps.setString(9,  comments.get(4));
            ps.setInt(  10,  Math.round(ratings.get(4)));
            ps.setString(11, comments.size() > 5 ? comments.get(5) : "");
            ps.setInt(  12, token.getUserId());
            ps.setInt(  13, val.getIdLibro());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in updateVal", e);
            return false;
        }
    }

    public boolean updateCon(Token token, List<Libro> libri) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        if (libri.size() < 2 || libri.size() > 4) {
            return false;
        }
        String sql = """
            UPDATE consigli
               SET lib_1 = ?, lib_2 = ?, lib_3 = ?, consiglio_time = CURRENT_TIMESTAMP
             WHERE id_utente = ? AND idlibro = ?
            """;
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            int refId = libri.get(0).getId();
            ps.setObject(1, libri.size() > 1 ? libri.get(1).getId() : null, Types.INTEGER);
            ps.setObject(2, libri.size() > 2 ? libri.get(2).getId() : null, Types.INTEGER);
            ps.setObject(3, libri.size() > 3 ? libri.get(3).getId() : null, Types.INTEGER);
            ps.setInt(4, token.getUserId());
            ps.setInt(5, refId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in updateCon", e);
            return false;
        }
    }

    public boolean deleteVal(Token token, Libro libro) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        String sql = "DELETE FROM valutazioni WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.getUserId());
            ps.setInt(2, libro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in deleteVal", e);
            return false;
        }
    }

    public boolean deleteCon(Token token, Libro libro) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        String sql = "DELETE FROM consigli WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.getUserId());
            ps.setInt(2, libro.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in deleteCon", e);
            return false;
        }
    }

    public LocalDate getValDate(Token token, Libro libro) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        String sql = "SELECT valutazione_time FROM valutazioni WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.getUserId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("valutazione_time");
                    return ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in getValDate", e);
            return null;
        }
    }

    public LocalDate getConsDate(Token token, Libro libro) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        String sql = "SELECT consiglio_time FROM consigli WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.getUserId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("consiglio_time");
                    return ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in getConsDate", e);
            return null;
        }
    }

    public List<Libro> getConsiglio(Token token, Libro libro) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        String sql = "SELECT lib_1, lib_2, lib_3 FROM consigli WHERE id_utente = ? AND idlibro = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, token.getUserId());
            ps.setInt(2, libro.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                List<Libro> result = new ArrayList<>();
                result.add(getLibro(libro.getId()));
                for (int i = 0; i < 3; i++) {
                    int consigliatoId = rs.getInt("lib_" + (i+1));
                    if (!rs.wasNull()) {
                        result.add(getLibro(consigliatoId));
                    }
                }
                return result;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in getConsiglio", e);
            return null;
        }
    }

    public Valutazione getValutazione(Token token, Libro libro) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        String valSql = """
        SELECT c_stile, v_stile,
               c_contenuto, v_contenuto,
               c_gradevolezza, v_gradevolezza,
               c_originalita, v_originalita,
               c_edizione, v_edizione,
               c_finale, v_finale
          FROM valutazioni
         WHERE id_utente = ? AND idlibro = ?
        """;
        try (Connection conn = getConnection();
                PreparedStatement psVal  = conn.prepareStatement(valSql)) {
            psVal.setInt(1, token.getUserId());
            psVal.setInt(2, libro.getId());
            try (ResultSet rsV = psVal.executeQuery()) {
                if (!rsV.next()) {
                    return null;
                }
                List<Float> ratings = List.of(
                        rsV.getFloat("v_stile"),
                        rsV.getFloat("v_contenuto"),
                        rsV.getFloat("v_gradevolezza"),
                        rsV.getFloat("v_originalita"),
                        rsV.getFloat("v_edizione"),
                        rsV.getFloat("v_finale")
                );
                List<String> comments = List.of(
                        rsV.getString("c_stile"),
                        rsV.getString("c_contenuto"),
                        rsV.getString("c_gradevolezza"),
                        rsV.getString("c_originalita"),
                        rsV.getString("c_edizione"),
                        rsV.getString("c_finale")
                );
                return new Valutazione(String.valueOf(token.getUserId()), ratings, comments, libro);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore in getValutazione", e);
            return null;
        }
    }

    public boolean cambiaPassword(Token token, String newPassword) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        String update = "UPDATE UTENTI SET PASSWORD = ? WHERE ID = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, token.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel cambio password per utente " + token.getUserId(), e);
        }
        return false;
    }

    public boolean eliminaAccount(Token token) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        String delConsigli = "DELETE FROM consigli WHERE id_utente = ?";
        String delValutazioni = "DELETE FROM valutazioni WHERE id_utente = ?";
        String delLibrerie = "DELETE FROM librerie WHERE id_utente = ?";
        String delSessioni = "DELETE FROM sessioni_login WHERE idutente = ?";
        String delUtente = "DELETE FROM utenti WHERE id = ?";
        try {
            Connection conn = getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(delConsigli);
                 PreparedStatement p2 = conn.prepareStatement(delValutazioni);
                 PreparedStatement p3 = conn.prepareStatement(delLibrerie);
                 PreparedStatement p4 = conn.prepareStatement(delSessioni);
                 PreparedStatement p5 = conn.prepareStatement(delUtente)) {
                p1.setInt(1, token.getUserId()); p1.executeUpdate();
                p2.setInt(1, token.getUserId()); p2.executeUpdate();
                p3.setInt(1, token.getUserId()); p3.executeUpdate();
                p4.setInt(1, token.getUserId()); p4.executeUpdate();
                p5.setInt(1, token.getUserId());
                boolean ok = p5.executeUpdate() > 0;
                if (ok)
                    conn.commit();
                else
                    conn.rollback();
                return ok;
            } catch (SQLException e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Errore nell'eliminazione account utente " + token.getUserId(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore di gestione transazione per eliminazione account", e);
        }
        return false;
    }
}