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

import java.util.List;
import java.util.Objects;

public class CercaLibroController {

    @FXML public Button GoBackButton_MainMenu;
    @FXML public Button ExitButton;
    @FXML public MenuItem MenuCercaTitolo;
    @FXML public MenuButton MenuTipoRicerca;
    @FXML public MenuItem MenuCercaAutore;
    @FXML public MenuItem MenuCercaAutoreAnno;
    @FXML public TextField campoRicercaAnno;
    @FXML public TextField campoRicerca;
    @FXML public ListView<Libro> listaLibri;
    @FXML public Button bottoneCerca;

    private String searchType;

    @FXML
    public void initialize() {
        campoRicercaAnno.setVisible(false);
        campoRicercaAnno.setDisable(true);
        ImageView arrowImage = new ImageView( new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/icons/arrow_down_icon.png"))));
        arrowImage.setFitWidth(12);
        arrowImage.setFitHeight(12);
        MenuTipoRicerca.setGraphic(arrowImage);
        searchType = "";
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
            aggiornaLista(testo, anno);
        } else {
            listaLibri.setItems(FXCollections.observableArrayList());
            CliUtil.getInstance().createAlert("Errore", "Inserire almeno 2 caratteri per la ricerca.").showAndWait();
        }
    }

    private void aggiornaLista(String titolo, String anno) {
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

    @FXML
    private void handleListaDoppioClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Libro selezionato = listaLibri.getSelectionModel().getSelectedItem();
            if (selezionato != null) {
                CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOlIBRO, selezionato);
            }
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
        CliUtil.getInstance().buildStage(FXMLtype.HOME, null);
    }

    public void ExitApplication() {
        CliUtil.getInstance().exitApplication();
    }
}
