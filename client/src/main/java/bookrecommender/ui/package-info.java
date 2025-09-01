/**
 * Controller JavaFX delle schermate dell’applicazione client.
 *
 * <p>Ogni classe in questo package controlla una vista FXML (login, registrazione,
 * ricerca, gestione librerie, dettaglio libro, creazione/modifica valutazioni, ecc.),
 * gestendo binding, validazioni, eventi utente e navigazione tra le schermate.</p>
 *
 * <p>I controller collaborano con:</p>
 * <ul>
 *   <li>{@link bookrecommender.util.CliUtil} per caricamento FXML, gestione {@code Stage}
 *       e navigazione;</li>
 *   <li>{@link bookrecommender.util.PasswordEngine} per la gestione della visibilità delle password;</li>
 *   <li>{@link bookrecommender.util.TableViewEngine} e
 *       {@link bookrecommender.util.TreeTableEngine} per tabelle e alberi con logiche riusabili;</li>
 *   <li>{@link bookrecommender.enums.FXMLtype} e {@link bookrecommender.enums.IMGtype}
 *       per riferimenti tipizzati a viste e icone.</li>
 * </ul>
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
package bookrecommender.ui;
