/**
 * Enumerazioni di supporto all’interfaccia grafica.
 *
 * <p>In particolare:</p>
 * <ul>
 *   <li>{@link bookrecommender.enums.FXMLtype}: associazioni tra schermate e percorsi FXML
 *       (con titoli finestra), usate per il caricamento dinamico delle viste;</li>
 *   <li>{@link bookrecommender.enums.IMGtype}: mappa delle icone utilizzate nella UI
 *       (percorsi immagini e factory per {@code Image}/{@code ImageView}).</li>
 * </ul>
 *
 * <p>Queste enum sono consumate da loader e controller per ridurre
 * le stringhe “magiche” e centralizzare i riferimenti a risorse.</p>
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
package bookrecommender.enums;