package bookrecommender.enums;

/**
 * Enum che rappresenta i diversi tipi di schermate FXML utilizzate
 * nell'interfaccia grafica dell'applicazione client.
 * <p>
 * Ogni valore dell'enum associa un percorso FXML e un titolo finestra,
 * utilizzato per costruire dinamicamente le schermate tramite {@code FXMLLoader}.
 * </p>
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
public enum FXMLtype {

    /**
     * Schermata di connessione al server remoto.
     */
    CONNESSIONE("/fxml/Connessione.fxml", "Connessione al server"),

    /**
     * Schermata principale dopo il login.
     */
    HOME("/fxml/Home.fxml", "Home"),

    /**
     * Schermata di login per l'autenticazione dell'utente.
     */
    LOGIN("/fxml/Login.fxml", "Login"),

    /**
     * Schermata di registrazione di un nuovo utente.
     */
    REGISTRAZIONE("/fxml/Registrazione.fxml", "Registrazione"),

    /**
     * Area riservata accessibile solo agli utenti autenticati.
     */
    AREARISERVATA("/fxml/AreaRiservata.fxml", "Area Riservata"),

    /**
     * Ricerca semplice di libri.
     */
    CERCA("/fxml/CercaLibro.fxml", "Cerca Libro"),

    /**
     * Ricerca avanzata con filtri specifici, riservata agli utenti autenticati.
     */
    CERCA_AVANZATO("/fxml/CercaLibroAvanzato.fxml", "Cerca Libro"),

    /**
     * Schermata per la creazione di una nuova libreria personale.
     */
    CREALIBRERIA("/fxml/CreaLibreria.fxml", "Crea Libreria"),

    /**
     * Visualizzazione dettagliata di un libro selezionato.
     */
    DETTAGLIOLIBRO("/fxml/DettaglioLibro.fxml", "Dettaglio Libro"),

    /**
     * Schermata per la creazione di una nuova valutazione su un libro.
     */
    CREAVALUTAZIONE("/fxml/CreaValutazione.fxml", "Crea Valutazione"),

    /**
     * Schermata per visualizzare una valutazione esistente.
     */
    VISUALIZZAVALUTAZIONE("/fxml/VisualizzaValutazione.fxml", "Visualizza Valutazione"),

    /**
     * Schermata per aggiungere un consiglio di lettura.
     */
    CREACONSIGLIO("/fxml/CreaConsiglio.fxml", "Aggiungi Consigli"),

    /**
     * Schermata per modificare una libreria esistente.
     */
    MODIFICALIBRERIA("/fxml/ModificaLibreria.fxml", "Modifica Libreria"),

    /**
     * Schermata per aggiungere un libro a una libreria.
     */
    AGGIUNGILIBROLIBRERIA("/fxml/AddLibroLibreria.fxml", "Aggiungi Libro alla Libreria"),

    /**
     * Schermata per la gestione di tutte le librerie dell'utente.
     */
    GESTIONELIBRERIE("/fxml/GestioneLibrerie.fxml","Gestione Librerie"),

    /**
     * Schermata per modificare un consiglio di lettura esistente.
     */
    MODIFICACONSIGLIO("/fxml/ModificaConsiglio.fxml", "Modifica Consiglio"),

    /**
     * Schermata per modificare una valutazione esistente.
     */
    MODIFICAVALUTAZIONE("/fxml/ModificaValutazione.fxml", "Modifica Valutazione"),

    /**
     * Schermata per modificare la password dell'utente.
     */
    CAMBIAPASSWORD("/fxml/CambiaPassword.fxml", "CambiaPassword"),


    /**
     * Schermata per visualizzare le informazioni del profilo utente e modificarle.
     */
    ACCOUNT("/fxml/Account.fxml", "Account");

    /** Percorso al file FXML associato alla schermata. */
    private final String path;

    /** Titolo finestra associato alla schermata. */
    private final String title;

    /**
     * Costruttore dell'enum {@code FXMLtype}.
     *
     * @param path  Il percorso del file .fxml associato alla schermata.
     * @param title Il titolo della finestra associata alla schermata.
     */
    FXMLtype(String path, String title) {
        this.path = path;
        this.title = title;
    }

    /**
     * Restituisce il percorso del file FXML associato a questo tipo di schermata.
     *
     * @return Il percorso del file FXML.
     */
    public String getPath() {
        return path;
    }

    /**
     * Restituisce il titolo della finestra associato a questo tipo di schermata.
     *
     * @return Il titolo della finestra.
     */
    public String getTitle() {
        return title;
    }
}
