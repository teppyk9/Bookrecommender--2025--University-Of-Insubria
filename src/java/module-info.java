module bookrecommender {
    requires java.rmi;
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.postgresql.jdbc;
    requires com.opencsv;
    requires java.desktop;

    opens bookrecommender.client to javafx.fxml;
    opens bookrecommender.server to javafx.fxml;
    exports bookrecommender.client;
    exports bookrecommender.common;
    exports bookrecommender.server;
}