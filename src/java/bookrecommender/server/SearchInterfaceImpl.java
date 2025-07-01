package bookrecommender.server;

import bookrecommender.common.Libro;
import bookrecommender.common.Libro_Details;
import bookrecommender.common.SearchInterface;
import bookrecommender.common.Token;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementazione dell'interfaccia remota {@link SearchInterface}, che consente ai client
 * di effettuare ricerche sui libri memorizzati nel database.
 * Questa classe viene esposta via RMI dal server e consente operazioni come:
 * ricerca per titolo, autore, anno, recupero dettagli di un libro e verifica
 * della presenza di valutazioni o consigli.
 * Ogni metodo è accompagnato da logging server-side per il monitoraggio delle richieste.
 */
public class SearchInterfaceImpl extends UnicastRemoteObject implements SearchInterface {
    /**UID per la serializzazione RMI.*/
    @Serial
    private static final long serialVersionUID = 1L;

    /**Riferimento al gestore del database. */
    private final DBManager dbManager;

    /**Logger utilizzato per registrare le operazioni eseguite dal client. */
    private final Logger logger;

    /**
     * Costruttore dell'implementazione remota dell'interfaccia di ricerca.
     * @param dbManager istanza del gestore del database da utilizzare.
     * @throws RemoteException se si verifica un errore nell'esportazione dell'oggetto remoto.
     */
    protected SearchInterfaceImpl(DBManager dbManager) throws RemoteException {
        super();
        this.dbManager = dbManager;
        this.logger = Logger.getLogger(LogRegInterfaceImpl.class.getName());
    }

    /**
     * Restituisce un oggetto {@link Libro} dato il suo ID univoco.
     * @param id identificatore del libro.
     * @return l'oggetto {@link Libro} corrispondente all'ID.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public Libro getLibro(int id) throws RemoteException {
        try {
            logger.info("Searching book with ID: " + id + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.getLibro(id);
    }

    /**
     * Cerca libri che corrispondono al titolo specificato.
     * @param title titolo del libro da cercare.
     * @return lista di libri con titolo corrispondente.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public List<Libro> searchByName(String title) throws RemoteException {
        try {
            logger.info("Searching for books with title: " + title + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.cercaLibriPerTitolo(title);
    }

    /**
     * Cerca libri scritti da un determinato autore.
     * @param author nome dell'autore.
     * @return lista di libri dell'autore specificato.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public List<Libro> searchByAuthor(String author) throws RemoteException {
        try {
            logger.info("Searching for books by author: " + author + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.cercaLibriPerAutore(author);
    }

    /**
     * Cerca libri scritti da un determinato autore in un determinato anno.
     * @param author nome dell'autore.
     * @param year   anno di pubblicazione.
     * @return lista di libri che soddisfano i criteri.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public List<Libro> searchByAuthorAndYear(String author, int year) throws RemoteException {
        try {
            logger.info("Searching for books by author: " + author + " and year: " + year + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.cercaLibriPerAutoreAnno(author, String.valueOf(year));
    }

    /**
     * Restituisce i dettagli completi di un libro selezionato.
     * @param libro oggetto {@link Libro} di cui si vogliono i dettagli.
     * @return oggetto {@link Libro_Details} con le informazioni dettagliate.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public Libro_Details getDetails(Libro libro) throws RemoteException {
        try{
            logger.info("Getting details for book with ID: " + libro.getId() + " From client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        return dbManager.getDetails(libro);
    }

    /**
     * Cerca libri per titolo, tenendo conto del token utente per eventuali filtri o preferenze.
     * @param token token utente autenticato.
     * @param title titolo da cercare.
     * @return lista di libri corrispondenti.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    public List<Libro> searchByName(Token token, String title) throws RemoteException {
        try{
            logger.info("Searching for books with title: " + title + " From client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        return dbManager.cercaLibriPerTitolo(token,title);
    }

    /**
     * Cerca libri per autore, tenendo conto del token utente.
     * @param token  token utente autenticato.
     * @param author autore da cercare.
     * @return lista di libri corrispondenti.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    public List<Libro> searchByAuthor(Token token, String author) throws RemoteException {
        try{
            logger.info("Searching for books with author: " + author + " From client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        return dbManager.cercaLibriPerAutore(token, author);
    }

    /**
     * Cerca libri per autore e anno, con token utente.
     * @param token  token utente autenticato.
     * @param author autore del libro.
     * @param year   anno di pubblicazione.
     * @return lista di libri corrispondenti.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    public List<Libro> searchByAuthorAndYear(Token token, String author, int year) throws RemoteException {
        try {
            logger.info("Searching for books by author: " + author + " and year: " + year + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.cercaLibriPerAutoreAnno(token, author, String.valueOf(year));
    }

    /**
     * Restituisce la lista completa dei libri visibili all’utente.
     * @param token token dell’utente autenticato.
     * @return lista di libri associati o visibili all’utente.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public List<Libro> getAllBooks(Token token) throws RemoteException {
        try{
            logger.info("Searching for books from client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        return dbManager.cercaTuttiLibriUtente(token);
    }

    /**
     * Verifica se il libro specificato ha almeno una valutazione o un consiglio associato.
     * @param libro oggetto {@link Libro} da analizzare.
     * @return {@code true} se il libro ha almeno una valutazione o consiglio, {@code false} altrimenti.
     * @throws RemoteException se si verifica un errore di comunicazione RMI.
     */
    @Override
    public boolean hasValRec(Libro libro) throws RemoteException {
        return dbManager.haValConsAss(libro.getId());
    }
}