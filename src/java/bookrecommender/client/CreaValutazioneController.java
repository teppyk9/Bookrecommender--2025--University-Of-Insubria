package bookrecommender.client;

import bookrecommender.common.Libro;
import bookrecommender.common.Valutazione;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

public class CreaValutazioneController {

    @FXML private Label titoloLibro;
    @FXML private Button BottoneSalva;
    @FXML private Label messaggioErrore;
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

    private Libro libro;
    private FXMLtype oldFXMLType;

    public void initialize() {
        messaggioErrore.setText("");
        configuraValutazione(List.of(starStile1, starStile2, starStile3, starStile4, starStile5), votoStile);
        configuraValutazione(List.of(starContenuto1, starContenuto2, starContenuto3, starContenuto4, starContenuto5), votoContenuto);
        configuraValutazione(List.of(starGradevolezza1, starGradevolezza2, starGradevolezza3, starGradevolezza4, starGradevolezza5), votoGradevolezza);
        configuraValutazione(List.of(starOriginalita1, starOriginalita2, starOriginalita3, starOriginalita4, starOriginalita5), votoOriginalita);
        configuraValutazione(List.of(starEdizione1, starEdizione2, starEdizione3, starEdizione4, starEdizione5), votoEdizione);
    }

    public void setLibro(Libro libro, FXMLtype oldFXMLType) {
        this.libro = libro;
        this.oldFXMLType = oldFXMLType;
        titoloLibro.setText(libro.getTitolo());
        Platform.runLater(() -> {
            Stage stage = (Stage) BottoneSalva.getScene().getWindow();
            stage.setOnCloseRequest(evt -> {
                exitApplication();
                evt.consume();
            });
        });
    }

    @FXML private void SalvaValutazione() {
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
                CliUtil.getInstance().buildStage(oldFXMLType,null, null);
            }else{
                messaggioErrore.setText("Errore durante il salvataggio della valutazione, potrebbe essere già stata effettuata una valutazione per questo libro.");
            }
        } catch (RemoteException e) {
            messaggioErrore.setText("Errore durante il controllo della valutazione: " + e.getMessage());
        }
    }

    private void aggiornaStelle(int numeroPiene, List<ImageView> stelle) {
        for (int i = 0; i < stelle.size(); i++) {
            stelle.get(i).setImage(i < numeroPiene ? CliUtil.getInstance().getStarFull() : CliUtil.getInstance().getStarEmpty());
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

    @FXML private void exitApplication() {
        if(CliUtil.getInstance().createConfirmation("Uscita", "Sei sicuro di voler uscire?\nTutte le modifiche andranno perse", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES){
            CliUtil.getInstance().buildStage(oldFXMLType, null, null);
        }
    }
}
