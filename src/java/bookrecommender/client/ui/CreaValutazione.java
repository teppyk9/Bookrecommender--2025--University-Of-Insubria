package bookrecommender.client.ui;

import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.enums.IMGtype;
import bookrecommender.client.util.CliUtil;
import bookrecommender.common.model.Libro;
import bookrecommender.common.model.Valutazione;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.List;

/**
 * Controller JavaFX responsabile dell'interfaccia grafica per la creazione di una {@link Valutazione}
 * su un {@link Libro}. Permette all’utente di esprimere un voto (da 1 a 5) e un commento per diversi aspetti del libro,
 * come stile, contenuto, gradevolezza, originalità ed edizione, e infine di inviare la valutazione al server remoto.
 */
public class CreaValutazione {

    @FXML private Button ExitButton;
    /** Etichetta che visualizza il titolo del libro da valutare*/
    @FXML private Label titoloLibro;

    /** Pulsante che avvia il salvataggio della valutazione*/
    @FXML private Button BottoneSalva;

    /** Etichetta che mostra eventuali messaggi d'errore all'utente*/
    @FXML private Label messaggioErrore;

    /** Stelle cliccabili per la valutazione dello "Stile"*/
    @FXML private ImageView starStile1;
    @FXML private ImageView starStile2;
    @FXML private ImageView starStile3;
    @FXML private ImageView starStile4;
    @FXML private ImageView starStile5;

    /** Etichetta che mostra il voto assegnato allo stile*/
    @FXML private Label votoStile;

    /** Campo testuale per commentare lo stile*/
    @FXML private TextField testoStile;

    /** Stelle cliccabili per la valutazione del "Contenuto"*/
    @FXML private ImageView starContenuto1;
    @FXML private ImageView starContenuto2;
    @FXML private ImageView starContenuto3;
    @FXML private ImageView starContenuto4;
    @FXML private ImageView starContenuto5;

    /** Etichetta che mostra il voto assegnato al contenuto*/
    @FXML private Label votoContenuto;

    /** Campo testuale per commentare il contenuto*/
    @FXML private TextField testoContenuto;

    /** Stelle cliccabili per la valutazione della "Gradevolezza"*/
    @FXML private ImageView starGradevolezza1;
    @FXML private ImageView starGradevolezza2;
    @FXML private ImageView starGradevolezza3;
    @FXML private ImageView starGradevolezza4;
    @FXML private ImageView starGradevolezza5;

    /** Etichetta che mostra il voto assegnato alla gradevolezza*/
    @FXML private Label votoGradevolezza;

    /** Campo testuale per commentare la gradevolezza*/
    @FXML private TextField testoGradevolezza;

    /** Stelle cliccabili per la valutazione dell'"Originalità"*/
    @FXML private ImageView starOriginalita1;
    @FXML private ImageView starOriginalita2;
    @FXML private ImageView starOriginalita3;
    @FXML private ImageView starOriginalita4;
    @FXML private ImageView starOriginalita5;

    /** Etichetta che mostra il voto assegnato all'originalità*/
    @FXML private Label votoOriginalita;

    /** Campo testuale per commentare l'originalità*/
    @FXML private TextField testoOriginalita;

    /** Stelle cliccabili per la valutazione dell'"Edizione"*/
    @FXML private ImageView starEdizione1;
    @FXML private ImageView starEdizione2;
    @FXML private ImageView starEdizione3;
    @FXML private ImageView starEdizione4;
    @FXML private ImageView starEdizione5;

    /**Etichetta che mostra il voto assegnato all'edizione*/
    @FXML private Label votoEdizione;

    /** Campo testuale per commentare l'edizione*/
    @FXML private TextField testoEdizione;

    /** Campo testuale per un commento finale generico alla valutazione*/
    @FXML private TextField testoFinale;

    private Libro libro;
    private FXMLtype oldFXMLType;

