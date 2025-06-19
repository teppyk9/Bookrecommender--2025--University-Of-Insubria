package bookrecommender.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MonitorInterface extends Remote {

    boolean ping() throws RemoteException;

    void registerListener(ServerListener listener) throws RemoteException;
}
