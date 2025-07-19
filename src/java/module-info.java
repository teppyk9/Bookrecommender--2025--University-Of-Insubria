module bookrecommender {
    requires java.rmi;
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.postgresql.jdbc;
    requires java.desktop;
    requires com.zaxxer.hikari;

    opens bookrecommender.client to javafx.fxml;
    opens bookrecommender.server to javafx.fxml;
    exports bookrecommender.client;
    exports bookrecommender.server;
    exports bookrecommender.common.interfaces;
    exports bookrecommender.common.model;
    exports bookrecommender.server.ui;
    opens bookrecommender.server.ui to javafx.fxml;
    exports bookrecommender.server.dao;
    opens bookrecommender.server.dao to javafx.fxml;
    exports bookrecommender.server.service;
    opens bookrecommender.server.service to javafx.fxml;
    exports bookrecommender.server.util;
    opens bookrecommender.server.util to javafx.fxml;
    exports bookrecommender.client.ui;
    opens bookrecommender.client.ui to javafx.fxml;
    exports bookrecommender.client.util;
    opens bookrecommender.client.util to javafx.fxml;
    exports bookrecommender.client.enums;
    opens bookrecommender.client.enums to javafx.fxml;
    exports bookrecommender.client.listener;
    opens bookrecommender.client.listener to javafx.fxml;
}