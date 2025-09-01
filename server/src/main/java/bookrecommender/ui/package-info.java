/**
 * Controller JavaFX e componenti UI della console server.
 *
 * <p>Le principali classi sono:</p>
 * <ul>
 *   <li>{@link bookrecommender.ui.ServerControl} — pannello di controllo/log della console;</li>
 *   <li>{@link bookrecommender.ui.ServerConfig} — pannello di configurazione
 *       (database, porta RMI, avvio/arresto del servizio);</li>
 *   <li>{@link bookrecommender.ui.TextFlowHandler} — {@link java.util.logging.Handler}
 *       che indirizza i log verso un {@link javafx.scene.text.TextFlow} per la visualizzazione in tempo reale.</li>
 * </ul>
 *
 * <p>I controller collaborano con {@link bookrecommender.util.ServerUtil} per il
 * caricamento FXML, la gestione degli {@link javafx.stage.Stage} e
 * le operazioni di servizio.</p>
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
package bookrecommender.ui;