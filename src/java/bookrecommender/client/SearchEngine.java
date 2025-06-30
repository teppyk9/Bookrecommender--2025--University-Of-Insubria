package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Objects;

public abstract class SearchEngine {
    protected abstract TextField getCampoRicerca();
    protected abstract TextField getCampoRicercaAnno();
    protected abstract MenuButton getMenuTipoRicerca();
    protected abstract MenuItem getMenuCercaTitolo();
    protected abstract MenuItem getMenuCercaAutore();
    protected abstract MenuItem getMenuCercaAutoreAnno();
    protected abstract String getSearchType();
    protected abstract void setSearchType(String type);

    protected abstract TreeTableView<Libro> getSTreeTableView();
    protected abstract TreeTableColumn<Libro, String> getSTitoloCol();
    protected abstract TreeTableColumn<Libro, String> getSAutoreCol();
    protected abstract TreeTableColumn<Libro, Integer> getSAnnoCol();
    protected abstract TreeTableColumn<Libro, Void> getSRecensioniCol();
    protected abstract TreeTableColumn<Libro, Void> getSAggiungiAdvCol();
    protected abstract TreeTableColumn<Libro, Void> getSAddRemCol();

    protected abstract List<Libro> searchByTitle(String testo);
    protected abstract List<Libro> searchByAuthor(String testo);
    protected abstract List<Libro> searchByAuthorAndYear(String testo, int anno);

    protected abstract TreeTableView<Libro> getOTreeTableView();
    protected abstract TreeTableColumn<Libro, String> getOTitoloCol();
    protected abstract TreeTableColumn<Libro, String> getOAutoreCol();
    protected abstract TreeTableColumn<Libro, Integer> getOAnnoCol();

