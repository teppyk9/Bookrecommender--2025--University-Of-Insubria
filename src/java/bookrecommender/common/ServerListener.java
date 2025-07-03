package bookrecommender.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaccia remota per un listener che riceve notifiche dal server.
 * <p>
 * Consente di essere avvisati quando il server sta per terminare l'esecuzione.
 */
public interface ServerListener extends Remote {

    /**
     * Metodo chiamato dal server per notificare che sta per arrestarsi.
     * @throws RemoteException in caso di errore di comunicazione remota
     */
    void serverWillStop() throws RemoteException;
}