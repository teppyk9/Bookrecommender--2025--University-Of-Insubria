package bookrecommender.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main_Client extends Application {
    @Override
    public void start(Stage primaryStage){
        CliUtil.getInstance().init(primaryStage);
        CliUtil.getInstance().loadFXML("/bookrecommender/client/Home.fxml", "Book Recommender");
    }
    public static void main(String[] args) {
        launch(args);
    }
}