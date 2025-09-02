package bookrecommender;
import java.io.IOException;
import java.io.InputStream;
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

    /** Modalità di esecuzione dell’utility. */
    enum Mode { BASIC, FULL }

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
        String pass = scanOrDefault(in,"password (\u001B[31mvisible\u001B[0m)", "");
        String db = scanOrDefault(in,"new database name", DEF_DB);

        String modeStr = scanOrDefault(in, """
        L'utente ha a disposizione 2 modalità di esecuzione.
        [\u001B[33mbasic\u001B[0m] - Creazione delle tabelle e popolamento della tabella libri.
        [\u001B[33mfull\u001B[0m]  - Creazione delle tabelle, popolamento della tabella libri e di un dump per il testing.
        Username \u001B[32mtest\u001B[0m con password \u001B[32mtesttest\u001B[0m.
        Scegliere la modalità (basic|full)""", "full").toLowerCase(Locale.ROOT);

        Mode mode = modeStr.startsWith("f") ? Mode.FULL : Mode.BASIC;

        System.out.print("L'auto-commit è disattivato per default, per attivarlo scrivere > autocommit:");
        String ac = in.nextLine().trim();
        boolean txPerFile = TX_PER_FILE;
        if (ac.equalsIgnoreCase("autocommit")) {
            System.out.println("[INFO] Auto-commit attivato.");
            txPerFile = false;
        }else
            System.out.println("[INFO] Auto-commit disattivato (default).");

        System.out.println("------------------------------------------------------------");
        System.out.printf(Locale.ROOT, "Mode: %s%n", mode);
        System.out.println("SQL : classpath:/data");
        System.out.printf(Locale.ROOT, "Host: %s%nPort: %s%nUser: %s%nDB  : %s%n", host, port, user, db);
        System.out.printf(Locale.ROOT, "Tx per file: %s (autocommit=%s)%n", txPerFile, !txPerFile);
        System.out.println("------------------------------------------------------------");

        try {
            String maintenanceDbUrl = String.format(Locale.ROOT, "jdbc:postgresql://%s:%s/%s", host, port, "postgres");
            try (Connection adminConn = DriverManager.getConnection(maintenanceDbUrl, user, pass)) {
                ensureDatabaseExists(adminConn, db);
            }

            String targetDbUrl = String.format(Locale.ROOT, "jdbc:postgresql://%s:%s/%s", host, port, db);
            try (Connection targetConn = DriverManager.getConnection(targetDbUrl, user, pass)) {
                targetConn.setAutoCommit(true);

                List<String> wanted = buildWantedList(mode);
                System.out.println("[INFO] Esecuzione file SQL (classpath/resources) in ordine:");
                for (String res : wanted) {
                    System.out.println("  -> " + res);
                    String script = readResourceUtf8(res);
                    if (script == null) {
                        System.err.println("  [WARN] Risorsa non trovata: " + res);
                        continue;
                    }
                    List<String> statements = splitPostgresStatements(script);
                    executeStatements(targetConn, statements, res, txPerFile);
                }
            }

            System.out.println("\nCompletato.");
            System.out.println("Premere un tasto per uscire...");
            in.nextLine();
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
     * Legge una riga da console mostrando un valore di default.
     *
     * @param sc    scanner per l’input standard
     * @param label etichetta del parametro mostrata all’utente
     * @param def   valore di default usato se l’utente invia una riga vuota
     * @return stringa inserita dall’utente o il valore di default
     */
    private static String scanOrDefault(Scanner sc, String label, String def) {
        System.out.print("DEFAULT [" + "\u001B[34m" + def + "\u001B[0m" + "] - " + label + ":");
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("[INFO] Using default: " + def);
            return def;
        }
        return line;
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
     * Suddivide uno script SQL PostgreSQL in statement terminati da <code>;</code>,
     * preservando correttamente:
     * <ul>
     *   <li>Stringhe con apici singoli ed escaping <code>''</code>,</li>
     *   <li>Commenti <code>-- ... \n</code> e <code>/* ... *&#47;</code>,</li>
     *   <li>Blocchi <em>dollar-quoted</em> (<code>$tag$ ... $tag$</code>).</li>
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
     * Esegue in sequenza una lista di istruzioni SQL sulla connessione specificata,
     * applicando la politica transazionale definita dal flag {@code txPerFile}.
     * <ul>
     *   <li>Se {@code txPerFile} è {@code true}: disattiva l’auto-commit, esegue tutti
     *       gli statement in un’unica transazione e invoca {@code commit()} al termine.
     *       In caso di errore effettua {@code rollback()}.</li>
     *   <li>Se {@code txPerFile} è {@code false}: lascia l’auto-commit attivo ed esegue
     *       ogni statement in commit immediato.</li>
     * </ul>
     * Gli errori vengono riportati a console con il nome logico della sorgente e il messaggio SQL.
     *
     * @param conn       connessione JDBC verso il database di destinazione
     * @param statements lista di istruzioni SQL già separate (senza punto e virgola finale)
     * @param label      nome descrittivo della sorgente (es. Nome file) usato nei log
     * @param txPerFile  {@code true} per eseguire tutti gli statement in un’unica transazione,
     *                   {@code false} per auto-commit per statement
     * @throws SQLException se falliscono l’esecuzione degli statement o le operazioni di commit/rollback
     */
    private void executeStatements(Connection conn, List<String> statements, String label, boolean txPerFile) throws SQLException {
        if (statements.isEmpty()) {
            System.out.println("  [INFO] Nessuno statement in " + label);
            return;
        }
        boolean oldAuto = conn.getAutoCommit();
        if (txPerFile) conn.setAutoCommit(false);
        try (Statement st = conn.createStatement()) {
            for (String sql : statements) {
                String trimmed = sql.trim();
                if (trimmed.isEmpty()) continue;
                st.execute(trimmed);
            }
            if (txPerFile) conn.commit();
            System.out.println("  [OK] " + label);
        } catch (SQLException ex) {
            if (txPerFile) {
                try { conn.rollback(); } catch (SQLException ignore) {}
            }
            System.err.println("  [ERR] Fallito su " + label + ": " + ex.getMessage());
            throw ex;
        } finally {
            if (txPerFile) conn.setAutoCommit(oldAuto);
        }
    }

    /**
     * Restituisce la lista ordinata dei file SQL da eseguire in base alla modalità scelta.
     * <ul>
     *   <li>{@link Mode#BASIC}: include soltanto <code>1-tablecreation.sql</code> e <code>2-libri.sql</code>.</li>
     *   <li>{@link Mode#FULL}: include anche <code>3-dump.sql</code> dopo i precedenti.</li>
     * </ul>
     *
     * @param mode modalità di setup (BASIC o FULL)
     * @return lista di percorsi relativi nel classpath (es. {@code data/1-tablecreation.sql})
     */
    private List<String> buildWantedList(Mode mode) {
        List<String> basic = List.of(
                "/data/1-tablecreation.sql",
                "/data/2-libri.sql"
        );
        if (mode == Mode.BASIC) return basic;
        List<String> full = new ArrayList<>(basic);
        full.add("/data/3-dump.sql");
        return full;
    }

    /**
     * Carica una risorsa testuale dal classpath e la restituisce come stringa UTF-8 normalizzata.
     * <p>
     * Viene rimosso un eventuale BOM iniziale e convertiti i terminatori di riga
     * da CRLF a LF, per garantire coerenza tra piattaforme.
     * </p>
     *
     * @param resourcePath percorso relativo della risorsa all’interno del classpath
     *                     (es. {@code data/1-tablecreation.sql})
     * @return contenuto della risorsa come stringa UTF-8, oppure {@code null} se la risorsa non esiste
     * @throws IOException se si verificano errori di lettura dello stream
     */
    private String readResourceUtf8(String resourcePath) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            byte[] bytes = is.readAllBytes();
            String s = new String(bytes, StandardCharsets.UTF_8);
            if (!s.isEmpty() && s.charAt(0) == '\uFEFF') s = s.substring(1);
            return s.replace("\r\n", "\n");
        }
    }
}
