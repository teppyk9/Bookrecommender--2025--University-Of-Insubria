package bookrecommender.client.ui;

import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.client.enums.FXMLtype;
import bookrecommender.common.model.Valutazione;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

public class ModificaValutazione {
    @FXML private Button GoBackButton;
    @FXML private Button SalvaModificheButton;
    @FXML private ImageView starStile1,starStile2,starStile3,starStile4,starStile5;
    @FXML private Label votoStile;
    @FXML private TextField testoStile;
    @FXML private ImageView starContenuto1,starContenuto2,starContenuto3,starContenuto4,starContenuto5;
    @FXML private Label votoContenuto;
    @FXML private TextField testoContenuto;
    @FXML private ImageView starGradevolezza1,starGradevolezza2,starGradevolezza3,starGradevolezza4,starGradevolezza5;
    @FXML private Label votoGradevolezza;
    @FXML private TextField testoGradevolezza;
    @FXML private ImageView starOriginalita1,starOriginalita2,starOriginalita3,starOriginalita4,starOriginalita5;
    @FXML private Label votoOriginalita;
    @FXML private TextField testoOriginalita;
    @FXML private ImageView starEdizione1,starEdizione2,starEdizione3,starEdizione4,starEdizione5;
    @FXML private Label votoEdizione;
    @FXML private TextField testoEdizione, testoFinale;
    @FXML private Label TitoloLabel;

    private Valutazione myVal;
    private FXMLtype oldFXMLType;
    private boolean isMod = false;

    @FXML private void initialize() {
        GoBackButton.setGraphic(IMGtype.INDIETRO.getImageView(50,50));
        Platform.runLater(() -> {
            Stage s = (Stage) SalvaModificheButton.getScene().getWindow();
            s.setOnCloseRequest(evt -> { ExitApplication(); evt.consume(); });
        });
    }

    public void setValutazione(Valutazione v, FXMLtype oldFXMLType) {
        this.myVal = v;
        this.oldFXMLType = oldFXMLType;

        configureInteractive(List.of(starStile1,starStile2,starStile3,starStile4,starStile5), votoStile, this::setModified);
        configureInteractive(List.of(starContenuto1,starContenuto2,starContenuto3,starContenuto4,starContenuto5), votoContenuto, this::setModified);
        configureInteractive(List.of(starGradevolezza1,starGradevolezza2,starGradevolezza3,starGradevolezza4,starGradevolezza5), votoGradevolezza, this::setModified);
        configureInteractive(List.of(starOriginalita1,starOriginalita2,starOriginalita3,starOriginalita4,starOriginalita5), votoOriginalita, this::setModified);
        configureInteractive(List.of(starEdizione1,starEdizione2,starEdizione3,starEdizione4,starEdizione5), votoEdizione, this::setModified);

        TitoloLabel.setText(v.getLibro().getTitolo());
        SalvaModificheButton.setDisable(true);

        List<Float> val = v.getValutazioni();
        List<String> com = v.getCommenti();

        CliUtil.getInstance().setStar(starStile1,starStile2,starStile3,starStile4,starStile5, val.get(0), IMGtype.STARtype.WHITE);
        CliUtil.getInstance().setStar(starContenuto1,starContenuto2,starContenuto3,starContenuto4,starContenuto5, val.get(1), IMGtype.STARtype.WHITE);
        CliUtil.getInstance().setStar(starGradevolezza1,starGradevolezza2,starGradevolezza3,starGradevolezza4,starGradevolezza5, val.get(2), IMGtype.STARtype.WHITE);
        CliUtil.getInstance().setStar(starOriginalita1,starOriginalita2,starOriginalita3,starOriginalita4,starOriginalita5, val.get(3), IMGtype.STARtype.WHITE);
        CliUtil.getInstance().setStar(starEdizione1,starEdizione2,starEdizione3,starEdizione4,starEdizione5, val.get(4), IMGtype.STARtype.WHITE);

        votoStile.setText(String.valueOf(val.get(0)));
        votoContenuto.setText(String.valueOf(val.get(1)));
        votoGradevolezza.setText(String.valueOf(val.get(2)));
        votoOriginalita.setText(String.valueOf(val.get(3)));
        votoEdizione.setText(String.valueOf(val.get(4)));

        if (com.get(0).isEmpty())
            testoStile.setPromptText("Nessuna recensione per lo stile");
        else
            testoStile.setText(com.get(0));

        if (com.get(1).isEmpty())
            testoContenuto.setPromptText("Nessuna recensione per il contenuto");
        else
            testoContenuto.setText(com.get(1));

        if (com.get(2).isEmpty())
            testoGradevolezza.setPromptText("Nessuna recensione per la gradevolezza");
        else
            testoGradevolezza.setText(com.get(2));

        if (com.get(3).isEmpty())
            testoOriginalita.setPromptText("Nessuna recensione per l'originalità");
        else
            testoOriginalita.setText(com.get(3));

        if (com.get(4).isEmpty())
            testoEdizione.setPromptText("Nessuna recensione per l'edizione");
        else
            testoEdizione.setText(com.get(4));

        if (com.get(5).isEmpty())
            testoFinale.setPromptText("Nessuna recensione per la valutazione finale");
        else
            testoFinale.setText(com.get(5));

        testoStile.textProperty().addListener((o,oldN,newN)-> setModified());
        testoContenuto.textProperty().addListener((o,oldN,newN)-> setModified());
        testoGradevolezza.textProperty().addListener((o,oldN,newN)-> setModified());
        testoOriginalita.textProperty().addListener((o,oldN,newN)-> setModified());
        testoEdizione.textProperty().addListener((o,oldN,newN)-> setModified());
        testoFinale.textProperty().addListener((o,oldN,newN)-> setModified());
    }

