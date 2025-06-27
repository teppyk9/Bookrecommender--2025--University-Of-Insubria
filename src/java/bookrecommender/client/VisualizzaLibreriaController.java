package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VisualizzaLibreriaController extends AbstractSearchController{
    @FXML private TextField campoRicerca;
    @FXML private TextField campoRicercaAnno;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;
    @FXML private ListView<Libro> listaLibri;

    @FXML private ListView<Libro> ListaLibrerie;
    @FXML private TextField NomeLibreria;
    @FXML private Text Titolo_Librerie;
    @FXML private Button BottoneCambiaNome;
    @FXML private Button BottoneEliminaLibreria;

    private String searchType = "";
    private String LibName;
    private List<Libro> LibriLibrerie;
    private List<Libro> OriginalLibri;
    private ListView<Libro> lastSelectedView;

    @FXML
    public void initialize() {
        OriginalLibri = new ArrayList<>();
        LibriLibrerie = new ArrayList<>();
        NomeLibreria.setDisable(true);
        NomeLibreria.setEditable(false);
        NomeLibreria.setVisible(false);
        initCommon();
        listaLibri.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) -> {
            if (newV != null) lastSelectedView = listaLibri;
        });
        ListaLibrerie.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) -> {
            if (newV != null) lastSelectedView = ListaLibrerie;
        });
        Platform.runLater(() -> {
            Node a = MenuTipoRicerca.lookup(".arrow");
            if (a != null) {
                a.setVisible(false);
                a.setManaged(false);
            }
            saveFlag();
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

    @Override protected TextField getCampoRicerca(){
        return campoRicerca;
    }

    @Override protected TextField getCampoRicercaAnno(){
        return campoRicercaAnno;
    }

    @Override protected ListView<Libro> getListaRisultati(){
        return listaLibri;
    }

    @Override protected String getSearchType(){
        return searchType;
    }

    @Override protected void setSearchType(String type){
        this.searchType = type;
    }

    @Override protected MenuButton getMenuTipoRicerca(){
        return MenuTipoRicerca;
    }

    @Override protected MenuItem getItemTitolo(){
        return MenuCercaTitolo;
    }

    @Override protected MenuItem getItemAutore(){
        return MenuCercaAutore;
    }

    @Override protected MenuItem getItemAutoreAnno(){
        return MenuCercaAutoreAnno;
    }

    @Override protected void mostraDettagli(Libro libro){
        CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, libro);
    }

    @Override
    protected List<Libro> searchByTitle(String testo){
        try{
            return CliUtil.getInstance().getSearchService().searchByName(testo);
        }catch(RemoteException e){
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    @Override
    protected List<Libro> searchByAuthor(String testo){
        try{
            return CliUtil.getInstance().getSearchService().searchByAuthor(testo);
        }catch(RemoteException e){
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    @Override
    protected List<Libro> searchByAuthorAndYear(String testo, int anno){
        try{
            return CliUtil.getInstance().getSearchService().searchByAuthorAndYear(testo, anno);
        }catch(RemoteException e){
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }
    @FXML
    private void handleListaDoppioClick_1(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Libro sel = listaLibri.getSelectionModel().getSelectedItem();
            if (sel != null)
                mostraDettagli(sel);
        }
    }

    @FXML
    private void handleListaDoppioClick_2(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Libro sel = ListaLibrerie.getSelectionModel().getSelectedItem();
            if (sel != null)
                mostraDettagli(sel);
        }
    }

    @FXML
    private void ExitApplication() {
        saveFlag();
    }

    private void saveFlag() {
        Stage stage = (Stage) BottoneCambiaNome.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            if (hannoDifferenze(OriginalLibri, LibriLibrerie) || !NomeLibreria.getText().isEmpty()) {
                CliUtil.getInstance().createConfirmation("Conferma uscita", "Tutte le modifiche andranno perse!\nSei sicuro di voler uscire?", true).showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        stage.close();
                    } else {
                        event.consume();
                    }
                });
            }
        });
    }

    @FXML
    private void cambiaNome() {
        NomeLibreria.setDisable(false);
        NomeLibreria.setEditable(true);
        NomeLibreria.setVisible(true);
    }

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
                    CliUtil.getInstance().createAlert("Errore", "Impossibile eliminare la libreria: " + e.getMessage()).showAndWait();
                }
            }
        });
    }

    @FXML
    private void AggiungiLibro() {
        Libro sel = listaLibri.getSelectionModel().getSelectedItem();
        if (sel != null && !LibriLibrerie.contains(sel)) {
            LibriLibrerie.add(sel);
            ListaLibrerie.setItems(FXCollections.observableArrayList(LibriLibrerie));
        }
    }

    @FXML
    private void RimuoviLibro() {
        Libro sel = lastSelectedView.getSelectionModel().getSelectedItem();
        if (sel != null && LibriLibrerie.contains(sel)) {
            LibriLibrerie.remove(sel);
            ListaLibrerie.setItems(FXCollections.observableArrayList(LibriLibrerie));
        }
    }

    @FXML
    private void SalvaLibreria() {
        if (!NomeLibreria.getText().isEmpty() && NomeLibreria.getText().trim().length() >= 5 && NomeLibreria.getText().trim().length() <= 50) {
            try {
                CliUtil.getInstance().getLibService().modifyLibName(CliUtil.getInstance().getCurrentToken(), LibName, NomeLibreria.getText().trim());
            } catch (Exception e) {
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
        }
        if (hannoDifferenze(OriginalLibri, LibriLibrerie) && LibriLibrerie.size() >= 3) {
            try {
                List<Integer> risultati = CliUtil.getInstance().getLibService().updateLib(CliUtil.getInstance().getCurrentToken(), LibName, LibriLibrerie);
                if (risultati.get(0) == 1) {
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
                        if (i + 2 < risultati.size()) sb.append(System.lineSeparator());
                    }
                    CliUtil.getInstance().createAlert("Errore", sb.toString()).showAndWait();
                }
            } catch (Exception ignored) {
            }
        } else if (LibriLibrerie.size() < 3) {
            CliUtil.getInstance().createAlert("Errore", "La libreria deve contenere almeno 3 libri.").showAndWait();
        } else {
            CliUtil.getInstance().createAlert("Errore", "Nessuna modifica effettuata.").showAndWait();
        }
    }

    @FXML
    private void aggiungiConsiglio() {
        Libro sel = ListaLibrerie.getSelectionModel().getSelectedItem();
        if (sel != null && OriginalLibri.contains(sel)) {
            CliUtil.getInstance().buildStage(FXMLtype.CREACONSIGLIO, sel);
        } else {
            CliUtil.getInstance().createAlert("Errore", "Selezionare un libro valido dalla libreria.").showAndWait();
        }
    }

    @FXML
    private void valutaLibro() {
        Libro sel = ListaLibrerie.getSelectionModel().getSelectedItem();
        if (sel != null && OriginalLibri.contains(sel)) {
            CliUtil.getInstance().buildStage(FXMLtype.CREAVALUTAZIONE, sel);
        } else {
            CliUtil.getInstance().createAlert("Errore", "Selezionare un libro valido dalla libreria.").showAndWait();
        }
    }

    private boolean hannoDifferenze(List<Libro> list1, List<Libro> list2) {
        if (list1.size() != list2.size()) return true;
        Set<Libro> set = new HashSet<>(list2);
        for (Libro libro : list1) if (!set.contains(libro)) return true;
        return false;
    }
}
