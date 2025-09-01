package bookrecommender;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/**
 * Utility per l’inizializzazione di un database PostgreSQL:
 * chiede i parametri di connessione, garantisce l’esistenza del DB di destinazione
 * e applica gli script SQL presenti nella cartella <code>data</code> (ordine alfabetico).
 * <p>
 * Supporta due modalità di esecuzione:
 * <ul>
 *   <li><b>Transazione per file</b> (default): ogni file .sql è eseguito in una singola transazione
 *       con commit/rollback atomico.</li>
 *   <li><b>Auto-commit</b>: esegue ogni statement con commit immediato.</li>
 * </ul>
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */

public class DBCreator {

    /** Host di default del server PostgreSQL (usato se l’utente preme Invio). */
    private static final String DEF_HOST = "localhost";
    /** Porta di default di PostgreSQL (usata se l’utente preme Invio). */
    private static final String DEF_PORT = "5432";
    /** Utente di default per la connessione al database. */
    private static final String DEF_USER = "postgres";
    /** Nome di default del database di destinazione (verrà creato se assente). */
    private static final String DEF_DB = "bookrecommender";
    /**
     * Flag di default per l’esecuzione “transaction-per-file”.
     * Se true, ogni file .sql viene eseguito in una transazione dedicata
     * (commit se tutto OK, rollback in caso di errori).
     */
    private static final boolean TX_PER_FILE = true;

