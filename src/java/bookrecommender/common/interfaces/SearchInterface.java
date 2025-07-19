package bookrecommender.common.interfaces;

import bookrecommender.common.model.Libro;
import bookrecommender.common.model.Libro_Details;
import bookrecommender.common.model.Token;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interfaccia remota per la ricerca e il recupero di informazioni sui libri.
 * <p>
 * Fornisce metodi per cercare libri per titolo, autore, anno, e per ottenere dettagli
 * avanzati di un libro. Alcuni metodi supportano l'autenticazione tramite {@link Token}.
 * </p>
 * Tutti i metodi possono lanciare {@link RemoteException} in caso di problemi di comunicazione remota.
 */
public interface SearchInterface extends Remote {

    /**
     * Recupera un libro tramite il suo identificatore univoco.
     * @param id l'identificatore del libro
     * @return il libro corrispondente all'id fornito
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    Libro getLibro(int id) throws RemoteException;

    /**
     * Cerca libri il cui titolo corrisponde (o contiene) la stringa fornita.
     * @param title il titolo o parte del titolo del libro
     * @return lista di libri che corrispondono al titolo
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    List<Libro> searchByName(String title) throws RemoteException;

    /**
     * Cerca libri scritti dall'autore specificato.
     * @param author il nome dell'autore
     * @return lista di libri scritti dall'autore
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    List<Libro> searchByAuthor(String author) throws RemoteException;

    /**
     * Cerca libri scritti dall'autore specificato e pubblicati nell'anno indicato.
     * @param author il nome dell'autore
     * @param year   l'anno di pubblicazione
     * @return lista di libri corrispondenti ai criteri di ricerca
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    List<Libro> searchByAuthorAndYear(String author, int year) throws RemoteException;

    /**
     * Ottiene i dettagli completi di un libro specifico.
     * @param libro il libro di cui si vogliono ottenere i dettagli
     * @return oggetto {@link Libro_Details} contenente informazioni dettagliate
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    Libro_Details getDetails(Libro libro) throws RemoteException;

    /**
     * Cerca libri il cui titolo corrisponde (o contiene) la stringa fornita,
     * con autenticazione tramite token.
     * @param token token di autenticazione dell'utente
     * @param title il titolo o parte del titolo del libro
     * @return lista di libri che corrispondono al titolo
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    List<Libro> searchByName(Token token, String title) throws RemoteException;

    /**
     * Cerca libri scritti dall'autore specificato,
     * con autenticazione tramite token.
     * @param token  token di autenticazione dell'utente
     * @param author il nome dell'autore
     * @return lista di libri scritti dall'autore
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    List<Libro> searchByAuthor(Token token, String author) throws RemoteException;

    /**
     * Cerca libri scritti dall'autore specificato e pubblicati nell'anno indicato,
     * con autenticazione tramite token.
     * @param token  token di autenticazione dell'utente
     * @param author il nome dell'autore
     * @param year   l'anno di pubblicazione
     * @return lista di libri corrispondenti ai criteri di ricerca
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    List<Libro> searchByAuthorAndYear(Token token, String author, int year) throws RemoteException;

    /**
     * Ottiene la lista completa di tutti i libri disponibili,
     * con autenticazione tramite token.
     * @param token token di autenticazione dell'utente
     * @return lista di tutti i libri
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    List<Libro> getAllBooks(Token token) throws RemoteException;

    /**
     * Verifica se un libro ha valutazioni o recensioni associate.
     * @param libro il libro da verificare
     * @return true se il libro ha valutazioni o recensioni, false altrimenti
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    boolean hasValRec (Libro libro) throws RemoteException;
}