    /**
     * Inizializza l’interfaccia grafica impostando le stelle e le etichette di voto
     * per ogni categoria di valutazione. Metodo chiamato automaticamente da JavaFX dopo il caricamento del file FXML.
     */
    @FXML private void initialize() {
        ExitButton.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        messaggioErrore.setText("");
        configuraValutazione(List.of(starStile1, starStile2, starStile3, starStile4, starStile5), votoStile);
        configuraValutazione(List.of(starContenuto1, starContenuto2, starContenuto3, starContenuto4, starContenuto5), votoContenuto);
        configuraValutazione(List.of(starGradevolezza1, starGradevolezza2, starGradevolezza3, starGradevolezza4, starGradevolezza5), votoGradevolezza);
        configuraValutazione(List.of(starOriginalita1, starOriginalita2, starOriginalita3, starOriginalita4, starOriginalita5), votoOriginalita);
        configuraValutazione(List.of(starEdizione1, starEdizione2, starEdizione3, starEdizione4, starEdizione5), votoEdizione);
    }

    /**
     * Imposta il libro da valutare e memorizza il tipo della schermata chiamante.
     * Mostra anche il titolo del libro nella GUI e configura il comportamento di chiusura finestra.
     *
     * @param libro Il libro da valutare.
     * @param oldFXMLType Il tipo di FXML della schermata da cui si è arrivati.
     */
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

    /**
     * Gestisce il salvataggio della valutazione dell'utente per un libro.
     * <p>
     * Il metodo verifica che tutti i voti (da 1 a 5) siano stati assegnati per le 5 categorie
     * di valutazione: stile, contenuto, gradevolezza, originalità ed edizione. Se almeno uno
     * dei voti è mancante (pari a 0 o non numerico), viene mostrato un messaggio d'errore.
     * </p>
     * <p>
     * Se la validazione è superata, viene creato un oggetto {@link Valutazione} contenente:
     * <ul>
     *     <li>Una lista di punteggi numerici per ciascuna categoria</li>
     *     <li>Una lista di commenti testuali per ogni categoria, più un commento finale</li>
     *     <li>Il libro associato alla valutazione</li>
     * </ul>
     * </p>
     * <p>
     * La valutazione viene poi inviata al server tramite il servizio remoto ottenuto da {@code CliUtil}.
     * Se il salvataggio ha successo, l'utente riceve una conferma e viene riportato alla schermata precedente.
     * In caso di fallimento o errore di comunicazione, viene mostrato un messaggio d'errore.
     * </p>
     */
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
        } catch (Exception e) {
            CliUtil.getInstance().LogOut(e);        }
    }

    /**
     * Aggiorna graficamente l’immagine delle stelle in base al numero selezionato.
     *
     * @param numeroPiene Numero di stelle da visualizzare come piene.
     * @param stelle Lista delle stelle da aggiornare.
     */
    private void aggiornaStelle(int numeroPiene, List<ImageView> stelle) {
        for (int i = 0; i < stelle.size(); i++) {
            stelle.get(i).setImage(i < numeroPiene ? IMGtype.STAR_4_4_WHITE.getImage() : IMGtype.STAR_0_4_WHITE.getImage());
        }
    }

    /**
     * Configura l’interazione per un gruppo di stelle: al passaggio del mouse cambiano visivamente,
     * e al click vengono salvate e mostrate come voto selezionato.
     *
     * @param stelle Lista delle immagini delle stelle.
     * @param labelVoto Etichetta associata per mostrare il valore selezionato.
     */
    private void configuraValutazione(List<ImageView> stelle, Label labelVoto) {
        final int[] valore = {0};
        for (int i = 0; i < stelle.size(); i++) {
            final int index = i;
            ImageView stella = stelle.get(i);
            stella.setImage(IMGtype.STAR_0_4_WHITE.getImage());

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

    /**
     * Pulisce eventuali messaggi di errore dalla GUI.
     * Viene invocato al click su una stella o in altre interazioni correttive.
     */
    @FXML
    private void resetMessaggioErrore() {
        messaggioErrore.setText("");
    }

    /**
     * Mostra una finestra di conferma per l'uscita. Se l’utente conferma,
     * torna alla schermata precedente e abbandona le modifiche correnti.
     */
    @FXML private void exitApplication() {
        if(CliUtil.getInstance().createConfirmation("Uscita", "Sei sicuro di voler uscire?\nTutte le modifiche andranno perse", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES){
            CliUtil.getInstance().buildStage(oldFXMLType, null, null);
        }
    }
}
