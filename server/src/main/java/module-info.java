module bookrecommender.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.rmi;

    requires bookrecommender.common;
    requires com.zaxxer.hikari;

    opens bookrecommender to javafx.graphics;
    opens bookrecommender.ui to javafx.fxml;
    opens bookrecommender.util to javafx.fxml;

}
