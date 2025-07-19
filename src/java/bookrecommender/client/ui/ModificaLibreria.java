package bookrecommender.client.ui;

import bookrecommender.client.util.CliUtil;
import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.util.TableViewEngine;
import bookrecommender.common.model.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ModificaLibreria extends TableViewEngine {
    @FXML private ProgressIndicator loadingCircle;
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
        Platform.runLater(() -> {
            Stage stage = (Stage) BottoneCambiaNome.getScene().getWindow();
            stage.setOnCloseRequest(event -> saveFlag());
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
            } catch (RemoteException e) {
                CliUtil.getInstance().createAlert("Errore", "Connessione all'interfaccia scaduta\n" + e.getLocalizedMessage()).showAndWait();
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
        saveFlag();
    }

    private void saveFlag() {
        if (CliUtil.getInstance().hannoDifferenze(OriginalLibri, new ArrayList<>(risTableView.getItems())) || !NomeLibreria.getText().isEmpty()) {
            CliUtil.getInstance().createConfirmation("Conferma uscita", "Tutte le modifiche andranno perse!\nSei sicuro di voler uscire?", true).showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES)
                    CliUtil.getInstance().buildStage(FXMLtype.GESTIONELIBRERIE, null, null);
            });
        }else
            CliUtil.getInstance().buildStage(FXMLtype.GESTIONELIBRERIE, null, null);
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
