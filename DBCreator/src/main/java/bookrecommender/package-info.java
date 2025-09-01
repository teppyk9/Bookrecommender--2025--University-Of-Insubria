/**
 * Utility console per l’inizializzazione di un database PostgreSQL.
 *
 * <p>
 * Il package espone la sola classe {@link bookrecommender.DBCreator}, che:
 * </p>
 * <ul>
 *   <li>chiede all’utente i parametri di connessione (host, porta, utente, password, nome DB)
 *       proponendo valori di default;</li>
 *   <li>verifica l’esistenza del database di destinazione (via DB di manutenzione
 *       <code>postgres</code>) e, se assente, lo crea;</li>
 *   <li>risolve la cartella degli script SQL (per default <code>data</code> vicina
 *       all’eseguibile; può essere forzata con <code>-Dsql.dir</code>);</li>
 *   <li>legge i file <code>.sql</code> in UTF-8 (rimuovendo eventuale BOM),
 *       li ordina alfabeticamente ed esegue gli statement in sequenza;</li>
 *   <li>parsa gli script rispettando stringhe, commenti e blocchi
 *       <em>dollar-quoted</em> (<code>$tag$...$tag$</code>), separando gli statement su <code>;</code>;</li>
 *   <li>supporta due politiche transazionali: <b>transazione per file</b> (default, con commit/rollback atomico)
 *       oppure <b>auto-commit</b> (attivabile digitando <code>autocommit</code> all’avvio);</li>
 *   <li>produce un report per file con conteggio di successi/errori e, in caso d’errore,
 *       logga <code>SQLState</code> e <code>errorCode</code> del driver JDBC.</li>
 * </ul>
 *
 * <h2>Risoluzione cartella script</h2>
 * <ol>
 *   <li>Se specificata, usa la proprietà di sistema <code>sql.dir</code> (deve esistere ed essere una directory);</li>
 *   <li>altrimenti ricerca una cartella <code>data</code> salendo fino a 6 livelli dalla directory dell’eseguibile
 *       (gestite varianti comuni come <code>classes</code>, <code>target_DBCreator</code> e <code>bin</code>→<code>data</code>).</li>
 * </ol>
 *
 * <h2>Configurazione</h2>
 * <ul>
 *   <li><code>-Dsql.dir=&lt;percorso/alla/data&gt;</code> per forzare la cartella degli script SQL.</li>
 * </ul>
 *
 * <h2>Esempio d’uso</h2>
 * <pre>{@code
 * $ java -cp your.jar bookrecommender.DBCreator
 * }</pre>
 *
 * <p><strong>Nota:</strong> l’esecuzione degli script modifica lo schema dati del DB di destinazione.
 * È responsabilità dell’utente fornire script idempotenti o nella corretta sequenza.</p>
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