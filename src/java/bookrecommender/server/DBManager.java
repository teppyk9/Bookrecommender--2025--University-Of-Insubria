package bookrecommender.server;

import bookrecommender.common.*;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManager {

    private static Connection conn;

    private static final Logger logger = Logger.getLogger(DBManager.class.getName());

    public DBManager() {
        //al momento non è necessario alcun codice nel costruttore, poi ci penso se serve
    }

    public boolean tryConnection(String url, String user, String password) {
        if (url == null || user == null || password == null) {
            logger.log(Level.SEVERE, "Parametri di connessione non impostati.");
            return false;
        }
        try (Connection ignored = DriverManager.getConnection(url, user, password)) {
            logger.log(Level.INFO, "Test connessione al database avvenuto con successo.");
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella connessione al database", e);
            return false;
        }
    }

    public boolean connect(String url, String user, String password) {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, user, password);
                logger.log(Level.INFO, "Connessione al database avvenuta con successo.");
            } else {
                logger.log(Level.WARNING, "Tentativo di riconnessione al database già connesso.");
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella connessione al database", e);
            conn = null;
            return false;
        }
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                logger.log(Level.INFO, "Connessione al database chiusa con successo.");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore nella chiusura della connessione al database", e);
            }
        } else {
            logger.log(Level.WARNING, "Nessuna connessione al database da chiudere.");
        }
    }

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

    public List<Libro> cercaLibriPerTitolo(String titolo) {
        List<Libro> risultati = new ArrayList<>();
        String query = "SELECT * FROM LIBRI WHERE LOWER(TITOLO) LIKE LOWER(?)";

        return selectLibro(titolo, risultati, query);
    }

    public List<Libro> cercaLibriPerAutore(String author) {
        List<Libro> risultati = new ArrayList<>();
        String query = "SELECT * FROM LIBRI WHERE LOWER(AUTORE) LIKE LOWER(?)";

        return selectLibro(author, risultati, query);
    }

    public List<Libro> selectLibro(String titolo, List<Libro> risultati, String query) {
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + titolo + "%");

            resultStmt(risultati, stmt);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di un libro", e);
        }
        return risultati;
    }

    public List<Libro> cercaLibriPerAutoreAnno(String author, String year) {
        List<Libro> risultati = new ArrayList<>();
        String query = "SELECT * FROM LIBRI WHERE LOWER(AUTORE) LIKE LOWER(?) AND CAST(ANNOPUBBLICAZIONE AS TEXT) LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + author + "%");
            stmt.setString(2, "%" + year + "%");

            resultStmt(risultati, stmt);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella ricerca di un libro", e);
        }
        return risultati;
    }

    public void svuotaSessioniLogin() {
        String sql = "TRUNCATE TABLE SESSIONI_LOGIN RESTART IDENTITY";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel restart delle sessioni", e);
        }
    }

    public Token loginUtente(String username, String password, String ipClient) {
        String query = "SELECT ID, PASSWORD FROM UTENTI WHERE USERNAME = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

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

    private String generaToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[24];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    public RegToken Register(String nome, String cognome, String CF, String email, String username, String password) {
        String checkUsername = "SELECT 1 FROM UTENTI WHERE USERNAME = ?";
        String checkCF = "SELECT 1 FROM UTENTI WHERE CODICE_FISCALE = ?";
        String checkEmail = "SELECT 1 FROM UTENTI WHERE EMAIL = ?";
        String insertQuery = "INSERT INTO UTENTI (USERNAME, NOME, COGNOME, CODICE_FISCALE, EMAIL, PASSWORD) VALUES (?, ?, ?, ?, ?, ?)";

        try (
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

    public boolean LogOut(Token token) {
        String deleteQuery = "DELETE FROM SESSIONI_LOGIN WHERE TOKEN = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setString(1, token.getToken());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il logout dell'utente", e);
            return false;
        }
    }

    public boolean isTokenNotValid(Token token) {
        String query = "SELECT 1 FROM SESSIONI_LOGIN WHERE TOKEN = ? AND IP_CLIENT = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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

    public boolean creaLibreria(Token token, String nome, List<Libro> libri) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        try {
            String checkQuery = "SELECT 1 FROM LIBRERIE WHERE ID_UTENTE = ? AND TITOLO_LIBRERIA = ?";
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

        try (PreparedStatement insertLibStmt = conn.prepareStatement(insertLibQuery, Statement.RETURN_GENERATED_KEYS)) {
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

    public boolean eliminaLibreria(Token token, String nome) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        String deleteQuery = "DELETE FROM LIBRERIE WHERE ID_UTENTE = ? AND TITOLO_LIBRERIA = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, nome);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'eliminazione della libreria", e);
            return false;
        }
    }

    public boolean aggiornaLibreria(Token token, String nome, List<Libro> libridel, List<Libro> libriadd) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        String deleteQuery = "DELETE FROM LIBRERIA_LIBRO WHERE IDLIBRERIA = (SELECT ID FROM LIBRERIE WHERE ID_UTENTE = ? AND TITOLO_LIBRERIA = ?) AND IDLIBRO = ?";
        String insertQuery = "INSERT INTO LIBRERIA_LIBRO (IDLIBRERIA, IDLIBRO) VALUES ((SELECT ID FROM LIBRERIE WHERE ID_UTENTE = ? AND TITOLO_LIBRERIA = ?), ?)";

        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            for (Libro libro : libridel) {
                deleteStmt.setInt(1, token.getUserId());
                deleteStmt.setString(2, nome);
                deleteStmt.setInt(3, libro.getId());
                deleteStmt.addBatch();
            }
            for (Libro libro : libriadd) {
                insertStmt.setInt(1, token.getUserId());
                insertStmt.setString(2, nome);
                insertStmt.setInt(3, libro.getId());
                insertStmt.addBatch();
            }
            deleteStmt.executeBatch();
            insertStmt.executeBatch();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nell'aggiornamento della libreria", e);
            return false;
        }
    }

    public List<Libro> getLibreria(Token token, String nome) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        List<Libro> libri = new ArrayList<>();
        String query = "SELECT L.* FROM LIBRERIE R JOIN LIBRERIA_LIBRO LL ON R.ID = LL.IDLIBRERIA JOIN LIBRI L ON LL.IDLIBRO = L.ID WHERE R.ID_UTENTE = ? AND R.TITOLO_LIBRERIA = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, nome);
            resultStmt(libri, stmt);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero della libreria", e);
        }
        return libri;
    }

    public List<String> getLibrerie(Token token) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return null;
        }
        List<String> librerie = new ArrayList<>();
        String query = "SELECT TITOLO_LIBRERIA FROM LIBRERIE WHERE ID_UTENTE = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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

        try (PreparedStatement psValutazioni = conn.prepareStatement(queryValutazioni)) {
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

        try (PreparedStatement psConsigli = conn.prepareStatement(queryConsigli)) {
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

    public boolean addValutazione(Token token, Valutazione valutazione) {
        if (isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        List<String> commenti = valutazione.getCommenti();
        List<Float> valutazioni = valutazione.getValutazioni();
        String insertQuery = """
                INSERT INTO valutazioni (idlibro, id_utente, v_stile, c_stile, v_contenuto, c_contenuto,
                                         v_gradevolezza, c_gradevolezza, v_originalita, c_originalita,
                                         v_edizione, c_edizione, c_finale)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)""";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
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
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
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
}