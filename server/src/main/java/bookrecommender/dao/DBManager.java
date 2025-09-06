package bookrecommender.dao;

import bookrecommender.util.ServerUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.List;
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
     * @return {@link ServerUtil.DBChecks}, se il pool è stato inizializzato (o era già attivo) {@code success()} = {@code true}
     * e {@code errorMessage} = {@code null}, in caso di errore {@code success()} = {@code false} e {@code errorMessage} con l'errore corrispondente
     */
    public ServerUtil.DBChecks connect(String name, String port, String user, String password) {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.log(Level.WARNING, "Pool già inizializzato.");
            return new ServerUtil.DBChecks(true, null);
        }
        String url = "jdbc:postgresql://localhost:" + port + "/" + name;
        ServerUtil.DBChecks checks = SchemaCheck(url,user, password);
        if(checks.success()) {
            try {
                HikariConfig config = getHikariConfig(url, user, password);
                dataSource = new HikariDataSource(config);
                logger.log(Level.INFO, "HikariCP pool creato con successo.");
                return new ServerUtil.DBChecks(true, null);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Errore inizializzazione HikariCP", e);
                return new ServerUtil.DBChecks(false, "Connection pool error");
            }
        }else
            return checks;
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

    /**
     * Rappresenta un singolo controllo di conformità dello schema.
     * Ogni controllo è composto da un nome descrittivo e da una query SQL che deve restituire
     * una singola riga con un valore booleano/numerico interpretabile come vero/falso.
     *
     * @param name nome leggibile del controllo (usato nei log in caso di fallimento)
     * @param sql  query SQL che deve restituire TRUE (o un numero > 0) se il controllo è soddisfatto
     */
    private record Check(String name, String sql) {}

    /**
     * Esegue una batteria di controlli sullo schema del database (tabelle, PK, UNIQUE, FK e principali colonne)
     * seguendo una strategia "fail-fast": al primo controllo che fallisce viene registrato un errore
     * e viene restituito un risultato negativo.
     * <p>
     * Le query sono costruite su {@code information_schema} e assumono schema {@code public}.
     * Alcuni confronti (PK/UNIQUE/FK) effettuano cast a {@code ::text} per evitare mismatch di tipi
     * (es. {@code information_schema.sql_identifier[]} vs {@code text[]}).
     *
     * @param url  URL JDBC (es. {@code jdbc:postgresql://host:5432/db})
     * @param user utente del DB
     * @param pass password del DB
     * @return {@link ServerUtil.DBChecks}esito aggregato dei controlli; {@code success=true} se tutti i check restituiscono {@code true}
     */
    private static ServerUtil.DBChecks SchemaCheck(String url, String user, String pass){
        List<Check> checks = List.of(
                c("Tabella 'libri' esiste", existsTable("libri")),
                c("Tabella 'utenti' esiste", existsTable("utenti")),
                c("Tabella 'valutazioni' esiste", existsTable("valutazioni")),
                c("Tabella 'consigli' esiste", existsTable("consigli")),
                c("Tabella 'librerie' esiste", existsTable("librerie")),
                c("Tabella 'libreria_libro' esiste", existsTable("libreria_libro")),
                c("Tabella 'sessioni_login' esiste", existsTable("sessioni_login")),

                c("PK libri(id)", pkEquals("libri","id")),
                c("libri.id serial", colIs("libri","id","integer", "NO")),
                c("libri.titolo text not null", colIs("libri","titolo","text", "NO")),
                c("libri.autore text not null", colIs("libri","autore","text", "NO")),
                c("libri.descrizione text", colIs("libri","descrizione","text", "YES")),
                c("libri.categoria text", colIs("libri","categoria","text", "YES")),
                c("libri.editore text", colIs("libri","editore","text", "YES")),
                c("libri.prezzo numeric(3,1)", colIsNumeric("libri","prezzo", 1)),
                c("libri.annopubblicazione smallint", colIs("libri","annopubblicazione","smallint", "YES")),
                c("libri.mesepubblicazione smallint", colIs("libri","mesepubblicazione","smallint", "YES")),

                c("PK utenti(id)", pkEquals("utenti","id")),
                c("utenti.id serial", colIs("utenti","id","integer", "NO")),
                c("utenti.username unique", uniqueOn("utenti", new String[]{"username"})),
                c("utenti.codice_fiscale unique", uniqueOn("utenti", new String[]{"codice_fiscale"})),
                c("utenti.email unique", uniqueOn("utenti", new String[]{"email"})),

                c("valutazioni.idlibro FK -> libri(id)", fkExists("valutazioni", new String[]{"idlibro"},"libri",new String[]{"id"})),
                c("valutazioni.id_utente FK -> utenti(id)", fkExists("valutazioni", new String[]{"id_utente"},"utenti",new String[]{"id"})),
                c("valutazioni.v_stile smallint not null", colIs("valutazioni","v_stile","smallint", "NO")),
                c("valutazioni.v_contenuto smallint not null", colIs("valutazioni","v_contenuto","smallint", "NO")),
                c("valutazioni.v_gradevolezza smallint not null", colIs("valutazioni","v_gradevolezza","smallint", "NO")),
                c("valutazioni.v_originalita smallint not null", colIs("valutazioni","v_originalita","smallint", "NO")),
                c("valutazioni.v_edizione smallint not null", colIs("valutazioni","v_edizione","smallint", "NO")),
                c("valutazioni.v_finale numeric(3,2)", colIsNumeric("valutazioni","v_finale", 2)),

                c("consigli.idlibro FK -> libri(id)", fkExists("consigli", new String[]{"idlibro"},"libri",new String[]{"id"})),
                c("consigli.id_utente FK -> utenti(id)", fkExists("consigli", new String[]{"id_utente"},"utenti",new String[]{"id"})),
                c("consigli.lib_1 FK -> libri(id)", fkExists("consigli", new String[]{"lib_1"},"libri",new String[]{"id"})),
                c("consigli.lib_2 FK -> libri(id)", fkExists("consigli", new String[]{"lib_2"},"libri",new String[]{"id"})),
                c("consigli.lib_3 FK -> libri(id)", fkExists("consigli", new String[]{"lib_3"},"libri",new String[]{"id"})),

                c("PK librerie(id)", pkEquals("librerie","id")),
                c("librerie.id_utente FK -> utenti(id)", fkExists("librerie", new String[]{"id_utente"},"utenti",new String[]{"id"})),

                c("PK libreria_libro(idlibreria,idlibro)", pkEquals("libreria_libro","idlibreria","idlibro")),
                c("libreria_libro.idlibreria FK -> librerie(id)", fkExists("libreria_libro", new String[]{"idlibreria"},"librerie",new String[]{"id"})),
                c("libreria_libro.idlibro FK -> libri(id)", fkExists("libreria_libro", new String[]{"idlibro"},"libri",new String[]{"id"})),

                c("PK sessioni_login(id)", pkEquals("sessioni_login","id")),
                c("sessioni_login.idutente FK -> utenti(id)", fkExists("sessioni_login", new String[]{"idutente"},"utenti",new String[]{"id"})),
                c("sessioni_login.token unique", uniqueOn("sessioni_login", new String[]{"token"}))
        );
        return run(url, user, pass, checks);
    }

    /**
     * Esegue in sequenza i controlli passati; interrompe al primo controllo non soddisfatto
     * (fail-fast) restituendo un {@link ServerUtil.DBChecks} negativo.
     * In caso di eccezione durante l’esecuzione di una query, logga l’errore e restituisce esito negativo.
     *
     * @param url  URL JDBC
     * @param user utente DB
     * @param pass password DB
     * @param checks lista dei controlli da eseguire (in ordine)
     * @return {@link ServerUtil.DBChecks} con {@code success=true} se tutti i controlli hanno esito positivo
     */
    private static ServerUtil.DBChecks run(String url, String user, String pass, List<Check> checks){
        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            for (Check chk : checks) {
                if (!isTrue(c, chk.sql())) {
                    logger.log(Level.SEVERE, "SCHEMA NON CONFORME  Check fallito: " + chk.name());
                    return new ServerUtil.DBChecks(false, "Schema non conforme!");
                }
            }
            return new ServerUtil.DBChecks(true, null);
        }catch(Exception e) {
            logger.log(Level.SEVERE, "SCHEMA NON CONFORME  Check fallito: " + e.getMessage());
            return new ServerUtil.DBChecks(false, "Errore durante il check dello schema");
        }
    }

    /**
     * Esegue una query che deve restituire esattamente una riga e una colonna, interpretata come:
     * <ul>
     *   <li>booleano: TRUE/FALSE</li>
     *   <li>numero: vero se &gt; 0</li>
     *   <li>stringa: accettati "t", "true" o "1" (case-insensitive)</li>
     * </ul>
     *
     * @param c   connessione JDBC aperta
     * @param sql query SQL del controllo (ritorno a singola colonna/riga)
     * @return {@code true} se il valore è interpretabile come vero, altrimenti {@code false}
     * @throws SQLException in caso di errore di esecuzione della query
     */
    private static boolean isTrue(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (!rs.next()) return false;
            Object v = rs.getObject(1);
            if (v == null) return false;
            if (v instanceof Boolean b) return b;
            if (v instanceof Number n)  return n.longValue() > 0;
            String s = v.toString();
            return "t".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s) || "1".equals(s);
        }
    }

    /**
     * Factory di comodo per creare un {@link Check}.
     *
     * @param name nome leggibile del controllo (per i log)
     * @param sql  query SQL associata
     * @return istanza di {@link Check}
     */
    private static Check c(String name, String sql) { return new Check(name, sql); }

    /**
     * Escapa i singoli apici in una stringa destinata a essere interpolata in SQL literal,
     * sostituendo {@code '} con {@code ''}.
     *
     * @param s stringa di input (può essere {@code null})
     * @return stringa con apici raddoppiati (mai {@code null})
     */
    private static String q(String s) { return s.replace("'", "''"); }

    /**
     * Genera una query booleana che verifica l’esistenza di una tabella nello schema {@code public}.
     *
     * @param table nome tabella
     * @return SQL che ritorna TRUE se la tabella esiste
     */
    private static String existsTable(String table) {
        return "SELECT EXISTS (SELECT 1 FROM information_schema.tables " +
                "WHERE table_schema='public' AND table_name='%s');".formatted(q(table));
    }

    /**
     * Genera una query booleana che verifica che la Primary Key della tabella corrisponda
     * esattamente (ordine incluso) all’elenco di colonne specificato.
     * <p>
     * Implementazione basata su {@code information_schema.table_constraints} e
     * {@code information_schema.key_column_usage}. Si effettua un cast a {@code ::text}
     * degli identificatori per confrontarli con un literal {@code text[]} ed evitare errori di tipo.
     *
     * @param table        nome tabella
     * @param colsInOrder  colonne della PK in ordine
     * @return SQL che ritorna TRUE se la PK coincide con {@code colsInOrder}
     */
    private static String pkEquals(String table, String... colsInOrder) {
        String array = "ARRAY[" + String.join(",", java.util.Arrays.stream(colsInOrder)
                .map(c -> "'" + q(c) + "'").toList()) + "]::text[]";
        return """
        SELECT COALESCE((
          SELECT array_agg(kcu.column_name::text ORDER BY kcu.ordinal_position) = %s
          FROM information_schema.table_constraints tc
          JOIN information_schema.key_column_usage kcu
            ON tc.constraint_name=kcu.constraint_name
           AND tc.table_schema=kcu.table_schema
           AND tc.table_name=kcu.table_name
          WHERE tc.table_schema='public' AND tc.table_name='%s' AND tc.constraint_type='PRIMARY KEY'
        ), FALSE);
    """.formatted(array, q(table));
    }

    /**
     * Genera una query booleana che verifica l’esistenza di una colonna con:
     * nome, {@code data_type} (valore di {@code information_schema.columns.data_type})
     * e nullability attesa.
     * <p>
     * Nota: questo controllo non valida {@code character_maximum_length} o precision/scale
     * (per NUMERIC usare {@link #colIsNumeric(String, String, int)}).
     *
     * @param table      nome tabella
     * @param column     nome colonna
     * @param dataType   valore atteso di {@code information_schema.columns.data_type} (es. "text", "integer")
     * @param isNullable "YES" o "NO" (se {@code null} il requisito di nullability è ignorato)
     * @return SQL che ritorna TRUE se la colonna rispetta i vincoli indicati
     */
    private static String colIs(String table, String column, String dataType, String isNullable) {
        String sql = """
            SELECT EXISTS (
              SELECT 1
              FROM information_schema.columns
              WHERE table_schema='public'
                AND table_name='%s'
                AND column_name='%s'
                AND data_type='%s'
        """.formatted(q(table), q(column), q(dataType));
        if (isNullable != null) sql += " AND is_nullable='" + q(isNullable) + "'";
        return sql + ");";
    }

    /**
     * Genera una query booleana che verifica che una colonna NUMERIC abbia
     * {@code numeric_precision=3} e la {@code numeric_scale} specificata.
     * <p>
     * Questo metodo è specializzato per i casi presenti nello schema (es. {@code numeric(3,1)} o {@code numeric(3,2)}).
     *
     * @param table  nome tabella
     * @param column nome colonna
     * @param scale  scala decimale attesa (es. 1 o 2)
     * @return SQL che ritorna TRUE se la colonna è NUMERIC(3, scale)
     */
    private static String colIsNumeric(String table, String column, int scale) {
        return """
            SELECT EXISTS (
              SELECT 1
              FROM information_schema.columns
              WHERE table_schema='public'
                AND table_name='%s'
                AND column_name='%s'
                AND data_type='numeric'
                AND numeric_precision=%d
                AND numeric_scale=%d
            );
        """.formatted(q(table), q(column), 3, scale);
    }

    /**
     * Genera una query booleana che verifica l’esistenza di un indice UNIQUE sulla tabella
     * esattamente sulle colonne indicate (ordine incluso).
     * <p>
     * Implementazione basata su {@code information_schema.table_constraints} e
     * {@code information_schema.key_column_usage} con aggregazione e confronto di array
     * (si effettua cast a {@code ::text}).
     *
     * @param table       nome tabella
     * @param colsInOrder elenco di colonne in ordine per l’univocità
     * @return SQL che ritorna TRUE se esiste un vincolo UNIQUE su quelle colonne
     */
    private static String uniqueOn(String table, String[] colsInOrder) {
        String array = "ARRAY[" + String.join(",", java.util.Arrays.stream(colsInOrder)
                .map(c -> "'" + q(c) + "'").toList()) + "]::text[]";
        return """
        SELECT EXISTS (
          SELECT 1
          FROM (
            SELECT array_agg(kcu.column_name::text ORDER BY kcu.ordinal_position) AS cols
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
              ON tc.constraint_name=kcu.constraint_name
             AND tc.table_schema=kcu.table_schema
             AND tc.table_name=kcu.table_name
            WHERE tc.table_schema='public' AND tc.table_name='%s' AND tc.constraint_type='UNIQUE'
            GROUP BY tc.constraint_name
          ) u
          WHERE u.cols = %s
        );
    """.formatted(q(table), array);
    }

    /**
     * Genera una query booleana che verifica l’esistenza di una (o più) FOREIGN KEY
     * dalla tabella/colonne {@code fromTable.fromCols[]} alla tabella/colonne {@code toTable.toCols[]},
     * confrontando le coppie collegate nell’ordine dichiarato.
     * <p>
     * Implementazione basata su {@code information_schema.table_constraints},
     * {@code key_column_usage}, {@code referential_constraints} e
     * {@code constraint_column_usage}, con aggregazione e cast a {@code ::text}.
     *
     * @param fromTable tabella sorgente (FK)
     * @param fromCols  colonne sorgente in ordine
     * @param toTable   tabella di destinazione (PK/UNIQUE)
     * @param toCols    colonne di destinazione in ordine
     * @return SQL che ritorna TRUE se esiste la FK attesa
     */
    private static String fkExists(String fromTable, String[] fromCols, String toTable, String[] toCols) {
        String fromArray = "ARRAY[" + String.join(",", java.util.Arrays.stream(fromCols)
                .map(c -> "'" + q(c) + "'").toList()) + "]::text[]";
        String toArray   = "ARRAY[" + String.join(",", java.util.Arrays.stream(toCols)
                .map(c -> "'" + q(c) + "'").toList()) + "]::text[]";
        return """
        SELECT EXISTS (
          SELECT 1
          FROM (
            SELECT tc.table_name AS from_table,
                   array_agg(kcu.column_name::text ORDER BY kcu.ordinal_position) AS from_cols,
                   ccu.table_name AS to_table,
                   array_agg(ccu.column_name::text ORDER BY kcu.ordinal_position) AS to_cols
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
              ON tc.constraint_name=kcu.constraint_name
             AND tc.table_schema=kcu.table_schema
            JOIN information_schema.referential_constraints rc
              ON rc.constraint_name=tc.constraint_name
             AND rc.constraint_schema=tc.table_schema
            JOIN information_schema.constraint_column_usage ccu
              ON ccu.constraint_name=rc.unique_constraint_name
             AND ccu.constraint_schema=rc.unique_constraint_schema
            WHERE tc.constraint_type='FOREIGN KEY'
              AND tc.table_schema='public'
              AND tc.table_name='%s'
            GROUP BY tc.constraint_name, tc.table_name, ccu.table_name
          ) fk
          WHERE fk.from_table='%s'
            AND fk.from_cols=%s
            AND fk.to_table='%s'
            AND fk.to_cols=%s
        );
    """.formatted(q(fromTable), q(fromTable), fromArray, q(toTable), toArray);
    }
}
