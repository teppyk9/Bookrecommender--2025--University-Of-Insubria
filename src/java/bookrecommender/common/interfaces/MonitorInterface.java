package bookrecommender.common.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaccia remota per la registrazione di listener nel sistema di raccomandazione.
 * <p>
 * Permette ai client di registrare un {@link ServerListener} per ricevere
 * notifiche o eventi generati dal server.
 * </p>
 *
 * <p>Essendo un'interfaccia RMI, tutti i metodi possono lanciare
 * {@link RemoteException} in caso di problemi di comunicazione remota.</p>
 *
 * @see ServerListener
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
