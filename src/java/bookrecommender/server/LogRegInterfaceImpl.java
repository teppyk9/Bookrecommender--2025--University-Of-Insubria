package bookrecommender.server;

import bookrecommender.common.LogRegInterface;
import bookrecommender.common.RegToken;
import bookrecommender.common.Token;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class LogRegInterfaceImpl extends UnicastRemoteObject implements LogRegInterface{

    @Serial
    private static final long serialVersionUID = 1L;
    private final DBManager dbManager;
    private final Logger logger;

    protected LogRegInterfaceImpl(DBManager dbManager) throws RemoteException {
        super();
        this.dbManager = dbManager;
        this.logger = Logger.getLogger(LogRegInterfaceImpl.class.getName());
    }

    @Override
    public Token TryLogin(String username, String password) throws RemoteException {
        try {
            logger.info("TryLogin called with username: " + username + ", password: " + password + ", client host: " + getClientHost());
            return dbManager.loginUtente(username, password, getClientHost());
        }catch(ServerNotActiveException e){
            return null;
        }
    }

    @Override
    public RegToken Register(String nome, String cognome, String CF, String email, String username, String password) throws RemoteException {
        try {
            logger.info("Register called with nome: " + nome + ", cognome: " + cognome + ", CF: " + CF + ", email: " + email + ", username: " + username + ", client host: " + getClientHost());
            return dbManager.Register(nome, cognome, CF, email, username, password);
        } catch (ServerNotActiveException e) {
            return null;
        }
    }

    @Override
    public boolean LogOut(Token token) throws RemoteException {
        try {
            logger.info("LogOut called for token: " + token.getToken() + ", client host: " + getClientHost());
        } catch (ServerNotActiveException ignored) {}
        return dbManager.LogOut(token);
    }
}
