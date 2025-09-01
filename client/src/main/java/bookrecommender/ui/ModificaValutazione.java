package bookrecommender.ui;

import bookrecommender.enums.FXMLtype;
import bookrecommender.enums.IMGtype;
import bookrecommender.util.CliUtil;
import bookrecommender.model.Valutazione;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.List;
/**
 * Controller JavaFX per la schermata di modifica di una {@link Valutazione}.
 * <p>
 * Gestisce 5 aspetti valutabili tramite stelle (Stile, Contenuto, Gradevolezza,
 * Originalità, Edizione) e relativi commenti testuali, oltre a un testo finale
 * riepilogativo. Usa immagini stelle di {@link IMGtype} e componenti JavaFX
 * annotati con {@code @FXML}.
 * </p>
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
public class ModificaValutazione {
    /** Pulsante “indietro”: ritorna alla schermata precedente con gestione modifiche non salvate. Non nullo dopo il caricamento FXML. */
    @FXML private Button GoBackButton;
    /** Pulsante di conferma che salva la valutazione corrente. Non nullo dopo il caricamento FXML. */
    @FXML private Button SalvaModificheButton;
    /** Stelle interattive per il voto di Stile (1–5). Non nulle dopo il caricamento FXML. */
    @FXML private ImageView starStile1,starStile2,starStile3,starStile4,starStile5;
    /** Etichetta che mostra il valore numerico del voto di Stile. Non nulla dopo il caricamento FXML. */
    @FXML private Label votoStile;
    /** Commento testuale relativo allo Stile. Non nullo dopo il caricamento FXML. */
    @FXML private TextArea testoStile;
    /** Stelle interattive per il voto di Contenuto (1–5). Non nulle dopo il caricamento FXML. */
    @FXML private ImageView starContenuto1,starContenuto2,starContenuto3,starContenuto4,starContenuto5;
    /** Etichetta che mostra il valore numerico del voto di Contenuto. Non nulla dopo il caricamento FXML. */
    @FXML private Label votoContenuto;
    /** Commento testuale relativo al Contenuto. Non nullo dopo il caricamento FXML. */
    @FXML private TextArea testoContenuto;
    /** Stelle interattive per il voto di Gradevolezza (1–5). Non nulle dopo il caricamento FXML. */
    @FXML private ImageView starGradevolezza1,starGradevolezza2,starGradevolezza3,starGradevolezza4,starGradevolezza5;
    /** Etichetta che mostra il valore numerico del voto di Gradevolezza. Non nulla dopo il caricamento FXML. */
    @FXML private Label votoGradevolezza;
    /** Commento testuale relativo alla Gradevolezza. Non nullo dopo il caricamento FXML. */
    @FXML private TextArea testoGradevolezza;
    /** Stelle interattive per il voto di Originalità (1–5). Non nulle dopo il caricamento FXML. */
    @FXML private ImageView starOriginalita1,starOriginalita2,starOriginalita3,starOriginalita4,starOriginalita5;
    /** Etichetta che mostra il valore numerico del voto di Originalità. Non nulla dopo il caricamento FXML. */
    @FXML private Label votoOriginalita;
    /** Commento testuale relativo all’Originalità. Non nullo dopo il caricamento FXML. */
    @FXML private TextArea testoOriginalita;
    /** Stelle interattive per il voto di Edizione (1–5). Non nulle dopo il caricamento FXML. */
    @FXML private ImageView starEdizione1,starEdizione2,starEdizione3,starEdizione4,starEdizione5;
    /** Etichetta che mostra il valore numerico del voto di Edizione. Non nulla dopo il caricamento FXML. */
    @FXML private Label votoEdizione;
    /** Commento testuale per l’Edizione e campo di testo finale riepilogativo. Non nulli dopo il caricamento FXML. */
    @FXML private TextArea testoEdizione, testoFinale;
    /** Etichetta del titolo (es. titolo del libro o intestazione della schermata). Non nulla dopo il caricamento FXML. */
    @FXML private Label TitoloLabel;

    /** Oggetto usato per caricare e salvare la valutazione. */
    private Valutazione myVal;
    /** Tipo di schermata di provenienza, usato per la navigazione di ritorno. */
    private FXMLtype oldFXMLType;

    private boolean isMod = false;

    /**
     * Inizializza la UI e il comportamento interattivo delle stelle.
     * <p>
     * Imposta icone/grafica, allineamenti, listener di hover/click sulle stelle
     * e stato iniziale dei controlli. Eseguito automaticamente dal loader FXML
     * sul JavaFX Application Thread.
     * </p>
     */
    @FXML private void initialize() {
        GoBackButton.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        GoBackButton.setAlignment(Pos.TOP_LEFT);
        testoStile.setEditable(true);
        testoContenuto.setEditable(true);
        testoGradevolezza.setEditable(true);
        testoEdizione.setEditable(true);
        testoOriginalita.setEditable(true);
        testoFinale.setEditable(true);
        Platform.runLater(() -> {
            Stage s = (Stage) SalvaModificheButton.getScene().getWindow();
            s.setOnCloseRequest(evt -> { ExitApplication(); evt.consume(); });
        });
    }

    /**
     * Inizializza la UI e il comportamento interattivo delle stelle.
     * <p>
     * Imposta icone/grafica, allineamenti, listener di hover/click sulle stelle
     * e stato iniziale dei controlli. Eseguito automaticamente dal loader FXML
     * sul JavaFX Application Thread.
     * </p>
     */
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

    /**
     * Salva le modifiche apportate alla valutazione.
     * <p>
     * Legge i voti (1–5) e i commenti dai controlli, aggiorna {@link #myVal} e
     * invia i dati al livello applicativo/servizio. Mostra esiti di successo o errore.
     * </p>
     */
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
        } catch (Exception ex) {
            CliUtil.getInstance().LogOut(ex);
        }
    }

    /**
     * Elimina definitivamente la valutazione corrente previa conferma dell’utente.
     * <p>Mostra notifica di esito e aggiorna la schermata/navigazione.</p>
     */
    @FXML private void eliminaValutazione() {
        if (CliUtil.getInstance().createConfirmation("Conferma eliminazione", "Sei sicuro di voler eliminare questa valutazione? Non potrà essere recuperata.", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
            try {
                if (CliUtil.getInstance().getLibService().deleteVal(CliUtil.getInstance().getCurrentToken(), myVal.getLibro())) {
                    CliUtil.getInstance().createConfirmation("Valutazione eliminata", "La valutazione è stata eliminata con successo.", false).showAndWait();
                    CliUtil.getInstance().buildStage(oldFXMLType,null,null);
                } else {
                    CliUtil.getInstance().createAlert("Errore", "Eliminazione fallita\nNon è stato possibile eliminare la valutazione. Riprova più tardi.").showAndWait();
                }
            } catch (Exception ex) {
                CliUtil.getInstance().LogOut(ex);
            }
        }
    }

    /**
     * Ritorna alla schermata precedente.
     * <p>
     * Se sono presenti modifiche non salvate, chiede conferma (ed eventualmente
     * offre di salvare) prima di uscire.
     * </p>
     */
    @FXML private void ExitApplication() {
        if (isMod && CliUtil.getInstance().createConfirmation("Conferma uscita", "Hai modificato la valutazione. Vuoi salvare le modifiche prima di uscire?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {salvaModifiche();
        } else {
            CliUtil.getInstance().buildStage(oldFXMLType,null,null);
        }
    }

    /**
     * Segna la schermata come “modificata” abilitando i controlli di salvataggio
     * e attivando la richiesta di conferma in uscita.
     */
    private void setModified() {
        isMod = true;
        SalvaModificheButton.setDisable(false);
    }

    /**
     * Configura il comportamento interattivo di un gruppo di stelle.
     * <p>
     * Gestisce hover e click per aggiornare dinamicamente la grafica delle stelle
     * e il valore numerico mostrato in {@code labelVoto}. Invoca {@code onModify}
     * quando l’utente effettua una modifica.
     * </p>
     *
     * @param stelle    lista delle icone stella (in ordine) da 1 a 5
     * @param labelVoto etichetta che visualizza il voto selezionato
     * @param onModify  azione da eseguire quando la valutazione cambia
     */
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

    /**
     * Aggiorna la grafica delle stelle impostando piene le prime {@code pieno}
     * e vuote le successive.
     *
     * @param stelle elenco delle stelle da aggiornare
     * @param pieno  numero di stelle piene da mostrare (tipicamente 0–5)
     */
    private void updateStars(List<ImageView> stelle, float pieno) {
        for (int i = 0; i < stelle.size(); i++) {
            stelle.get(i).setImage(i < pieno ? IMGtype.STAR_4_4_WHITE.getImage() : IMGtype.STAR_0_4_WHITE.getImage());
        }
    }
}
