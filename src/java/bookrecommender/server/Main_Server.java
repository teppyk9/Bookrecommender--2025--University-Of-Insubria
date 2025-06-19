package bookrecommender.server;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main_Server extends Application {
    @Override
    public void start(Stage primaryStage){
        ServerUtil.getInstance().init(primaryStage);
        ServerUtil.getInstance().loadFXML("/bookrecommender/server/fxml/ServerControl.fxml","Server log-control",false);
        ServerUtil.getInstance().loadFXML("/bookrecommender/server/fxml/ServerConfig.fxml","Server Configuration",true);
    }
    public static void main(String[] args) {
        launch(args);
    }
}