package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VisualizzaLibreriaController extends TableViewEngine{
    @FXML private TextField campoRicerca;
    @FXML private TextField campoRicercaAnno;
    @FXML private MenuButton MenuTipoRicerca;
    @FXML private MenuItem MenuCercaTitolo;
    @FXML private MenuItem MenuCercaAutore;
    @FXML private MenuItem MenuCercaAutoreAnno;
    @FXML private TableView <Libro>tableView;
    @FXML private TableColumn <Libro, String> titoloCol;
    @FXML private TableColumn <Libro, String>autoreCol;
    @FXML private TableColumn <Libro, Integer> annoCol;
    @FXML private TableColumn <Libro, Void> azioniCol;
    @FXML private TableView <Libro> risTableView;
    @FXML private TableColumn <Libro, String> risTitoloCol;
    @FXML private TableColumn <Libro, String> risAutoreCol;
    @FXML private TableColumn <Libro, Integer> risAnnoCol;
    @FXML private TableColumn <Libro, Void> risAzioniCol;
    @FXML private TextField NomeLibreria;
    @FXML private Text Titolo_Librerie;
    @FXML private Button BottoneCambiaNome;
    @FXML private Button BottoneEliminaLibreria;

    private String LibName;
    private List<Libro> OriginalLibri;

    @FXML
    public void initialize() {
        OriginalLibri = new ArrayList<>();
        NomeLibreria.setDisable(true);
        NomeLibreria.setEditable(false);
        NomeLibreria.setVisible(false);
        initBasicSearch();
        initSAddRemCol();
        initOActionCol(true);
        initOTableView();
        initTableViews();
        Platform.runLater(() -> {
            Stage stage = (Stage) BottoneCambiaNome.getScene().getWindow();
            stage.setOnCloseRequest(event -> saveFlag(stage));
        });
    }

    public void setLibreria(String nomeLibreria) {
        LibName = nomeLibreria;
        Titolo_Librerie.setText(LibName);
        try {
            OriginalLibri = new ArrayList<>(CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nomeLibreria));
            risTableView.setItems(FXCollections.observableArrayList(OriginalLibri));
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

    @Override protected boolean getSearchType(){
        return false;
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

    @Override protected TableColumn<Libro, Void> getSRecensioniCol() {return null;}

    @Override protected TableColumn<Libro, Void> getSAggiungiAdvCol() {return null;}

    @Override protected TableColumn<Libro, Void> getSAddRemCol() {return azioniCol;}

    @Override protected TableView<Libro> getOTableView() {return risTableView;}

    @Override protected TableColumn<Libro, String> getOTitoloCol() {return risTitoloCol;}

    @Override protected TableColumn<Libro, String> getOAutoreCol() {return risAutoreCol;}

    @Override protected TableColumn<Libro, Integer> getOAnnoCol() {return risAnnoCol;}

    @Override protected TableColumn<Libro, Void> getOActionCol() {return risAzioniCol;}

    @FXML
    private void ExitApplication() {
        saveFlag((Stage) BottoneCambiaNome.getScene().getWindow());
    }

    private void saveFlag(Stage stage) {
        if (hannoDifferenze(OriginalLibri, new ArrayList<>(risTableView.getItems())) || !NomeLibreria.getText().isEmpty()) {
            CliUtil.getInstance().createConfirmation("Conferma uscita", "Tutte le modifiche andranno perse!\nSei sicuro di voler uscire?", true).showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES)
                    stage.close();
            });
        }else
            stage.close();
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
    private void SalvaLibreria() {
        if (!NomeLibreria.getText().isEmpty() && NomeLibreria.getText().trim().length() >= 5 && NomeLibreria.getText().trim().length() <= 50) {
            try {
                if(!CliUtil.getInstance().getLibService().modifyLibName(CliUtil.getInstance().getCurrentToken(), LibName, NomeLibreria.getText().trim()))
                    CliUtil.getInstance().createAlert("Errore", "Impossibile modificare il nome della libreria.").showAndWait();
            } catch (Exception e) {
                CliUtil.getInstance().createAlert("Errore", "Impossibile modificare il nome della libreria: " + e.getMessage()).showAndWait();
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

    private boolean hannoDifferenze(List<Libro> list1, List<Libro> list2) {
        if (list1.size() != list2.size()) return true;
        Set<Libro> set = new HashSet<>(list2);
        for (Libro libro : list1) if (!set.contains(libro)) return true;
        return false;
    }
}
