package bookrecommender.client.util;

import bookrecommender.client.enums.IMGtype;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import java.util.List;

public abstract class ValutazioniEngine {
    protected void updateStars(List<ImageView> stelle, float pieno) {
        for (int i = 0; i < stelle.size(); i++) {
            stelle.get(i).setImage(i < pieno ? IMGtype.STAR_4_4_BLACK.getImage() : IMGtype.STAR_0_4_BLACK.getImage());
        }
    }

    protected void configureInteractive(List<ImageView> stelle, Label labelVoto, Runnable onModify) {
        updateStars(stelle, 0);
        for (int i = 0; i < stelle.size(); i++) {
            int idx = i;
            ImageView st = stelle.get(i);
            st.setOnMouseEntered(e -> updateStars(stelle, idx + 1));
            st.setOnMouseExited (e -> updateStars(stelle, Float.parseFloat(labelVoto.getText())));
            st.setOnMouseClicked(e -> {
                float v = idx + 1;
                labelVoto.setText(String.valueOf(v));
                updateStars(stelle, v);
                onModify.run();
            });
        }
    }

    protected void displayGroup(ImageView[] stars, Label voto, String commento, Label area,float valore, String emptyMsg) {
        CliUtil.getInstance().setStar(stars[0], stars[1], stars[2], stars[3], stars[4], valore);
        voto.setText(String.valueOf(valore));
        area.setText(commento.isEmpty() ? emptyMsg : commento);
    }
}