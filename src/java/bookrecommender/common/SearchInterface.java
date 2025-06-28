package bookrecommender.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SearchInterface extends Remote {
    Libro getLibro(int id) throws RemoteException;
    List<Libro> searchByName(String title) throws RemoteException;
    List<Libro> searchByAuthor(String author) throws RemoteException;
    List<Libro> searchByAuthorAndYear(String author, int year) throws RemoteException;
    Libro_Details getDetails(Libro libro) throws RemoteException;
    List<Libro> searchByName(Token token, String title) throws RemoteException;
    List<Libro> searchByAuthor(Token token, String author) throws RemoteException;
    List<Libro> searchByAuthorAndYear(Token token, String author, int year) throws RemoteException;
    List<Libro> getAllBooks(Token token) throws RemoteException;
    boolean hasValRec (Libro libro) throws RemoteException;
}
