package bookrecommender.client;

import bookrecommender.common.Valutazione;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

public class ModificaValutazioneController {
    public Button SalvaModificheButton;
    @FXML private ImageView starStile1;
    @FXML private ImageView starStile2;
    @FXML private ImageView starStile3;
    @FXML private ImageView starStile4;
    @FXML private ImageView starStile5;
    @FXML private Label votoStile;
    @FXML private TextField testoStile;
    @FXML private ImageView starContenuto1;
    @FXML private ImageView starContenuto2;
    @FXML private ImageView starContenuto3;
    @FXML private ImageView starContenuto4;
    @FXML private ImageView starContenuto5;
    @FXML private Label votoContenuto;
    @FXML private TextField testoContenuto;
    @FXML private ImageView starGradevolezza1;
    @FXML private ImageView starGradevolezza2;
    @FXML private ImageView starGradevolezza3;
    @FXML private ImageView starGradevolezza4;
    @FXML private ImageView starGradevolezza5;
    @FXML private Label votoGradevolezza;
    @FXML private TextField testoGradevolezza;
    @FXML private ImageView starOriginalita1;
    @FXML private ImageView starOriginalita2;
    @FXML private ImageView starOriginalita3;
    @FXML private ImageView starOriginalita4;
    @FXML private ImageView starOriginalita5;
    @FXML private Label votoOriginalita;
    @FXML private TextField testoOriginalita;
    @FXML private ImageView starEdizione1;
    @FXML private ImageView starEdizione2;
    @FXML private ImageView starEdizione3;
    @FXML private ImageView starEdizione4;
    @FXML private ImageView starEdizione5;
    @FXML private Label votoEdizione;
    @FXML private TextField testoEdizione;
    @FXML private TextField testoFinale;
    @FXML private Label TitoloLabel;

    private Valutazione myValutazione;

    public void initialize() {
        configuraValutazione(List.of(starStile1, starStile2, starStile3, starStile4, starStile5), votoStile);
        configuraValutazione(List.of(starContenuto1, starContenuto2, starContenuto3, starContenuto4, starContenuto5), votoContenuto);
        configuraValutazione(List.of(starGradevolezza1, starGradevolezza2, starGradevolezza3, starGradevolezza4, starGradevolezza5), votoGradevolezza);
        configuraValutazione(List.of(starOriginalita1, starOriginalita2, starOriginalita3, starOriginalita4, starOriginalita5), votoOriginalita);
        configuraValutazione(List.of(starEdizione1, starEdizione2, starEdizione3, starEdizione4, starEdizione5), votoEdizione);
    }

    public void setValutazione(Valutazione val) {
        this.myValutazione = val;
        TitoloLabel.setText(myValutazione.getLibro().getTitolo());

        List<Float> valutazioni = myValutazione.getValutazioni();
        List<String> commenti = myValutazione.getCommenti();

        CliUtil.getInstance().setStar(starStile1, starStile2, starStile3, starStile4, starStile5, valutazioni.get(0));
        CliUtil.getInstance().setStar(starContenuto1, starContenuto2, starContenuto3, starContenuto4, starContenuto5, valutazioni.get(1));
        CliUtil.getInstance().setStar(starGradevolezza1, starGradevolezza2, starGradevolezza3, starGradevolezza4, starGradevolezza5, valutazioni.get(2));
        CliUtil.getInstance().setStar(starOriginalita1, starOriginalita2, starOriginalita3, starOriginalita4, starOriginalita5, valutazioni.get(3));
        CliUtil.getInstance().setStar(starEdizione1, starEdizione2, starEdizione3, starEdizione4, starEdizione5, valutazioni.get(4));

        votoStile.setText(String.valueOf(valutazioni.get(0)));
        votoContenuto.setText(String.valueOf(valutazioni.get(1)));
        votoGradevolezza.setText(String.valueOf(valutazioni.get(2)));
        votoOriginalita.setText(String.valueOf(valutazioni.get(3)));
        votoEdizione.setText(String.valueOf(valutazioni.get(4)));

        if(commenti.get(0).isEmpty()) {
            testoStile.setPromptText("Nessuna recensione per lo stile");
        } else {
            testoStile.setText(commenti.get(0));
        }
        if(commenti.get(1).isEmpty()) {
            testoContenuto.setPromptText("Nessuna recensione per il contenuto");
        } else {
            testoContenuto.setText(commenti.get(1));
        }
        if(commenti.get(2).isEmpty()) {
            testoGradevolezza.setPromptText("Nessuna recensione per la gradevolezza");
        } else {
            testoGradevolezza.setText(commenti.get(2));
        }
        if(commenti.get(3).isEmpty()) {
            testoOriginalita.setPromptText("Nessuna recensione per l'originalità");
        } else {
            testoOriginalita.setText(commenti.get(3));
        }
        if(commenti.get(4).isEmpty()) {
            testoEdizione.setPromptText("Nessuna recensione per l'edizione");
        } else {
            testoEdizione.setText(commenti.get(4));
        }
        if(commenti.get(5).isEmpty()) {
            testoFinale.setPromptText("Nessuna recensione per la valutazione finale");
        } else {
            testoFinale.setText(commenti.get(5));
        }
    }

