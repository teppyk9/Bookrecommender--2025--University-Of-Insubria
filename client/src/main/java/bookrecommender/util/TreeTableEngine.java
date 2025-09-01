package bookrecommender.util;

import bookrecommender.model.Libro;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Componente astratto di supporto alla gestione di una {@link TreeTableView} che
 * visualizza strutture gerarchiche (es. librerie → libri) con caricamento lazy.
 * <p>
 * Responsabilità principali:
 * <ul>
 *   <li>Inizializzare l'albero (root nascosta, row factory con doppio click);</li>
 *   <li>Popolare i nodi di primo livello (librerie) e gestire l'espansione con
 *       caricamento dei figli su richiesta (placeholder + listener {@code branchExpanded});</li>
 *   <li>Esporre hook astratti per integrare la logica del dominio (conteggi, date di creazione,
 *       presenza in libreria, libro di riferimento, caricamento figli reali, handling del doppio click).</li>
 * </ul>
 * Le sottoclassi (controller JavaFX) forniscono le mappe/stati e implementano gli hook.
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
public abstract class TreeTableEngine {

    /** TreeTableView FXML a cui agganciare root, colonne e listener. */
    @FXML protected TreeTableView<Object> treeTableView;

    /** Radice logica dell'albero (non visibile). */
    protected TreeItem<Object> rootItem;

