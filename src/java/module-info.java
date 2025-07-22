module bookrecommender {
    requires java.rmi;
    requires java.sql;
    requires javafx.fxml;
    requires org.postgresql.jdbc;
    requires java.desktop;
    requires com.zaxxer.hikari;
    requires javafx.graphics;
    requires javafx.controls;

    opens bookrecommender.client to javafx.fxml;
    opens bookrecommender.client.ui to javafx.fxml;
    opens bookrecommender.client.util to javafx.fxml;
    opens bookrecommender.client.enums to javafx.fxml;
    opens bookrecommender.client.listener to javafx.fxml;
    opens bookrecommender.server to javafx.fxml;
    opens bookrecommender.server.ui to javafx.fxml;
    opens bookrecommender.server.dao to javafx.fxml;
    opens bookrecommender.server.service to javafx.fxml;
    opens bookrecommender.server.util to javafx.fxml;
    exports bookrecommender.client;
    exports bookrecommender.client.ui;
    exports bookrecommender.client.util;
    exports bookrecommender.client.enums;
    exports bookrecommender.client.listener;
    exports bookrecommender.server;
    exports bookrecommender.common.interfaces;
    exports bookrecommender.common.model;
    exports bookrecommender.server.ui;
    exports bookrecommender.server.dao;
    exports bookrecommender.server.service;
    exports bookrecommender.server.util;
}