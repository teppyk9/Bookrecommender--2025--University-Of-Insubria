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
 *   <li>parsa gli script rispettando stringhe, commenti e blocchi
 *       <em>dollar-quoted</em> (<code>$tag$...$tag$</code>), separando gli statement su <code>;</code>;</li>
 *   <li>supporta due politiche transazionali: <b>transazione per file</b> (default, con commit/rollback atomico)
 *       oppure <b>auto-commit</b> (attivabile digitando <code>autocommit</code> all’avvio);</li>
 *   <li>produce un report per file con conteggio di successi/errori e, in caso d’errore,
 *       logga <code>SQLState</code> e <code>errorCode</code> del driver JDBC.</li>
 * </ul>
 * All'utente vengono proposte due modalità di setup:
 * <ul>
 *     <li><b>Basic</b>: crea le tabelle e popola la tabella libri.</li>
 *     <li><b>Full</b>: crea le tabelle, popola la
 *     tabella libri e aggiunge un dump per il testing (utente "test" con password "testtest").</li>
 * </ul>
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