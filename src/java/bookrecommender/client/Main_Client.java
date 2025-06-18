package bookrecommender.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main_Client extends Application {
    @Override
    public void start(Stage primaryStage){
        CliUtil.getInstance().init(primaryStage);
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null);
    }
    public static void main(String[] args) {
        launch(args);
    }
}