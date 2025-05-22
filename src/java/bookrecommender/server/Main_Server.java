package bookrecommender.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

class Main_Server{
    private static final int SERVER_PORT = 3939;
    private static final int REGISTRY_PORT = 1099;

    public static void main(String[] args) {
        DBManager dbManager = new DBManager();
        try {
            Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);
            SearchInterfaceImpl server = new SearchInterfaceImpl(SERVER_PORT, dbManager);
            registry.rebind("Search_Interface", server);
            System.err.println("Server ready");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}