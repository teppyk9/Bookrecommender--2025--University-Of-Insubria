/**
 * Accesso ai dati e gestione connessioni per il server <em>BookRecommender</em>.
 *
 * <p>Include la classe {@link bookrecommender.dao.DBManager} che centralizza:
 * <ul>
 *   <li>Creazione e gestione del pool di connessioni (HikariCP);</li>
 *   <li>Apertura/chiusura della connessione condivisa e test di reachability;</li>
 *   <li>Esecuzione di query/updates a supporto dei servizi RMI;</li>
 *   <li>Utility specifiche lato server (es. Reset delle sessioni di login).</li>
 * </ul>
 * Le altre componenti del server (GUI, servizi RMI) dipendono da questo livello
 * per ottenere connessioni e interagire con PostgreSQL.</p>
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbain Gabriele, 755699, VA
 */
package bookrecommender.dao;