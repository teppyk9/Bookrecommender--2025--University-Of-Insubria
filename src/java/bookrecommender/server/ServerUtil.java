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

    private DBManager dbManager;

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
            logger.log(Level.WARNING, "PrimaryStage già inizializzato.");
        }
    }

    public Stage getPrimaryStage() {
        if (primaryStage == null) {
            logger.log(Level.SEVERE, "PrimaryStage non inizializzato.");
        }
        return primaryStage;
    }

    public void setDBManager() {
        if (dbManager == null) {
            dbManager = new DBManager();
        } else {
            logger.log(Level.WARNING, "DBManager già inizializzato.");
        }
    }

    public boolean isTcpPortAvailable(int portNumber) {
        try (ServerSocket ss = new ServerSocket(portNumber)) {
            ss.setReuseAddress(true);
            logger.info("Test sulla porta TCP " + portNumber + " riuscito.");
            return true;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Test sulla porta TCP " + portNumber + " fallito: " + e.getMessage());
            return false;
        }
    }

    public boolean tryConnectToDb(String url, String user, String password) {
        return dbManager.tryConnection(url, user, password);
    }

    public boolean connectToDb(String url, String user, String password){
        return dbManager.connect(url, user, password);
    }

    public boolean setServer(int port) {
        dbManager.svuotaSessioniLogin();
        try {
            Registry registry = LocateRegistry.createRegistry(port);
            SearchInterfaceImpl searchServer = new SearchInterfaceImpl(dbManager);
            LogRegInterfaceImpl logRegServer = new LogRegInterfaceImpl(dbManager);
            LibInterfaceImpl libServer = new LibInterfaceImpl(dbManager);
            registry.rebind("Search_Interface", searchServer);
            registry.rebind("LogReg_Interface", logRegServer);
            registry.rebind("Lib_Interface", libServer);
            logger.info("Server ready");
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore nell'inizializzazione del server>", e);
            return false;
        }
    }

    public void loadFXML(String fxmlFile, String title, boolean newWindow) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = newWindow
                    ? new Stage()
                    : primaryStage;
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setResizable(false);
            stage.getIcons().setAll(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/server/icons/server_connection.png"))));
            stage.show();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Errore nel caricamento del file FXML: " + fxmlFile, e);
        }
    }
}
