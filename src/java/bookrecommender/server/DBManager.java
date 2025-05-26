package bookrecommender.server;

import bookrecommender.common.Libro;
import bookrecommender.common.RegToken;
import bookrecommender.common.Token;

import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/bookrecommender";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    private static Connection conn;

    private static final Logger logger = Logger.getLogger(DBManager.class.getName());

    public DBManager() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "Errore nella creazione di DBManager", e);
        }
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

    public List<Libro> cercaLibriPerAutoreAnno(String author, String year) {
        List<Libro> risultati = new ArrayList<>();
        String query = "SELECT * FROM LIBRI WHERE LOWER(AUTORE) LIKE LOWER(?) AND CAST(ANNOPUBBLICAZIONE AS TEXT) LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + author + "%");
            stmt.setString(2,"%" + year + "%");

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
                        if(rows>0) {
                            return new Token(token, userId, ipClient);
                        } else {
                            return null;
                        }
                    }else return null;
                }else return null;
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

    public RegToken Register(String nome, String cognome, String CF, String email, String username, String password){
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


            try(ResultSet rsU = checkUStmt.executeQuery();
                ResultSet rsCF = checkCFStmt.executeQuery();
                ResultSet rsEmail = checkEmailStmt.executeQuery()) {

                boolean existsUsername = rsU.next();
                boolean existsCF = rsCF.next();
                boolean existsEmail = rsEmail.next();
                if (existsUsername || existsCF || existsEmail) {
                    return new RegToken(existsUsername, existsCF, existsEmail, false);
                }
            }catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore nella registrazione di un utente CASE: ResultSet not valid", e);
                return new RegToken(false, false, false,false);
            }

            insertStmt.setString(1, username);
            insertStmt.setString(2, nome);
            insertStmt.setString(3, cognome);
            insertStmt.setString(4, CF);
            insertStmt.setString(5, email);
            insertStmt.setString(6, password);

            int rows = insertStmt.executeUpdate();
            if(rows > 0) {
                return new RegToken(true, true, true,true); // Registrazione avvenuta con successo
            } else {
                return new RegToken(false, false, false,false); // Errore durante la registrazione
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella registrazione di un utente CASE: InsertStatement not valid", e);
            return new RegToken(false, false, false,false);
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
}
