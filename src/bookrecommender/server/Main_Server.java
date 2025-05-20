package bookrecommender.server;

import bookrecommender.common.Libro;
import bookrecommender.common.SearchInterface;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

class Main_Server extends UnicastRemoteObject implements SearchInterface {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int SERVER_PORT = 3939;
    private static final int REGISTRY_PORT = 1099;

    private static final String SERVER_NAME = "BookRecommender";

    private final DBManager dbManager = new DBManager();

    public Main_Server() throws Exception {
        super(SERVER_PORT);
    }

    public static void main(String[] args) {
        try {
            Main_Server server = new Main_Server();
            Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);
            registry.rebind(SERVER_NAME, server);
            System.err.println("Server ready");
        } catch (Exception e) {
            System.out.println("Server could not start");
            System.exit(0);
        }
    }

    @Override
    public List<Libro> searchByName(String title) throws RemoteException {
        try {
            System.out.println("Searching for books with title: " + title + " From client " + getClientHost());
        }catch (ServerNotActiveException ignored){}
        return dbManager.cercaLibriPerTitolo(title);
    }
}