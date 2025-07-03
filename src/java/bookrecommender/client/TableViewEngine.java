package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Objects;

public abstract class TableViewEngine {
    /**
     * Restituisce il campo di input per il testo della ricerca.
     *
     * @return il TextField usato per inserire il testo di ricerca
     */
    protected abstract TextField getCampoRicerca();

    /**
     * Restituisce il campo di input per l’anno della ricerca.
     *
     * @return il TextField usato per inserire l’anno di ricerca
     */
    protected abstract TextField getCampoRicercaAnno();

    /**
     * Restituisce il MenuButton per selezionare il tipo di ricerca.
     *
     * @return il MenuButton che mostra le opzioni di ricerca
     */
    protected abstract MenuButton getMenuTipoRicerca();

    /**
     * Restituisce il MenuItem per avviare la ricerca per titolo.
     *
     * @return il MenuItem associato alla ricerca per titolo
     */
    protected abstract MenuItem getMenuCercaTitolo();

    /**
     * Restituisce il MenuItem per avviare la ricerca per autore.
     *
     * @return il MenuItem associato alla ricerca per autore
     */
    protected abstract MenuItem getMenuCercaAutore();

    /**
     * Restituisce il MenuItem per avviare la ricerca per autore e anno.
     *
     * @return il MenuItem associato alla ricerca per autore e anno
     */
    protected abstract MenuItem getMenuCercaAutoreAnno();

    /**
     * Restituisce la TableView contenente i risultati di ricerca primari.
     *
     * @return la TableView dei libri trovati
     */
    protected abstract TableView<Libro> getSTableView();

    /**
     * Restituisce la colonna {@code Titolo} nella TableView dei risultati.
     *
     * @return la TableColumn per il titolo del libro
     */
    protected abstract TableColumn<Libro, String> getSTitoloCol();

    /**
     * Restituisce la colonna {@code Autore} nella TableView dei risultati.
     *
     * @return la TableColumn per l’autore del libro
     */
    protected abstract TableColumn<Libro, String> getSAutoreCol();

    /**
     * Restituisce la colonna {@code Anno} nella TableView dei risultati.
     *
     * @return la TableColumn per l’anno di pubblicazione
     */
    protected abstract TableColumn<Libro, Integer> getSAnnoCol();

    /**
     * Restituisce la colonna delle recensioni nella TableView dei risultati.
     *
     * @return la TableColumn che mostra lo stato delle recensioni
     */
    protected abstract TableColumn<Libro, Void> getSRecensioniCol();

    /**
     * Restituisce la colonna dei comandi di aggiunta/avviso nella TableView.
     *
     * @return la TableColumn con i pulsanti di azione avanzata
     */
    protected abstract TableColumn<Libro, Void> getSAggiungiAdvCol();

    /**
     * Restituisce la colonna dei comandi di nella TableView.
     *
     * @return la TableColumn con le opzioni associate ai libri
     */
    protected abstract TableColumn<Libro, Void> getSAddRemCol();

    /**
     * Restituisce la TableView secondaria.
     *
     * @return la TableView secondaria
     */
    protected abstract TableView<Libro> getOTableView();

    /**
     * Restituisce la colonna {@code Titolo} nella TableView secondaria.
     *
     * @return la TableColumn per il titolo nella TableView secondaria
     */
    protected abstract TableColumn<Libro, String> getOTitoloCol();

    /**
     * Restituisce la colonna {@code Autore} nella TableView secondaria.
     *
     * @return la TableColumn per l’autore nella TableView secondaria
     */
    protected abstract TableColumn<Libro, String> getOAutoreCol();

    /**
     * Restituisce la colonna {@code Anno} nella TableView secondaria.
     *
     * @return la TableColumn per l’anno nella TableView secondaria
     */
    protected abstract TableColumn<Libro, Integer> getOAnnoCol();

    /**
     * Restituisce la colonna di azioni nella TableView secondaria.
     *
     * @return la TableColumn con i comandi aggiuntivi
     */
    protected abstract TableColumn<Libro, Void> getOActionCol();

    /**
     * Determina l'ambito delle ricerche in base al token dell'utente.
     *
     * @return {@code true} se la ricerca utilizza il token associato all'utente
     *         per interrogare solo le sue librerie;
     *         {@code false} se la ricerca viene effettuata sull'intero database.
     */
    protected abstract boolean getSearchType();

