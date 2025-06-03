package bookrecommender.client;

import bookrecommender.common.Libro;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CercaLibroAvanzatoController {

    public TextField campoRicerca;
    public Button bottoneCerca;
    public MenuButton MenuTipoRicerca;
    public MenuItem MenuCercaTitolo;
    public MenuItem MenuCercaAutore;
    public MenuItem MenuCercaAutoreAnno;
    public TextField campoRicercaAnno;
    public Button BottoneSalvaLibreria;
    public ListView <Libro> listaLibri;
    public Button BottoneApriLibreria;
    public Button BottoneAggiungiLibro;
    public Button BottoneRimuoviLibro;
    public ListView <Libro> ListaLibrerie;
    public Button GoBackButton_MainMenu;
    public Button ExitButton;
    public Button BottoneCreaLibreria;
    public Text Titolo_Librerie;
    public TextField NomeLibreria;

    private String searchType = "";

    private List<Libro> Librilibrerie;

    private List<String> Librerie;


    @FXML
    public void initialize() {
        Librilibrerie = new ArrayList<>();
        campoRicercaAnno.setVisible(false);
        campoRicercaAnno.setDisable(true);
        ImageView arrowImage = new ImageView( new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/arrow_down_icon.png"))));
        arrowImage.setFitWidth(12);
        arrowImage.setFitHeight(12);
        MenuTipoRicerca.setGraphic(arrowImage);
        aggiornaListaLibrerie();
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

    private void aggiornaListaLibrerie() {
        try {
            Librerie = CliUtil.getInstance().getLibService().getLibs(CliUtil.getInstance().getCurrentToken());
            if (Librerie == null) {
                CliUtil.getInstance().createAlert("Errore", "Nessuna libreria trovata.").showAndWait();
                return;
            }
            ObservableList<String> risultati = FXCollections.observableArrayList(Librerie);
            ListView<String> ListaLibrerie = new ListView<>(risultati);
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore durante il caricamento delle librerie", e.getMessage()).showAndWait();
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
        CliUtil.getInstance().showLibroDetails(libro);
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

    public void GoToMainMenu() {
        CliUtil.getInstance().loadFXML("/bookrecommender/client/AreaRiservata.fxml", "Book Recommender");
    }

    public void ExitApplication() {
        CliUtil.getInstance().exitApplication();
    }

    public void RimuoviLibro() {
    }

    public void AggiungiLibro() {

    }

    public void ApriLibreria() {
    }

    public void SalvaLibreria() {
    }

    public void CreaLibreria() {
    }
}
