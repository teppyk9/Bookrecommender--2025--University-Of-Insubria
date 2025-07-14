package bookrecommender.server;

import bookrecommender.common.LibInterface;
import bookrecommender.common.Libro;
import bookrecommender.common.Token;
import bookrecommender.common.Valutazione;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementazione remota dell'interfaccia {@link LibInterface} per la gestione
 * delle librerie personali degli utenti. Fornisce funzionalità RMI per la creazione,
 * modifica, eliminazione, valutazione e consultazione delle librerie e dei libri.
 * Tutte le operazioni sono registrate tramite {@link Logger} e delegate al {@link DBManager}.
 * In caso di chiamata RMI non valida (es. Client sconosciuto), i metodi restituiscono valori di fallback.
 */
public class LibInterfaceImpl extends UnicastRemoteObject implements LibInterface {
    @Serial
    private static final long serialVersionUID = 1L;
    private final DBManager dbManager;
    private final Logger logger;

    /**
     * Costruttore della classe {@code LibInterfaceImpl}
     * @param dbManager Il gestore della logica applicativa e dell'accesso al database.
     * @throws RemoteException Se si verifica un errore nella configurazione dell'oggetto remoto.
     */
    protected LibInterfaceImpl(DBManager dbManager) throws RemoteException {
        super();
        this.dbManager = dbManager;
        this.logger = Logger.getLogger(LibInterfaceImpl.class.getName());
    }

    /**
     * Crea una nuova libreria per l'utente specificato.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria da creare.
     * @param libri Lista dei libri iniziali da inserire nella libreria.
     * @return {@code true} se la libreria è stata creata con successo, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean createLib(Token token, String nome, List<Libro> libri) throws RemoteException {
        try {
            logger.info("Creazione libreria: " + nome + " da parte di " + token.getUserId() + "con IP: " + getClientHost());
        }catch(ServerNotActiveException e){
            return false;
        }
        return dbManager.creaLibreria(token, nome, libri);
    }

    /**
     * Elimina una libreria dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria da eliminare.
     * @return {@code true} se la libreria è stata eliminata con successo, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean deleteLib(Token token, String nome) throws RemoteException {
        try {
            logger.info("Eliminazione libreria: " + nome + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        return dbManager.eliminaLibreria(token, nome);
    }

    /**
     * Aggiorna il contenuto di una libreria dell'utente, aggiungendo o rimuovendo libri.
     * Vengono effettuati controlli per evitare rimozioni di libri valutati o consigliati.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria da aggiornare.
     * @param libriUp Lista aggiornata dei libri desiderati nella libreria.
     * @return Lista di interi: {@code [1]} se aggiornamento riuscito, {@code [0, idLibro, codiceErrore, ...]} altrimenti.
     *         I codici errore indicano: 0 → valutazione presente, 1 → libro consigliato, 2 → libro usato in un consiglio.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public List<Integer> updateLib(Token token, String nome, List<Libro> libriUp) throws RemoteException {
        try {
            logger.info("Aggiornamento libreria: " + nome + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return List.of(0);
        }
        return dbManager.aggiornaLibreria(token, nome, libriUp);
    }

    /**
     * Recupera i libri presenti in una specifica libreria dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria.
     * @return Lista dei libri contenuti nella libreria, oppure {@code null} in caso di errore.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public List<Libro> getLib(Token token, String nome) throws RemoteException {
    try {
        logger.info("Recupero libreria: " + nome + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
    } catch (ServerNotActiveException e) {
        return null;
    }
    return dbManager.getLibreria(token, nome);
    }

    /**
     * Recupera tutti i nomi delle librerie associate all'utente.
     * @param token Token di autenticazione dell'utente.
     * @return Lista dei nomi delle librerie, oppure {@code null} in caso di errore.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public List<String> getLibs(Token token) throws RemoteException {
    try {
        logger.info("Recupero librerie da parte di " + token.getUserId() + " con IP: " + getClientHost());
    } catch (ServerNotActiveException e) {
        return null;
    }
        return dbManager.getLibrerie(token);
    }

    /**
     * Aggiunge una valutazione a un libro per conto dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param valutazione Oggetto {@link Valutazione} contenente punteggi e commenti.
     * @return {@code true} se la valutazione è stata inserita correttamente, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean addValutazione(Token token, Valutazione valutazione) throws RemoteException {
        try{
            logger.info("Aggiunta valutazione da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        return dbManager.addValutazione(token, valutazione);
    }

    /**
     * Aggiunge un consiglio associato a un libro dell'utente, suggerendo fino a 3 altri libri.
     * @param token Token di autenticazione dell'utente.
     * @param libri Lista dei libri: il primo è il libro target, gli altri sono suggerimenti.
     * @return {@code true} se il consiglio è stato salvato correttamente, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean addConsiglio(Token token, List<Libro> libri) throws RemoteException {
        try{
            logger.info("Aggiunta consigli da parte di " + token.getUserId() + " con IP: " + getClientHost() + " a libro: " + libri.get(0).getId());
        } catch (ServerNotActiveException e) {
            return false;
        }
        return dbManager.addConsiglio(token, libri);
    }

    /**
     * Modifica il nome di una libreria dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param oldName Nome attuale della libreria.
     * @param newName Nuovo nome desiderato.
     * @return {@code true} se il nome è stato modificato correttamente, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean modifyLibName(Token token, String oldName, String newName) throws RemoteException {
        try{
            logger.info("Modifica del nome della libreria " + oldName + " con il nome: " + newName + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        return dbManager.modificaNomeLibreria(token, oldName, newName);
    }

    /**
     * Verifica se un determinato libro è presente in almeno una libreria dell'utente.
     * @param token Token di autenticazione dell'utente.
     * @param libro Libro da cercare.
     * @return {@code true} se il libro è presente in una delle librerie dell'utente, {@code false} altrimenti.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public boolean isLibPresent(Token token, Libro libro) throws RemoteException {
        //TODO: Gestire ServerNotActiveException e capire se avere una stampa di log
        return dbManager.utenteContieneLibro(token.getUserId(), libro.getId());
    }

    /**
     * Restituisce la data di creazione di una libreria.
     * @param token Token di autenticazione dell'utente.
     * @param nome Nome della libreria.
     * @return {@link LocalDate} corrispondente alla data di creazione della libreria,
     *         oppure {@code null} se non trovata o in caso di errore.
     * @throws RemoteException In caso di errore di comunicazione remota.
     */
    @Override
    public LocalDate getCreationDate(Token token, String nome) throws RemoteException {
        //TODO: Gestire ServerNotActiveException e capire se avere una stampa di log
        return dbManager.dataCreazioneLibreria(token, nome);
    }

