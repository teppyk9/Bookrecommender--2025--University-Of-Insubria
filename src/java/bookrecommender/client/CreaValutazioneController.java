package bookrecommender.client;

import bookrecommender.common.Libro;
import bookrecommender.common.Valutazione;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

public class CreaValutazioneController {

    public Button BottoneSalva;
    public Label messaggioErrore;
    public ImageView starStile1;
    public ImageView starStile2;
    public ImageView starStile3;
    public ImageView starStile4;
    public ImageView starStile5;
    public Label votoStile;
    public TextField testoStile;
    public ImageView starContenuto1;
    public ImageView starContenuto2;
    public ImageView starContenuto3;
    public ImageView starContenuto4;
    public ImageView starContenuto5;
    public Label votoContenuto;
    public TextField testoContenuto;
    public ImageView starGradevolezza1;
    public ImageView starGradevolezza2;
    public ImageView starGradevolezza3;
    public ImageView starGradevolezza4;
    public ImageView starGradevolezza5;
    public Label votoGradevolezza;
    public TextField testoGradevolezza;
    public ImageView starOriginalita1;
    public ImageView starOriginalita2;
    public ImageView starOriginalita3;
    public ImageView starOriginalita4;
    public ImageView starOriginalita5;
    public Label votoOriginalita;
    public TextField testoOriginalita;
    public ImageView starEdizione1;
    public ImageView starEdizione2;
    public ImageView starEdizione3;
    public ImageView starEdizione4;
    public ImageView starEdizione5;
    public Label votoEdizione;
    public TextField testoEdizione;
    public TextField testoFinale;

    private Libro libro;

    public void initialize() {
        messaggioErrore.setText("");
        configuraValutazione(List.of(starStile1, starStile2, starStile3, starStile4, starStile5), votoStile);
        configuraValutazione(List.of(starContenuto1, starContenuto2, starContenuto3, starContenuto4, starContenuto5), votoContenuto);
        configuraValutazione(List.of(starGradevolezza1, starGradevolezza2, starGradevolezza3, starGradevolezza4, starGradevolezza5), votoGradevolezza);
        configuraValutazione(List.of(starOriginalita1, starOriginalita2, starOriginalita3, starOriginalita4, starOriginalita5), votoOriginalita);
        configuraValutazione(List.of(starEdizione1, starEdizione2, starEdizione3, starEdizione4, starEdizione5), votoEdizione);
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public void SalvaValutazione() {
        try {
            if (Integer.parseInt(votoStile.getText()) == 0 || Integer.parseInt(votoContenuto.getText()) == 0 || Integer.parseInt(votoGradevolezza.getText()) == 0 ||
                    Integer.parseInt(votoOriginalita.getText()) == 0 || Integer.parseInt(votoEdizione.getText()) == 0) {
                messaggioErrore.setText("Completa tutte le valutazioni prima di salvare.");
                return;
            }
        }catch (NumberFormatException e) {
            messaggioErrore.setText("Completa tutte le valutazioni prima di salvare.");
            return;
        }
        Valutazione valutazione = new Valutazione(
                "null",
                List.of(Float.parseFloat(votoStile.getText()), Float.parseFloat(votoContenuto.getText()), Float.parseFloat(votoGradevolezza.getText()),
                        Float.parseFloat(votoOriginalita.getText()), Float.parseFloat(votoEdizione.getText())),
                List.of(testoStile.getText(), testoContenuto.getText(), testoGradevolezza.getText(),
                        testoOriginalita.getText(), testoEdizione.getText(), testoFinale.getText()),
                libro
        );
        try{
            if (CliUtil.getInstance().getLibService().addValutazione(CliUtil.getInstance().getCurrentToken(), valutazione)) {
                CliUtil.getInstance().createConfirmation("Valutazione salvata", "La valutazione è stata salvata con successo.",false).showAndWait();
                Stage stage = (Stage) BottoneSalva.getScene().getWindow();
                stage.close();
            }else{
                messaggioErrore.setText("Errore durante il salvataggio della valutazione, potrebbe essere già stata effettuata una valutazione per questo libro.");
            }
        } catch (RemoteException e) {
            messaggioErrore.setText("Errore durante il controllo della valutazione: " + e.getMessage());
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
                resetMessaggioErrore();
                valore[0] = index + 1;
                labelVoto.setText(String.valueOf(valore[0]));
                aggiornaStelle(valore[0], stelle);
            });
        }
    }

    @FXML
    private void resetMessaggioErrore() {
        messaggioErrore.setText("");
    }
}
