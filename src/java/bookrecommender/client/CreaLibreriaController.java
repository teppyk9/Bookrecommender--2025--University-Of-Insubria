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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreaLibreriaController {
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

    private String searchType = "";

    private List<Libro> LibriLibrerie;

    @FXML
    public void initialize() {
        LibriLibrerie = new ArrayList<>();
        campoRicercaAnno.setVisible(false);
        campoRicercaAnno.setDisable(true);

        ImageView arrowImage = new ImageView( new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png"))));
        arrowImage.setFitWidth(12);
        arrowImage.setFitHeight(12);
        MenuTipoRicerca.setGraphic(arrowImage);
        Platform.runLater(() -> {
            Node arrow = MenuTipoRicerca.lookup(".arrow");
            if (arrow != null) {
                arrow.setVisible(false);
                arrow.setManaged(false);
            }
        });
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

    public void handleListaDoppioClick_2(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
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
        stage.close();
    }

    public void RimuoviLibro() {
        Libro selezionato = listaLibri.getSelectionModel().getSelectedItem();
        if(selezionato != null && LibriLibrerie.contains(selezionato)) {
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
        if(LibriLibrerie.size() > 2 && NomeLibreria.getText() != null && !NomeLibreria.getText().trim().isEmpty() && NomeLibreria.getText().length() > 5 && NomeLibreria.getText().length() < 50) {
            try {
                if(CliUtil.getInstance().getLibService().createLib(CliUtil.getInstance().getCurrentToken(), NomeLibreria.getText().trim(), LibriLibrerie)) {
                    CliUtil.getInstance().createConfirmation("Successo", "Libreria salvata con successo!", false).showAndWait();
                    Stage stage = (Stage) BottoneSalvaLibreria.getScene().getWindow();
                    stage.close();
                }
                else {
                    CliUtil.getInstance().createAlert("Errore", "Impossibile salvare la libreria.").showAndWait();
                }
            }catch(Exception e) {
                CliUtil.getInstance().createAlert("Errore", "Impossibile salvare la libreria: " + e.getMessage()).showAndWait();
            }
        }else CliUtil.getInstance().createAlert("Errore", "La libreria deve contenere almeno 3 libri e il nome deve essere compreso tra 5 e 50 caratteri.").showAndWait();
    }
}
