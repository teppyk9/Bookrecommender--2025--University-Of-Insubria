package bookrecommender.server;

import bookrecommender.common.MonitorInterface;
import bookrecommender.common.ServerListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementazione dell'interfaccia remota {@link MonitorInterface}, utilizzata
 * per registrare listener remoti e notificare eventi importanti come l'arresto del server.
 * I listener registrati devono implementare l'interfaccia {@link ServerListener}.
 * La lista dei listener è thread-safe grazie all'uso di {@link CopyOnWriteArrayList}.
 * Questa classe viene esportata come oggetto RMI.
 */
public class MonitorInterfaceImpl extends UnicastRemoteObject implements MonitorInterface {

    /**Lista thread-safe dei listener remoti registrati*/
    private final List<ServerListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Costruttore della classe {@code MonitorInterfaceImpl}.
     * Esporta l'oggetto remoto per consentire invocazioni da parte dei client.
     * @throws RemoteException se si verifica un errore durante l'esportazione RMI.
     */
    public MonitorInterfaceImpl() throws RemoteException {
        super();
    }

    @Override
    public void registerListener(ServerListener listener) throws RemoteException {
        listeners.add(listener);
    }

    /**
     * Notifica a tutti i listener registrati che il server sta per arrestarsi.
     * Il metodo richiama in remoto {@code serverWillStop()} su ciascun listener.
     * Eventuali errori di rete sono ignorati.
     */
    public void notifyShutdown() {
        for (ServerListener l : listeners) {
            try { l.serverWillStop(); } catch (RemoteException ignored) {}
        }
    }
}