/**
 * Servizi e helper condivisi per il client JavaFX.
 *
 * <p>Comprende:</p>
 * <ul>
 *   <li>{@link bookrecommender.util.CliUtil}: Classe Singleton, punto centrale per caricamento FXML,
 *       gestione finestre/stage, icone, effetto hover, dialoghi e interazione con RMI;</li>
 *   <li>{@link bookrecommender.util.PasswordEngine}: utilità per campi password
 *       (mostra/nascondi, gestione eventi UI);</li>
 *   <li>{@link bookrecommender.util.TableViewEngine} e
 *       {@link bookrecommender.util.TreeTableEngine}: classi base che incapsulano
 *       logiche comuni di popolamento, colonne, placeholder, progress, azioni su righe,
 *       per tabelle e tree-table JavaFX.</li>
 * </ul>
 *
 * <p>L’obiettivo è ridurre duplicazioni nei controller e centralizzare la
 * gestione delle risorse e dei componenti UI ricorrenti.</p>
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
package bookrecommender.util;