package bookrecommender.ui;

import bookrecommender.enums.FXMLtype;
import bookrecommender.enums.IMGtype;
import bookrecommender.util.CliUtil;
import bookrecommender.util.TableViewEngine;
import bookrecommender.model.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller JavaFX per la schermata di modifica delle librerie utente.
 * <p>
 * Consente di cercare libri (tabella S), aggiungerli/rimuoverli dalla libreria (tabella O),
 * rinominare ed eliminare la libreria corrente e salvare le modifiche.
 * Vincoli: nome libreria 5–50 caratteri; la libreria deve contenere almeno 3 libri per essere salvata.
 * </p>
 * <p>
 * Estende {@link TableViewEngine} riutilizzandone la logica di ricerca/tabelle.
 * Usa i servizi remoti tramite {@link CliUtil} ({@code getLibService()}, {@code getSearchService()}).
 * </p>
 */
public class ModificaLibreria extends TableViewEngine {
    /** Pulsante di uscita: apre la schermata Gestione Librerie, con conferma se ci sono modifiche non salvate. Non nullo dopo il caricamento FXML. */
@FXML private Button ExitButton;

/** Pulsante per avviare la ricerca dei libri nella tabella S. Non nullo dopo il caricamento FXML. */
@FXML private Button bottoneCerca;

/** Indicatore di progresso per le operazioni asincrone. Non nullo dopo il caricamento FXML. */
@FXML private ProgressIndicator loadingCircle;

/** Campo di testo per i termini di ricerca (titolo/autore). Non nullo dopo il caricamento FXML. */
@FXML private TextField campoRicerca;

/** Campo di testo per il filtro sull'anno (facoltativo). Non nullo dopo il caricamento FXML. */
@FXML private TextField campoRicercaAnno;

/** Menu per la scelta del tipo di ricerca. Non nullo dopo il caricamento FXML. */
@FXML private MenuButton MenuTipoRicerca;

/** Voce di menu: ricerca per titolo. Non nullo dopo il caricamento FXML. */
@FXML private MenuItem MenuCercaTitolo;

/** Voce di menu: ricerca per autore. Non nullo dopo il caricamento FXML. */
@FXML private MenuItem MenuCercaAutore;

/** Voce di menu: ricerca per autore + anno. Non nullo dopo il caricamento FXML. */
@FXML private MenuItem MenuCercaAutoreAnno;

/** Tabella S con i risultati della ricerca. Non nullo dopo il caricamento FXML. */
@FXML private TableView<Libro> tableView;

/** Colonna Titolo della tabella S. Non nullo dopo il caricamento FXML. */
@FXML private TableColumn<Libro,String> titoloCol;

/** Colonna Autore della tabella S. Non nullo dopo il caricamento FXML. */
@FXML private TableColumn<Libro,String> autoreCol;

/** Colonna Anno della tabella S. Non nullo dopo il caricamento FXML. */
@FXML private TableColumn<Libro,Integer> annoCol;

/** Colonna azioni (aggiungi/rimuovi) nella tabella S. Non nullo dopo il caricamento FXML. */
@FXML private TableColumn<Libro,Void> azioniCol;

/** Tabella O con i libri attualmente presenti nella libreria. Non nullo dopo il caricamento FXML. */
@FXML private TableView<Libro> risTableView;

/** Colonna Titolo della tabella O. Non nullo dopo il caricamento FXML. */
@FXML private TableColumn<Libro,String> risTitoloCol;

/** Colonna Autore della tabella O. Non nullo dopo il caricamento FXML. */
@FXML private TableColumn<Libro,String> risAutoreCol;

/** Colonna Anno della tabella O. Non nullo dopo il caricamento FXML. */
@FXML private TableColumn<Libro,Integer> risAnnoCol;

/** Colonna azioni (rimuovi) nella tabella O. Non nullo dopo il caricamento FXML. */
@FXML private TableColumn<Libro,Void> risAzioniCol;

/** Campo di testo per rinominare la libreria; disabilitato finché non si sceglie “cambia nome”. Non nullo dopo il caricamento FXML. */
@FXML private TextField NomeLibreria;

/** Testo di intestazione con il nome corrente della libreria. Non nullo dopo il caricamento FXML. */
@FXML private Text Titolo_Librerie;

/** Pulsante per abilitare l’editing del nome libreria. Non nullo dopo il caricamento FXML. */
@FXML private Button BottoneCambiaNome;

/** Pulsante per eliminare definitivamente la libreria (con conferma). Non nullo dopo il caricamento FXML. */
@FXML private Button BottoneEliminaLibreria;

    /** Menu per limitare il numero di risultati mostrati in S. Non nullo dopo il caricamento FXML. */
@FXML private MenuButton limiterBox;


    /** Nome corrente della libreria visualizzata/modificata. */
    private String LibName;

    /** Copia iniziale dei libri presenti nella libreria, usata per rilevare modifiche non salvate. */
    private List<Libro> OriginalLibri;


