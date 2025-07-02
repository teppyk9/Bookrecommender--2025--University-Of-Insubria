package bookrecommender.server;

import java.util.*;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Handler personalizzato per la gestione dei log in un'interfaccia JavaFX.
 * Questo handler estende {@link Handler} del framework di logging Java
 * e consente di visualizzare i messaggi di log all’interno di un componente
 * {@link TextFlow}, permettendo all’utente di vedere in tempo reale l’attività
 * del server.
 * Ogni messaggio è stilizzato con una classe CSS diversa a seconda del livello di severità
 * (es. INFO, WARNING, SEVERE).
 */
public class TextFlowHandler extends Handler {
    /** Contenitore visuale per i messaggi di log. */
    private final TextFlow flow;
    /** ScrollPane contenente il TextFlow, usato per mantenere lo scroll in fondo. */
    private final ScrollPane scrollPane;
    /** Mappa tra livelli di log e classi CSS da applicare. */
    private final Map<Level,String> styleMap = Map.of(
            Level.INFO,    "info",
            Level.WARNING, "warning",
            Level.SEVERE,  "severe"
    );

    /**
     * Crea un nuovo {@code TextFlowHandler} associato a un'area di log testuale
     * @param flow       componente JavaFX in cui inserire i log
     * @param scrollPane componente per lo scorrimento automatico in basso
     */
    public TextFlowHandler(TextFlow flow, ScrollPane scrollPane) {
        this.flow = flow;
        this.scrollPane = scrollPane;
        setLevel(Level.ALL);
        setFormatter(new SimpleFormatter());
    }

    /**
     * Metodo chiamato automaticamente dal framework di logging per ogni messaggio.
     * Il testo del messaggio viene formattato, assegnato a una classe CSS secondo
     * il suo livello di severità, e aggiunto all'interfaccia JavaFX in modo asincrono
     * tramite {@code Platform.runLater()}.
     * @param record il record di log da visualizzare
     */
    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) return;
        String msg = getFormatter().format(record);
        String cssClass = styleMap.getOrDefault(record.getLevel(), "info");
        Text txt = new Text(msg);
        txt.getStyleClass().add(cssClass);

        // Aggiunta sicura al thread JavaFX
        Platform.runLater(() -> {
            flow.getChildren().add(txt);
            scrollPane.setVvalue(1.0);
        });
    }

    /**
     * Metodo richiesto ma non utilizzato in questa implementazione.
     */
    @Override public void flush() {}

    /**
     * Metodo richiesto ma non utilizzato in questa implementazione.
     */
    @Override public void close() throws SecurityException {}
}