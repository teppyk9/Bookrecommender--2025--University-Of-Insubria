package bookrecommender.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaccia remota per registrare listener nel sistema di raccomandazione.
 * <p>
 * Consente ai client di registrare un {@link ServerListener} per ricevere
 * notifiche o eventi generati dal server.
 * </p>
 * <p>
 * Essendo un'interfaccia RMI, i metodi possono lanciare
 * {@link RemoteException} in caso di problemi di comunicazione remota.
 * </p> *
 * @see ServerListener
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
public interface MonitorInterface extends Remote {

    /**
     * Registra un listener sul server per ricevere notifiche o aggiornamenti.
     *
     * @param listener l'oggetto listener da registrare
     * @throws RemoteException se si verifica un errore durante la comunicazione RMI
     */
    void registerListener(ServerListener listener) throws RemoteException;
}
