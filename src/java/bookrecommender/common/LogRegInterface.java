package bookrecommender.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaccia remota RMI che definisce le operazioni di login, registrazione e logout per un utente.
 * Implementata dal server, questa interfaccia viene richiamata dal client tramite RMI per autenticare o registrare utenti.
 */
public interface LogRegInterface extends Remote {

    /**
     * Esegue il tentativo di login per un utente, autenticandolo con le credenziali fornite.
     * @param username nome utente
     * @param password password in chiaro
     * @return un oggetto {@link Token} contenente informazioni sull'autenticazione, oppure {@code null} se fallita
     * @throws RemoteException se si verifica un errore nella comunicazione RMI
     */
    Token TryLogin(String username, String password) throws RemoteException;

    /**
     * Registra un nuovo utente nel sistema con le informazioni personali e le credenziali.
     * @param nome      nome dell'utente
     * @param cognome   cognome dell'utente
     * @param CF        codice fiscale
     * @param email     indirizzo email
     * @param username  nome utente scelto
     * @param password  password in chiaro
     * @return un oggetto {@link RegToken} con informazioni sul risultato della registrazione
     * @throws RemoteException se si verifica un errore nella comunicazione RMI
     */
    RegToken Register(String nome, String cognome,String CF,String email,String username,String password) throws RemoteException;

    /**
     * Effettua il logout dell'utente associato al {@link Token} specificato.
     * @param token token dell'utente da disconnettere
     * @return {@code true} se il logout è andato a buon fine, {@code false} altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione RMI
     */
    boolean LogOut(Token token) throws RemoteException;

    boolean cambiaPassword(Token token, String newPassword) throws RemoteException;

    boolean eliminaAccount(Token token) throws RemoteException;
}
