package bookrecommender.client;

public enum FXMLtype {
    CONNESSIONE("/bookrecommender/client/fxml/Connessione.fxml", "Connessione al server"),
    HOME("/bookrecommender/client/fxml/Home.fxml", "Home"),
    LOGIN("/bookrecommender/client/fxml/Login.fxml", "Login"),
    REGISTRAZIONE("/bookrecommender/client/fxml/Registrazione.fxml", "Registrazione"),
    AREARISERVATA("/bookrecommender/client/fxml/AreaRiservata.fxml", "Area Riservata"),
    CERCA("/bookrecommender/client/fxml/CercaLibro.fxml", "Cerca Libro"),
    CERCA_AVANZATO("/bookrecommender/client/fxml/CercaLibroAvanzato.fxml", "Cerca Libro Avanzato"),
    CREALIBRERIA("/bookrecommender/client/fxml/CreaLibreria.fxml", "Crea Libreria"),
    DETTAGLIOlIBRO("/bookrecommender/client/fxml/DettaglioLibro.fxml", "Dettaglio Libro"),
    CREAVALUTAZIONE("/bookrecommender/client/fxml/Valutazione.fxml", "Crea Valutazione"),
    VALUTAZIONE("/bookrecommender/client/fxml/VisualizzaValutazione.fxml", "Visualizza Valutazione");

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
