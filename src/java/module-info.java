module bookrecommender {
    requires java.rmi;
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.postgresql.jdbc;

    opens bookrecommender.client to javafx.fxml;
    exports bookrecommender.client;
    exports bookrecommender.common;
    exports bookrecommender.server;
}