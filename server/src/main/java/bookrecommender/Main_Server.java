package bookrecommender;
/**
 * Classe di avvio dell'applicazione server.
 * <p>
 * Contiene esclusivamente il metodo {@code main} che delega
 * l'esecuzione a {@link GUI}, la classe JavaFX responsabile
 * della gestione dell'interfaccia grafica.
 * </p>
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbain Gabriele, 755699, VA
 */
public class Main_Server{

    /**
     * Entry point dell'applicazione server.
     * <p>
     * Avvia l'interfaccia grafica JavaFX invocando
     * {@link javafx.application.Application#launch(Class, String...)}
     * con {@link GUI} come classe principale.
     * </p>
     *
     * @param args argomenti da riga di comando passati all'applicazione
     */
    public static void main(String[] args) {
        GUI.launch(GUI.class, args);
    }
}