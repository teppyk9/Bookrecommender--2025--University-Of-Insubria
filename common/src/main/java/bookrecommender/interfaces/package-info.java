/**
 * Contratti remoti RMI del sistema <em>BookRecommender</em>.
 *
 * <p>Le interfacce qui definite sono implementate dal server e
 * invocate dal client tramite RMI. Tutti i metodi possono lanciare
 * {@link java.rmi.RemoteException} in caso di problemi di comunicazione.</p>
 *
 * <p>Principali interfacce:</p>
 * <ul>
 *   <li>{@link bookrecommender.interfaces.LogRegInterface} — login, registrazione,
 *       logout, gestione credenziali e recupero info profilo;</li>
 *   <li>{@link bookrecommender.interfaces.SearchInterface} — ricerca libri per vari criteri
 *       e recupero di {@link bookrecommender.model.Libro_Details};</li>
 *   <li>{@link bookrecommender.interfaces.LibInterface} — gestione librerie utente,
 *       valutazioni e consigli; richiede {@link bookrecommender.model.Token};</li>
 *   <li>{@link bookrecommender.interfaces.MonitorInterface} — registrazione di un
 *       {@link bookrecommender.interfaces.ServerListener} per notifiche dal server;</li>
 *   <li>{@link bookrecommender.interfaces.ServerListener} — callback remota invocata
 *       dal server (es. Segnale di arresto imminente).</li>
 * </ul>
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbain Gabriele, 755699, VA
 */
package bookrecommender.interfaces;
