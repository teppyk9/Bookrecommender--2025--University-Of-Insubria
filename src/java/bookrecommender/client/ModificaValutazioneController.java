package bookrecommender.client;

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

public class ModificaValutazioneController {
    @FXML
    private Button SalvaModificheButton;
    @FXML
    private ImageView starStile1;
    @FXML
    private ImageView starStile2;
    @FXML
    private ImageView starStile3;
    @FXML
    private ImageView starStile4;
    @FXML
    private ImageView starStile5;
    @FXML
    private Label votoStile;
    @FXML
    private TextField testoStile;
    @FXML
    private ImageView starContenuto1;
    @FXML
    private ImageView starContenuto2;
    @FXML
    private ImageView starContenuto3;
    @FXML
    private ImageView starContenuto4;
    @FXML
    private ImageView starContenuto5;
    @FXML
    private Label votoContenuto;
    @FXML
    private TextField testoContenuto;
    @FXML
    private ImageView starGradevolezza1;
    @FXML
    private ImageView starGradevolezza2;
    @FXML
    private ImageView starGradevolezza3;
    @FXML
    private ImageView starGradevolezza4;
    @FXML
    private ImageView starGradevolezza5;
    @FXML
    private Label votoGradevolezza;
    @FXML
    private TextField testoGradevolezza;
    @FXML
    private ImageView starOriginalita1;
    @FXML
    private ImageView starOriginalita2;
    @FXML
    private ImageView starOriginalita3;
    @FXML
    private ImageView starOriginalita4;
    @FXML
    private ImageView starOriginalita5;
    @FXML
    private Label votoOriginalita;
    @FXML
    private TextField testoOriginalita;
    @FXML
    private ImageView starEdizione1;
    @FXML
    private ImageView starEdizione2;
    @FXML
    private ImageView starEdizione3;
    @FXML
    private ImageView starEdizione4;
    @FXML
    private ImageView starEdizione5;
    @FXML
    private Label votoEdizione;
    @FXML
    private TextField testoEdizione;
    @FXML
    private TextField testoFinale;
    @FXML
    private Label TitoloLabel;

    private Valutazione myValutazione;

