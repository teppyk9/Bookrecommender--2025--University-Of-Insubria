package bookrecommender.client;

import bookrecommender.common.Libro;
import bookrecommender.common.Libro_Details;
import bookrecommender.common.Valutazione;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.List;

public class DettaglioLibroAvanzatoController {
    public Label titoloLabel;
    public Label autoreLabel;
    public Label categoriaLabel;
    public Label editoreLabel;
    public Label prezzoLabel;
    public Label annoLabel;
    public Label meseLabel;
    public TextArea descrizioneArea;
    public ImageView starTotal1;
    public ImageView starTotal2;
    public ImageView starTotal3;
    public ImageView starTotal4;
    public ImageView starTotal5;
    public Label votoTotal;
    public ImageView starStile1;
    public ImageView starStile2;
    public ImageView starStile3;
    public ImageView starStile4;
    public ImageView starStile5;
    public Label votoStile;
    public ImageView starContenuto1;
    public ImageView starContenuto2;
    public ImageView starContenuto3;
    public ImageView starContenuto4;
    public ImageView starContenuto5;
    public Label votoContenuto;
    public ImageView starGradevolezza1;
    public ImageView starGradevolezza2;
    public ImageView starGradevolezza3;
    public ImageView starGradevolezza4;
    public ImageView starGradevolezza5;
    public Label votoGradevolezza;
    public ImageView starOriginalita1;
    public ImageView starOriginalita2;
    public ImageView starOriginalita3;
    public ImageView starOriginalita4;
    public ImageView starOriginalita5;
    public Label votoOriginalita;
    public ImageView starEdizione1;
    public ImageView starEdizione2;
    public ImageView starEdizione3;
    public ImageView starEdizione4;
    public ImageView starEdizione5;
    public Label votoEdizione;
    public ListView<Valutazione> listaValutazioni;
    public ListView<Libro> listaConsigli;

    private Libro_Details details;
    private Hashtable<String, List<Libro>> consigli;
    private List<Valutazione> valutazioni;

    public void setLibro(Libro libro){
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

        try {
            details = CliUtil.getInstance().getSearchService().getDetails(libro.getId());
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore di connessione", "Impossibile recuperare i dettagli del libro.").showAndWait();
        }

        setStar(starTotal1, starTotal2, starTotal3, starTotal4, starTotal5, details.getmFinale());
        votoTotal.setText(String.format("%.1f", details.getmFinale()));
        setStar(starStile1, starStile2, starStile3, starStile4, starStile5, details.getmStile());
        votoStile.setText(String.format("%.1f", details.getmStile()));
        setStar(starContenuto1, starContenuto2, starContenuto3, starContenuto4, starContenuto5, details.getmContenuto());
        votoContenuto.setText(String.format("%.1f", details.getmContenuto()));
        setStar(starGradevolezza1, starGradevolezza2, starGradevolezza3, starGradevolezza4, starGradevolezza5, details.getmGradevolezza());
        votoGradevolezza.setText(String.format("%.1f", details.getmGradevolezza()));
        setStar(starOriginalita1, starOriginalita2, starOriginalita3, starOriginalita4, starOriginalita5, details.getmOriginalita());
        votoOriginalita.setText(String.format("%.1f", details.getmOriginalita()));
        setStar(starEdizione1, starEdizione2, starEdizione3, starEdizione4, starEdizione5, details.getmEdizione());
        votoEdizione.setText(String.format("%.1f", details.getmEdizione()));

        listaValutazioni.setItems(FXCollections.observableArrayList(details.getValutazioni()));
        listaConsigli.setItems(FXCollections.observableArrayList(details.getConsigli().values().stream().flatMap(List::stream).toList()));
    }

    private void setStar(ImageView star1, ImageView star2, ImageView star3, ImageView star4, ImageView star5, float voto) {
        ImageView[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            if (i < voto) {
               if( voto - i >= 0.5) {
                   stars[i].setImage(CliUtil.getInstance().getStarHalf());
               } else {
                   stars[i].setImage(CliUtil.getInstance().getStarFull());
               }
            } else {
                stars[i].setImage(CliUtil.getInstance().getStarEmpty());
            }
        }
    }

    public void clickValutazione(MouseEvent mouseEvent) {
    }

    public void clickConsiglio(MouseEvent mouseEvent) {
    }
}