    /**
     * Inizializza la UI: imposta icone/allineamenti, rende non ridimensionabili le colonne,
     * nasconde il campo di rinomina, inizializza tabelle/colonne/azioni.
     * Eseguito automaticamente dal loader FXML sul JavaFX Application Thread.
     */
    @FXML private void initialize() {
        ExitButton.setGraphic(IMGtype.INDIETRO.getImageView(43,43));
        ExitButton.setAlignment(Pos.TOP_LEFT);
        bottoneCerca.setGraphic(IMGtype.CERCA.getImageView(25,25));
        bottoneCerca.setAlignment(Pos.CENTER_LEFT);
        bottoneCerca.setStyle("-fx-padding: 0");
        OriginalLibri = new ArrayList<>();
        NomeLibreria.setDisable(true);
        NomeLibreria.setEditable(false);
        NomeLibreria.setVisible(false);
        titoloCol.setResizable(false);
        annoCol.setResizable(false);
        autoreCol.setResizable(false);
        azioniCol.setResizable(false);
        risTitoloCol.setResizable(false);
        risAnnoCol.setResizable(false);
        risAutoreCol.setResizable(false);
        risAzioniCol.setResizable(false);
        azioniCol.setSortable(false);
        risAzioniCol.setSortable(false);

        List.of(titoloCol, annoCol, autoreCol, azioniCol, risTitoloCol, risAnnoCol, risAutoreCol, risAzioniCol)
                .forEach(col -> col.setStyle("-fx-alignment: CENTER;"));

        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        risTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);


