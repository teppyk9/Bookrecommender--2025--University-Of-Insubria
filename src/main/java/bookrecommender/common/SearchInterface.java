package bookrecommender.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SearchInterface extends Remote {
    List<Libro> searchByName(String title) throws RemoteException;
}
