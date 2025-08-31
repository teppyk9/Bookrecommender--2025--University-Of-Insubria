module bookrecommender.client {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.rmi;

    requires bookrecommender.common;
    requires java.logging;

    opens bookrecommender to javafx.fxml, javafx.graphics;
    opens bookrecommender.ui to javafx.fxml, javafx.graphics;
    opens bookrecommender.listener to javafx.fxml, javafx.graphics;
    opens bookrecommender.util to javafx.fxml, javafx.graphics;
    opens bookrecommender.enums to javafx.fxml, javafx.graphics;

    exports bookrecommender;
    exports bookrecommender.ui;
    exports bookrecommender.listener;
    exports bookrecommender.util;
    exports bookrecommender.enums;
}