    /**
     * Campo che rappresenta il tipo di ricerca selezionato.
     * <p>
     * Può assumere i valori {@code Titolo}, {@code Autore} o {@code AutoreAnno}.
     */

    protected abstract Libro getMyLibro();

    private String searchType = "";

    protected void initBasicSearch() {
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
                new ReadOnlyStringWrapper(cellData.getValue().getTitolo())
        );
        getSAutoreCol().setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getAutore())
        );
        getSAnnoCol().setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>((int) cellData.getValue().getAnnoPubblicazione())
        );
    }

    protected void initSRecensioniCol(){
        getSRecensioniCol().setCellFactory(col -> new TableCell<>() {
            private final ImageView check = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/check-green.png")), 16, 16, true, true));
            private final ImageView noCheck = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/alert_icon.png")), 16, 16, true, true));
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Libro l = getTableView().getItems().get(getIndex());
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
        getSAggiungiAdvCol().setCellFactory(col -> new TableCell<>() {
            private final MenuButton menu = new MenuButton();
            {
                menu.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png")), 14, 14, true, true)));
                menu.setStyle("-fx-background-color: transparent;"
                        + "-fx-border-color: transparent;"
                        + "-fx-padding: 0;"
                        + "-fx-cursor: hand;"
                        + "-fx-focus-color: transparent;"
                        + "-fx-faint-focus-color: transparent;");
                MenuItem recensisci = new MenuItem("Aggiungi Recensione");
                MenuItem consigli = new MenuItem("Aggiungi Consigli");
                MenuItem libreria = new MenuItem("Aggiungi ad una libreria");
                menu.getItems().addAll(recensisci, consigli, libreria);

                recensisci.setOnAction(evt -> {
                    Libro l = getTableView().getItems().get(getIndex());
                    try {
                        if (CliUtil.getInstance().getLibService().isLibPresent(CliUtil.getInstance().getCurrentToken(), l))
                            CliUtil.getInstance().buildStage(FXMLtype.CREAVALUTAZIONE, l);
                        else
                            CliUtil.getInstance().createAlert("Errore", "Il libro non è presente in nessuna libreria.").showAndWait();
                    } catch (RemoteException e) {
                        CliUtil.getInstance().createAlert("Errore", "Connessione all'interfaccia scaduta\n" + e.getLocalizedMessage()).showAndWait();
                    }
                });
                consigli.setOnAction(evt -> {
                    Libro l = getTableView().getItems().get(getIndex());
                    try {
                        if (CliUtil.getInstance().getLibService().isLibPresent(CliUtil.getInstance().getCurrentToken(), l))
                            CliUtil.getInstance().buildStage(FXMLtype.CREACONSIGLIO, l);
                        else
                            CliUtil.getInstance().createAlert("Errore", "Il libro non è presente in nessuna libreria.").showAndWait();
                    } catch (RemoteException e) {
                        CliUtil.getInstance().createAlert("Errore", "Connessione all'interfaccia scaduta\n" + e.getLocalizedMessage()).showAndWait();
                    }
                });
                libreria.setOnAction(evt -> {
                    Libro l = getTableView().getItems().get(getIndex());
                    if(l != null)
                        CliUtil.getInstance().buildStage(FXMLtype.AGGIUNGILIBROLIBRERIA,l);
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
        getSAddRemCol().setCellFactory(col -> new TableCell<>() {
            private final MenuButton menu = new MenuButton();
            {
                menu.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png")), 14, 14, true, true)));
                menu.setStyle("-fx-background-color: transparent;"
                        + "-fx-border-color: transparent;"
                        + "-fx-padding: 0;"
                        + "-fx-cursor: hand;"
                        + "-fx-focus-color: transparent;"
                        + "-fx-faint-focus-color: transparent;");
                MenuItem aggiungi = new MenuItem("Aggiungi");
                MenuItem rimuovi = new MenuItem("Rimuovi");
                menu.getItems().addAll(aggiungi, rimuovi);

                aggiungi.setOnAction(evt -> {
                    Libro l = getTableView().getItems().get(getIndex());
                    TableView<Libro> target = getOTableView();
                    ObservableList<Libro> items = target.getItems();
                    if (items == null) {
                        items = FXCollections.observableArrayList();
                        target.setItems(items);
                    }
                    if (!containsLibro(items, l)) items.add(l);
                });
                rimuovi.setOnAction(evt -> {
                    Libro l = getTableView().getItems().get(getIndex());
                    TableView<Libro> target = getOTableView();
                    ObservableList<Libro> items = target.getItems();
                    if (containsLibro(items, l)) items.removeIf(item -> item.equals(l));
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
     *         <li><strong>Rimuovi</strong>: rimuove il libro corrente dal <code>TableView</code>.</li>
     *       </ul>
     *       L'icona mostrata è una freccia rivolta verso il basso.</li>
     *   <li>Se <code>type</code> è <code>false</code>, ogni cella avrà solo la voce:
     *       <ul>
     *         <li><strong>Rimuovi</strong>: rimuove il libro corrente dal <code>TableView</code>.</li>
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
            getOActionCol().setCellFactory(col -> new TableCell<>() {
                private final MenuButton menu = new MenuButton();
                {
                    menu.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png")), 14, 14, true, true)));
                    menu.setStyle("-fx-background-color: transparent;"
                            + "-fx-border-color: transparent;"
                            + "-fx-padding: 0;"
                            + "-fx-cursor: hand;"
                            + "-fx-focus-color: transparent;"
                            + "-fx-faint-focus-color: transparent;");
                    MenuItem valuta = new MenuItem("Valuta");
                    MenuItem consiglia = new MenuItem("Aggiungi Consigli");
                    MenuItem rimuovi = new MenuItem("Rimuovi");
                    MenuItem modValuta = new MenuItem("Modifica Valutazione");
                    MenuItem modCons = new MenuItem("Modifica Consigli");

                    valuta.setOnAction(evt -> CliUtil.getInstance().buildStage(FXMLtype.CREAVALUTAZIONE, getTableView().getItems().get(getIndex())));
                    consiglia.setOnAction(evt -> CliUtil.getInstance().buildStage(FXMLtype.CREACONSIGLIO, getTableView().getItems().get(getIndex())));
                    rimuovi.setOnAction(evt -> {
                        Libro l = getTableView().getItems().get(getIndex());
                        ObservableList<Libro> items = getOTableView().getItems();
                        if (containsLibro(items, l)) items.removeIf(item -> item.equals(l));
                    });
                    modValuta.setOnAction(evt -> {
                        try {
                            CliUtil.getInstance().buildStage(FXMLtype.MODIFICAVALUTAZIONE, CliUtil.getInstance().getLibService().getValutazione(CliUtil.getInstance().getCurrentToken(), getTableView().getItems().get(getIndex())));
                        } catch (RemoteException e) {
                            CliUtil.getInstance().createAlert("Errore", "Connessione all'interfaccia scaduta\n" + e.getLocalizedMessage()).showAndWait();
                        }
                    });
                    modCons.setOnAction(evt -> CliUtil.getInstance().buildStage(FXMLtype.MODIFICACONSIGLIO, getTableView().getItems().get(getIndex())));
                    menu.getItems().addAll(valuta, modValuta, consiglia, modCons, rimuovi);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setGraphic(null);
                        return;
                    }
                    boolean hasVal = false;
                    boolean hasCon = false;
                    try {
                        hasVal = CliUtil.getInstance().getLibService().existVal(CliUtil.getInstance().getCurrentToken(), getTableView().getItems().get(getIndex()));
                        hasCon = CliUtil.getInstance().getLibService().existCon(CliUtil.getInstance().getCurrentToken(), getTableView().getItems().get(getIndex()));
                    } catch (RemoteException e) {
                        CliUtil.getInstance().createAlert("Errore", "Connessione all'interfaccia scaduta\n" + e.getLocalizedMessage()).showAndWait();
                    }
                    if (hasVal) {
                        menu.getItems().removeIf(menuItem ->  menuItem.getText().equals("Valuta"));
                    } else {
                        menu.getItems().removeIf(menuItem ->  menuItem.getText().equals("Modifica Valutazione"));
                    }
                    if (hasCon) {
                        menu.getItems().removeIf(menuItem ->  menuItem.getText().equals("Aggiungi Consigli"));
                    } else {
                        menu.getItems().removeIf(menuItem ->  menuItem.getText().equals("Modifica Consigli"));
                    }
                    setGraphic(menu);
                    setAlignment(Pos.CENTER);
                }
            });
        } else {
            getOActionCol().setCellFactory(col -> new TableCell<>() {
                private final Button rimuovi = new Button();
                {
                    rimuovi.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/minus-circle-red.png")), 16, 16, true, true)));
                    rimuovi.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: hand;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
                    );
                    ScaleTransition enlarge = new ScaleTransition(Duration.millis(100), rimuovi);
                    enlarge.setToX(1.1);
                    enlarge.setToY(1.1);

                    ScaleTransition shrink = new ScaleTransition(Duration.millis(100), rimuovi);
                    shrink.setToX(1.0);
                    shrink.setToY(1.0);

                    rimuovi.setOnMouseEntered(e -> {
                        shrink.stop();
                        enlarge.playFromStart();
                    });

                    rimuovi.setOnMouseExited(e -> {
                        enlarge.stop();
                        shrink.playFromStart();
                    });
                    rimuovi.setOnAction(evt -> {
                        Libro l = getTableView().getItems().get(getIndex());
                        ObservableList<Libro> items = getOTableView().getItems();
                        if (containsLibro(items, l)) items.removeIf(item -> item.equals(l));
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : rimuovi);
                    setAlignment(Pos.CENTER);
                }
            });
        }
    }

    protected void initOTableView() {
        getOTitoloCol().setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getTitolo())
        );
        getOAutoreCol().setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getAutore())
        );
        getOAnnoCol().setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>((int) cellData.getValue().getAnnoPubblicazione())
        );
    }

    protected void initTableViews(){
        getSTableView().setRowFactory(tv -> initRows());
        if(getOTableView() != null) getOTableView().setRowFactory(tv -> initRows());
    }

    private TableRow<Libro> initRows(){
        TableRow<Libro> row = new TableRow<>();
        row.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2 && !row.isEmpty()) {
                CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, row.getItem());
            }
        });
        return row;
    }

    private void switchType(String key, String text) {
        getMenuTipoRicerca().getItems().setAll(getMenuCercaTitolo(), getMenuCercaAutore(), getMenuCercaAutoreAnno());
        getCampoRicercaAnno().setVisible(false);
        getCampoRicercaAnno().setDisable(true);
        searchType = key;
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
            getSTableView().setItems(FXCollections.observableArrayList());
            CliUtil.getInstance().createAlert("Errore", "Inserire almeno 2 caratteri per la ricerca.").showAndWait();
            return;
        }
        List<Libro> risultati;
        try {
            switch (searchType) {
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
            ObservableList<Libro> data = FXCollections.observableArrayList(risultati);
            getSTableView().setItems(data);
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

    @FXML
    private void getAllBooks() {
        try {
            List<Libro> libri = CliUtil.getInstance().getSearchService().getAllBooks(CliUtil.getInstance().getCurrentToken());
            libri.remove(getMyLibro());
            getSTableView().setItems(FXCollections.observableArrayList(libri));
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore", "Impossibile caricare i libri").showAndWait();
        }
    }

    private boolean validateYear(String anno) {
        if (anno == null || anno.trim().isEmpty() || !anno.matches("\\d{1,4}")) {
            CliUtil.getInstance().createAlert("Errore", "Inserire un anno valido (fino a 4 cifre).").showAndWait();
            return false;
        }
        return true;
    }

    private boolean containsLibro(List<Libro> list, Libro target) {
        if (list == null || target == null) return false;
        return list.stream().anyMatch(item -> Objects.equals(item, target));
    }

    private List<Libro> searchByTitle(String testo){
        try {
            return getSearchType()
                    ? CliUtil.getInstance().getSearchService().searchByName(CliUtil.getInstance().getCurrentToken(), testo)
                    : CliUtil.getInstance().getSearchService().searchByName(testo);
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    private List<Libro> searchByAuthor(String testo){
        try {
            return getSearchType()
                    ? CliUtil.getInstance().getSearchService().searchByAuthor(CliUtil.getInstance().getCurrentToken(), testo)
                    : CliUtil.getInstance().getSearchService().searchByAuthor(testo);
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }

    private List<Libro> searchByAuthorAndYear(String testo, int anno){
        try {
            return getSearchType()
                    ? CliUtil.getInstance().getSearchService().searchByAuthorAndYear(CliUtil.getInstance().getCurrentToken(), testo, anno)
                    : CliUtil.getInstance().getSearchService().searchByAuthorAndYear(testo, anno);
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore durante la ricerca", e.getMessage()).showAndWait();
            return null;
        }
    }
}
