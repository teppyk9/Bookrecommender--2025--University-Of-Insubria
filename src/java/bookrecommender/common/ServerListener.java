package bookrecommender.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerListener extends Remote {

    void serverWillStop() throws RemoteException;
}