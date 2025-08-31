package bookrecommender.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaccia remota per un listener che riceve notifiche dal server.
 * <p>
 * Consente di essere avvisati quando il server sta per terminare l'esecuzione.
 * </p>
 * <p>
 * Essendo un'interfaccia RMI, i metodi possono lanciare {@link RemoteException}
 * in caso di problemi di comunicazione remota.
 * </p>
 */
public interface ServerListener extends Remote {

    /**
     * Notifica che il server sta per arrestarsi.
     *
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    void serverWillStop() throws RemoteException;
}
