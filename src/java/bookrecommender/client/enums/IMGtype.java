package bookrecommender.client.enums;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public enum IMGtype {

    CONFIRM("/bookrecommender/client/icons/alert_confirmation_icon.png"),
    RED_CROSS("/bookrecommender/client/icons/alert_icon.png"),
    INFO("/bookrecommender/client/icons/alert_info_icon.png"),
    ARROW_DOWN("/bookrecommender/client/icons/arrow_down_icon.png"),
    CHECK("/bookrecommender/client/icons/check-green.png"),
    RIMUOVI("/bookrecommender/client/icons/minus-circle-red.png"),
    ICONA_PROG("/bookrecommender/client/icons/program_icon.png"),
    SERVER("/bookrecommender/client/icons/server_connection.png"),
    STAR_0_4("/bookrecommender/client/icons/star-empty.png"),
    STAR_1_4("/bookrecommender/client/icons/star-1-4.png"),
    STAR_2_4("/bookrecommender/client/icons/star-half.png"),
    STAR_3_4("/bookrecommender/client/icons/star-3-4.png"),
    STAR_4_4("/bookrecommender/client/icons/star-full.png"),
    VISIBLE_PASSWORD("/bookrecommender/client/icons/visible-password.png"),
    INVISIBLE_PASSWORD("/bookrecommender/client/icons/invisible-password.png");


    private final String path;
    IMGtype(String path){
        this.path = path;
    }

    public ImageView getImageView(double width, double height) {
        ImageView imageView = new ImageView(getImage());
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }
    public Image getImage(){
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }
}
