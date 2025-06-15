package bookrecommender.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LibInterface extends Remote {
    boolean createLib (Token token, String nome, List<Libro> libri) throws RemoteException;
    boolean deleteLib (Token token, String nome) throws RemoteException;
    boolean updateLib (Token token, String nome, List<Libro> libridel, List<Libro> libriadd) throws RemoteException;
    List<Libro> getLib (Token token, String nome) throws RemoteException;
    List<String> getLibs (Token token) throws RemoteException;
    boolean addValutazione (Token token, Valutazione valutazione) throws RemoteException;
}
