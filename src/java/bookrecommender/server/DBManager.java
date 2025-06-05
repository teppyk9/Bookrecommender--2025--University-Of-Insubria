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

    public boolean creaLibreria(Token token, String nome, List<Libro> libri){
        if(isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.getToken() + " utente di id " + token.getUserId() + " IP:" + token.getIpClient());
            return false;
        }
        try{
            String checkQuery = "SELECT 1 FROM LIBRERIE WHERE ID_UTENTE = ? AND TITOLO_LIBRERIA = ?";
            PreparedStatement stmt = conn.prepareStatement(checkQuery);
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, nome);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
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
                        }catch (SQLException e) {
                            logger.log(Level.SEVERE, "Errore nell'inserimento dei libri nella libreria", e);
                            return false;
                        }
                        return true;
                    }
                }
            }else{
                logger.log(Level.WARNING, "Nessuna riga inserita nella libreria per l'utente con ID: " + token.getUserId());
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella creazione della libreria", e);
        }
        return false;
    }

    public boolean eliminaLibreria(Token token, String nome) {
        if(isTokenNotValid(token)) {
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
        if(isTokenNotValid(token)) {
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
        if(isTokenNotValid(token)) {
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
        if(isTokenNotValid(token)) {
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
}
