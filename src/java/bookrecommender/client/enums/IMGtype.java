package bookrecommender.client.enums;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public enum IMGtype {

    CONFIRM("/bookrecommender/client/icons/alert_confirmation_icon.png"),
    RED_CROSS("/bookrecommender/client/icons/alert_icon.png"),
    INFO("/bookrecommender/client/icons/alert_info_icon.png"),
    ARROW_DOWN("/bookrecommender/client/icons/Expand_arrowDown_fcf7f8_OK.png"),
    CHECK("/bookrecommender/client/icons/check-green.png"),
    RIMUOVI("/bookrecommender/client/icons/minus-circle-red.png"),
    ICONA_PROG("/bookrecommender/client/icons/program_icon.png"),
    SERVER("/bookrecommender/client/icons/server_connection.png"),
    STAR_0_4_RED("/bookrecommender/client/icons/star-red-0-4.png"),
    STAR_1_4_RED("/bookrecommender/client/icons/star-red-1-4.png"),
    STAR_2_4_RED("/bookrecommender/client/icons/star-red-2-4.png"),
    STAR_3_4_RED("/bookrecommender/client/icons/star-red-3-4.png"),
    STAR_4_4_RED("/bookrecommender/client/icons/star-red-4-4.png"),
    STAR_0_4_WHITE("/bookrecommender/client/icons/star-white-0-4.png"),
    STAR_1_4_WHITE("/bookrecommender/client/icons/star-white-1-4.png"),
    STAR_2_4_WHITE("/bookrecommender/client/icons/star-white-2-4.png"),
    STAR_3_4_WHITE("/bookrecommender/client/icons/star-white-3-4.png"),
    STAR_4_4_WHITE("/bookrecommender/client/icons/star-white-4-4.png"),
    VISIBLE_PASSWORD("/bookrecommender/client/icons/Visible_FCF7F8_OKK.png"),
    INVISIBLE_PASSWORD("/bookrecommender/client/icons/Invisible_FCF7F8_OK.png"),
    INDIETRO("/bookrecommender/client/icons/GoBack_a31621_100OK.png"),
    CERCA("/bookrecommender/client/icons/Search_a31621_OK.png"),
    IMPOSTAZIONI("/bookrecommender/client/icons/impostazioni.png"),
    LOGOUT("/bookrecommender/client/icons/Logout_a31621_OK.png"),
    EDIT("/bookrecommender/client/icons/Edit_fcf7f8_OK.png"),
    SAVE("/bookrecommender/client/icons/Save_fcf7f8_OK.png"),;

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

    public enum STARtype{
        RED(STAR_0_4_RED, STAR_1_4_RED, STAR_2_4_RED, STAR_3_4_RED, STAR_4_4_RED),
        WHITE(STAR_0_4_WHITE, STAR_1_4_WHITE, STAR_2_4_WHITE, STAR_3_4_WHITE, STAR_4_4_WHITE);
        private final IMGtype[] stars;

        STARtype(IMGtype... stars) {
            this.stars = stars;
        }

        public IMGtype getStar(int index) {
            if (index < 0 || index >= stars.length) {
                return stars[0];
            }
            return stars[index];
        }
    }
}
