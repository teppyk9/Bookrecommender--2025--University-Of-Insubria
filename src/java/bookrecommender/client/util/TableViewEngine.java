package bookrecommender.client.util;

import bookrecommender.client.enums.FXMLtype;
import bookrecommender.client.enums.IMGtype;
import bookrecommender.common.model.Libro;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.rmi.RemoteException;
import java.util.*;

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
    protected abstract TableColumn<Libro, Boolean> getSRecensioniCol();

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

    protected abstract FXMLtype getMyFXMLtype();

    protected abstract ProgressIndicator getProgressIndicator();

    protected abstract MenuButton getLimiterBox();

    private String searchType = "";
    private final Map<Libro, Boolean> hasRec = new HashMap<>();
    private final Map<Libro, Boolean> hasVal = new HashMap<>();
    private final Map<Libro, Boolean> hasCon = new HashMap<>();
    private final Map<Libro, Boolean> inLib = new HashMap<>();

    protected void initBasicSearch() {
        getCampoRicercaAnno().setVisible(false);
        getCampoRicercaAnno().setDisable(true);
        getMenuTipoRicerca().setGraphic(IMGtype.ARROW_DOWN.getImageView(12,12));
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
        getSRecensioniCol().setCellValueFactory(cellData ->
                new ReadOnlyBooleanWrapper(hasRec.get(cellData.getValue())));

        getSRecensioniCol().setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(item ? IMGtype.CHECK.getImageView(12,12) : IMGtype.RED_CROSS.getImageView(12,12));
                    setAlignment(Pos.CENTER);
                }
            }
        });
    }

    protected void initSAggiungiAdvCol(){
        getSAggiungiAdvCol().setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                MenuButton menu = menuAzioni(getTableView(), getIndex());
                menu.getItems().removeIf(menuItem -> menuItem.getText().equals("Rimuovi"));
                setGraphic(menu);
                setAlignment(Pos.CENTER);
            }
        });
    }

    protected void initSAddRemCol(){
        getSAddRemCol().setCellFactory(col -> new TableCell<>() {
            private final MenuButton menu = new MenuButton();
            {
                menu.setGraphic(IMGtype.ARROW_DOWN.getImageView(12,12));
                CliUtil.getInstance().styleIconControl(menu);
                MenuItem aggiungi = new MenuItem("Aggiungi");
                MenuItem rimuovi = new MenuItem("Rimuovi");
                menu.getItems().addAll(aggiungi, rimuovi);
                menu.skinProperty().addListener((obs, oldSkin, newSkin) -> {
                    if (newSkin != null) {
                        Node arrow = menu.lookup(".arrow");
                        if (arrow != null) {
                            arrow.setVisible(false);
                            arrow.setManaged(false);
                        }
                    }
                });

                aggiungi.setOnAction(evt -> {
                    Libro l = getTableView().getItems().get(getIndex());
                    TableView<Libro> target = getOTableView();
                    ObservableList<Libro> items = target.getItems();
                    if (items == null) {
                        items = FXCollections.observableArrayList();
                        target.setItems(items);
                    }
                    if (!containsLibro(items, l)) {
                        inLib.put(l, true);
                        try {
                            hasRec.put(l, CliUtil.getInstance().getSearchService().hasValRec(l));
                            if( hasRec.get(l)) {
                                hasVal.put(l, CliUtil.getInstance().getLibService().existVal(CliUtil.getInstance().getCurrentToken(), l));
                                hasCon.put(l, CliUtil.getInstance().getLibService().existCon(CliUtil.getInstance().getCurrentToken(), l));
                            } else {
                                hasVal.put(l, false);
                                hasCon.put(l, false);
                            }
                        } catch (RemoteException e) {
                            CliUtil.getInstance().LogOut(e);
                        }
                        items.add(l);
                    }
                });
                rimuovi.setOnAction(evt -> {
                    Libro l = getTableView().getItems().get(getIndex());
                    TableView<Libro> target = getOTableView();
                    ObservableList<Libro> items = target.getItems();
                    if (containsLibro(items, l)) {
                        items.removeIf(item -> item.equals(l));
                        inLib.remove(l);
                        hasVal.remove(l);
                        hasCon.remove(l);
                        hasRec.remove(l);
                    }
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
     */
    protected void initOActionCol(){
        getOActionCol().setCellFactory(col -> new TableCell<>() {
            private final Button rimuovi = new Button();
            {
                rimuovi.setGraphic(IMGtype.RIMUOVI.getImageView(12,12));
                CliUtil.getInstance().styleIconControl(rimuovi);
                rimuovi.setOnAction(evt -> {
                    Libro l = getTableView().getItems().get(getIndex());
                    ObservableList<Libro> items = getOTableView().getItems();
                    if (containsLibro(items, l)) {
                        items.removeIf(item -> item.equals(l));
                        inLib.remove(l);
                        hasVal.remove(l);
                        hasCon.remove(l);
                        hasRec.remove(l);
                    }
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

    protected void initForConsigli(){
        initBasicSearch();
        initSAddRemCol();
        initOActionCol();
        initOTableView();
        initTableViews();
    }

    protected void initLimiter(){
        List<String> options = List.of("200", "500", "1000", "2000", "No Limit");
        for (String label : options) {
            MenuItem item = new MenuItem(label);
            item.setOnAction(e -> getLimiterBox().setText(label));
            getLimiterBox().getItems().add(item);
        }
        getLimiterBox().setGraphic(IMGtype.ARROW_DOWN.getImageView(12,12));
        getLimiterBox().skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Node arrow = getLimiterBox().lookup(".arrow");
                if (arrow != null) {
                    arrow.setVisible(false);
                    arrow.setManaged(false);
                }
            }
        });
    }

    private int getMaxResults() {
        if (getLimiterBox() == null) {
            return 200;
        }

        if (getLimiterBox().getText() == null || getLimiterBox().getText().equals("No Limit")) {
            return Integer.MAX_VALUE;
        }

        try {
            return Integer.parseInt(getLimiterBox().getText());
        } catch (NumberFormatException e) {
            return 200;
        }
    }

    private TableRow<Libro> initRows(){
        TableRow<Libro> row = new TableRow<>();
        row.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2 && !row.isEmpty()) {
                CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO, null, row.getItem());
            }
        });
        return row;
    }

    private MenuButton menuAzioni(TableView<Libro> tableView, int idx) {
        MenuButton menu = new MenuButton();
        menu.setGraphic(IMGtype.ARROW_DOWN.getImageView(12,12));
        CliUtil.getInstance().styleIconControl(menu);
        MenuItem libreria = new MenuItem("Aggiungi ad una libreria");
        libreria.setOnAction(evt -> {
            Libro l = tableView.getItems().get(idx);
            if(l != null)
                CliUtil.getInstance().buildStage(FXMLtype.AGGIUNGILIBROLIBRERIA,getMyFXMLtype(), l);
        });
        if (inLib.get(tableView.getItems().get(idx))) {
            if( hasVal.get(tableView.getItems().get(idx))) {
                MenuItem modValuta = getMenuItem(tableView, idx);
                menu.getItems().add(modValuta);
            } else {
                MenuItem valuta = new MenuItem("Valuta");
                valuta.setOnAction(evt -> CliUtil.getInstance().buildStage(FXMLtype.CREAVALUTAZIONE, getMyFXMLtype(), tableView.getItems().get(idx)));
                menu.getItems().add(valuta);
            }
            if( hasCon.get(tableView.getItems().get(idx))) {
                MenuItem modCons = new MenuItem("Modifica Consigli");
                modCons.setOnAction(evt -> CliUtil.getInstance().buildStage(FXMLtype.MODIFICACONSIGLIO, getMyFXMLtype(), tableView.getItems().get(idx)));
                menu.getItems().add(modCons);
            } else {
                MenuItem consiglia = new MenuItem("Aggiungi Consigli");
                consiglia.setOnAction(evt -> CliUtil.getInstance().buildStage(FXMLtype.CREACONSIGLIO, getMyFXMLtype(), tableView.getItems().get(idx)));
                menu.getItems().add(consiglia);
            }
        }
        menu.getItems().add(libreria);
        menu.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Node arrow = menu.lookup(".arrow");
                if (arrow != null) {
                    arrow.setVisible(false);
                    arrow.setManaged(false);
                }
            }
        });
        return menu;
    }

    private MenuItem getMenuItem(TableView<Libro> tableView, int idx) {
        MenuItem modValuta = new MenuItem("Modifica Valutazione");
        modValuta.setOnAction(evt -> {
            try {
                CliUtil.getInstance().buildStage(FXMLtype.MODIFICAVALUTAZIONE, getMyFXMLtype(), CliUtil.getInstance().getLibService().getValutazione(CliUtil.getInstance().getCurrentToken(), tableView.getItems().get(idx)));
            } catch (RemoteException e) {
                CliUtil.getInstance().LogOut(e);
            }
        });
        return modValuta;
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

        getProgressIndicator().setProgress(-1);
        getProgressIndicator().setVisible(true);

        Task<List<Libro>> searchTask = new Task<>() {
            @Override
            protected List<Libro> call() throws Exception {
                List<Libro> risultati;
                switch (searchType) {
                    case "Titolo":
                        risultati = searchByTitle(testo);
                        break;
                    case "Autore":
                        risultati = searchByAuthor(testo);
                        break;
                    case "AutoreAnno":
                        if (!validateYear(anno)) {
                            return Collections.emptyList();
                        }
                        risultati = searchByAuthorAndYear(testo, Integer.parseInt(anno));
                        break;
                    default:
                        throw new IllegalStateException("Tipo di ricerca non selezionato.");
                }
                if(getMyFXMLtype() == FXMLtype.CERCA || getMyFXMLtype() == FXMLtype.CERCA_AVANZATO || getMyFXMLtype() == FXMLtype.GESTIONELIBRERIE) {
                    hasRec.clear();
                    assert risultati != null;
                    for (Libro l : risultati) {
                        hasRec.put(l, CliUtil.getInstance().getSearchService().hasValRec(l));
                    }
                }
                return risultati;
            }
        };

        searchTask.setOnSucceeded(evt -> {
            List<Libro> risultati = searchTask.getValue();
            ObservableList<Libro> data = FXCollections.observableArrayList(risultati);
            if(getMyFXMLtype() == FXMLtype.CERCA_AVANZATO || getMyFXMLtype() == FXMLtype.GESTIONELIBRERIE) {
                setLibriP(risultati);
            }
            getSTableView().setItems(data);
            getProgressIndicator().setVisible(false);
        });

        searchTask.setOnFailed(evt -> {
            Throwable ex = searchTask.getException();
            CliUtil.getInstance().LogOut((Exception) ex);
            getProgressIndicator().setVisible(false);
        });

        Thread thread = new Thread(searchTask);
        thread.setDaemon(true);
        thread.start();
    }


    private void setLibriP(List<Libro> libri){
        if(CliUtil.getInstance().getCurrentToken() != null){
            hasVal.clear();
            hasCon.clear();
            inLib.clear();
            for(Libro l : libri){
                try {
                    inLib.put(l, CliUtil.getInstance().getLibService().isLibPresent(CliUtil.getInstance().getCurrentToken(), l));
                    if(inLib.get(l) && hasRec.get(l)) {
                        hasVal.put(l, CliUtil.getInstance().getLibService().existVal(CliUtil.getInstance().getCurrentToken(), l));
                        hasCon.put(l, CliUtil.getInstance().getLibService().existCon(CliUtil.getInstance().getCurrentToken(), l));
                    }else{
                        hasVal.put(l, false);
                        hasCon.put(l, false);
                    }
                } catch (RemoteException e) {
                    CliUtil.getInstance().LogOut(e);
                }
            }
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
            CliUtil.getInstance().LogOut(e);
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
                    : CliUtil.getInstance().getSearchService().searchByName(testo, getMaxResults());
        } catch (RemoteException e) {
            CliUtil.getInstance().LogOut(e);
            return null;
        }
    }

    private List<Libro> searchByAuthor(String testo){
        try {
            return getSearchType()
                    ? CliUtil.getInstance().getSearchService().searchByAuthor(CliUtil.getInstance().getCurrentToken(), testo)
                    : CliUtil.getInstance().getSearchService().searchByAuthor(testo, getMaxResults());
        } catch (RemoteException e) {
            CliUtil.getInstance().LogOut(e);
            return null;
        }
    }

    private List<Libro> searchByAuthorAndYear(String testo, int anno){
        try {
            return getSearchType()
                    ? CliUtil.getInstance().getSearchService().searchByAuthorAndYear(CliUtil.getInstance().getCurrentToken(), testo, anno)
                    : CliUtil.getInstance().getSearchService().searchByAuthorAndYear(testo, anno, getMaxResults());
        } catch (RemoteException e) {
            CliUtil.getInstance().LogOut(e);
            return null;
        }
    }

    protected Map<Libro, Boolean> getHasRec() {
        return this.hasRec;
    }

    protected Map<Libro, Boolean> getHasVal() {
        return this.hasVal;
    }

    protected Map<Libro, Boolean> getHasCon() {
        return this.hasCon;
    }

    protected Map<Libro, Boolean> getInLib() {
        return this.inLib;
    }

}