    protected void initBasicSearch() {
        getSTreeTableView().setShowRoot(false);
        getCampoRicercaAnno().setVisible(false);
        getCampoRicercaAnno().setDisable(true);
        ImageView arrow = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png")), 12, 12, true, true));
        getMenuTipoRicerca().setGraphic(arrow);
        Platform.runLater(() -> {
            Node a = getMenuTipoRicerca().lookup(".arrow");
            if (a != null) {
                a.setVisible(false);
                a.setManaged(false);
            }
        });

        getMenuCercaTitolo().setOnAction(e -> switchType("Titolo", "Titolo"));
        getMenuCercaAutore().setOnAction(e -> switchType("Autore", "Autore"));
        getMenuCercaAutoreAnno().setOnAction(e -> {
            switchType("AutoreAnno", "Autore e Anno");
            getCampoRicercaAnno().setVisible(true);
            getCampoRicercaAnno().setDisable(false);
        });

        getSTitoloCol().setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getValue().getTitolo())
        );

        getSAutoreCol().setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getValue().getAutore())
        );

        getSAnnoCol().setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>((int) cellData.getValue().getValue().getAnnoPubblicazione())
        );
    }

    protected void initSRecensioniCol(){
        getSRecensioniCol().setCellFactory(col -> new TreeTableCell<>() {
            private final ImageView check = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/check-green.png")), 16, 16, true, true));
            private final ImageView noCheck = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_icon.png")), 16, 16, true, true));
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Libro l = getTreeTableView().getTreeItem(getIndex()).getValue();
                    boolean has = false;
                    try {
                        has = CliUtil.getInstance().getSearchService().hasValRec(l);
                    } catch (RemoteException e) {
                        CliUtil.getInstance().createAlert("Errore", "Impossibile verificare le recensioni: " + e.getMessage()).showAndWait();
                    }
                    setGraphic(has ? check : noCheck);
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    protected void initSAggiungiAdvCol(){
        getSAggiungiAdvCol().setCellFactory(col -> new TreeTableCell<>() {
            private final MenuButton menu = new MenuButton("", new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/plus-circle-green.png") ), 16, 16, true, true)));
            {
                MenuItem recensisci = new MenuItem("Recensisci");
                MenuItem consigli = new MenuItem("Aggiungi Consigli");
                MenuItem libreria = new MenuItem("Aggiungi ad una libreria");
                menu.getItems().addAll(recensisci, consigli, libreria);

                recensisci.setOnAction(evt -> {
                    Libro l = getTreeTableView().getTreeItem(getIndex()).getValue();
                    try {
                        if(CliUtil.getInstance().getLibService().isLibPresent(CliUtil.getInstance().getCurrentToken(), l))
                            CliUtil.getInstance().buildStage(FXMLtype.CREAVALUTAZIONE, l);
                        else
                            CliUtil.getInstance().createAlert("Errore", "Il libro non è presente in nessuna libreria.").showAndWait();
                    } catch (RemoteException e) {
                        CliUtil.getInstance().createAlert("Errore","Connessione all'interfaccia scaduta\n" + e.getLocalizedMessage()).showAndWait();
                    }
                });
                consigli.setOnAction(evt -> {
                    Libro l = getTreeTableView().getTreeItem(getIndex()).getValue();
                    try{
                        if(CliUtil.getInstance().getLibService().isLibPresent(CliUtil.getInstance().getCurrentToken(), l))
                            CliUtil.getInstance().buildStage(FXMLtype.CREACONSIGLIO, l);
                        else
                            CliUtil.getInstance().createAlert("Errore", "Il libro non è presente in nessuna libreria.").showAndWait();
                    } catch (RemoteException e) {
                        CliUtil.getInstance().createAlert("Errore","Connessione all'interfaccia scaduta\n" + e.getLocalizedMessage()).showAndWait();
                    }
                    CliUtil.getInstance().buildStage(FXMLtype.CREACONSIGLIO, l);
                });
                libreria.setOnAction(evt -> {
                    Libro l = getTreeTableView().getTreeItem(getIndex()).getValue();
                    // va creata l'interfaccia per aggiungere un libro a una libreria
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : menu);
                setAlignment(Pos.CENTER);
            }
        });
    }

    protected void initSAddRemCol(){
        getSAddRemCol().setCellFactory(col -> new TreeTableCell<>() {
        });
    }

    protected void initTreeTableViewSearch(){
        getSTreeTableView().setRowFactory(tv -> {
            TreeTableRow<Libro> row = new TreeTableRow<>();
            row.setOnMouseClicked(evt -> {
                if (evt.getClickCount() == 2 && !row.isEmpty()) {
                    Libro sel = row.getItem();
                    CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, sel);
                }
            });
            return row;
        });
    }

    private void switchType(String key, String text) {
        getMenuTipoRicerca().getItems().setAll(getMenuCercaTitolo(), getMenuCercaAutore(), getMenuCercaAutoreAnno());
        getCampoRicercaAnno().setVisible(false);
        getCampoRicercaAnno().setDisable(true);
        setSearchType(key);
        getMenuTipoRicerca().setText(text);
        switch (key) {
            case "Titolo":
                getMenuTipoRicerca().getItems().remove(getMenuCercaTitolo());
                break;
            case "Autore":
                getMenuTipoRicerca().getItems().remove(getMenuCercaAutore());
                break;
            case "AutoreAnno":
                getMenuTipoRicerca().getItems().remove(getMenuCercaAutoreAnno());
                break;
        }
    }

    @FXML
    private void handleClickCerca() {
        String testo = getCampoRicerca().getText();
        String anno = getCampoRicercaAnno().getText();
        if (testo == null || testo.length() < 2) {
            getSTreeTableView().setRoot(new TreeItem<>());
            CliUtil.getInstance().createAlert("Errore", "Inserire almeno 2 caratteri per la ricerca.").showAndWait();
            return;
        }
        List<Libro> risultati;
        try {
            switch (getSearchType()) {
                case "Titolo": risultati = searchByTitle(testo); break;
                case "Autore": risultati = searchByAuthor(testo); break;
                case "AutoreAnno":
                    if (!validateYear(anno)) return;
                    risultati = searchByAuthorAndYear(testo, Integer.parseInt(anno));
                    break;
                default:
                    CliUtil.getInstance().createAlert("Errore", "Tipo di ricerca non selezionato.").showAndWait();
                    return;
            }
            TreeItem<Libro> root = new TreeItem<>();
            if (risultati != null && !risultati.isEmpty()) {
                for (Libro l : risultati) root.getChildren().add(new TreeItem<>(l));
            } else {
                CliUtil.getInstance().createAlert("Nessun risultato", "Nessun libro trovato.").showAndWait();
            }
            root.setExpanded(true);
            getSTreeTableView().setRoot(root);
        } catch (Exception e) {
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
        }
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


    private boolean validateYear(String anno) {
        if (anno == null || anno.trim().isEmpty() || !anno.matches("\\d{1,4}")) {
            CliUtil.getInstance().createAlert("Errore", "Inserire un anno valido (fino a 4 cifre).").showAndWait();
            return false;
        }
        return true;
    }
}
