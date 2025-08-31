package bookrecommender.interfaces;

import bookrecommender.model.RegToken;
import bookrecommender.model.Token;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interfaccia remota RMI che definisce le operazioni di login, registrazione e logout per un utente.
 * Implementata dal server, questa interfaccia viene richiamata dal client tramite RMI per autenticare o registrare utenti.
 */
public interface LogRegInterface extends Remote {

    /**
     * Esegue il tentativo di login per un utente, autenticandolo con le credenziali fornite.
     *
     * @param username nome utente
     * @param password password in chiaro
     * @return un oggetto {@link Token} contenente informazioni sull'autenticazione, oppure {@code null} se fallita
     * @throws RemoteException se si verifica un errore nella comunicazione RMI
     */
    Token TryLogin(String username, String password) throws RemoteException;

    /**
     * Registra un nuovo utente nel sistema con le informazioni personali e le credenziali.
     *
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
     *
     * @param token token dell'utente da disconnettere
     * @return {@code true} se il logout è andato a buon fine, {@code false} altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione RMI
     */
    boolean LogOut(Token token) throws RemoteException;

    /**
     * Modifica la password dell'utente autenticato.
     *
     * @param token       token di autenticazione dell'utente
     * @param newPassword nuova password da impostare (in chiaro)
     * @return {@code true} se la password è stata aggiornata, {@code false} se rifiutata o non valida
     * @throws RemoteException se si verifica un errore nella comunicazione RMI
     */
    boolean cambiaPassword(Token token, String newPassword) throws RemoteException;

    /**
     * Elimina definitivamente l'account dell'utente autenticato.
     *
     * @param token token di autenticazione dell'utente
     * @return {@code true} se l'account è stato eliminato, {@code false} altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione RMI
     */
    boolean eliminaAccount(Token token) throws RemoteException;

    /**
     * Cambia l'indirizzo email associato all'account dell'utente autenticato.
     *
     * @param token   token di autenticazione dell'utente
     * @param newEmail nuovo indirizzo email da impostare
     * @return {@code true} se l'email è stata aggiornata (e non era già in uso), {@code false} altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione RMI
     */
    boolean cambiaEmail(Token token, String newEmail) throws RemoteException;

    /**
     * Cambia lo username associato all'account dell'utente autenticato.
     *
     * @param token       token di autenticazione dell'utente
     * @param newUsername nuovo username da impostare
     * @return {@code true} se lo username è stato aggiornato (e non era già in uso), {@code false} altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione RMI
     */
    boolean cambiaUsername(Token token, String newUsername) throws RemoteException;

    /**
     * Restituisce le informazioni di profilo dell'utente autenticato.
     * <p>
     * La struttura esatta della lista dipende dall'implementazione, ma può includere
     * campi come nome, cognome, codice fiscale, email, username, ecc.
     * </p>     *
     * @param token token di autenticazione dell'utente
     * @return elenco di stringhe con le informazioni dell'utente; può essere vuota se non disponibili
     * @throws RemoteException se si verifica un errore nella comunicazione RMI
     */
    List<String> getUserInfo(Token token) throws RemoteException;
}
