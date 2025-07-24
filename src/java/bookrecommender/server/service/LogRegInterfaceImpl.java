package bookrecommender.server.service;

import bookrecommender.common.interfaces.LogRegInterface;
import bookrecommender.common.model.RegToken;
import bookrecommender.common.model.Token;
import bookrecommender.server.util.ServerUtil;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione dell'interfaccia remota {@link LogRegInterface} per la gestione
 * delle operazioni di login, registrazione e logout degli utenti.
 * Questa classe viene esposta dal server tramite RMI e si occupa di validare
 * le credenziali degli utenti, registrarli nel sistema e gestirne la sessione.
 * Le operazioni sono loggate con {@link Logger} per monitoraggio lato server.
 */
public class LogRegInterfaceImpl extends UnicastRemoteObject implements LogRegInterface{

    /**Serial version UID per la serializzazione RMI */
    @Serial
    private static final long serialVersionUID = 1L;

    /**Logger per il tracciamento delle operazioni lato server*/
    private final Logger logger;

    /**
     * Costruttore della classe {@code LogRegInterfaceImpl}.
     * @throws RemoteException se si verifica un errore nell'esportazione dell'oggetto remoto.
     */
    public LogRegInterfaceImpl() throws RemoteException {
        super();
        this.logger = Logger.getLogger(LogRegInterfaceImpl.class.getName());
    }