    private boolean isModifica = false;

    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) SalvaModificheButton.getScene().getWindow();
            stage.setOnCloseRequest(evt -> {
                ExitApplication();
                evt.consume();
            });
        });
    }

    public void setValutazione(Valutazione val) {
        this.myValutazione = val;

        configuraValutazione(List.of(starStile1, starStile2, starStile3, starStile4, starStile5), votoStile);
        configuraValutazione(List.of(starContenuto1, starContenuto2, starContenuto3, starContenuto4, starContenuto5), votoContenuto);
        configuraValutazione(List.of(starGradevolezza1, starGradevolezza2, starGradevolezza3, starGradevolezza4, starGradevolezza5), votoGradevolezza);
        configuraValutazione(List.of(starOriginalita1, starOriginalita2, starOriginalita3, starOriginalita4, starOriginalita5), votoOriginalita);
        configuraValutazione(List.of(starEdizione1, starEdizione2, starEdizione3, starEdizione4, starEdizione5), votoEdizione);

        TitoloLabel.setText(myValutazione.getLibro().getTitolo());

        SalvaModificheButton.setDisable(true);

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

        if (commenti.get(0).isEmpty()) {
            testoStile.setPromptText("Nessuna recensione per lo stile");
        } else {
            testoStile.setText(commenti.get(0));
        }
        if (commenti.get(1).isEmpty()) {
            testoContenuto.setPromptText("Nessuna recensione per il contenuto");
        } else {
            testoContenuto.setText(commenti.get(1));
        }
        if (commenti.get(2).isEmpty()) {
            testoGradevolezza.setPromptText("Nessuna recensione per la gradevolezza");
        } else {
            testoGradevolezza.setText(commenti.get(2));
        }
        if (commenti.get(3).isEmpty()) {
            testoOriginalita.setPromptText("Nessuna recensione per l'originalità");
        } else {
            testoOriginalita.setText(commenti.get(3));
        }
        if (commenti.get(4).isEmpty()) {
            testoEdizione.setPromptText("Nessuna recensione per l'edizione");
        } else {
            testoEdizione.setText(commenti.get(4));
        }
        if (commenti.get(5).isEmpty()) {
            testoFinale.setPromptText("Nessuna recensione per la valutazione finale");
        } else {
            testoFinale.setText(commenti.get(5));
        }

        testoFinale.textProperty().addListener((obs, oldText, newText) -> setMod());

        testoEdizione.textProperty().addListener((obs, oldText, newText) -> setMod());

        testoOriginalita.textProperty().addListener((obs, oldText, newText) -> setMod());

        testoGradevolezza.textProperty().addListener((obs, oldText, newText) -> setMod());

        testoContenuto.textProperty().addListener((obs, oldText, newText) -> setMod());

        testoStile.textProperty().addListener((obs, oldText, newText) -> setMod());

    }

    private void aggiornaStelle(Float numeroPiene, List<ImageView> stelle) {
        for (int i = 0; i < stelle.size(); i++) {
            stelle.get(i).setImage(i < numeroPiene ? CliUtil.getInstance().getStarFull() : CliUtil.getInstance().getStarEmpty());
        }
    }

    private void configuraValutazione(List<ImageView> stelle, Label labelVoto) {
        final float[] valore = {0};
        for (int i = 0; i < stelle.size(); i++) {
            final int index = i;
            ImageView stella = stelle.get(i);
            stella.setImage(CliUtil.getInstance().getStarEmpty());

            stella.setOnMouseEntered(e -> aggiornaStelle((float) (index + 1), stelle));
            stella.setOnMouseExited(e -> aggiornaStelle(Float.parseFloat(labelVoto.getText()), stelle));
            stella.setOnMouseClicked(e -> {
                valore[0] = index + 1;
                labelVoto.setText(String.valueOf(valore[0]));
                aggiornaStelle(valore[0], stelle);
                setMod();
            });
        }
    }

    public void salvaModifiche() {
        float stile, contenuto, gradevolezza, originalita, edizione;
        try {
            stile = Float.parseFloat(votoStile.getText());
            contenuto = Float.parseFloat(votoContenuto.getText());
            gradevolezza = Float.parseFloat(votoGradevolezza.getText());
            originalita = Float.parseFloat(votoOriginalita.getText());
            edizione = Float.parseFloat(votoEdizione.getText());
            if (stile == 0 || contenuto == 0 || gradevolezza == 0 || originalita == 0 || edizione == 0) {
                return;
            }
        } catch (NumberFormatException e) {
            return;
        }
        Valutazione valutazione = new Valutazione(
                "null", List.of(stile, contenuto, gradevolezza, originalita, edizione),
                List.of(testoStile.getText(), testoContenuto.getText(), testoGradevolezza.getText(),
                        testoOriginalita.getText(), testoEdizione.getText(), testoFinale.getText()), myValutazione.getLibro()
        );
        try {
            if (CliUtil.getInstance().getLibService().updateVal(CliUtil.getInstance().getCurrentToken(), valutazione)) {
                CliUtil.getInstance().createConfirmation("Valutazione salvata", "La valutazione è stata salvata con successo.", false).showAndWait();
                Stage stage = (Stage) SalvaModificheButton.getScene().getWindow();
                stage.close();
            } else {
                CliUtil.getInstance().createAlert("Errore", "Salvataggio fallito\n" + "Non è stato possibile salvare la valutazione. Riprova più tardi.").showAndWait();
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore di rete", "Si è verificato un errore di rete durante il salvataggio della valutazione. Riprova più tardi.").showAndWait();
        }
    }

    @FXML private void eliminaValutazione() {
        if (CliUtil.getInstance().createConfirmation("Conferma eliminazione", "Sei sicuro di voler eliminare questa valutazione? Non potrà essere recuperata.", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
            try {
                if (CliUtil.getInstance().getLibService().deleteVal(CliUtil.getInstance().getCurrentToken(), myValutazione.getLibro())) {
                    CliUtil.getInstance().createConfirmation("Valutazione eliminata", "La valutazione è stata eliminata con successo.", false).showAndWait();
                    Stage stage = (Stage) SalvaModificheButton.getScene().getWindow();
                    stage.close();
                } else {
                    CliUtil.getInstance().createAlert("Errore", "Eliminazione fallita\n" + "Non è stato possibile eliminare la valutazione. Riprova più tardi.").showAndWait();
                }
            } catch (RemoteException e) {
                CliUtil.getInstance().createAlert("Errore di rete", "Si è verificato un errore di rete durante l'eliminazione della valutazione. Riprova più tardi.").showAndWait();
            }
        }
    }

    @FXML private void ExitApplication() {
        if(isModifica && CliUtil.getInstance().createConfirmation("Conferma uscita", "Hai modificato la valutazione. Vuoi salvare le modifiche prima di uscire?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES){
            salvaModifiche();
        }else {
            Stage stage = (Stage) SalvaModificheButton.getScene().getWindow();
            stage.close();
        }
    }

    private void setMod() {
        isModifica = true;
        SalvaModificheButton.setDisable(false);
    }

}
