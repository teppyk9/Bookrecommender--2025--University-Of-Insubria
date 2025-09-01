/**
 * Package root del client JavaFX di <em>BookRecommender</em>.
 *
 * <p>Contiene i punti di ingresso dell’applicazione (es. {@link bookrecommender.Main_Client})
 * e la classe principale JavaFX ({@link bookrecommender.GUI}) che orchestra il ciclo di vita
 * dell’interfaccia: inizializzazione, caricamento viste FXML e gestione finestre.</p>
 *
 * <p>Architettura del modulo (sottopacchetti):</p>
 * <ul>
 *   <li>{@code enums}: enumerazioni di supporto a UI e caricamento risorse;</li>
 *   <li>{@code listener}: listener RMI lato client per eventi provenienti dal server;</li>
 *   <li>{@code ui}: controller JavaFX per le varie schermate FXML;</li>
 *   <li>{@code util}: servizi condivisi (loader FXML, gestione Stage, helper per tabelle, ecc.).</li>
 * </ul>
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
 * @author Fabbain Gabriele, 755699, VA
 */
package bookrecommender;