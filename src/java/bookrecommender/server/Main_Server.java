package bookrecommender.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

class Main_Server{
    private static final int SERVER_PORT = 3939;
    private static final int REGISTRY_PORT = 1099;

    private static final Logger logger = Logger.getLogger(Main_Server.class.getName());

    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        dbManager.svuotaSessioniLogin();
        try {
            Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);
            SearchInterfaceImpl SearchServer = new SearchInterfaceImpl(SERVER_PORT, dbManager);
            LogRegInterfaceImpl LogRegServer = new LogRegInterfaceImpl(SERVER_PORT, dbManager);
            registry.rebind("Search_Interface", SearchServer);
            registry.rebind("LogReg_Interface", LogRegServer);
            System.err.println("Server ready");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore nell'inizializzazione del server>", e);
            System.exit(0);
        }
    }
}