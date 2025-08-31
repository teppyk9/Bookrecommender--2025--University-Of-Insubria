package bookrecommender;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class DBCreator {

    private static final String DEF_HOST = "localhost";
    private static final String DEF_PORT = "5432";
    private static final String DEF_USER = "postgres";
    private static final String DEF_DB   = "mydb";
    private static final boolean TX_PER_FILE = true;

    public static void main(String[] args) {
        new DBCreator().run();
    }

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


    private static String scanOrDefault(Scanner sc, String label, String def) {
        System.out.println("DEFAULT [" + def + "] - " + label + ":");
        String line = sc.nextLine().trim();
        if (line.isEmpty()) {
            System.out.println("[INFO] Using default: " + def);
            return def;
        }
        return line;
    }

    private Path getExecutableDir() throws URISyntaxException {
        var url = DBCreator.class.getProtectionDomain().getCodeSource().getLocation();
        Path p = Paths.get(url.toURI());
        if (Files.isRegularFile(p) && p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar")) {
            return p.getParent();
        }
        return p;
    }

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

    private String quoteIdent(String ident) {
        return "\"" + ident.replace("\"", "\"\"") + "\"";
    }

    private List<Path> collectSqlFiles(Path baseDir) throws IOException {
        if (!Files.exists(baseDir)) return List.of();
        try (var stream = Files.list(baseDir)) {
            return stream
                    .filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase(Locale.ITALY).endsWith(".sql"))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase(Locale.ITALY)))
                    .toList();
        }
    }

    private String readFileUtf8(Path p) throws IOException {
        byte[] bytes = Files.readAllBytes(p);
        String s = new String(bytes, StandardCharsets.UTF_8);
        if (!s.isEmpty() && s.charAt(0) == '\uFEFF') s = s.substring(1);
        return s.replace("\r\n", "\n");
    }

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

    private boolean isTagChar(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }

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

    private String shortPreview(String sql) {
        String one = sql.replace('\n', ' ').replaceAll("\\s+", " ").trim();
        return one.length() > 120 ? one.substring(0, 117) + "..." : one;
    }
}
