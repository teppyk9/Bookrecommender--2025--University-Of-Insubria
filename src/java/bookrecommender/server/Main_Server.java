package bookrecommender.server;

import bookrecommender.server.util.ServerUtil;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principale del lato server dell'applicazione Book Recommender.
 * Estende {@link Application} per avviare l'interfaccia JavaFX e caricare le componenti grafiche del server,
 * tra cui il pannello di log e la configurazione del server.
 * Utilizza {@link ServerUtil} come gestore singleton per l'inizializzazione e il caricamento delle interfacce FXML.
 */
public class Main_Server extends Application {
    /**
     * Metodo di avvio dell'applicazione JavaFX lato server.
     * Viene eseguita l'inizializzazione del server e caricati i pannelli
     * FXML: uno per il controllo dei log, l'altro per la configurazione.
     * @param primaryStage lo {@link Stage} primario fornito da JavaFX.
     */
    @Override
    public void start(Stage primaryStage){
        ServerUtil.getInstance().init(primaryStage);
        ServerUtil.getInstance().loadFXML("/bookrecommender/server/fxml/ServerControl.fxml","Server log-control",false);
        ServerUtil.getInstance().loadFXML("/bookrecommender/server/fxml/ServerConfig.fxml","Server Configuration",true);
    }

    /**
     * Metodo main: punto di ingresso dell'applicazione server.
     * Richiama il metodo {@link #launch(String...)} per avviare
     * l'applicazione JavaFX.
     * @param args eventuali argomenti da linea di comando.
     */
    public static void main(String[] args) {
        launch(args);
    }
}