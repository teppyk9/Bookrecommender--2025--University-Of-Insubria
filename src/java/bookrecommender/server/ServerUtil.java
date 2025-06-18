package bookrecommender.server;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerUtil {
    private Stage primaryStage;

    private int registryPort;

    private DBManager dbManager;

    private Registry registry;
    private SearchInterfaceImpl SearchServer;
    private LogRegInterfaceImpl LogRegServer;
    private LibInterfaceImpl LibServer;

    //1099

    private static final Logger logger = Logger.getLogger(ServerUtil.class.getName());

    private ServerUtil() {
        // Al momento non è necessario alcun codice nel costruttore, poi ci penso se serve
    }

    private static class Holder {
        private static final ServerUtil INSTANCE = new ServerUtil();
    }

    public static ServerUtil getInstance() {
        return ServerUtil.Holder.INSTANCE;
    }

    public void init(Stage stage) {
        if (this.primaryStage == null) {
            this.primaryStage = stage;
        } else {
            logger.log(Level.WARNING, "PrimaryStage already initialized.");
        }
    }

    public Stage getPrimaryStage() {
        if (primaryStage == null) {
            logger.log(Level.SEVERE, "PrimaryStage has not been initialized. Call init() first.");
        }
        return primaryStage;
    }

    public void setDBManager() {
        if (dbManager == null) {
            dbManager = new DBManager();
        } else {
            logger.log(Level.WARNING, "DBManager already initialized.");
        }
    }

    public static boolean isTcpPortAvailable() {
        try (ServerSocket ss = new ServerSocket(ServerUtil.getInstance().registryPort)) {
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean tryDBConnection(String url, String user, String password){
        dbManager.setConnection(url, user, password);
        if (!dbManager.connect()) {
            logger.log(Level.SEVERE, "Database connection failed.");
            return false;
        } else {
            logger.log(Level.INFO, "Database connection established.");
            return true;
        }
    }

    public void setPort(int port) {
        registryPort = port;
    }

    public void setServer() {
        dbManager.svuotaSessioniLogin();
        try {
            registry = LocateRegistry.createRegistry(registryPort);
            SearchServer = new SearchInterfaceImpl(dbManager);
            LogRegServer = new LogRegInterfaceImpl(dbManager);
            LibServer = new LibInterfaceImpl(dbManager);
            registry.rebind("Search_Interface", SearchServer);
            registry.rebind("LogReg_Interface", LogRegServer);
            registry.rebind("Lib_Interface", LibServer);
            System.err.println("Server ready");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore nell'inizializzazione del server>", e);
        }
    }

    public void loadFXML(String fxmlFile, String title) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        try {
            Parent root = loader.load();
            Stage stage = getPrimaryStage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.getIcons().setAll(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/server_connection.png"))));
            stage.setResizable(false);
            stage.show();
            stage.getScene().getRoot().requestFocus();
        }catch(Exception e) {
            logger.log(Level.SEVERE, "Error loading FXML file", e);
        }
    }
}