    @Override
    public boolean existVal(Token token, Libro libro) throws RemoteException {
        //TODO: Gestire ServerNotActiveException e capire se avere una stampa di log
        return dbManager.existVal(token, libro);
    }

    @Override
    public boolean existCon(Token token, Libro libro) throws RemoteException {
        //TODO: Gestire ServerNotActiveException e capire se avere una stampa di log
        return dbManager.existCon(token, libro);
    }

    @Override
    public boolean updateVal(Token token, Valutazione valutazione) throws RemoteException {
        try{
            logger.info("Modifica valutazione per libro " + valutazione.getIdLibro() + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        return dbManager.updateVal(token, valutazione);
    }

    @Override
    public boolean updateCon(Token token, List<Libro> libri) throws RemoteException {
        try{
            logger.info("Modifica consigli per libro " + libri.get(0).getId() + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        return dbManager.updateCon(token, libri);
    }

    @Override
    public boolean deleteVal(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Eliminazione valutazione del libro " + libro.getId() + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        return dbManager.deleteVal(token, libro);
    }

    @Override
    public boolean deleteCon(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Eliminazione consigli del libro " + libro.getId() + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        return dbManager.deleteCon(token, libro);
    }

    @Override
    public LocalDate getValDate(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Recupero data di modifica per la valutazione del libro " + libro.getId()  + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return null;
        }
        return dbManager.getValDate(token, libro);
    }

    @Override
    public LocalDate getConDate(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Recupero data di modifica per il consiglio del libro " + libro.getId()  + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return null;
        }
        return dbManager.getConsDate(token, libro);
    }

    @Override
    public List<Libro> getConsigli(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Recupero consiglio del libro " + libro.getId()  + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return null;
        }
        return dbManager.getConsiglio(token, libro);
    }

    @Override
    public Valutazione getValutazione(Token token, Libro libro) throws RemoteException {
        try{
            logger.info("Recupero valutazione del libro " + libro.getId()  + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return null;
        }
        return dbManager.getValutazione(token, libro);
    }
}