    private void aggiornaStelle(int numeroPiene, List<ImageView> stelle) {
        for (int i = 0; i < stelle.size(); i++) {
            stelle.get(i).setImage(i < numeroPiene
                    ? CliUtil.getInstance().getStarFull()
                    : CliUtil.getInstance().getStarEmpty());
        }
    }

    private void configuraValutazione(List<ImageView> stelle, Label labelVoto) {
        final int[] valore = {0};
        for (int i = 0; i < stelle.size(); i++) {
            final int index = i;
            ImageView stella = stelle.get(i);
            stella.setImage(CliUtil.getInstance().getStarEmpty());

            stella.setOnMouseEntered(e -> aggiornaStelle(index + 1, stelle));
            stella.setOnMouseExited(e -> aggiornaStelle(valore[0], stelle));
            stella.setOnMouseClicked(e -> {
                valore[0] = index + 1;
                labelVoto.setText(String.valueOf(valore[0]));
                aggiornaStelle(valore[0], stelle);
            });
        }
    }

    public void salvaModifiche() {
        try {
            if (Integer.parseInt(votoStile.getText()) == 0 || Integer.parseInt(votoContenuto.getText()) == 0 || Integer.parseInt(votoGradevolezza.getText()) == 0 ||
                    Integer.parseInt(votoOriginalita.getText()) == 0 || Integer.parseInt(votoEdizione.getText()) == 0) {
                return;
            }
        }catch (NumberFormatException e) {
            return;
        }
        Valutazione valutazione = new Valutazione(
                "null",
                List.of(Float.parseFloat(votoStile.getText()), Float.parseFloat(votoContenuto.getText()), Float.parseFloat(votoGradevolezza.getText()),
                        Float.parseFloat(votoOriginalita.getText()), Float.parseFloat(votoEdizione.getText())),
                List.of(testoStile.getText(), testoContenuto.getText(), testoGradevolezza.getText(),
                        testoOriginalita.getText(), testoEdizione.getText(), testoFinale.getText()),myValutazione.getLibro()
        );
        try{
            if (CliUtil.getInstance().getLibService().updateVal(CliUtil.getInstance().getCurrentToken(), valutazione)) {
                CliUtil.getInstance().createConfirmation("Valutazione salvata", "La valutazione è stata salvata con successo.",false).showAndWait();
                Stage stage = (Stage) SalvaModificheButton.getScene().getWindow();
                stage.close();
            }else{
                CliUtil.getInstance().createAlert("Errore", "Salvataggio fallito\n" + "Non è stato possibile salvare la valutazione. Riprova più tardi.").showAndWait();
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore di rete", "Si è verificato un errore di rete durante il salvataggio della valutazione. Riprova più tardi.").showAndWait();
        }
    }

    public void eliminaValutazione() {

    }
}
