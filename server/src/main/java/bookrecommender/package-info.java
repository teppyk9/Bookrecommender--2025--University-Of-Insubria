/**
 * Package principale del server <em>BookRecommender</em>.
 *
 * <p>Contiene i punti di ingresso dell’applicazione server:
 * {@link bookrecommender.Main_Server} (entry-point) e
 * {@link bookrecommender.GUI} (classe JavaFX che inizializza l’interfaccia
 * e coordina il caricamento dei pannelli di controllo e configurazione).</p>
 *
 * <p>Il modulo server espone servizi RMI, si collega a PostgreSQL e fornisce
 * una GUI per il monitoraggio dei log e la configurazione del servizio.</p>
 *
 * <strong>Book Recommender</strong> è un sistema per la valutazione
 * e raccomandazione di libri, che consente:
 * <ul>
 *   <li>Agli utenti registrati di inserire recensioni;</li>
 *   <li>A tutti gli utenti di consultare le valutazioni e
 *       ricevere consigli di lettura.</li>
 * </ul>
 * </p>
 * <p>
 * Per ulteriori dettagli sul funzionamento del programma, incluse
 * le istruzioni per l’avvio, consultare il Manuale Utente.
 * </p>
 *
 * Progetto svolto nell'ambito del corso di Laboratorio Interdisciplinare B -
 * Università degli Studi dell' Insubria, A.A. 2024/2025.
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
package bookrecommender;