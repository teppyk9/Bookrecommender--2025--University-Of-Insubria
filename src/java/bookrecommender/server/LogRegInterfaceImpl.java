package bookrecommender.server;

import bookrecommender.common.LogRegInterface;
import bookrecommender.common.RegToken;
import bookrecommender.common.Token;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
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

    /**Gestore della logica di accesso al database*/
    private final DBManager dbManager;

    /**Logger per il tracciamento delle operazioni lato server*/
    private final Logger logger;

    /**
     * Costruttore della classe {@code LogRegInterfaceImpl}.
     * @param dbManager il gestore delle operazioni sul database.
     * @throws RemoteException se si verifica un errore nell'esportazione dell'oggetto remoto.
     */
    protected LogRegInterfaceImpl(DBManager dbManager) throws RemoteException {
        super();
        this.dbManager = dbManager;
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
            logger.info("TryLogin called with username: " + username + ", password: " + password + ", client host: " + getClientHost());
            return dbManager.loginUtente(username, password, getClientHost());
        }catch(ServerNotActiveException e){
            return null;
        }
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
            return dbManager.Register(nome, cognome, CF, email, username, password);
        } catch (ServerNotActiveException e) {
            return null;
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
            logger.info("LogOut called for token: " + token.getToken() + ", client host: " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        return dbManager.LogOut(token);
    }

    @Override
    public boolean cambiaPassword(Token token, String newPassword) throws RemoteException {
        try {
            logger.info("PasswordChange called for token: " + token.getToken() + ", client host: " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        return dbManager.cambiaPassword(token, newPassword);
    }

    @Override
    public boolean eliminaAccount(Token token) throws RemoteException {
        try {
            logger.info("UserDelete called for token: " + token.getToken() + ", client host: " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        return dbManager.eliminaAccount(token);
    }
}
