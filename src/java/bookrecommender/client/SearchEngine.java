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
    protected abstract TreeTableColumn<Libro, Void> getOActionCol();

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
            private final MenuButton menu = new MenuButton("", new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png") ), 16, 16, true, true)));
            {
                MenuItem aggiungi = new MenuItem("Aggiungi");
                MenuItem rimuovi = new MenuItem("Rimuovi");
                menu.getItems().addAll(aggiungi, rimuovi);

                aggiungi.setOnAction(evt -> {
                    Libro l = getTreeTableView().getTreeItem(getIndex()).getValue();
                    if(!containsLibro(getOTreeTableView().getRoot(),l)) { //errore sulla rimozione
                        if (getOTreeTableView().getRoot() == null) {
                            TreeItem<Libro> rootItem = new TreeItem<>();
                            getOTreeTableView().setRoot(rootItem);
                        }
                        getOTreeTableView().getRoot().getChildren().add(new TreeItem<>(l));
                    }
                });
                rimuovi.setOnAction(evt -> {
                    Libro l = getTreeTableView().getTreeItem(getIndex()).getValue();
                    if(containsLibro(getOTreeTableView().getRoot(), l))
                        getOTreeTableView().getRoot().getChildren().removeIf(item -> item.getValue().equals(l));
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

    /**
     * Inizializza la colonna di azione (OActionCol) impostandone il CellFactory in base al tipo di menu desiderato.
     * <ul>
     *   <li>Se <code>type</code> è <code>true</code>, ogni cella conterrà un <code>MenuButton</code>
     *       con le voci:
     *       <ul>
     *         <li><strong>Valuta</strong>: apre la finestra per inserire una valutazione sul libro corrente;</li>
     *         <li><strong>Crea Consiglio</strong>: apre la finestra per creare un consiglio sul libro corrente;</li>
     *         <li><strong>Rimuovi</strong>: rimuove il libro corrente dal <code>TreeTableView</code>.</li>
     *       </ul>
     *       L'icona mostrata è una freccia rivolta verso il basso.</li>
     *   <li>Se <code>type</code> è <code>false</code>, ogni cella avrà solo la voce:
     *       <ul>
     *         <li><strong>Rimuovi</strong>: rimuove il libro corrente dal <code>TreeTableView</code>.</li>
     *       </ul>
     *       L'icona mostrata è un cerchio rosso con meno.</li>
     * </ul>
     * In entrambi i casi, l'aggiornamento della grafica della cella avviene nel metodo
     * <code>updateItem(Void, boolean)</code>, che imposta allineamento e graphic a seconda che la cella sia vuota.
     *
     * @param type se <code>true</code> viene creato un menu completo (valuta, consiglia, rimuovi);
     *             se <code>false</code> viene creato un menu minimale (solo rimuovi)
     */
    protected void initOActionCol(boolean type){
        if(type){
            getOActionCol().setCellFactory(col -> new TreeTableCell<>() {
                private final MenuButton menu = new MenuButton("", new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png") ), 16, 16, true, true)));
                {
                    MenuItem valuta = new MenuItem("Valuta");
                    MenuItem consiglia = new MenuItem("Crea Consiglio");
                    MenuItem rimuovi = new MenuItem("Rimuovi");
                    menu.getItems().addAll(valuta, consiglia, rimuovi);

                    valuta.setOnAction(evt -> {
                        Libro l = getTreeTableView().getTreeItem(getIndex()).getValue();
                        CliUtil.getInstance().buildStage(FXMLtype.CREAVALUTAZIONE, l);
                    });

                    consiglia.setOnAction(evt -> {
                        Libro l = getTreeTableView().getTreeItem(getIndex()).getValue();
                        CliUtil.getInstance().buildStage(FXMLtype.CREACONSIGLIO, l);
                    });

                    rimuovi.setOnAction(evt -> {
                        Libro l = getTreeTableView().getTreeItem(getIndex()).getValue();
                        if(containsLibro(getOTreeTableView().getRoot(), l))
                            getOTreeTableView().getRoot().getChildren().removeIf(item -> item.getValue().equals(l));
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : menu);
                    setAlignment(Pos.CENTER);
                }
            });
        } else {
            getOActionCol().setCellFactory(col -> new TreeTableCell<>() {
                private final MenuButton menu = new MenuButton("", new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/minus-circle-red.png") ), 16, 16, true, true)));
                {
                    MenuItem rimuovi = new MenuItem("Rimuovi");
                    menu.getItems().add(rimuovi);

                    rimuovi.setOnAction(evt -> {
                        Libro l = getTreeTableView().getTreeItem(getIndex()).getValue();
                        if(containsLibro(getOTreeTableView().getRoot(), l))
                            getOTreeTableView().getRoot().getChildren().removeIf(item -> item.getValue().equals(l));
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
    }

    protected void initOTableView() {
        getOTreeTableView().setShowRoot(false);
        getOTitoloCol().setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getValue().getTitolo()));
        getOAutoreCol().setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getValue().getAutore()));
        getOAnnoCol().setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>((int) cellData.getValue().getValue().getAnnoPubblicazione()));
    }

    protected void initTreeTableViews(){
        getSTreeTableView().setRowFactory(tv -> initTables());
        if(getOTreeTableView() != null){
            getOTreeTableView().setRowFactory(tv -> initTables());
        }
    }

    private TreeTableRow<Libro> initTables(){
        TreeTableRow<Libro> row = new TreeTableRow<>();
        row.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2 && !row.isEmpty()) {
                Libro sel = row.getItem();
                CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, sel);
            }
        });
        return row;
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

    private boolean containsLibro(TreeItem<Libro> node, Libro target) {
        if (node == null || target == null) {
            return false;
        }
        if (node.getValue().equals(target)) {
            return true; //errore sulla rimozione
        }
        for (TreeItem<Libro> child : node.getChildren()) {
            if (containsLibro(child, target)) {
                return true;
            }
        }
        return false;
    }
}