        initLimiter();
        Platform.runLater(() -> {
            Stage stage = (Stage) BottoneCambiaNome.getScene().getWindow();
            stage.setOnCloseRequest(event -> saveFlag());
        });
    }

    /**
     * Imposta il contesto per la libreria indicata: aggiorna il titolo,
     * carica i libri via servizio remoto, popola le tabelle e inizializza colonne/azioni.
     *
     * @param nomeLibreria nome della libreria da caricare nella schermata
     */
    public void setLibreria(String nomeLibreria) {
        LibName = nomeLibreria;
        Titolo_Librerie.setText(LibName);
        try {
            OriginalLibri = new ArrayList<>(CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nomeLibreria));
            risTableView.setItems(FXCollections.observableArrayList(OriginalLibri));
        } catch (Exception e) {
            CliUtil.getInstance().LogOut(e);
        }
        for(Libro l : OriginalLibri){
            try {
                getInLib().put(l, CliUtil.getInstance().getLibService().isLibPresent(CliUtil.getInstance().getCurrentToken(), l));
                getHasRec().put(l, CliUtil.getInstance().getSearchService().hasValRec(l));
                if(getInLib().get(l) && getHasRec().get(l)) {
                    getHasVal().put(l, CliUtil.getInstance().getLibService().existVal(CliUtil.getInstance().getCurrentToken(), l));
                    getHasCon().put(l, CliUtil.getInstance().getLibService().existCon(CliUtil.getInstance().getCurrentToken(), l));
                }else{
                    getHasVal().put(l, false);
                    getHasCon().put(l, false);
                }
            } catch (Exception e) {
                CliUtil.getInstance().LogOut(e);
            }
        }
        initBasicSearch();
        initSAddRemCol();
        initOActionCol();
        initOTableView();
        initTableViews();
    }

    @Override protected TextField getCampoRicerca(){
        return campoRicerca;
    }

    @Override protected TextField getCampoRicercaAnno(){
        return campoRicercaAnno;
    }

    @Override protected boolean getSearchType(){
        return false;
    }

    @Override
    protected Libro getMyLibro() {return null;}

    @Override
    protected FXMLtype getMyFXMLtype() {
        return FXMLtype.MODIFICALIBRERIA;
    }

    @Override
    protected ProgressIndicator getProgressIndicator() {
        return loadingCircle;
    }

    @Override
    protected MenuButton getLimiterBox() {
        return limiterBox;
    }

    @Override protected MenuButton getMenuTipoRicerca(){
        return MenuTipoRicerca;
    }

    @Override protected MenuItem getMenuCercaTitolo() {return MenuCercaTitolo;}

    @Override protected MenuItem getMenuCercaAutore() {return MenuCercaAutore;}

    @Override protected MenuItem getMenuCercaAutoreAnno() {return MenuCercaAutoreAnno;}

    @Override protected TableView<Libro> getSTableView() {return tableView;}

    @Override protected TableColumn<Libro, String> getSTitoloCol() {return titoloCol;}

    @Override protected TableColumn<Libro, String> getSAutoreCol() {return autoreCol;}

    @Override protected TableColumn<Libro, Integer> getSAnnoCol() {return annoCol;}

    @Override protected TableColumn<Libro, Boolean> getSRecensioniCol() {return null;}

    @Override protected TableColumn<Libro, Void> getSAggiungiAdvCol() {return null;}

    @Override protected TableColumn<Libro, Void> getSAddRemCol() {return azioniCol;}

    @Override protected TableView<Libro> getOTableView() {return risTableView;}

    @Override protected TableColumn<Libro, String> getOTitoloCol() {return risTitoloCol;}

    @Override protected TableColumn<Libro, String> getOAutoreCol() {return risAutoreCol;}

    @Override protected TableColumn<Libro, Integer> getOAnnoCol() {return risAnnoCol;}

    @Override protected TableColumn<Libro, Void> getOActionCol() {return risAzioniCol;}

    /**
     * Gestisce l’uscita dalla schermata: se ci sono modifiche non salvate
     * (rinomina in corso o differenze tra liste), chiede conferma prima di uscire.
     */
    @FXML
    private void ExitApplication() {
        saveFlag();
    }

    /**
     * Verifica se ci sono modifiche non salvate (rinomina in corso o differenze tra liste).
     * Se sì, chiede conferma prima di tornare alla schermata di gestione librerie;
     * altrimenti esegue direttamente la navigazione.
     */
    private void saveFlag() {
        if (CliUtil.getInstance().hannoDifferenze(OriginalLibri, new ArrayList<>(risTableView.getItems())) || !NomeLibreria.getText().isEmpty()) {
            CliUtil.getInstance().createConfirmation("Conferma uscita", "Tutte le modifiche andranno perse!\nSei sicuro di voler uscire?", true).showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES)
                    CliUtil.getInstance().buildStage(FXMLtype.GESTIONELIBRERIE, null, null);
            });
        }else
            CliUtil.getInstance().buildStage(FXMLtype.GESTIONELIBRERIE, null, null);
    }

    /** Abilita l’editing del nome della libreria rendendo il campo visibile e modificabile. */
    @FXML
    private void cambiaNome() {
        NomeLibreria.setDisable(false);
        NomeLibreria.setEditable(true);
        NomeLibreria.setVisible(true);
    }

    /**
     * Mostra una conferma ed elimina la libreria corrente tramite servizio remoto.
     * In caso di successo mostra conferma e chiude la finestra; altrimenti mostra un avviso d’errore.
     */
    @FXML
    private void eliminaLibreria() {
        CliUtil.getInstance().createConfirmation("Conferma eliminazione", "Sei sicuro di voler eliminare la libreria '" + LibName + "'?", true).showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    if (CliUtil.getInstance().getLibService().deleteLib(CliUtil.getInstance().getCurrentToken(), LibName)) {
                        CliUtil.getInstance().createConfirmation("Successo", "Libreria eliminata con successo.", false).showAndWait();
                        ((Stage)BottoneEliminaLibreria.getScene().getWindow()).close();
                    } else {
                        CliUtil.getInstance().createAlert("Errore", "Impossibile eliminare la libreria.").showAndWait();
                    }
                } catch (Exception e) {
                    CliUtil.getInstance().LogOut(e);
                }
            }
        });
    }

    /**
     * Salva le modifiche: rinomina la libreria se il nome è valido (5–50 caratteri)
     * e/o aggiorna l’elenco dei libri se è cambiato. Richiede almeno 3 libri totali.
     * Mostra conferme/avvisi a seconda dell’esito.
     */
    @FXML
    private void SalvaLibreria() {
        if (!NomeLibreria.getText().isEmpty() && NomeLibreria.getText().trim().length() >= 5 && NomeLibreria.getText().trim().length() <= 50) {
            try {
                if(!CliUtil.getInstance().getLibService().modifyLibName(CliUtil.getInstance().getCurrentToken(), LibName, NomeLibreria.getText().trim()))
                    CliUtil.getInstance().createAlert("Errore", "Impossibile modificare il nome della libreria.").showAndWait();
            } catch (Exception e) {
                CliUtil.getInstance().LogOut(e);
                return;
            }
            LibName = NomeLibreria.getText().trim();
            Titolo_Librerie.setText(LibName);
            NomeLibreria.setText("");
            NomeLibreria.setDisable(true);
            NomeLibreria.setEditable(false);
            NomeLibreria.setVisible(false);
            CliUtil.getInstance().createConfirmation("Successo", "Libreria rinominata con successo.", false).showAndWait();
            return;
        }
        List<Libro> LibriLibrerie = new ArrayList<>(risTableView.getItems());
        if (CliUtil.getInstance().hannoDifferenze(OriginalLibri, LibriLibrerie) && LibriLibrerie.size() >= 3) {
            try {
                List<Integer> risultati = CliUtil.getInstance().getLibService().updateLib(CliUtil.getInstance().getCurrentToken(), LibName, LibriLibrerie);
                if (risultati.get(0) == 1) {
                    CliUtil.getInstance().createConfirmation("Successo", "Libreria '" + LibName + "' modificata con successo.", false).showAndWait();
                    OriginalLibri = new ArrayList<>(LibriLibrerie);
                    NomeLibreria.setDisable(true);
                    NomeLibreria.setEditable(false);
                    NomeLibreria.setVisible(false);
                } else {
                    CliUtil.getInstance().reviewLibUpdate(risultati);
                }
            } catch (Exception ignored) {
            }
        } else if (LibriLibrerie.size() < 3) {
            CliUtil.getInstance().createAlert("Errore", "La libreria deve contenere almeno 3 libri.").showAndWait();
        } else {
            CliUtil.getInstance().createAlert("Errore", "Nessuna modifica effettuata.").showAndWait();
        }
    }
}
