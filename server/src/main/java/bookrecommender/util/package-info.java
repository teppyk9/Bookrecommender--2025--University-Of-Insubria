/**
 * Utility e servizi di orchestrazione del server <em>BookRecommender</em>.
 *
 * <p>Contiene {@link bookrecommender.util.ServerUtil}, singleton responsabile di:</p>
 * <ul>
 *   <li>Gestione dello {@link javafx.stage.Stage} principale e caricamento FXML;</li>
 *   <li>Test e apertura connessioni al database tramite {@link bookrecommender.dao.DBManager};</li>
 *   <li>Avvio del registry RMI e <em>binding</em> dei servizi
 *       ({@link bookrecommender.service.SearchInterfaceImpl},
 *        {@link bookrecommender.service.LogRegInterfaceImpl},
 *        {@link bookrecommender.service.LibInterfaceImpl},
 *        {@link bookrecommender.service.MonitorInterfaceImpl});</li>
 *   <li>Notifica ai client e chiusura ordinata del server.</li>
 * </ul>
 *
 * <p>Questa utility centralizza la logica infrastrutturale, riducendo
 * il codice boilerplate nei controller e nelle classi di avvio.</p>
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbain Gabriele, 755699, VA
 */
package bookrecommender.util;