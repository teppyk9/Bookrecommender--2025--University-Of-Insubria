package bookrecommender.server;

import bookrecommender.common.Libro;
import bookrecommender.common.Libro_Details;
import bookrecommender.common.SearchInterface;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class SearchInterfaceImpl extends UnicastRemoteObject implements SearchInterface {
    @Serial
    private static final long serialVersionUID = 1L;

    private final DBManager dbManager;
    protected SearchInterfaceImpl(int port, DBManager dbManager) throws RemoteException {
        super(port);
        this.dbManager = dbManager;
    }

    @Override
    public List<Libro> searchByName(String title) throws RemoteException {
        try {
            System.out.println("Searching for books with title: " + title + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.cercaLibriPerTitolo(title);
    }

    @Override
    public List<Libro> searchByAuthor(String author) throws RemoteException {
        try {
            System.out.println("Searching for books by author: " + author + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.cercaLibriPerAutore(author);
    }

    @Override
    public List<Libro> searchByAuthorAndYear(String author, int year) throws RemoteException {
        try {
            System.out.println("Searching for books by author: " + author + " and year: " + year + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.cercaLibriPerAutoreAnno(author, String.valueOf(year));
    }

    @Override
    public Libro_Details getDetails(int id) throws RemoteException {
        return null;
    }
}
