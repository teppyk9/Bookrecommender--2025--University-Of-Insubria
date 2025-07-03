package bookrecommender.client;

public enum FXMLtype {
    CONNESSIONE("/bookrecommender/client/fxml/Connessione.fxml", "Connessione al server"),
    HOME("/bookrecommender/client/fxml/Home.fxml", "Home"),
    LOGIN("/bookrecommender/client/fxml/Login.fxml", "Login"),
    REGISTRAZIONE("/bookrecommender/client/fxml/Registrazione.fxml", "Registrazione"),
    AREARISERVATA("/bookrecommender/client/fxml/AreaRiservata.fxml", "Area Riservata"),
    CERCA("/bookrecommender/client/fxml/CercaLibro.fxml", "Cerca Libro"),
    CERCA_AVANZATO("/bookrecommender/client/fxml/CercaLibroAvanzato.fxml", "Cerca Libro"),
    CREALIBRERIA("/bookrecommender/client/fxml/CreaLibreria.fxml", "Crea Libreria"),
    DETTAGLIOLIBRO("/bookrecommender/client/fxml/DettaglioLibro.fxml", "Dettaglio Libro"),
    CREAVALUTAZIONE("/bookrecommender/client/fxml/CreaValutazione.fxml", "Crea Valutazione"),
    VISUALIZZAVALUTAZIONE("/bookrecommender/client/fxml/VisualizzaValutazione.fxml", "Visualizza Valutazione"),
    CREACONSIGLIO("/bookrecommender/client/fxml/CreaConsiglio.fxml", "Aggiungi Consigli"),
    MODIFICALIBRERIA("/bookrecommender/client/fxml/ModificaLibreria.fxml", "Modifica Libreria"),
    AGGIUNGILIBROLIBRERIA("/bookrecommender/client/fxml/AddLibroLibreria.fxml", "Aggiungi Libro alla Libreria"),
    GESTIONELIBRERIE("/bookrecommender/client/fxml/GestioneLibrerie.fxml","Gestione Librerie"),
    MODIFICACONSIGLIO("/bookrecommender/client/fxml/ModificaConsiglio.fxml", "Modifica Consiglio"),
    MODIFICAVALUTAZIONE("/bookrecommender/client/fxml/ModificaValutazione.fxml", "Modifica Valutazione");

    private final String path;
    private final String title;

    FXMLtype(String path, String title) {
        this.path = path;
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }
}
