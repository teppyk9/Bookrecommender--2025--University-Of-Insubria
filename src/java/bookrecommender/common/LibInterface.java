package bookrecommender.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaccia remota per la gestione delle librerie utente, delle valutazioni e dei consigli.
 * Espone metodi remoti RMI per l’interazione con il server da parte del client.
 * Ogni operazione richiede un {@link Token} per identificare l’utente autenticato.
 */
public interface LibInterface extends Remote {

    /**
     * Crea una nuova libreria associata all’utente.
     * @param token token di autenticazione dell’utente
     * @param nome nome della nuova libreria
     * @param libri lista iniziale di libri da inserire nella libreria
     * @return true se la libreria è stata creata con successo, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean createLib (Token token, String nome, List<Libro> libri) throws RemoteException;


    /**
     * Elimina una libreria esistente dell’utente.
     * @param token token di autenticazione dell’utente
     * @param nome nome della libreria da eliminare
     * @return true se la libreria è stata eliminata, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean deleteLib (Token token, String nome) throws RemoteException;


    /**
     * Aggiorna una libreria esistente con una nuova lista di libri.
     * @param token token di autenticazione dell’utente
     * @param nome nome della libreria da aggiornare
     * @param libriUp nuova lista di libri
     * @return lista di ID dei libri aggiunti alla libreria (che prima non erano presenti)
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    List<Integer> updateLib (Token token, String nome, List<Libro> libriUp) throws RemoteException;

    /**
     * Restituisce i libri contenuti in una libreria.
     * @param token token di autenticazione dell’utente
     * @param nome nome della libreria richiesta
     * @return lista di libri contenuti nella libreria
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    List<Libro> getLib (Token token, String nome) throws RemoteException;

    /**
     * Restituisce i nomi di tutte le librerie dell’utente.
     * @param token token di autenticazione dell’utente
     * @return lista dei nomi delle librerie disponibili
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    List<String> getLibs (Token token) throws RemoteException;

    /**
     * Aggiunge una valutazione a un libro da parte dell’utente.
     * @param token token di autenticazione dell’utente
     * @param valutazione oggetto che contiene le informazioni sulla valutazione
     * @return true se l’aggiunta è andata a buon fine, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean addValutazione (Token token, Valutazione valutazione) throws RemoteException;

    /**
     * Aggiunge un consiglio contenente una lista di libri.
     * @param token token di autenticazione dell’utente
     * @param libri lista di libri da consigliare
     * @return true se il consiglio è stato registrato correttamente, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean addConsiglio (Token token, List<Libro> libri) throws RemoteException;

    /**
     * Modifica il nome di una libreria esistente.
     * @param token token di autenticazione dell’utente
     * @param oldName nome attuale della libreria
     * @param newName nuovo nome da assegnare alla libreria
     * @return true se il nome è stato modificato con successo, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean modifyLibName (Token token, String oldName, String newName) throws RemoteException;

    /**
     * Verifica se un libro è presente in almeno una libreria dell’utente.
     * @param token token di autenticazione dell’utente
     * @param libro libro da cercare
     * @return true se il libro è presente in una delle librerie dell’utente, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean isLibPresent (Token token, Libro libro) throws RemoteException;

    /**
     * Restituisce la data di creazione di una libreria.
     * @param token token di autenticazione dell’utente
     * @param nome nome della libreria
     * @return data di creazione della libreria
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    LocalDate getCreationDate(Token token, String nome) throws RemoteException;

    boolean existVal(Token token, Libro libro) throws RemoteException;

    boolean existCon(Token token, Libro libro) throws RemoteException;

    boolean updateVal(Token token, Valutazione valutazione) throws RemoteException;

    boolean updateCon(Token token, List<Libro> libri) throws RemoteException;

    boolean deleteVal(Token token, Libro libro) throws RemoteException;

    boolean deleteCon(Token token, Libro libro) throws RemoteException;

    LocalDate getValDate(Token token, Libro libro) throws RemoteException;

    LocalDate getConDate(Token token, Libro libro) throws RemoteException;

    List<Libro> getConsigli(Token token, Libro libro) throws RemoteException;

    Valutazione getValutazione(Token token, Libro libro) throws RemoteException;
}
