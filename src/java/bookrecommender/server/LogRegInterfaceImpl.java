package bookrecommender.server;

import bookrecommender.common.LogRegInterface;
import bookrecommender.common.RegToken;
import bookrecommender.common.Token;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

public class LogRegInterfaceImpl extends UnicastRemoteObject implements LogRegInterface{

    @Serial
    private static final long serialVersionUID = 1L;
    private final DBManager dbManager;

    protected LogRegInterfaceImpl(int port, DBManager dbManager) throws RemoteException {
        super(port);
        this.dbManager = dbManager;
    }

    @Override
    public Token TryLogin(String username, String password) throws RemoteException {
        try {
            return dbManager.loginUtente(username, password, getClientHost());
        }catch(ServerNotActiveException e){
            return null;
        }
    }

    @Override
    public RegToken Register(String nome, String cognome, String CF, String email, String username, String password) throws RemoteException {
        return DBManager.Register(nome, cognome, CF, email, username, password);
    }
}
