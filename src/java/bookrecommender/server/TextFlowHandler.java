package bookrecommender.server;

import java.util.*;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TextFlowHandler extends Handler {
    private final TextFlow flow;
    private final ScrollPane scrollPane;
    private final Map<Level,String> styleMap = Map.of(
            Level.INFO,    "info",
            Level.WARNING, "warning",
            Level.SEVERE,  "severe"
    );

    public TextFlowHandler(TextFlow flow, ScrollPane scrollPane) {
        this.flow = flow;
        this.scrollPane = scrollPane;
        setLevel(Level.ALL);
        setFormatter(new SimpleFormatter());
    }

    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) return;
        String msg = getFormatter().format(record);
        String cssClass = styleMap.getOrDefault(record.getLevel(), "info");
        Text txt = new Text(msg);
        txt.getStyleClass().add(cssClass);

        Platform.runLater(() -> {
            flow.getChildren().add(txt);
            scrollPane.setVvalue(1.0);
        });
    }

    @Override public void flush() {}
    @Override public void close() throws SecurityException {}
}