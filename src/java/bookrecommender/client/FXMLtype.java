package bookrecommender.client;

public enum FXMLtype {
    CONNESSIONE("/bookrecommender/client/Connessione.fxml", "Connessione al server"),
    HOME("/bookrecommender/client/Home.fxml", "Home"),
    LOGIN("/bookrecommender/client/Login.fxml", "Login"),
    REGISTRAZIONE("/bookrecommender/client/Registrazione.fxml", "Registrazione"),
    AREARISERVATA("/bookrecommender/client/AreaRiservata.fxml", "Area Riservata"),
    CERCA("/bookrecommender/client/CercaLibro.fxml", "Cerca Libro"),
    CERCA_AVANZATO("/bookrecommender/client/CercaLibroAvanzato.fxml", "Cerca Libro Avanzato"),
    CREALIBRERIA("/bookrecommender/client/CreaLibreria.fxml", "Crea Libreria"),
    DETTAGLIOlIBRO("/bookrecommender/client/DettaglioLibro.fxml", "Dettaglio Libro"),
    CREAVALUTAZIONE("/bookrecommender/client/Valutazione.fxml", "Crea Valutazione"),
    VALUTAZIONE("/bookrecommender/client/VisualizzaValutazione.fxml", "Visualizza Valutazione");

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
