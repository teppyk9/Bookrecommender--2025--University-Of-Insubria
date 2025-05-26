package bookrecommender.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LogRegInterface extends Remote {
    Token TryLogin(String username, String password) throws RemoteException;
    RegToken Register(String nome, String cognome,String CF,String email,String username,String password) throws RemoteException;
}
