package bookrecommender;

import bookrecommender.enums.FXMLtype;
import bookrecommender.util.CliUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class GUI extends Application {
    /**
     * Metodo di avvio dell'applicazione JavaFX lato server.
     * Viene eseguita l'inizializzazione del server e caricati i pannelli
     * FXML: uno per il controllo dei log, l'altro per la configurazione.
     * @param primaryStage lo {@link Stage} primario fornito da JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        CliUtil.getInstance().init(primaryStage);
        CliUtil.getInstance().buildStage(FXMLtype.CONNESSIONE, null, null);
    }
}