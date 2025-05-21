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
        if(libro.getCategoria() == null || libro.getCategoria().isEmpty()) {
            categoriaLabel.setText("Categoria: Non specificata");
        } else {
            categoriaLabel.setText("Categoria: " + libro.getCategoria());
        }
        if(libro.getEditore() == null || libro.getEditore().isEmpty()) {
            editoreLabel.setText("Editore: Non specificato");
        } else {
            editoreLabel.setText("Editore: " + libro.getEditore());
        }
        if(libro.getPrezzo() == 0) {
            prezzoLabel.setText("Prezzo: Non specificato");
        } else {
            prezzoLabel.setText("Prezzo: €" + libro.getPrezzo());
        }
        if(libro.getAnnoPubblicazione() == 0) {
            annoLabel.setText("Anno pubblicazione: Non specificato");
        } else {
            annoLabel.setText("Anno pubblicazione: " + libro.getAnnoPubblicazione());
        }
        meseLabel.setText("Mese pubblicazione: " + libro.getMesePubblicazione());
        if(libro.getDescrizione() == null || libro.getDescrizione().isEmpty()) {
            descrizioneArea.setText("Nessuna descrizione associata");
        } else {
            descrizioneArea.setText(libro.getDescrizione());
        }
    }
}
