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

public class SearchInterfaceImpl extends UnicastRemoteObject implements SearchInterface {
    @Serial
    private static final long serialVersionUID = 1L;

    private final DBManager dbManager;
    private final Logger logger;

    protected SearchInterfaceImpl(DBManager dbManager) throws RemoteException {
        super();
        this.dbManager = dbManager;
        this.logger = Logger.getLogger(LogRegInterfaceImpl.class.getName());
    }

    @Override
    public List<Libro> searchByName(String title) throws RemoteException {
        try {
            logger.info("Searching for books with title: " + title + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.cercaLibriPerTitolo(title);
    }

    @Override
    public List<Libro> searchByAuthor(String author) throws RemoteException {
        try {
            logger.info("Searching for books by author: " + author + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.cercaLibriPerAutore(author);
    }

    @Override
    public List<Libro> searchByAuthorAndYear(String author, int year) throws RemoteException {
        try {
            logger.info("Searching for books by author: " + author + " and year: " + year + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.cercaLibriPerAutoreAnno(author, String.valueOf(year));
    }

    @Override
    public Libro_Details getDetails(Libro libro) throws RemoteException {
        try{
            logger.info("Getting details for book with ID: " + libro.getId() + " From client " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        return dbManager.getDetails(libro);
    }

    public List<Libro> searchByName(Token token, String title) throws RemoteException {
        return null;
    }

    public List<Libro> searchByAuthor(Token token, String author) throws RemoteException {
        return null;
    }


    public List<Libro> searchByAuthorAndYear(Token token, String author, int year) throws RemoteException {
        return null;
    }
}