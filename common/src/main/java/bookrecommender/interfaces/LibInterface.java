package bookrecommender.interfaces;

import bookrecommender.model.Libro;
import bookrecommender.model.Token;
import bookrecommender.model.Valutazione;

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

    /**
     * Verifica se esiste una valutazione dell’utente per il libro indicato.
     * @param token token di autenticazione dell’utente
     * @param libro libro di cui verificare la valutazione
     * @return true se la valutazione esiste, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean existVal(Token token, Libro libro) throws RemoteException;

    /**
     * Verifica se esiste un consiglio associato al libro indicato per l’utente.
     * @param token token di autenticazione dell’utente
     * @param libro libro di riferimento per il consiglio
     * @return true se il consiglio esiste, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean existCon(Token token, Libro libro) throws RemoteException;

    /**
     * Aggiorna la valutazione esistente per il libro indicato.
     * @param token token di autenticazione dell’utente
     * @param valutazione nuova valutazione da salvare (sostituisce quella esistente)
     * @return true se l’aggiornamento è andato a buon fine, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean updateVal(Token token, Valutazione valutazione) throws RemoteException;

    /**
     * Aggiorna il consiglio esistente associato al libro indicato.
     * @param token token di autenticazione dell’utente
     * @param libri nuova lista di libri consigliati da salvare
     * @return true se l’aggiornamento è andato a buon fine, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean updateCon(Token token, List<Libro> libri) throws RemoteException;

    /**
     * Elimina la valutazione dell’utente relativa al libro indicato.
     * @param token token di autenticazione dell’utente
     * @param libro libro per cui eliminare la valutazione
     * @return true se l’eliminazione è andata a buon fine, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean deleteVal(Token token, Libro libro) throws RemoteException;

    /**
     * Elimina il consiglio dell’utente relativo al libro indicato.
     * @param token token di autenticazione dell’utente
     * @param libro libro di riferimento per cui eliminare il consiglio
     * @return true se l’eliminazione è andata a buon fine, false altrimenti
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    boolean deleteCon(Token token, Libro libro) throws RemoteException;

    /**
     * Restituisce la data associata (creazione o ultima modifica) alla valutazione dell’utente per il libro.
     * @param token token di autenticazione dell’utente
     * @param libro libro di riferimento
     * @return data della valutazione (creazione o ultima modifica, a seconda dell’implementazione)
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    LocalDate getValDate(Token token, Libro libro) throws RemoteException;

    /**
     * Restituisce la data associata (creazione o ultima modifica) al consiglio dell’utente per il libro.
     * @param token token di autenticazione dell’utente
     * @param libro libro di riferimento
     * @return data del consiglio (creazione o ultima modifica, a seconda dell’implementazione)
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    LocalDate getConDate(Token token, Libro libro) throws RemoteException;

    /**
     * Restituisce la lista dei libri consigliati associati al libro indicato.
     * @param token token di autenticazione dell’utente
     * @param libro libro di riferimento per il consiglio
     * @return lista di libri consigliati
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    List<Libro> getConsigli(Token token, Libro libro) throws RemoteException;

    /**
     * Restituisce la valutazione dell’utente per il libro indicato.
     *
     * @param token token di autenticazione dell’utente
     * @param libro libro valutato
     * @return la valutazione esistente, oppure {@code null} se non presente
     * @throws RemoteException se si verifica un errore nella comunicazione remota
     */
    Valutazione getValutazione(Token token, Libro libro) throws RemoteException;
}
