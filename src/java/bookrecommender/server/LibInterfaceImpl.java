package bookrecommender.server;

import bookrecommender.common.LibInterface;
import bookrecommender.common.Libro;
import bookrecommender.common.Token;
import bookrecommender.common.Valutazione;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.logging.Logger;

public class LibInterfaceImpl extends UnicastRemoteObject implements LibInterface {
    @Serial
    private static final long serialVersionUID = 1L;
    private final DBManager dbManager;
    private final Logger logger;

    protected LibInterfaceImpl(DBManager dbManager) throws RemoteException {
        super();
        this.dbManager = dbManager;
        this.logger = Logger.getLogger(LibInterfaceImpl.class.getName());
    }

    @Override
    public boolean createLib(Token token, String nome, List<Libro> libri) throws RemoteException {
        try {
            logger.info("Creazione libreria: " + nome + " da parte di " + token.getUserId() + "con IP: " + getClientHost());
        }catch(ServerNotActiveException e){
            return false;
        }
        return dbManager.creaLibreria(token, nome, libri);
    }

    @Override
    public boolean deleteLib(Token token, String nome) throws RemoteException {
        try {
            logger.info("Eliminazione libreria: " + nome + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        return dbManager.eliminaLibreria(token, nome);
    }

    @Override
    public boolean updateLib(Token token, String nome, List<Libro> libridel, List<Libro> libriadd) throws RemoteException {
        try {
            logger.info("Aggiornamento libreria: " + nome + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        return dbManager.aggiornaLibreria(token, nome, libridel, libriadd);
    }

    @Override
    public List<Libro> getLib(Token token, String nome) throws RemoteException {
    try {
        logger.info("Recupero libreria: " + nome + " da parte di " + token.getUserId() + " con IP: " + getClientHost());
    } catch (ServerNotActiveException e) {
        return null;
    }
    return dbManager.getLibreria(token, nome);
    }

    @Override
    public List<String> getLibs(Token token) throws RemoteException {
    try {
        logger.info("Recupero librerie da parte di " + token.getUserId() + " con IP: " + getClientHost());
    } catch (ServerNotActiveException e) {
        return null;
    }
        return dbManager.getLibrerie(token);
    }

    @Override
    public boolean addValutazione(Token token, Valutazione valutazione) throws RemoteException {
        try{
            logger.info("Aggiunta valutazione da parte di " + token.getUserId() + " con IP: " + getClientHost());
        } catch (ServerNotActiveException e) {
            return false;
        }
        return dbManager.addValutazione(token, valutazione);
    }
}
