package bookrecommender.client.ui;

import bookrecommender.client.util.ValutazioniEngine;
import bookrecommender.common.model.Valutazione;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.List;

public class VisualizzaValutazione extends ValutazioniEngine {
    @FXML private Label TitoloLabel;
    @FXML private Label recensioneDiLabel;
    @FXML private ImageView starStile1, starStile2, starStile3, starStile4, starStile5;
    @FXML private Label votoStile;
    @FXML private ImageView starContenuto1, starContenuto2, starContenuto3, starContenuto4, starContenuto5;
    @FXML private Label votoContenuto;
    @FXML private ImageView starGradevolezza1, starGradevolezza2, starGradevolezza3, starGradevolezza4, starGradevolezza5;
    @FXML private Label votoGradevolezza;
    @FXML private ImageView starOriginalita1, starOriginalita2, starOriginalita3, starOriginalita4, starOriginalita5;
    @FXML private Label votoOriginalita;
    @FXML private ImageView starEdizione1, starEdizione2, starEdizione3, starEdizione4, starEdizione5;
    @FXML private Label votoEdizione;
    @FXML private ImageView starMedia1, starMedia2, starMedia3, starMedia4, starMedia5;
    @FXML private Label votoMedia;
    @FXML private Label stileTextArea, contenutoTextArea, gradevolezzaTextArea, originalitaTextArea, edizioneTextArea, finaleTextArea;

    public void setValutazione(Valutazione v) {
        stileTextArea.setWrapText(true);
        contenutoTextArea.setWrapText(true);
        gradevolezzaTextArea.setWrapText(true);
        originalitaTextArea.setWrapText(true);
        edizioneTextArea.setWrapText(true);
        finaleTextArea.setWrapText(true);

        TitoloLabel.setText(v.getLibro().getTitolo());
        recensioneDiLabel.setText("Recensione di " + v.getUsername());

        List<Float> val = v.getValutazioni();
        List<String> com = v.getCommenti();

        displayGroup(new ImageView[]{starStile1,starStile2,starStile3,starStile4,starStile5}, votoStile, com.get(0), stileTextArea,val.get(0), "Nessuna recensione per lo stile");
        displayGroup(new ImageView[]{starContenuto1,starContenuto2,starContenuto3,starContenuto4,starContenuto5}, votoContenuto, com.get(1), contenutoTextArea,val.get(1), "Nessuna recensione per il contenuto");
        displayGroup(new ImageView[]{starGradevolezza1,starGradevolezza2,starGradevolezza3,starGradevolezza4,starGradevolezza5}, votoGradevolezza, com.get(2), gradevolezzaTextArea,val.get(2), "Nessuna recensione per la gradevolezza");
        displayGroup(new ImageView[]{starOriginalita1,starOriginalita2,starOriginalita3,starOriginalita4,starOriginalita5}, votoOriginalita, com.get(3), originalitaTextArea,val.get(3), "Nessuna recensione per l'originalità");
        displayGroup(new ImageView[]{starEdizione1,starEdizione2,starEdizione3,starEdizione4,starEdizione5}, votoEdizione, com.get(4), edizioneTextArea,val.get(4), "Nessuna recensione per l'edizione");
        displayGroup(new ImageView[]{starMedia1,starMedia2,starMedia3,starMedia4,starMedia5}, votoMedia, com.get(5), finaleTextArea,val.get(5), "Nessuna recensione per la valutazione finale");
    }

    public void exitApplication() {
        Stage stage = (Stage) TitoloLabel.getScene().getWindow();
        stage.close();
    }
}
