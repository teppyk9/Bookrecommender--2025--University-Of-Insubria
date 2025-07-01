package bookrecommender.server;

import bookrecommender.common.MonitorInterface;
import bookrecommender.common.ServerListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MonitorInterfaceImpl extends UnicastRemoteObject implements MonitorInterface {

    private final List<ServerListener> listeners = new CopyOnWriteArrayList<>();

    public MonitorInterfaceImpl() throws RemoteException {
        super();
    }

    @Override
    public void registerListener(ServerListener listener) throws RemoteException {
        listeners.add(listener);
    }

    public void notifyShutdown() {
        for (ServerListener l : listeners) {
            try { l.serverWillStop(); } catch (RemoteException ignored) {}
        }
    }
}