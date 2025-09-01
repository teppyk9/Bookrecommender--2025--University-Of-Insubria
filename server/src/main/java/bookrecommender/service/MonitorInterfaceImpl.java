package bookrecommender.service;

import bookrecommender.interfaces.MonitorInterface;
import bookrecommender.interfaces.ServerListener;
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
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
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

    /**
     * Registra un listener remoto per ricevere notifiche dal server.
     * <p>
     * La struttura dati è thread-safe; possono essere registrati più listener.
     * Non è prevista la rimozione né il controllo duplicati in questa implementazione.
     * Il parametro non deve essere {@code null}.
     * </p>
     *
     * @param listener istanza remota che implementa {@link ServerListener}
     * @throws RemoteException se si verifica un errore di comunicazione RMI
     * @throws NullPointerException se {@code listener} è {@code null}
     */
    @Override
    public void registerListener(ServerListener listener) throws RemoteException {
        listeners.add(listener);
    }

    /**
     * Notifica a tutti i listener registrati che il server sta per arrestarsi.
     * <p>
     * Per ogni listener viene invocato in remoto {@link ServerListener#serverWillStop()}.
     * Eventuali {@link RemoteException} sollevate dai singoli listener vengono ignorate
     * per proseguire la notifica agli altri.
     * </p>
     */
    public void notifyShutdown() {
        for (ServerListener l : listeners) {
            try { l.serverWillStop(); } catch (RemoteException ignored) {}
        }
    }
}