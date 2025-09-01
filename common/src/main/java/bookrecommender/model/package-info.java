/**
 * Modelli di dominio serializzabili per <em>BookRecommender</em>.
 *
 * <p>Queste classi sono trasferite tra client e server (RMI) e
 * rappresentano le entità principali del sistema:</p>
 *
 * <ul>
 *   <li>{@link bookrecommender.model.Libro} — dati essenziali di un libro
 *       (id, titolo, autore, descrizione, categoria, editore, prezzo, anno/mese);
 *       uguaglianza e hash basati sull'ID;</li>
 *   <li>{@link bookrecommender.model.Libro_Details} — dettagli estesi di un libro
 *       con raccolta di valutazioni e calcolo delle medie per le metriche (es. stile, contenuto, finale);</li>
 *   <li>{@link bookrecommender.model.Valutazione} — recensione di un utente su un libro,
 *       con voti numerici e commenti testuali;</li>
 *   <li>{@link bookrecommender.model.Token} — record identificativo della sessione
 *       (token, userId, ipClient) rilasciato al login;</li>
 *   <li>{@link bookrecommender.model.RegToken} — record di verifica preregistrazione
 *       (disponibilità username/CF/email e controllo complessivo) con metodo
 *       {@link bookrecommender.model.RegToken#RegistrationAvailable()}.</li>
 * </ul>
 *
 * <p>Tutte le classi implementano {@link java.io.Serializable} per consentire
 * la trasmissione remota e l’eventuale persistenza.</p>
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbain Gabriele, 755699, VA
 */
package bookrecommender.model;