    @FXML private void salvaModifiche() {
        float s,c,g,o,e;
        try {
            s = Float.parseFloat(votoStile.getText());
            c = Float.parseFloat(votoContenuto.getText());
            g = Float.parseFloat(votoGradevolezza.getText());
            o = Float.parseFloat(votoOriginalita.getText());
            e = Float.parseFloat(votoEdizione.getText());
            if (s==0||c==0||g==0||o==0||e==0) return;
        } catch (NumberFormatException ex) {
            return;
        }
        Valutazione nv = new Valutazione("null", List.of(s,c,g,o,e), List.of(testoStile.getText(), testoContenuto.getText(), testoGradevolezza.getText(), testoOriginalita.getText(), testoEdizione.getText(), testoFinale.getText()), myVal.getLibro());
        try {
            if (CliUtil.getInstance().getLibService().updateVal(CliUtil.getInstance().getCurrentToken(), nv)) {
                CliUtil.getInstance().createConfirmation("Valutazione salvata", "La valutazione è stata salvata con successo.", false).showAndWait();
                CliUtil.getInstance().buildStage(oldFXMLType,null,null);
            } else {
                CliUtil.getInstance().createAlert("Errore", "Salvataggio fallito\nNon è stato possibile salvare la valutazione. Riprova più tardi.").showAndWait();
            }
        } catch (RemoteException ex) {
            CliUtil.getInstance().LogOut(ex);
        }
    }

    @FXML private void eliminaValutazione() {
        if (CliUtil.getInstance().createConfirmation("Conferma eliminazione", "Sei sicuro di voler eliminare questa valutazione? Non potrà essere recuperata.", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
            try {
                if (CliUtil.getInstance().getLibService().deleteVal(CliUtil.getInstance().getCurrentToken(), myVal.getLibro())) {
                    CliUtil.getInstance().createConfirmation("Valutazione eliminata", "La valutazione è stata eliminata con successo.", false).showAndWait();
                    CliUtil.getInstance().buildStage(oldFXMLType,null,null);
                } else {
                    CliUtil.getInstance().createAlert("Errore", "Eliminazione fallita\nNon è stato possibile eliminare la valutazione. Riprova più tardi.").showAndWait();
                }
            } catch (RemoteException ex) {
                CliUtil.getInstance().LogOut(ex);
            }
        }
    }

    @FXML private void ExitApplication() {
        if (isMod && CliUtil.getInstance().createConfirmation("Conferma uscita", "Hai modificato la valutazione. Vuoi salvare le modifiche prima di uscire?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {salvaModifiche();
        } else {
            CliUtil.getInstance().buildStage(oldFXMLType,null,null);
        }
    }

    private void setModified() {
        isMod = true;
        SalvaModificheButton.setDisable(false);
    }

    private void configureInteractive(List<ImageView> stelle, Label labelVoto, Runnable onModify) {
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

    private void updateStars(List<ImageView> stelle, float pieno) {
        for (int i = 0; i < stelle.size(); i++) {
            stelle.get(i).setImage(i < pieno ? IMGtype.STAR_4_4_WHITE.getImage() : IMGtype.STAR_0_4_WHITE.getImage());
        }
    }
}
