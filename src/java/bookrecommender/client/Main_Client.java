package bookrecommender.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main_Client extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/bookrecommender/client/CercaLibro.fxml"));
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/program_icon.png")));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Book Recommender");
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();  // Avvia JavaFX
    }
}