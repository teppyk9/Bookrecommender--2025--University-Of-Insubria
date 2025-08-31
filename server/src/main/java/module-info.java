module bookrecommender.server {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.sql;
    requires java.rmi;

    requires bookrecommender.common;
    requires com.zaxxer.hikari;

    opens bookrecommender to javafx.fxml, javafx.graphics;
    opens bookrecommender.ui to javafx.fxml, javafx.graphics;
    opens bookrecommender.util to javafx.fxml, javafx.graphics;
    opens bookrecommender.dao to javafx.fxml, javafx.graphics;
    opens bookrecommender.service to javafx.fxml, javafx.graphics;

    exports bookrecommender;
    exports bookrecommender.ui;
    exports bookrecommender.util;
    exports bookrecommender.dao;
    exports bookrecommender.service;
}
