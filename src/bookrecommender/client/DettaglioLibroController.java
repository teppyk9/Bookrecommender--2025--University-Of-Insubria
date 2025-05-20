package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DettaglioLibroController {

    @FXML private Label titoloLabel;
    @FXML private Label autoreLabel;
    @FXML private Label categoriaLabel;
    @FXML private Label editoreLabel;
    @FXML private Label prezzoLabel;
    @FXML private Label annoLabel;
    @FXML private Label meseLabel;
    @FXML private TextArea descrizioneArea;

    public void setLibro(Libro libro) {
        titoloLabel.setText(libro.getTitolo());
        autoreLabel.setText("Autore: " + libro.getAutore());
        categoriaLabel.setText("Categoria: " + libro.getCategoria());
        editoreLabel.setText("Editore: " + libro.getEditore());
        prezzoLabel.setText("Prezzo: €" + libro.getPrezzo());
        annoLabel.setText("Anno pubblicazione: " + libro.getAnnoPubblicazione());
        meseLabel.setText("Mese pubblicazione: " + libro.getMesePubblicazione());
        descrizioneArea.setText(libro.getDescrizione());
    }
}
