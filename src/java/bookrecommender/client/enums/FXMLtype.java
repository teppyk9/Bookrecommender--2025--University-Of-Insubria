package bookrecommender.client.enums;

/**
 * Enum che rappresenta i diversi tipi di schermate FXML utilizzate
 * nell'interfaccia grafica dell'applicazione client.
 * <p>
 * Ogni valore dell'enum associa un percorso FXML e un titolo finestra,
 * utilizzato per costruire dinamicamente le schermate tramite {@code FXMLLoader}.
 * </p>
 */
public enum FXMLtype {

    /**
     * Schermata di connessione al server remoto.
     */
    CONNESSIONE("/bookrecommender/client/fxml/Connessione.fxml", "Connessione al server"),

    /**
     * Schermata principale dopo il login.
     */
    HOME("/bookrecommender/client/fxml/Home.fxml", "Home"),

    /**
     * Schermata di login per l'autenticazione dell'utente.
     */
    LOGIN("/bookrecommender/client/fxml/Login.fxml", "Login"),

    /**
     * Schermata di registrazione di un nuovo utente.
     */
    REGISTRAZIONE("/bookrecommender/client/fxml/Registrazione.fxml", "Registrazione"),

    /**
     * Area riservata accessibile solo agli utenti autenticati.
     */
    AREARISERVATA("/bookrecommender/client/fxml/AreaRiservata.fxml", "Area Riservata"),

    /**
     * Ricerca semplice di libri.
     */
    CERCA("/bookrecommender/client/fxml/CercaLibro.fxml", "Cerca Libro"),

    /**
     * Ricerca avanzata con filtri specifici.
     */
    CERCA_AVANZATO("/bookrecommender/client/fxml/CercaLibroAvanzato.fxml", "Cerca Libro"),

    /**
     * Schermata per la creazione di una nuova libreria personale.
     */
    CREALIBRERIA("/bookrecommender/client/fxml/CreaLibreria.fxml", "Crea Libreria"),

    /**
     * Visualizzazione dettagliata di un libro selezionato.
     */
    DETTAGLIOLIBRO("/bookrecommender/client/fxml/DettaglioLibro.fxml", "Dettaglio Libro"),

    /**
     * Schermata per la creazione di una nuova valutazione su un libro.
     */
    CREAVALUTAZIONE("/bookrecommender/client/fxml/CreaValutazione.fxml", "Crea Valutazione"),

    /**
     * Schermata per visualizzare una valutazione esistente.
     */
    VISUALIZZAVALUTAZIONE("/bookrecommender/client/fxml/VisualizzaValutazione.fxml", "Visualizza Valutazione"),

    /**
     * Schermata per aggiungere un consiglio di lettura.
     */
    CREACONSIGLIO("/bookrecommender/client/fxml/CreaConsiglio.fxml", "Aggiungi Consigli"),

    /**
     * Schermata per modificare una libreria esistente.
     */
    MODIFICALIBRERIA("/bookrecommender/client/fxml/ModificaLibreria.fxml", "Modifica Libreria"),

    /**
     * Schermata per aggiungere un libro a una libreria.
     */
    AGGIUNGILIBROLIBRERIA("/bookrecommender/client/fxml/AddLibroLibreria.fxml", "Aggiungi Libro alla Libreria"),

    /**
     * Schermata per la gestione di tutte le librerie dell'utente.
     */
    GESTIONELIBRERIE("/bookrecommender/client/fxml/GestioneLibrerie.fxml","Gestione Librerie"),

    /**
     * Schermata per modificare un consiglio di lettura esistente.
     */
    MODIFICACONSIGLIO("/bookrecommender/client/fxml/ModificaConsiglio.fxml", "Modifica Consiglio"),

    /**
     * Schermata per modificare una valutazione esistente.
     */
    MODIFICAVALUTAZIONE("/bookrecommender/client/fxml/ModificaValutazione.fxml", "Modifica Valutazione"),

    /**
     * Schermata per modificare la password dell'utente o per eliminare l'account.
     */
    CAMBIAPASSWORD("/bookrecommender/client/fxml/CambiaPassword.fxml", "Impostazioni"),

    ACCOUNT("/bookrecommender/client/fxml/Account.fxml", "Account");


    private final String path;
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
