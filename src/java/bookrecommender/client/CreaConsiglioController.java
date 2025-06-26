package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreaConsiglioController {
    public TextField campoRicerca;
    public Button bottoneCerca;
    public MenuButton MenuTipoRicerca;
    public MenuItem MenuCercaTitolo;
    public MenuItem MenuCercaAutore;
    public MenuItem MenuCercaAutoreAnno;
    public TextField campoRicercaAnno;
    public ListView <Libro> listaLibri;
    public Button GoBackButton_MainMenu;
    public ListView <Libro> ListaConsigli;
    public Button aggiungiButton;
    public Button rimuoviButton;
    public Button salvaButton;
    public Button Bottone_GetAllBooks;

    private String searchType = "";

    private Libro myLibro;

    public void initialize() {
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

    public void setLibro(Libro libro) {
        this.myLibro = libro;
    }

    public void keyEnterPressed_1(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            handleClickCerca();
        }
    }

    public void handleClickCerca() {
        String testo = campoRicerca.getText();
        String anno = campoRicercaAnno.getText();
        if (testo != null && testo.length() >= 2) {
            aggiornaListaCerca(testo, anno);
        } else {
            listaLibri.setItems(FXCollections.observableArrayList());
            CliUtil.getInstance().createAlert("Errore", "Inserire almeno 2 caratteri per la ricerca.").showAndWait();
        }
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

    public void keyEnterPressed_2(KeyEvent keyEvent) {
        if(keyEvent.getCode().getName().equals("Enter")) {
            handleClickCerca();
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
            Libro selezionato = ListaConsigli.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                mostraDettagli(selezionato);
            }
        }
    }

    public void GoToMainMenu() {
        Stage stage = (Stage) GoBackButton_MainMenu.getScene().getWindow();
        stage.close();
    }

    private void aggiornaListaCerca(String titolo, String anno) {
        try {
            List<Libro> risultati = null;
            boolean temp = false;
            switch (searchType) {
                case "Titolo":
                    risultati = CliUtil.getInstance().getSearchService().searchByName(CliUtil.getInstance().getCurrentToken(), titolo);
                    if(risultati != null) {
                        listaLibri.setItems(FXCollections.observableArrayList(risultati));
                        temp = true;
                    }
                    break;
                case "Autore":
                    risultati = CliUtil.getInstance().getSearchService().searchByAuthor(CliUtil.getInstance().getCurrentToken(), titolo);
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
                        risultati = CliUtil.getInstance().getSearchService().searchByAuthorAndYear(CliUtil.getInstance().getCurrentToken(), titolo, Integer.parseInt(anno));
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
    private void mostraDettagli(Libro libro) {
        CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, libro);
    }

    public void aggiungiLibro() {
        Libro selezionato = listaLibri.getSelectionModel().getSelectedItem();
        if(selezionato.getId() == myLibro.getId()) {
            CliUtil.getInstance().createAlert("Errore", "Non puoi consigliare il libro che stai aggiungendo.").showAndWait();
            return;
        }
        if (!ListaConsigli.getItems().contains(selezionato)) {
            if (ListaConsigli.getItems().size() >= 3) {
                CliUtil.getInstance().createAlert("Errore", "Non puoi consigliare più di 3 libri.").showAndWait();
                return;
            }
            ListaConsigli.getItems().add(selezionato);
        }
    }

    public void rimuoviLibro() {
        Libro selezionato = ListaConsigli.getSelectionModel().getSelectedItem();
        if (selezionato != null) {
            ListaConsigli.getItems().remove(selezionato);
        }
    }

    public void salvaConsiglio() {
        List<Libro> libriConsigliati = new ArrayList<>();
        libriConsigliati.add(myLibro);
        for(Libro libro : ListaConsigli.getItems()) {
            if (!libriConsigliati.contains(libro)) {
                libriConsigliati.add(libro);
            }
        }

        if (libriConsigliati.size() > 4) {
            CliUtil.getInstance().createAlert("Errore", "Non puoi consigliare più di 3").showAndWait();
            return;
        }
        try {
            if(CliUtil.getInstance().getLibService().addConsiglio(CliUtil.getInstance().getCurrentToken(), libriConsigliati)) {
                CliUtil.getInstance().createConfirmation("Successo", "Consiglio salvato con successo!",true).showAndWait();
                GoToMainMenu();
            }
            else {
                CliUtil.getInstance().createAlert("Errore", "Impossibile salvare il consiglio.").showAndWait();
                GoToMainMenu();
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore", "Impossibile salvare il consiglio: " + e.getMessage()).showAndWait();
            GoToMainMenu();
        }
    }

    public void getAllBooks() {
        try {
            List<Libro> libri = CliUtil.getInstance().getSearchService().getAllBooks(CliUtil.getInstance().getCurrentToken());
            libri.remove(myLibro);
            listaLibri.setItems(FXCollections.observableArrayList(libri));
        }catch (RemoteException e) {
            CliUtil.getInstance().createAlert(e.getLocalizedMessage(), "Errore durante la ricerca di tutti i tuoi libri").showAndWait();
        }
    }
}