    /**
     * Entry point dell’applicazione console.
     *
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        new DBCreator().run();
    }

    /**
     * Flusso principale dell’utility:
     * <ol>
     *   <li>Legge parametri di connessione (con valori di default).</li>
     *   <li>Determina la directory dell’eseguibile e risolve la cartella <code>data</code>.</li>
     *   <li>Si connette al DB <code>postgres</code> per garantire l’esistenza del DB target.</li>
     *   <li>Esegue in ordine alfabetico i file .sql contro il DB target
     *       applicando la politica di commit scelta.</li>
     * </ol>
     * Gestisce e logga eccezioni SQL/IO/impreviste.
     */
    private void run() {
        Scanner in = new Scanner(System.in);
        System.out.println("=== PostgreSQL DB Setup ===");
        String host = scanOrDefault(in,"host", DEF_HOST);
        String port = scanOrDefault(in,"port", DEF_PORT);
        String user = scanOrDefault(in,"user", DEF_USER);
        String pass = scanOrDefault(in,"password (visible)", "");
        String db = scanOrDefault(in,"new database name", DEF_DB);
        System.out.println("L'auto-commit è disattivato per default, per attivarlo scrivere > autocommit:");
        String ac = in.nextLine().trim();
        boolean txPerFile = TX_PER_FILE;
        if (ac.equalsIgnoreCase("autocommit")) {
            System.out.println("[INFO] Auto-commit attivato.");
            txPerFile = false;
        }else
            System.out.println("[INFO] Auto-commit disattivato (default).");
        Path exeDir;
        try {
            exeDir = getExecutableDir();
        } catch (Exception e) {
            System.err.println("Impossibile determinare la cartella dell'eseguibile: " + e.getMessage());
            return;
        }
        Path sqlDir;
        try {
            sqlDir = resolveSqlDir(exeDir);
        }catch (Exception e) {
            return;
        }
        System.out.println("------------------------------------------------------------");
        System.out.printf("Exe : %s%n", exeDir);
        System.out.printf("SQL : %s%n", sqlDir);
        System.out.printf("Host: %s%nPort: %s%nUser: %s%nDB  : %s%n", host, port, user, db);
        System.out.println("------------------------------------------------------------");

        try {
            String maintenanceDbUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, "postgres");
            try (Connection adminConn = DriverManager.getConnection(maintenanceDbUrl, user, pass)) {
                ensureDatabaseExists(adminConn, db);
            }

            String targetDbUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
            try (Connection targetConn = DriverManager.getConnection(targetDbUrl, user, pass)) {
                targetConn.setAutoCommit(true);

                List<Path> files = collectSqlFiles(sqlDir);
                if (files.isEmpty()) {
                    System.out.println("[WARN] Nessun file .sql trovato in: " + sqlDir);
                } else {
                    System.out.println("[INFO] Esecuzione file SQL (ordine alfabetico):");
                    for (Path p : files) {
                        System.out.println("  -> " + p.getFileName());
                        String script = readFileUtf8(p);
                        List<String> statements = splitPostgresStatements(script);
                        executeStatements(targetConn, statements, p.getFileName().toString(), txPerFile);
                    }
                }
            }

            System.out.println("\n✅ Completato.");
        } catch (SQLException e) {
            System.err.println("Errore SQL: " + e.getMessage());
            e.printStackTrace(System.err);
        } catch (IOException e) {
            System.err.println("Errore IO: " + e.getMessage());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            System.err.println("Errore imprevisto: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Determina la cartella che contiene gli script SQL.
     * <p>Priorità di risoluzione:</p>
     * <ol>
     *   <li>Proprietà di sistema <code>sql.dir</code> (se valida).</li>
     *   <li>Varianti comuni di output (<code>classes</code>, <code>target_DBCreator</code>, <code>bin</code>→<code>data</code>).</li>
     *   <li>Risalita fino a 6 livelli cercando una cartella <code>data</code>.</li>
     * </ol>
     *
     * @param exeDir directory dell’eseguibile (jar o cartella class)
     * @return path assoluto della cartella con gli script SQL
     * @throws IOException se la cartella non è trovata o <code>sql.dir</code> non è valida
     */
    private Path resolveSqlDir(Path exeDir) throws IOException {
        String prop = System.getProperty("sql.dir");
        if (prop != null && !prop.isBlank()) {
            Path p = Paths.get(prop).toAbsolutePath().normalize();
            if (Files.isDirectory(p)) return p;
            throw new IOException("sql.dir non esiste: " + p);
        }
        Path p = exeDir;

        if (p.getFileName() != null && p.getFileName().toString().equalsIgnoreCase("classes")) {
            p = p.getParent();
        }

        if (p != null && p.getFileName() != null &&
                p.getFileName().toString().equalsIgnoreCase("target_DBCreator")) {
            p = p.getParent();
        }

        if (p != null && p.getFileName() != null &&
                p.getFileName().toString().equalsIgnoreCase("bin")) {
            Path candidate = p.getParent().resolve("data").normalize();
            if (Files.isDirectory(candidate)) return candidate;
        }

        Path cur = exeDir;
        for (int i = 0; i < 6 && cur != null; i++, cur = cur.getParent()) {
            Path candidate = cur.resolve("data").normalize();
            if (Files.isDirectory(candidate)) return candidate;
        }

        throw new IOException("Cartella 'data' non trovata partendo da: " + exeDir +
                " (usa -Dsql.dir=\"/percorso/alla/data\" per forzare)");
    }

    /**
     * Legge una riga da console mostrando un valore di default.
     *
     * @param sc    scanner per l’input standard
     * @param label etichetta del parametro mostrata all’utente
     * @param def   valore di default usato se l’utente invia una riga vuota
     * @return stringa inserita dall’utente o il valore di default
     */
    private static String scanOrDefault(Scanner sc, String label, String def) {
        System.out.println("DEFAULT [" + def + "] - " + label + ":");
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("[INFO] Using default: " + def);
            return def;
        }
        return line;
    }

    /**
     * Restituisce la directory “dell’eseguibile”: se l’app gira da JAR, la cartella del JAR;
     * altrimenti la cartella delle classi compilate.
     *
     * @return path della directory dell’eseguibile
     * @throws URISyntaxException se l’URL del code source non è convertibile in URI
     */
    private Path getExecutableDir() throws URISyntaxException {
        var url = DBCreator.class.getProtectionDomain().getCodeSource().getLocation();
        Path p = Paths.get(url.toURI());
        if (Files.isRegularFile(p) && p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar")) {
            return p.getParent();
        }
        return p;
    }

    /**
     * Garantisce che il database di destinazione esista; se assente, lo crea.
     *
     * @param adminConn connessione al DB di manutenzione (tipicamente <code>postgres</code>)
     * @param dbName    nome del database target da verificare/creare
     * @throws SQLException in caso di errore nella verifica o creazione
     */
    private void ensureDatabaseExists(Connection adminConn, String dbName) throws SQLException {
        String existsSql = "SELECT 1 FROM pg_database WHERE datname = ?";
        try (PreparedStatement ps = adminConn.prepareStatement(existsSql)) {
            ps.setString(1, dbName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[INFO] Il database \"" + dbName + "\" esiste già. Salto la creazione.");
                    return;
                }
            }
        }
        String createSql = "CREATE DATABASE " + quoteIdent(dbName);
        try (Statement st = adminConn.createStatement()) {
            st.executeUpdate(createSql);
            System.out.println("[INFO] Database creato: " + dbName);
        }
    }

    /**
     * Esegue il quoting sicuro di un identificatore PostgreSQL.
     * Converte un eventuale carattere <code>"</code> in <code>""</code> e racchiude tra doppi apici.
     *
     * @param ident identificatore (schema, tabella, colonna, database)
     * @return identificatore quotato correttamente per PostgreSQL
     */
    private String quoteIdent(String ident) {
        return "\"" + ident.replace("\"", "\"\"") + "\"";
    }

    /**
     * Colleziona i file <code>.sql</code> presenti nella directory specificata,
     * ordinati alfabeticamente in modo case-insensitive.
     *
     * @param baseDir cartella di ricerca
     * @return lista dei path dei file SQL; vuota se la cartella non esiste
     * @throws IOException in caso di errore di lettura della directory
     */
    private List<Path> collectSqlFiles(Path baseDir) throws IOException {
        if (!Files.exists(baseDir)) return List.of();
        try (var stream = Files.list(baseDir)) {
            return stream
                    .filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase(Locale.ITALY).endsWith(".sql"))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase(Locale.ITALY)))
                    .toList();
        }
    }

    /**
     * Legge un file di testo in UTF-8, rimuovendo un eventuale BOM iniziale e normalizzando
     * i fine-riga a <code>\n</code>.
     *
     * @param p path del file da leggere
     * @return contenuto del file come stringa UTF-8 normalizzata
     * @throws IOException in caso di errore di I/O
     */
    private String readFileUtf8(Path p) throws IOException {
        byte[] bytes = Files.readAllBytes(p);
        String s = new String(bytes, StandardCharsets.UTF_8);
        if (!s.isEmpty() && s.charAt(0) == '\uFEFF') s = s.substring(1);
        return s.replace("\r\n", "\n");
    }

    /**
     * Suddivide uno script SQL PostgreSQL in statement terminati da <code>;</code>,
     * preservando correttamente:
     * <ul>
     *   <li>stringhe con apici singoli e escaping <code>''</code>,</li>
     *   <li>commenti <code>-- ... \n</code> e <code>/* ... *&#47;</code>,</li>
     *   <li>blocchi <em>dollar-quoted</em> (<code>$tag$ ... $tag$</code>).</li>
     * </ul>
     *
     * @param script contenuto testuale dello script SQL
     * @return lista degli statement pronti per l’esecuzione (senza i punti e virgola)
     */
    private List<String> splitPostgresStatements(String script) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inSingle = false, inLineComment = false, inBlockComment = false;
        String dollarTag = null;

        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);
            char next = (i + 1 < script.length()) ? script.charAt(i + 1) : '\0';

            if (inLineComment) {
                cur.append(c);
                if (c == '\n') inLineComment = false;
                continue;
            }
            if (inBlockComment) {
                cur.append(c);
                if (c == '*' && next == '/') { cur.append(next); i++; inBlockComment = false; }
                continue;
            }
            if (dollarTag != null) {
                cur.append(c);
                if (c == '$') {
                    int j = i + 1, k = j;
                    while (k < script.length() && isTagChar(script.charAt(k))) k++;
                    if (k < script.length() && script.charAt(k) == '$') {
                        String tag = script.substring(j, k);
                        if (Objects.equals(tag, dollarTag)) { cur.append(script, j, k + 1); i = k; dollarTag = null; }
                    }
                }
                continue;
            }
            if (inSingle) {
                cur.append(c);
                if (c == '\'') {
                    if (next == '\'') { cur.append(next); i++; }
                    else inSingle = false;
                }
                continue;
            }
            if (c == '-' && next == '-') { cur.append(c).append(next); i++; inLineComment = true; continue; }
            if (c == '/' && next == '*') { cur.append(c).append(next); i++; inBlockComment = true; continue; }
            if (c == '\'') { cur.append(c); inSingle = true; continue; }

            if (c == '$') {
                int j = i + 1, k = j;
                while (k < script.length() && isTagChar(script.charAt(k))) k++;
                if (k < script.length() && script.charAt(k) == '$') {
                    dollarTag = script.substring(j, k);
                    cur.append(script, i, k + 1);
                    i = k;
                    continue;
                }
            }

            if (c == ';') {
                String stmt = cur.toString().trim();
                if (!stmt.isEmpty()) out.add(stmt);
                cur.setLength(0);
                continue;
            }
            cur.append(c);
        }
        String tail = cur.toString().trim();
        if (!tail.isEmpty()) out.add(tail);
        return out;
    }

    /**
     * Indica se un carattere è ammesso nel tag di un blocco <em>dollar-quoted</em>
     * (<code>lettera</code>, <code>cifra</code>, <code>underscore</code>).
     *
     * @param ch carattere da verificare
     * @return true se valido per il tag; false altrimenti
     */
    private boolean isTagChar(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }

    /**
     * Esegue in sequenza gli statement SQL contro la connessione data,
     * applicando la politica di commit:
     * <ul>
     *   <li>Se <code>txPerFile</code> è true: disattiva l’auto-commit, crea un savepoint,
     *       esegue tutti gli statement e fa <em>commit</em> se nessun errore, altrimenti
     *       <em>rollback</em> al savepoint.</li>
     *   <li>Se <code>txPerFile</code> è false: lascia l’auto-commit attivo ed esegue
     *       ogni statement con commit immediato.</li>
     * </ul>
     * Gli errori sono loggati con anteprima della query, SQLState e vendor code.
     *
     * @param conn        connessione al database di destinazione
     * @param statements  lista di statement SQL (senza <code>;</code>)
     * @param sourceName  nome “umano” della sorgente (es. nome file) per i log
     * @param txPerFile   modalità transazionale per file
     * @throws SQLException se falliscono le operazioni di commit/rollback o settaggio transazionale
     */
    private void executeStatements(Connection conn, List<String> statements, String sourceName, boolean txPerFile) throws SQLException {
        int ok = 0, fail = 0;
        Savepoint sp = null;
        if (txPerFile) {
            try (Statement s = conn.createStatement()) {
            }
            conn.setAutoCommit(false);
            sp = conn.setSavepoint("begin_file_" + sourceName);
        }
        for (int i = 0; i < statements.size(); i++) {
            String s = statements.get(i).trim();
            if (s.isEmpty()) continue;
            try (Statement st = conn.createStatement()) {
                st.execute(s);
                ok++;
            } catch (SQLException ex) {
                fail++;
                System.err.println("  [ERR] " + sourceName + " stmt#" + (i + 1) + ": " + shortPreview(s));
                System.err.println("        SQLState=" + ex.getSQLState() + " Code=" + ex.getErrorCode());
                System.err.println("        Msg: " + ex.getMessage());
            }
        }
        if (txPerFile) {
            if (fail == 0) conn.commit();
            else { System.out.println("  [WARN] rollback file " + sourceName + " per errori."); conn.rollback(sp); }
        }
        System.out.println("  => " + ok + " OK, " + fail + " errori");
    }

    /**
     * Restituisce una anteprima monoriga e accorciata dello statement SQL
     * (spazi normalizzati; massimo 120 caratteri con ellissi).
     *
     * @param sql statement originale
     * @return anteprima breve per logging
     */
    private String shortPreview(String sql) {
        String one = sql.replace('\n', ' ').replaceAll("\\s+", " ").trim();
        return one.length() > 120 ? one.substring(0, 117) + "..." : one;
    }
}
