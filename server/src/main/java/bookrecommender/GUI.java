package bookrecommender;

import bookrecommender.util.ServerUtil;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principale JavaFX per l'interfaccia server.
 * <p>
 * Estende {@link javafx.application.Application} e rappresenta
 * il punto di ingresso grafico lato server: inizializza i servizi
 * attraverso {@link ServerUtil} e carica le interfacce FXML
 * necessarie al controllo dei log e alla configurazione.
 * </p>
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbain Gabriele, 755699, VA
 */
public class GUI extends Application {
    /**
     * Metodo di avvio dell'applicazione JavaFX lato server.
     * Viene eseguita l'inizializzazione del server e caricati i pannelli
     * FXML: uno per il controllo dei log, l'altro per la configurazione.
     * @param primaryStage lo {@link Stage} primario fornito da JavaFX.
     */
    @Override
    public void start(Stage primaryStage){
        ServerUtil.getInstance().init(primaryStage);
        ServerUtil.getInstance().loadFXML("/fxml/ServerControl.fxml","Server log-control",false);
        ServerUtil.getInstance().loadFXML("/fxml/ServerConfig.fxml","Server Configuration",true);
    }
}