    /**
     * Tenta il login di un utente verificando le credenziali fornite.
     * Se le credenziali sono corrette, viene restituito un {@link Token} valido.
     * In caso contrario, restituisce {@code null}.
     * Il metodo registra il tentativo di accesso e salva anche l'indirizzo del client remoto.
     * @param username nome utente fornito.
     * @param password password associata all'utente.
     * @return un {@link Token} se l'autenticazione ha successo, altrimenti {@code null}.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public Token TryLogin(String username, String password) throws RemoteException {
        try {
            logger.info("TryLogin called with username/email: " + username + ", password: " + password + ", client host: " + getClientHost());
        }catch(ServerNotActiveException ignored){}
        String checkQuery = "SELECT 1 FROM SESSIONI_LOGIN JOIN UTENTI ON UTENTI.ID = IDUTENTE WHERE USERNAME = ? OR EMAIL = ?";
        String query = "SELECT ID, PASSWORD FROM UTENTI WHERE USERNAME = ? OR EMAIL = ?";
        try(Connection conn = ServerUtil.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            stmt.setString(1, username);
            stmt.setString(2, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.warning("Utente già loggato: " + username);
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel controllo login di un utente", e);
        }
        try (Connection conn = ServerUtil.getInstance().getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("ID");
                    String storedPassword = rs.getString("PASSWORD");
                    int rows = 0;
                    if (password.equals(storedPassword)) {
                        String token = generaToken();

                        String insert = "INSERT INTO SESSIONI_LOGIN (IDUTENTE, IP_CLIENT, TOKEN) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insert)) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setString(2, getClientHost());
                            insertStmt.setString(3, token);
                            rows = insertStmt.executeUpdate();
                        } catch (ServerNotActiveException e) {
                            logger.severe("Errore durante l'inserimento del token: " + e.getMessage());
                        }
                        if (rows > 0) {
                            return new Token(token, userId, getClientHost());
                        } else {
                            return null;
                        }
                    } else return null;
                } else return null;
            } catch (ServerNotActiveException e) {
                logger.severe("Errore durante l'esecuzione della query di login: " + e.getMessage());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel login di un utente", e);
        }
        return null;
    }


    /**
     * Registra un nuovo utente nel sistema, se i dati forniti sono validi e univoci.
     * Restituisce un oggetto {@link RegToken} contenente un messaggio
     * informativo e un eventuale {@link Token} associato alla sessione utente creata.
     * @param nome     nome dell'utente.
     * @param cognome  cognome dell'utente.
     * @param CF       codice fiscale dell'utente.
     * @param email    indirizzo email dell'utente.
     * @param username username desiderato.
     * @param password password associata.
     * @return un {@link RegToken} contenente il risultato dell'operazione.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public RegToken Register(String nome, String cognome, String CF, String email, String username, String password) throws RemoteException {
        try {
            logger.info("Register called with nome: " + nome + ", cognome: " + cognome + ", CF: " + CF + ", email: " + email + ", username: " + username + ", client host: " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        String checkUsername = "SELECT 1 FROM UTENTI WHERE USERNAME = ?";
        String checkCF = "SELECT 1 FROM UTENTI WHERE CODICE_FISCALE = ?";
        String checkEmail = "SELECT 1 FROM UTENTI WHERE EMAIL = ?";
        String insertQuery = "INSERT INTO UTENTI (USERNAME, NOME, COGNOME, CODICE_FISCALE, EMAIL, PASSWORD) VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection conn = ServerUtil.getInstance().getConnection();
                PreparedStatement checkUStmt = conn.prepareStatement(checkUsername);
                PreparedStatement checkCFStmt = conn.prepareStatement(checkCF);
                PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmail);
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

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
                return new RegToken(true, true, true, true);
            } else {
                return new RegToken(false, false, false, false);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella registrazione di un utente CASE: InsertStatement not valid", e);
            return new RegToken(false, false, false, false);
        }
    }


    /**
     * Effettua il logout dell'utente associato al {@link Token} fornito,
     * invalidando il token e aggiornando lo stato sul server.
     * Registra l'operazione nel log di sistema.
     * @param token il {@link Token} della sessione utente da invalidare.
     * @return {@code true} se il logout ha avuto successo, {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public boolean LogOut(Token token) throws RemoteException {
        try {
            logger.info("LogOut called for token: " + token.token() + ", client host: " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        String deleteQuery = "DELETE FROM SESSIONI_LOGIN WHERE TOKEN = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
            stmt.setString(1, token.token());
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante il logout dell'utente", e);
            return false;
        }
    }

    @Override
    public boolean cambiaPassword(Token token, String newPassword) throws RemoteException {
        try {
            logger.info("PasswordChange called for token: " + token.token() + ", client host: " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        String update = "UPDATE UTENTI SET PASSWORD = ? WHERE ID = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, token.userId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel cambio password per utente " + token.userId(), e);
        }
        return false;
    }

    @Override
    public boolean eliminaAccount(Token token) throws RemoteException {
        try {
            logger.info("UserDelete called for token: " + token.token() + ", client host: " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        String delConsigli = "DELETE FROM consigli WHERE id_utente = ?";
        String delValutazioni = "DELETE FROM valutazioni WHERE id_utente = ?";
        String delLibrerie = "DELETE FROM librerie WHERE id_utente = ?";
        String delSessioni = "DELETE FROM sessioni_login WHERE idutente = ?";
        String delUtente = "DELETE FROM utenti WHERE id = ?";
        try {
            Connection conn = ServerUtil.getInstance().getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(delConsigli);
                 PreparedStatement p2 = conn.prepareStatement(delValutazioni);
                 PreparedStatement p3 = conn.prepareStatement(delLibrerie);
                 PreparedStatement p4 = conn.prepareStatement(delSessioni);
                 PreparedStatement p5 = conn.prepareStatement(delUtente)) {
                p1.setInt(1, token.userId()); p1.executeUpdate();
                p2.setInt(1, token.userId()); p2.executeUpdate();
                p3.setInt(1, token.userId()); p3.executeUpdate();
                p4.setInt(1, token.userId()); p4.executeUpdate();
                p5.setInt(1, token.userId());
                boolean ok = p5.executeUpdate() > 0;
                if (ok)
                    conn.commit();
                else
                    conn.rollback();
                return ok;
            } catch (SQLException e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Errore nell'eliminazione account utente " + token.userId(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore di gestione transazione per eliminazione account", e);
        }
        return false;
    }

    @Override
    public boolean cambiaEmail(Token token, String newEmail) throws RemoteException {
        try {
            logger.info("EmailChange called for token: " + token.token() + ", client host: " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        String checkEmail = "SELECT 1 FROM UTENTI WHERE EMAIL = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkEmail)) {
            checkStmt.setString(1, newEmail);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    logger.warning("Email già in uso: " + newEmail);
                    return false;
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore nel controllo dell'unicità dell'email", e);
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella connessione al database per il controllo dell'email", e);
            return false;
        }
        String updateEmail = "UPDATE UTENTI SET EMAIL = ? WHERE ID = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateEmail)) {
            stmt.setString(1, newEmail);
            stmt.setInt(2, token.userId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel cambio password per utente " + token.userId(), e);
        }
        return false;
    }

    @Override
    public boolean cambiaUsername(Token token, String newUsername) throws RemoteException {
        try {
            logger.info("UsernameChange called for token: " + token.token() + ", client host: " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return false;
        }
        String checkUsername = "SELECT 1 FROM UTENTI WHERE USERNAME = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
        PreparedStatement checkStmt = conn.prepareStatement(checkUsername)) {
            checkStmt.setString(1, newUsername);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    logger.warning("Username già in uso: " + newUsername);
                    return false;
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore nel controllo dell'unicità dello username", e);
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nella connessione al database per il controllo dello username", e);
            return false;
        }
        String update = "UPDATE UTENTI SET USERNAME = ? WHERE ID = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, newUsername);
            stmt.setInt(2, token.userId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel cambio password per utente " + token.userId(), e);
        }
        return false;
    }

    @Override
    public List<String> getUserInfo(Token token) throws RemoteException {
        try {
            logger.info("UserInfo called for token: " + token.token() + ", client host: " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        if (ServerUtil.getInstance().isTokenNotValid(token)) {
            logger.log(Level.WARNING, "Token non valido > " + token.token() + " utente di id " + token.userId() + " IP:" + token.ipClient());
            return List.of();
        }
        String getter = "SELECT USERNAME, NOME, COGNOME, CODICE_FISCALE, EMAIL, PASSWORD FROM UTENTI WHERE ID = ?";
        try (Connection conn = ServerUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(getter)) {
            stmt.setInt(1, token.userId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("USERNAME");
                    String nome = rs.getString("NOME");
                    String cognome = rs.getString("COGNOME");
                    String codiceFiscale = rs.getString("CODICE_FISCALE");
                    String email = rs.getString("EMAIL");
                    String password = rs.getString("PASSWORD");
                    return List.of(username, nome, cognome, codiceFiscale, email, password);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore nel recupero delle informazioni utente", e);
                return List.of();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel cambio password per utente " + token.userId(), e);
            return List.of();
        }
        return List.of();
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
}
