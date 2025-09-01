/**
 * Implementazioni dei servizi remoti (RMI) del server <em>BookRecommender</em>.
 *
 * <p>Le classi di questo package espongono via RMI i contratti
 * definiti nel modulo condiviso (<code>bookrecommender.interfaces</code>),
 * e usano il livello DAO per interagire con il database:</p>
 *
 * <ul>
 *   <li>{@link bookrecommender.service.LogRegInterfaceImpl} — autenticazione, registrazione,
 *       gestione sessioni;</li>
 *   <li>{@link bookrecommender.service.SearchInterfaceImpl} — ricerche libri;</li>
 *   <li>{@link bookrecommender.service.LibInterfaceImpl} — gestione librerie utente,
 *       valutazioni e consigli;</li>
 *   <li>{@link bookrecommender.service.MonitorInterfaceImpl} — registrazione e notifica
 *       eventi di servizio ai client (es. Shutdown).</li>
 * </ul>
 *
 * <p>Le istanze sono create e registrate nel registry RMI da
 * {@link bookrecommender.util.ServerUtil} all’avvio del server.</p>
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbain Gabriele, 755699, VA
 */
package bookrecommender.service;