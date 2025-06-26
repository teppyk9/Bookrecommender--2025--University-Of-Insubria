package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class VisualizzaLibreriaController {
    public TextField campoRicerca;
    public Button bottoneCerca;
    public MenuButton MenuTipoRicerca;
    public MenuItem MenuCercaTitolo;
    public MenuItem MenuCercaAutore;
    public MenuItem MenuCercaAutoreAnno;
    public TextField campoRicercaAnno;
    public Button BottoneSalvaLibreria;
    public ListView<Libro> listaLibri;
    public Button BottoneAggiungiLibro;
    public Button BottoneRimuoviLibro;
    public ListView <Libro> ListaLibrerie;
    public Button ExitButton;
    public Text Titolo_Librerie;
    public TextField NomeLibreria;
    public Button BottoneCambiaNome;
    public Button BottoneEliminaLibreria;
    public Button BottoneAggiungiConsiglio;
    public Button BottoneValuta;

    private String searchType = "";
    private String LibName;

    /**
     * Lista locale dei libri presenti nella libreria.
     */
    private List<Libro> LibriLibrerie;
    private List<Libro> OriginalLibri;

    private ListView<Libro> lastSelectedView;

    @FXML
    public void initialize() {
        OriginalLibri = new ArrayList<>();
        LibriLibrerie = new ArrayList<>();
        campoRicercaAnno.setVisible(false);
        campoRicercaAnno.setDisable(true);
        NomeLibreria.setDisable(true);
        NomeLibreria.setEditable(false);
        NomeLibreria.setVisible(false);
        ImageView arrowImage = new ImageView( new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png"))));
        arrowImage.setFitWidth(12);
        arrowImage.setFitHeight(12);
        MenuTipoRicerca.setGraphic(arrowImage);
        listaLibri.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) -> {
            if (newV != null) lastSelectedView = listaLibri;
        });
        ListaLibrerie.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) -> {
            if (newV != null) lastSelectedView = ListaLibrerie;
        });
        Platform.runLater(() -> {
            Node arrow = MenuTipoRicerca.lookup(".arrow");
            if (arrow != null) {
                arrow.setVisible(false);
                arrow.setManaged(false);
            }
            Stage stage = (Stage) BottoneCambiaNome.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if(hannoDifferenze(OriginalLibri, LibriLibrerie) || !NomeLibreria.getText().isEmpty()) {
                    CliUtil.getInstance().createConfirmation("Conferma uscita", "Tutte le modifiche andranno perse!\nSei sicuro di voler uscire?", true).showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            stage.close();
                        } else {
                            event.consume();
                        }
                    });
                }
            });
        });
    }

    public void setLibreria(String nomeLibreria) {
        LibName = nomeLibreria;
        Titolo_Librerie.setText(LibName);
        try {
            LibriLibrerie = CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nomeLibreria);
            OriginalLibri = new ArrayList<>(LibriLibrerie);
            ListaLibrerie.setItems(FXCollections.observableArrayList(LibriLibrerie));
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore", "Impossibile caricare la libreria: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleClickCerca() {
        String testo = campoRicerca.getText();
        String anno = campoRicercaAnno.getText();
        if (testo != null && testo.length() >= 2) {
            aggiornaListaCerca(testo, anno);
        } else {
            listaLibri.setItems(FXCollections.observableArrayList());
            CliUtil.getInstance().createAlert("Errore", "Inserire almeno 2 caratteri per la ricerca.").showAndWait();
        }
    }

    private void aggiornaListaCerca(String titolo, String anno) {
        try {
            List<Libro> risultati = null;
            boolean temp = false;
            switch (searchType) {
                case "Titolo":
                    risultati = CliUtil.getInstance().getSearchService().searchByName(titolo);
                    if(risultati != null) {
                        listaLibri.setItems(FXCollections.observableArrayList(risultati));
                        temp = true;
                    }
                    break;
                case "Autore":
                    risultati = CliUtil.getInstance().getSearchService().searchByAuthor(titolo);
                    if(risultati != null) {
                        listaLibri.setItems(FXCollections.observableArrayList(risultati));
                        temp = true;
                    }
                    break;
                case "AutoreAnno":
                    if(anno == null || anno.trim().isEmpty())
                        CliUtil.getInstance().createAlert("Errore", "Inserire un anno valido.").showAndWait();
                    else if(anno.length() > 4)
                        CliUtil.getInstance().createAlert("Errore", "L'anno deve essere composto da massimo 4 cifre.").showAndWait();
                    else if(!anno.matches("\\d+"))
                        CliUtil.getInstance().createAlert("Errore", "L'anno deve essere un numero.").showAndWait();
                    else {
                        risultati = CliUtil.getInstance().getSearchService().searchByAuthorAndYear(titolo, Integer.parseInt(anno));
                        temp = true;
                    }
                    if(risultati != null)
                        listaLibri.setItems(FXCollections.observableArrayList(risultati));
                    break;
                default:
                    CliUtil.getInstance().createAlert("Errore", "Tipo di ricerca non selezionato.").showAndWait();
                    break;
            }
            if((risultati == null || risultati.isEmpty()) && temp) {
                CliUtil.getInstance().createAlert("Nessun risultato", "Nessun libro trovato con i criteri di ricerca specificati.").showAndWait();
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
        }
    }

    public void handleListaDoppioClick_1(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Libro selezionato = listaLibri.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                mostraDettagli(selezionato);
            }
        }
    }

    public void handleListaDoppioClick_2(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Libro selezionato = ListaLibrerie.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                mostraDettagli(selezionato);
            }
        }
    }

    private void mostraDettagli(Libro libro) {
        CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, libro);
    }

    public void cercaTitolo() {
        MenuTipoRicerca.getItems().remove(MenuCercaTitolo);
        switch(searchType) {
            case "Autore":
                MenuTipoRicerca.getItems().add(MenuCercaAutore);
                break;
            case "AutoreAnno":
                MenuTipoRicerca.getItems().add(MenuCercaAutoreAnno);
                campoRicercaAnno.setDisable(true);
                campoRicercaAnno.setVisible(false);
                break;
            default:
                break;
        }
        searchType = "Titolo";
        MenuTipoRicerca.setText("Titolo");
    }

    public void cercaAutore() {
        MenuTipoRicerca.getItems().remove(MenuCercaAutore);
        switch(searchType) {
            case "Titolo":
                MenuTipoRicerca.getItems().add(MenuCercaTitolo);
                break;
            case "AutoreAnno":
                MenuTipoRicerca.getItems().add(MenuCercaAutoreAnno);
                campoRicercaAnno.setDisable(true);
                campoRicercaAnno.setVisible(false);
                break;
            default:
                break;
        }
        searchType = "Autore";
        MenuTipoRicerca.setText("Autore");
    }

    public void cercaAutoreAnno() {
        MenuTipoRicerca.getItems().remove(MenuCercaAutoreAnno);
        switch(searchType) {
            case "Titolo":
                MenuTipoRicerca.getItems().add(MenuCercaTitolo);
                break;
            case "Autore":
                MenuTipoRicerca.getItems().add(MenuCercaAutore);
                break;
            default:
                break;
        }
        searchType = "AutoreAnno";
        MenuTipoRicerca.setText("Autore e Anno");
        campoRicercaAnno.setDisable(false);
        campoRicercaAnno.setVisible(true);
    }

    public void keyEnterPressed_1(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            handleClickCerca();
        }
    }

    public void keyEnterPressed_2(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            handleClickCerca();
        }
    }

    public void ExitApplication() {
        Stage stage = (Stage) ExitButton.getScene().getWindow();
        if(hannoDifferenze(OriginalLibri, LibriLibrerie) || !NomeLibreria.getText().isEmpty()) {
            CliUtil.getInstance().createConfirmation("Conferma uscita", "Tutte le modifiche andranno perse!\nSei sicuro di voler uscire?", true).showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    stage.close();
                }
            });
        }else
            stage.close();
    }

    public void RimuoviLibro() {
        if (lastSelectedView == null) return;
        Libro selezionato = lastSelectedView.getSelectionModel().getSelectedItem();
        if (selezionato != null && LibriLibrerie.contains(selezionato)) {
            LibriLibrerie.remove(selezionato);
            ListaLibrerie.setItems(FXCollections.observableArrayList(LibriLibrerie));
        }
    }

    public void AggiungiLibro() {
        Libro selezionato = listaLibri.getSelectionModel().getSelectedItem();
        if(selezionato != null && !LibriLibrerie.contains(selezionato)) {
            LibriLibrerie.add(selezionato);
            ListaLibrerie.setItems(FXCollections.observableArrayList(LibriLibrerie));
        }
    }

    public void SalvaLibreria() {
        if(!NomeLibreria.getText().isEmpty() && (NomeLibreria.getText().trim().length() >= 5) && (NomeLibreria.getText().trim().length() <= 50)) {
            try {
                CliUtil.getInstance().getLibService().modifyLibName(CliUtil.getInstance().getCurrentToken(), LibName, NomeLibreria.getText().trim());
            }catch (Exception e) {
                CliUtil.getInstance().createAlert("Errore", "Impossibile modificare il nome della libreria: " + e.getMessage()).showAndWait();
                return;
            }
            LibName = NomeLibreria.getText().trim();
            Titolo_Librerie.setText(LibName);
            NomeLibreria.setDisable(true);
            NomeLibreria.setEditable(false);
            NomeLibreria.setVisible(false);
            CliUtil.getInstance().createConfirmation("Successo", "Libreria rinominata con successo.", false).showAndWait();
            return;
        }else{
            if(!NomeLibreria.getText().isEmpty()) {
                CliUtil.getInstance().createAlert("Errore", "Il nome della libreria deve essere compreso tra 5 e 50 caratteri.").showAndWait();
                return;
            }
        }
        if(hannoDifferenze(OriginalLibri, LibriLibrerie) && LibriLibrerie.size() >= 3) {
            List <Integer> risultati;
            try {
                risultati = CliUtil.getInstance().getLibService().updateLib(CliUtil.getInstance().getCurrentToken(), LibName, LibriLibrerie);
                if(risultati.get(0) == 1) {
                    CliUtil.getInstance().createConfirmation("Successo", "Libreria '" + LibName + "' modificata con successo.", false).showAndWait();
                    OriginalLibri = new ArrayList<>(LibriLibrerie);
                    NomeLibreria.setDisable(true);
                    NomeLibreria.setEditable(false);
                    NomeLibreria.setVisible(false);
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < risultati.size(); i += 2) {
                        int idLibro = risultati.get(i);
                        int codice = risultati.get(i + 1);
                        switch (codice) {
                            case 0:
                                sb.append("Il libro con titolo ").append(CliUtil.getInstance().getSearchService().getLibro(idLibro)).append(" ha valutazioni associate.");
                                break;
                            case 1:
                                sb.append("Il libro con titolo ").append(CliUtil.getInstance().getSearchService().getLibro(idLibro)).append(" è stato utilizzato come consiglio.");
                                break;
                            case 2:
                                sb.append("Il libro con titolo ").append(CliUtil.getInstance().getSearchService().getLibro(idLibro)).append(" ha libri consigliati ad esso associati.");
                                break;
                            default:
                                sb.append("Il libro con titolo ").append(CliUtil.getInstance().getSearchService().getLibro(idLibro)).append(" ha codice errore sconosciuto: ").append(codice).append(".");
                        }
                        if (i + 2 < risultati.size()) {
                            sb.append(System.lineSeparator());
                        }
                    }
                    CliUtil.getInstance().createAlert("Errore", String.valueOf(sb)).showAndWait();
                }
            }catch (Exception ignored) {}
        }else if(LibriLibrerie.size() < 3) {
            CliUtil.getInstance().createAlert("Errore", "La libreria deve contenere almeno 3 libri.").showAndWait();
        }else CliUtil.getInstance().createAlert("Errore", "Nessuna modifica effettuata.").showAndWait();
    }

    public void cambiaNome() {
        NomeLibreria.setDisable(false);
        NomeLibreria.setEditable(true);
        NomeLibreria.setVisible(true);
    }

    public void eliminaLibreria() {
        CliUtil.getInstance().createConfirmation("Conferma eliminazione", "Sei sicuro di voler eliminare la libreria '" + LibName + "'?", true).showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    if (CliUtil.getInstance().getLibService().deleteLib(CliUtil.getInstance().getCurrentToken(), LibName)) {
                        CliUtil.getInstance().createConfirmation("Successo", "Libreria eliminata con successo.",false).showAndWait();
                        Stage stage = (Stage) BottoneEliminaLibreria.getScene().getWindow();
                        stage.close();
                    } else {
                        CliUtil.getInstance().createAlert("Errore", "Impossibile eliminare la libreria.").showAndWait();
                    }
                }catch (Exception ignored) {}
            }
        });
    }

    public void aggiungiConsiglio() {
        Libro selezionato = ListaLibrerie.getSelectionModel().getSelectedItem();
        if(selezionato!= null && OriginalLibri.contains(selezionato)) {
            CliUtil.getInstance().buildStage(FXMLtype.CREACONSIGLIO, selezionato);
        }else
            CliUtil.getInstance().createAlert("Errore", "Selezionare un libro valido dalla libreria.").showAndWait();
    }

    private boolean hannoDifferenze(List<Libro> list1, List<Libro> list2) {
        if (list1.size() != list2.size()) {
            return true;
        }

        Set<Libro> set = new HashSet<>(list2);
        for (Libro libro : list1) {
            if (!set.contains(libro)) {
                return true;
            }
        }
        return false;
    }

    public void valutaLibro() {
        Libro selezionato = ListaLibrerie.getSelectionModel().getSelectedItem();
        if(selezionato != null && OriginalLibri.contains(selezionato)) {
            CliUtil.getInstance().buildStage(FXMLtype.CREAVALUTAZIONE, selezionato);
        } else {
            CliUtil.getInstance().createAlert("Errore", "Selezionare un libro valido dalla libreria.").showAndWait();
        }
    }
}
