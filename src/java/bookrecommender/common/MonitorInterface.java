package bookrecommender.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MonitorInterface extends Remote {

    void registerListener(ServerListener listener) throws RemoteException;
}
