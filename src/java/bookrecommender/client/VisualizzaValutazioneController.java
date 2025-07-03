package bookrecommender.client;

import bookrecommender.common.Valutazione;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.util.List;

public class VisualizzaValutazioneController {
    @FXML private Label TitoloLabel;
    @FXML private Label recensioneDiLabel;
    @FXML private ImageView starStile1;
    @FXML private ImageView starStile2;
    @FXML private ImageView starStile3;
    @FXML private ImageView starStile4;
    @FXML private ImageView starStile5;
    @FXML private Label votoStile;
    @FXML private ImageView starContenuto1;
    @FXML private ImageView starContenuto2;
    @FXML private ImageView starContenuto3;
    @FXML private ImageView starContenuto4;
    @FXML private ImageView starContenuto5;
    @FXML private Label votoContenuto;
    @FXML private ImageView starGradevolezza1;
    @FXML private ImageView starGradevolezza2;
    @FXML private ImageView starGradevolezza3;
    @FXML private ImageView starGradevolezza4;
    @FXML private ImageView starGradevolezza5;
    @FXML private Label votoGradevolezza;
    @FXML private ImageView starOriginalita1;
    @FXML private ImageView starOriginalita2;
    @FXML private ImageView starOriginalita3;
    @FXML private ImageView starOriginalita4;
    @FXML private ImageView starOriginalita5;
    @FXML private Label votoOriginalita;
    @FXML private ImageView starEdizione1;
    @FXML private ImageView starEdizione2;
    @FXML private ImageView starEdizione3;
    @FXML private ImageView starEdizione4;
    @FXML private ImageView starEdizione5;
    @FXML private Label votoEdizione;
    @FXML private ImageView starMedia1;
    @FXML private ImageView starMedia2;
    @FXML private ImageView starMedia3;
    @FXML private ImageView starMedia4;
    @FXML private ImageView starMedia5;
    @FXML private Label votoMedia;
    @FXML private Label stileTextArea;
    @FXML private Label contenutoTextArea;
    @FXML private Label gradevolezzaTextArea;
    @FXML private Label originalitaTextArea;
    @FXML private Label edizioneTextArea;
    @FXML private Label finaleTextArea;

    public void setValutazione(Valutazione valutazione) {
        stileTextArea.setWrapText(true);
        contenutoTextArea.setWrapText(true);
        gradevolezzaTextArea.setWrapText(true);
        originalitaTextArea.setWrapText(true);
        edizioneTextArea.setWrapText(true);
        finaleTextArea.setWrapText(true);

        TitoloLabel.setText(valutazione.getLibro().getTitolo());
        recensioneDiLabel.setText("Recensione di " + valutazione.getUsername());

        List<Float> valutazioni = valutazione.getValutazioni();
        List<String> commenti = valutazione.getCommenti();

        CliUtil.getInstance().setStar(starStile1, starStile2, starStile3, starStile4, starStile5, valutazioni.get(0));
        CliUtil.getInstance().setStar(starContenuto1, starContenuto2, starContenuto3, starContenuto4, starContenuto5, valutazioni.get(1));
        CliUtil.getInstance().setStar(starGradevolezza1, starGradevolezza2, starGradevolezza3, starGradevolezza4, starGradevolezza5, valutazioni.get(2));
        CliUtil.getInstance().setStar(starOriginalita1, starOriginalita2, starOriginalita3, starOriginalita4, starOriginalita5, valutazioni.get(3));
        CliUtil.getInstance().setStar(starEdizione1, starEdizione2, starEdizione3, starEdizione4, starEdizione5, valutazioni.get(4));
        CliUtil.getInstance().setStar(starMedia1, starMedia2, starMedia3, starMedia4, starMedia5, valutazioni.get(5));

        votoStile.setText(String.valueOf(valutazioni.get(0)));
        votoContenuto.setText(String.valueOf(valutazioni.get(1)));
        votoGradevolezza.setText(String.valueOf(valutazioni.get(2)));
        votoOriginalita.setText(String.valueOf(valutazioni.get(3)));
        votoEdizione.setText(String.valueOf(valutazioni.get(4)));
        votoMedia.setText(String.valueOf(valutazioni.get(5)));

        if(commenti.get(0).isEmpty()) {
            stileTextArea.setText("Nessuna recensione per lo stile");
        } else {
            stileTextArea.setText(commenti.get(0));
        }
        if(commenti.get(1).isEmpty()) {
            contenutoTextArea.setText("Nessuna recensione per il contenuto");
        } else {
            contenutoTextArea.setText(commenti.get(1));
        }
        if(commenti.get(2).isEmpty()) {
            gradevolezzaTextArea.setText("Nessuna recensione per la gradevolezza");
        } else {
            gradevolezzaTextArea.setText(commenti.get(2));
        }
        if(commenti.get(3).isEmpty()) {
            originalitaTextArea.setText("Nessuna recensione per l'originalità");
        } else {
            originalitaTextArea.setText(commenti.get(3));
        }
        if(commenti.get(4).isEmpty()) {
            edizioneTextArea.setText("Nessuna recensione per l'edizione");
        } else {
            edizioneTextArea.setText(commenti.get(4));
        }
        if(commenti.get(5).isEmpty()) {
            finaleTextArea.setText("Nessuna recensione per la valutazione finale");
        } else {
            finaleTextArea.setText(commenti.get(5));
        }
    }

    public void exitApplication(ActionEvent actionEvent) {

    }
}
