package bookrecommender.client;

import bookrecommender.common.Valutazione;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

import java.util.List;

public class VisualizzaValutazioneController {
    public Label TitoloLabel;
    public Label recensioneDiLabel;
    public ImageView starStile1;
    public ImageView starStile2;
    public ImageView starStile3;
    public ImageView starStile4;
    public ImageView starStile5;
    public Label votoStile;
    public ImageView starContenuto1;
    public ImageView starContenuto2;
    public ImageView starContenuto3;
    public ImageView starContenuto4;
    public ImageView starContenuto5;
    public Label votoContenuto;
    public ImageView starGradevolezza1;
    public ImageView starGradevolezza2;
    public ImageView starGradevolezza3;
    public ImageView starGradevolezza4;
    public ImageView starGradevolezza5;
    public Label votoGradevolezza;
    public ImageView starOriginalita1;
    public ImageView starOriginalita2;
    public ImageView starOriginalita3;
    public ImageView starOriginalita4;
    public ImageView starOriginalita5;
    public Label votoOriginalita;
    public ImageView starEdizione1;
    public ImageView starEdizione2;
    public ImageView starEdizione3;
    public ImageView starEdizione4;
    public ImageView starEdizione5;
    public Label votoEdizione;
    public ImageView starMedia1;
    public ImageView starMedia2;
    public ImageView starMedia3;
    public ImageView starMedia4;
    public ImageView starMedia5;
    public Label votoMedia;
    public TextArea stileTextArea;
    public TextArea contenutoTextArea;
    public TextArea gradevolezzaTextArea;
    public TextArea originalitaTextArea;
    public TextArea edizioneTextArea;
    public TextArea finaleTextArea;

    public void setValutazione(Valutazione valutazione) {
        stileTextArea.setWrapText(true);
        contenutoTextArea.setWrapText(true);
        gradevolezzaTextArea.setWrapText(true);
        originalitaTextArea.setWrapText(true);
        edizioneTextArea.setWrapText(true);
        finaleTextArea.setWrapText(true);
        stileTextArea.setEditable(false);
        contenutoTextArea.setEditable(false);
        gradevolezzaTextArea.setEditable(false);
        originalitaTextArea.setEditable(false);
        edizioneTextArea.setEditable(false);
        finaleTextArea.setEditable(false);

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
}
