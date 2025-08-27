module bookrecommender.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;

    requires bookrecommender.common;
    requires java.logging;

    opens bookrecommender to javafx.graphics;
    opens bookrecommender.ui to javafx.fxml;
    opens bookrecommender.listener to javafx.fxml;
    opens bookrecommender.util to javafx.fxml;
}