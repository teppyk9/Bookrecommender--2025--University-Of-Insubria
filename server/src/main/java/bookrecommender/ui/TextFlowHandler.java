package bookrecommender.ui;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Handler personalizzato per la gestione dei log in un'interfaccia JavaFX.
 * <p>
 * Estende {@link Handler} e visualizza i messaggi di log all’interno di un
 * {@link TextFlow}, così da poter seguire in tempo reale l’attività del server.
 * Ogni messaggio riceve una classe CSS in base al livello di severità
 * ({@link Level#INFO}, {@link Level#WARNING}, {@link Level#SEVERE}), utile per lo styling.
 * </p>
 *
 * <h2>Caratteristiche</h2>
 * <ul>
 *   <li>Thread-safe lato UI: l’append avviene su {@link Platform#runLater(Runnable)}.</li>
 *   <li>Scroll automatico a fondo lista tramite {@link ScrollPane} associato.</li>
 *   <li>Formattazione delegata al {@link Formatter} impostato (default: {@link SimpleFormatter}).</li>
 * </ul>
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
public class TextFlowHandler extends Handler {

    /** Contenitore visuale per i messaggi di log. */
    private final TextFlow flow;

    /** ScrollPane contenente il TextFlow, usato per mantenere lo scroll in fondo. */
    private final ScrollPane scrollPane;

    /**
     * Mappa tra livelli di log e classi CSS da applicare.
     * <p>
     * Le classi devono essere definite nel foglio di stile dell’applicazione
     * (es. <code>.info</code>, <code>.warning</code>, <code>.severe</code>).
     * </p>
     */
    private final Map<Level, String> styleMap = Map.of(
            Level.INFO,    "info",
            Level.WARNING, "warning",
            Level.SEVERE,  "severe"
    );

    /**
     * Crea un nuovo {@code TextFlowHandler} associato a un'area di log testuale.
     *
     * @param flow       componente JavaFX in cui inserire i log (non {@code null})
     * @param scrollPane componente per lo scorrimento automatico in basso (non {@code null})
     * @throws NullPointerException se {@code flow} o {@code scrollPane} sono {@code null}
     */
    public TextFlowHandler(TextFlow flow, ScrollPane scrollPane) {
        this.flow = Objects.requireNonNull(flow, "flow");
        this.scrollPane = Objects.requireNonNull(scrollPane, "scrollPane");
        setLevel(Level.ALL);
        setFormatter(new SimpleFormatter());
    }

    /**
     * Pubblica un record di log nell'interfaccia grafica.
     * <p>
     * Il testo è formattato con il {@link Formatter} corrente e viene applicata
     * una classe CSS in base al livello del record. L'append è eseguito
     * sul thread JavaFX tramite {@link Platform#runLater(Runnable)}.
     * </p>
     *
     * @param record il record di log da visualizzare (ignorato se non {@link #isLoggable(LogRecord) loggable})
     */
    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) return;

        String msg = getFormatter().format(record);
        String cssClass = styleMap.getOrDefault(record.getLevel(), "info");

        Text txt = new Text(msg);
        txt.getStyleClass().add(cssClass);

        // Aggiunta sicura al thread JavaFX e autoscroll in fondo
        Platform.runLater(() -> {
            flow.getChildren().add(txt);
            scrollPane.setVvalue(1.0);
        });
    }

    /**
     * Svuota eventuali buffer dell'handler.
     * <p>
     * Questa implementazione non mantiene buffer, quindi il metodo è un no-op.
     * </p>
     */
    @Override
    public void flush() {
        // no-op
    }

    /**
     * Rilascia le risorse dell'handler.
     * <p>
     * Questa implementazione non alloca risorse esterne; metodo no-op.
     * </p>
     *
     * @throws SecurityException se il {@link SecurityException SecurityManager}
     *                           vieta la chiusura
     */
    @Override
    public void close() throws SecurityException {
        // no-op
    }
}