    /**
     * Inizializza la {@link TreeTableView} impostando:
     * <ul>
     *   <li>root invisibile ({@code setShowRoot(false)});</li>
     *   <li>row factory con gestione del doppio click che delega a {@link #handleDoubleClick(Object)};</li>
     *   <li>root vuota pronta a ricevere nodi di primo livello.</li>
     * </ul>
     * Va tipicamente chiamato nel metodo {@code initialize()} del controller concreto.
     */
    protected void initializeTree() {
        rootItem = new TreeItem<>();
        treeTableView.setRoot(rootItem);
        treeTableView.setShowRoot(false);
        treeTableView.setRowFactory(tv -> {
            TreeTableRow<Object> row = new TreeTableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    handleDoubleClick(row.getItem());
                }
            });
            return row;
        });
    }

    /**
     * Collega una colonna "semplice" della TreeTable alla mappa fornita.
     * <p>
     * La colonna è resa non ridimensionabile e allineata al centro.
     * La value factory:
     * <ul>
     *   <li>se il valore del nodo è una {@link String}, usa tale stringa come chiave per cercare il valore in {@code map};</li>
     *   <li>altrimenti restituisce {@code null} (nessun valore mostrato per i nodi non stringa).</li>
     * </ul>
     *
     * @param <T>    tipo del valore mostrato in colonna
     * @param column colonna da configurare (verrà impostata {@code resizable=false} e centrata)
     * @param map    mappa chiave→valore; la chiave è la stringa contenuta nel nodo
     */
    protected <T> void loadSimple(TreeTableColumn<Object, T> column, Map<String, T> map) {
        column.setResizable(false);
        column.setStyle("-fx-alignment: CENTER;");
        column.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String s) {
                return new ReadOnlyObjectWrapper<>(map.get(s));
            }
            return new ReadOnlyObjectWrapper<>(null);
        });
    }

    /**
     * Carica le librerie (nodi di primo livello) e inizializza per ciascuna:
     * <ul>
     *   <li>conteggio dei libri in {@link #getLibCounts()};</li>
     *   <li>data di creazione in {@link #getLibDates()};</li>
     *   <li>eventuale presenza di {@link #getMyLibro()} nella libreria in {@link #getLibPresent()} (se la mappa non è {@code null});</li>
     *   <li>nodo figlio placeholder per abilitare il caricamento lazy dei libri all'espansione.</li>
     * </ul>
     * In caso di errore delega alla gestione centralizzata ({@code CliUtil.getInstance().LogOut(e)}).
     * <p>
     * Nota: il caricamento dei libri effettivi avviene all'espansione del nodo,
     * tramite {@link #caricaFigliLibri(TreeItem, String)}.
     */
    protected void loadLibraries() {
        rootItem.getChildren().clear();
        getLibCounts().clear();
        getLibDates().clear();
        if(getLibPresent() != null) {
            getLibPresent().clear();
        }
        try {
            List<String> libs = CliUtil.getInstance().getLibService().getLibs(CliUtil.getInstance().getCurrentToken());
            for (String nome : libs) {
                List<Libro> list = CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nome);
                getLibCounts().put(nome, list.size());
                getLibDates().put(nome, CliUtil.getInstance().getLibService().getCreationDate(CliUtil.getInstance().getCurrentToken(), nome));
                if (getLibPresent() != null) {
                    getLibPresent().put(nome, list.contains(getMyLibro()));
                }
                creaFigliLibri(nome);
            }
        } catch (Exception e) {
            CliUtil.getInstance().LogOut(e);
        }
    }

    /**
     * Crea il nodo di libreria con caricamento lazy dei libri.
     * <p>
     * Viene aggiunto un figlio placeholder (nodo con {@code value == null}); al primo evento di
     * espansione ({@code branchExpanded}) il placeholder viene rimosso e si invoca
     * {@link #caricaFigliLibri(TreeItem, String)} per popolare i libri reali.
     *
     * @param nome nome della libreria (usato come {@code value} del {@link TreeItem})
     */
    protected void creaFigliLibri(String nome) {
        TreeItem<Object> libNode = new TreeItem<>(nome);
        libNode.getChildren().add(new TreeItem<>());
        libNode.addEventHandler(TreeItem.branchExpandedEvent(), e -> {
            TreeItem<Object> item = e.getTreeItem();
            if (item.getChildren().size() == 1 && item.getChildren().get(0).getValue() == null) {
                item.getChildren().clear();
                caricaFigliLibri(item, nome);
            }
        });
        rootItem.getChildren().add(libNode);
    }

    /**
     * Gestore del doppio click su una riga della TreeTable.
     * <p>
     * Le sottoclassi possono:
     * <ul>
     *   <li>aprire una vista di dettaglio del libro/libreria;</li>
     *   <li>attivare un'azione contestuale (es. modifica/elimina);</li>
     *   <li>ignorare l'evento per determinati tipi di nodo.</li>
     * </ul>
     *
     * @param value valore associato al nodo cliccato (può essere una {@link String} per le librerie o altro per i figli)
     */
    protected abstract void handleDoubleClick(Object value);

    /**
     * Mappa mutabile: nome libreria → numero di libri.
     *
     * @return mappa dei conteggi per libreria (non {@code null})
     */
    protected abstract Map<String, Integer> getLibCounts();

    /**
     * Mappa mutabile: nome libreria → data di creazione.
     *
     * @return mappa delle date di creazione per libreria (non {@code null})
     */
    protected abstract Map<String, LocalDate> getLibDates();

    /**
     * Mappa mutabile: nome libreria → {@code true} se il {@link #getMyLibro()} è presente in quella libreria.
     * <p>
     * Può essere {@code null} se la funzionalità non è richiesta nella vista concreta.
     *
     * @return mappa di presenza (o {@code null} per non utilizzare la funzionalità)
     */
    protected abstract Map<String, Boolean> getLibPresent();

    /**
     * Libro di riferimento usato per valutare la presenza nelle varie librerie.
     *
     * @return il libro di contesto (può essere {@code null} se non rilevante per la vista)
     */
    protected abstract Libro getMyLibro();

    /**
     * Carica i figli reali (libri) del nodo di libreria specificato.
     * <p>
     * Viene invocato al primo expand del nodo (lazy loading). La sottoclasse deve
     * aggiungere a {@code libNode.getChildren()} i {@link TreeItem} dei libri.
     *
     * @param libNode nodo libreria da popolare
     * @param nomeLib nome della libreria a cui appartengono i figli
     */
    protected abstract void caricaFigliLibri(TreeItem<Object> libNode, String nomeLib);

}
