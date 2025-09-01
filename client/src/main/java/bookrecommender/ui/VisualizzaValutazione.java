package bookrecommender.ui;

import bookrecommender.enums.IMGtype;
import bookrecommender.util.CliUtil;
import bookrecommender.model.Valutazione;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controller JavaFX per la visualizzazione di una {@link Valutazione}.
 * <p>
 * Mostra le valutazioni (stile, contenuto, gradevolezza, originalità, edizione) tramite icone a stella
 * e i relativi commenti testuali, oltre alla media finale. La schermata è sola-lettura: non consente modifiche.
 * </p>
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
public class VisualizzaValutazione {

    /** Pulsante “indietro” in alto a sinistra. Non nullo dopo il caricamento FXML. */
    @FXML private Button GoBackButton;

    /** Etichetta con il titolo (tipicamente titolo del libro). Non nulla dopo il caricamento FXML. */
    @FXML private Label TitoloLabel;

    /** Etichetta “Recensione di …” con lo username dell’autore. Non nulla dopo il caricamento FXML. */
    @FXML private Label recensioneDiLabel;

    /** Stelle per il voto di Stile (1–5). Non nulle dopo il caricamento FXML. */
    @FXML private ImageView starStile1, starStile2, starStile3, starStile4, starStile5;

    /** Etichetta che visualizza il voto numerico di Stile. Non nulla dopo il caricamento FXML. */
    @FXML private Label votoStile;

    /** Stelle per il voto di Contenuto (1–5). Non nulle dopo il caricamento FXML. */
    @FXML private ImageView starContenuto1, starContenuto2, starContenuto3, starContenuto4, starContenuto5;

    /** Etichetta che visualizza il voto numerico di Contenuto. Non nulla dopo il caricamento FXML. */
    @FXML private Label votoContenuto;

    /** Stelle per il voto di Gradevolezza (1–5). Non nulle dopo il caricamento FXML. */
    @FXML private ImageView starGradevolezza1, starGradevolezza2, starGradevolezza3, starGradevolezza4, starGradevolezza5;

    /** Etichetta che visualizza il voto numerico di Gradevolezza. Non nulla dopo il caricamento FXML. */
    @FXML private Label votoGradevolezza;

    /** Stelle per il voto di Originalità (1–5). Non nulle dopo il caricamento FXML. */
    @FXML private ImageView starOriginalita1, starOriginalita2, starOriginalita3, starOriginalita4, starOriginalita5;

    /** Etichetta che visualizza il voto numerico di Originalità. Non nulla dopo il caricamento FXML. */
    @FXML private Label votoOriginalita;

    /** Stelle per il voto di Edizione (1–5). Non nulle dopo il caricamento FXML. */
    @FXML private ImageView starEdizione1, starEdizione2, starEdizione3, starEdizione4, starEdizione5;

    /** Etichetta che visualizza il voto numerico di Edizione. Non nulla dopo il caricamento FXML. */
    @FXML private Label votoEdizione;

    /** Stelle per la valutazione media finale (1–5). Non nulle dopo il caricamento FXML. */
    @FXML private ImageView starMedia1, starMedia2, starMedia3, starMedia4, starMedia5;

    /** Etichetta che visualizza il voto numerico della media finale. Non nulla dopo il caricamento FXML. */
    @FXML private Label votoMedia;

    /** Aree testo dei commenti: stile, contenuto, gradevolezza, originalità, edizione e commento finale. Non nulle dopo il caricamento FXML. */
    @FXML private TextArea stileTextArea, contenutoTextArea, gradevolezzaTextArea, originalitaTextArea, edizioneTextArea, finaleTextArea;

    /**
     * Inizializza la UI: imposta icona e allineamento del pulsante “indietro”.
     * <p>Eseguito automaticamente dal loader FXML sul JavaFX Application Thread.</p>
     */
    @FXML private void initialize() {
        GoBackButton.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        GoBackButton.setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Popola la schermata con i dati della {@link Valutazione} fornita.
     * <p>
     * Imposta il wrapping dei testi, aggiorna titolo e intestazione “Recensione di …”, e
     * per ciascun aspetto (indici 0..4) più media (indice 5) visualizza:
     * stelle, voto numerico e commento (o un messaggio “nessuna recensione …” se vuoto).
     * </p>
     *
     * @param v valutazione da mostrare (lista voti in {@code v.getValutazioni()} e commenti in {@code v.getCommenti()})
     */
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

        displayGroup(new ImageView[]{starStile1,starStile2,starStile3,starStile4,starStile5}, votoStile, com.get(0), stileTextArea,   val.get(0), "Nessuna recensione per lo stile",           IMGtype.STARtype.WHITE);
        displayGroup(new ImageView[]{starContenuto1,starContenuto2,starContenuto3,starContenuto4,starContenuto5}, votoContenuto, com.get(1), contenutoTextArea, val.get(1), "Nessuna recensione per il contenuto",      IMGtype.STARtype.WHITE);
        displayGroup(new ImageView[]{starGradevolezza1,starGradevolezza2,starGradevolezza3,starGradevolezza4,starGradevolezza5}, votoGradevolezza, com.get(2), gradevolezzaTextArea, val.get(2), "Nessuna recensione per la gradevolezza", IMGtype.STARtype.WHITE);
        displayGroup(new ImageView[]{starOriginalita1,starOriginalita2,starOriginalita3,starOriginalita4,starOriginalita5}, votoOriginalita, com.get(3), originalitaTextArea, val.get(3), "Nessuna recensione per l'originalità",   IMGtype.STARtype.WHITE);
        displayGroup(new ImageView[]{starEdizione1,starEdizione2,starEdizione3,starEdizione4,starEdizione5}, votoEdizione, com.get(4), edizioneTextArea,  val.get(4), "Nessuna recensione per l'edizione",         IMGtype.STARtype.WHITE);
        displayGroup(new ImageView[]{starMedia1,starMedia2,starMedia3,starMedia4,starMedia5}, votoMedia, com.get(5), finaleTextArea,    val.get(5), "Nessuna recensione per la valutazione finale", IMGtype.STARtype.RED);
    }

    /**
     * Chiude la finestra corrente della schermata.
     * <p>Recupera lo {@link Stage} dal nodo del titolo e invoca {@code close()}.</p>
     */
    @FXML private void exitApplication() {
        Stage stage = (Stage) TitoloLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Aggiorna un gruppo composto da 5 stelle, una label del voto e un’area di testo del commento.
     * <p>Imposta le stelle tramite {@code CliUtil.getInstance().setStar(...)} e aggiorna voto e commento
     * (mostrando {@code emptyMsg} se il commento è vuoto).</p>
     *
     * @param stars    array di 5 {@link ImageView} delle stelle in ordine
     * @param voto     etichetta che visualizza il valore numerico selezionato
     * @param commento testo del commento per l’aspetto considerato (può essere vuoto)
     * @param area     area di testo su cui mostrare il commento
     * @param valore   valore della valutazione (0–5)
     * @param emptyMsg messaggio mostrato se {@code commento} è vuoto
     * @param type     tema/colorazione delle stelle (vedi {@link IMGtype.STARtype})
     */
    private void displayGroup(ImageView[] stars, Label voto, String commento, TextArea area, float valore, String emptyMsg, IMGtype.STARtype type) {
        CliUtil.getInstance().setStar(stars[0], stars[1], stars[2], stars[3], stars[4], valore, type);
        voto.setText(String.valueOf(valore));
        area.setText(commento.isEmpty() ? emptyMsg : commento);
    }
}

