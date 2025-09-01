/**
 * Listener lato client per eventi provenienti dal server (RMI).
 *
 * <p>Contiene l’implementazione {@link bookrecommender.listener.ClientListener},
 * registrata come oggetto remoto, che riceve notifiche (es. Spegnimento server) e
 * coordina la UI: chiusura finestre, dialoghi informativi e tentativo di
 * riconnessione/uscita tramite le utility del client.</p>
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbain Gabriele, 755699, VA
 */
package bookrecommender.listener;