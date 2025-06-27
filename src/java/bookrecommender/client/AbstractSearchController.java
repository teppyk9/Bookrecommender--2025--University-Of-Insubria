package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.util.List;
import java.util.Objects;

public abstract class AbstractSearchController{

    protected abstract TextField getCampoRicerca();
    protected abstract TextField getCampoRicercaAnno();
    protected abstract ListView<Libro> getListaRisultati();
    protected abstract String getSearchType();
    protected abstract void setSearchType(String type);
    protected abstract MenuButton getMenuTipoRicerca();
    protected abstract MenuItem getItemTitolo();
    protected abstract MenuItem getItemAutore();
    protected abstract MenuItem getItemAutoreAnno();
    protected abstract void mostraDettagli(Libro item);

    protected abstract List<Libro> searchByTitle(String testo);
    protected abstract List<Libro> searchByAuthor(String testo);
    protected abstract List<Libro> searchByAuthorAndYear(String testo, int anno);

    @FXML
    private void cercaTitolo() {
        switchType("Titolo", getItemTitolo(), "Titolo");
    }

    @FXML
    private void cercaAutore() {
        switchType("Autore", getItemAutore(), "Autore");
    }

    @FXML
    private void cercaAutoreAnno() {
        switchType("AutoreAnno", getItemAutoreAnno(), "Autore e Anno");
        getCampoRicercaAnno().setVisible(true);
        getCampoRicercaAnno().setDisable(false);
    }

    @FXML
    private void handleClickCerca() {
        String testo = getCampoRicerca().getText();
        String anno  = getCampoRicercaAnno().getText();
        if (testo == null || testo.length() < 2) {
            getListaRisultati().setItems(FXCollections.emptyObservableList());
            CliUtil.getInstance().createAlert("Errore", "Inserire almeno 2 caratteri per la ricerca.").showAndWait();
            return;
        }
        aggiornaListaCerca(testo, anno);
    }

    @FXML
    private void keyEnterPressed_1(KeyEvent e) {
        if ("Enter".equals(e.getCode().getName()))
            handleClickCerca();
    }

    @FXML
    private void keyEnterPressed_2(KeyEvent e) {
        if ("Enter".equals(e.getCode().getName()))
            handleClickCerca();
    }

    protected void initCommon() {
        getCampoRicercaAnno().setVisible(false);
        getCampoRicercaAnno().setDisable(true);
        ImageView arrow = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png"))));
        arrow.setFitWidth(12);
        arrow.setFitHeight(12);
        getMenuTipoRicerca().setGraphic(arrow);
        Platform.runLater(() -> {
            Node a = getMenuTipoRicerca().lookup(".arrow");
            if (a != null) {
                a.setVisible(false);
                a.setManaged(false);
            }
        });
    }

    private void aggiornaListaCerca(String testo, String anno) {
        try {
            List<Libro> risultati;
            switch (getSearchType()) {
                case "Titolo":
                    risultati = searchByTitle(testo);
                    break;
                case "Autore":
                    risultati = searchByAuthor(testo);
                    break;
                case "AutoreAnno":
                    if (!validateYear(anno)) return;
                    risultati = searchByAuthorAndYear(testo, Integer.parseInt(anno));
                    break;
                default:
                    CliUtil.getInstance().createAlert("Errore", "Tipo di ricerca non selezionato.").showAndWait();
                    return;
            }
            if (risultati != null) {
                getListaRisultati().setItems(FXCollections.observableArrayList(risultati));
            }
            if (risultati == null || risultati.isEmpty()) {
                CliUtil.getInstance().createAlert("Nessun risultato", "Nessun libro trovato con i criteri specificati.").showAndWait();
            }
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
        }
    }

    private void switchType(String key, MenuItem removed, String text) {
        MenuButton menu = getMenuTipoRicerca();
        menu.getItems().remove(removed);
        switch (getSearchType()) {
            case "Titolo":
                menu.getItems().add(getItemTitolo());
                break;
            case "Autore":
                menu.getItems().add(getItemAutore());
                break;
            case "AutoreAnno":
                menu.getItems().add(getItemAutoreAnno());
                getCampoRicercaAnno().setVisible(false);
                getCampoRicercaAnno().setDisable(true);
                break;
        }
        setSearchType(key);
        menu.setText(text);
    }

    private boolean validateYear(String anno) {
        if (anno == null || anno.trim().isEmpty()) {
            CliUtil.getInstance().createAlert("Errore", "Inserire un anno valido.").showAndWait();
            return false;
        }
        if (anno.length() > 4) {
            CliUtil.getInstance().createAlert("Errore", "L'anno deve avere massimo 4 cifre.").showAndWait();
            return false;
        }
        if (!anno.matches("\\d+")) {
            CliUtil.getInstance().createAlert("Errore", "L'anno deve essere numerico.").showAndWait();
            return false;
        }
        return true;
    }

}