package bookrecommender.client.enums;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 * Enum che rappresenta le icone grafiche utilizzate nell'interfaccia client.
 * Ogni elemento dell'enum associa un percorso a un file immagine, utilizzabile tramite {@link Image} o {@link ImageView}.
 */
public enum IMGtype {

    /**
     * Icona di conferma.
     */
    CONFIRM("/bookrecommender/client/icons/alert_confirmation_icon.png"),

    /**
     * Icona di errore.
     */
    RED_CROSS("/bookrecommender/client/icons/alert_icon.png"),

    /**
     * Icona di informazione.
     */
    INFO("/bookrecommender/client/icons/alert_info_icon.png"),

    /**
     * Freccia per espandere o ridurre un elemento.
     */
    ARROW_DOWN("/bookrecommender/client/icons/Expand_arrowDown_fcf7f8_OK.png"),

    /**
     * Spunta verde per indicare un'azione completata con successo.
     */
    CHECK("/bookrecommender/client/icons/check-green.png"),

    /**
     * Icona di rimozione elemento, rappresentata da un cerchio rosso con un segno meno.
     */
    RIMUOVI("/bookrecommender/client/icons/minus-circle-red.png"),

    /**
     * Icona principale del programma, utilizzata per rappresentare l'applicazione.
     */
    ICONA_PROG("/bookrecommender/client/icons/program_icon.png"),

    /**
     * Icona principale del programma in modalità connessione, utilizzata per rappresentare l'applicazione durante la connessione al server.
     */
    SERVER("/bookrecommender/client/icons/server_connection.png"),

    /**
     * Icona di una stella rossa vuota.
     */
    STAR_0_4_RED("/bookrecommender/client/icons/star-red-0-4.png"),

    /**
     * Icona di una stella rossa piena per 1/4.
     */
    STAR_1_4_RED("/bookrecommender/client/icons/star-red-1-4.png"),

    /**
     * Icona di una stella rossa piena per 2/4.
     */
    STAR_2_4_RED("/bookrecommender/client/icons/star-red-2-4.png"),

    /**
     * Icona di una stella rossa piena per 3/4.
     */
    STAR_3_4_RED("/bookrecommender/client/icons/star-red-3-4.png"),

    /**
     * Icona di una stella rossa piena.
     */
    STAR_4_4_RED("/bookrecommender/client/icons/star-red-4-4.png"),

    /**
     * Icona di una stella bianca vuota.
     */
    STAR_0_4_WHITE("/bookrecommender/client/icons/star-white-0-4.png"),

    /**
     * Icona di una stella bianca piena per 1/4.
     */
    STAR_1_4_WHITE("/bookrecommender/client/icons/star-white-1-4.png"),

    /**
     * Icona di una stella bianca piena per 2/4.
     */
    STAR_2_4_WHITE("/bookrecommender/client/icons/star-white-2-4.png"),

    /**
     * Icona di una stella bianca piena per 3/4.
     */
    STAR_3_4_WHITE("/bookrecommender/client/icons/star-white-3-4.png"),

    /**
     * Icona di una stella bianca piena.
     */
    STAR_4_4_WHITE("/bookrecommender/client/icons/star-white-4-4.png"),

    /**
     * Icona per visualizzare una password in chiaro.
     */
    VISIBLE_PASSWORD("/bookrecommender/client/icons/Visible_FCF7F8_OKK.png"),

    /**
     * Icona per nascondere una password.
     */
    INVISIBLE_PASSWORD("/bookrecommender/client/icons/Invisible_FCF7F8_OK.png"),

    /**
     * Icona per tornare indietro nella navigazione.
     */
    INDIETRO("/bookrecommender/client/icons/GoBack_a31621_100OK.png"),

    /**
     * Icona per avviare una ricerca.
     */
    CERCA("/bookrecommender/client/icons/Search_a31621_OK.png"),

    /**
     * Icona per visualizzare le impostazioni dell'account.
     */
    IMPOSTAZIONI("/bookrecommender/client/icons/ImpostazioniAccount_A31621_OK.png"),

    /**
     * Icona per effettuare il logout dall'applicazione.
     */
    LOGOUT("/bookrecommender/client/icons/Logout_a31621_OK.png"),

    /**
     * Icona per modificare un elemento.
     */
    EDIT("/bookrecommender/client/icons/Edit_fcf7f8_OK.png"),

    /**
     * Icona per salvare un elemento.
     */
    SAVE("/bookrecommender/client/icons/Save_fcf7f8_OK.png"),;

    private final String path;

    /**
     * Costruttore dell'enum {@code IMGtype}.
     *
     * @param path Il percorso dell'immagine associata all'elemento enum.
     */
    IMGtype(String path){
        this.path = path;
    }

    /**
     * Restituisce un {@link ImageView} dell'immagine, con dimensioni specificate.
     * Il rapporto d'aspetto dell'immagine è mantenuto.
     *
     * @param width  Larghezza desiderata dell'immagine.
     * @param height Altezza desiderata dell'immagine.
     * @return L'istanza di {@code ImageView} con l'immagine scalata.
     */
    public ImageView getImageView(double width, double height) {
        ImageView imageView = new ImageView(getImage());
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

    /**
     * Restituisce l'immagine associata a questo tipo di icona.
     *
     * @return L'oggetto {@link Image} corrispondente al percorso specificato.
     * @throws NullPointerException se il percorso dell'immagine non viene trovato.
     */
    public Image getImage(){
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    /**
     * Enum interno che rappresenta le valutazioni tramite stelle,
     * distinte per colore (rosso o bianco) e livello (da 0/4 a 4/4).
     */
    public enum STARtype{
        RED(STAR_0_4_RED, STAR_1_4_RED, STAR_2_4_RED, STAR_3_4_RED, STAR_4_4_RED),
        WHITE(STAR_0_4_WHITE, STAR_1_4_WHITE, STAR_2_4_WHITE, STAR_3_4_WHITE, STAR_4_4_WHITE);

        private final IMGtype[] stars;

        /**
         * Costruttore dell'enum {@code STARtype}.
         *
         * @param stars Array di elementi {@code IMGtype} che rappresentano i livelli di valutazione.
         */
        STARtype(IMGtype... stars) {
            this.stars = stars;
        }


        /**
         * Restituisce l'icona della stella corrispondente all'indice specificato.
         * Se l'indice non è valido, restituisce il primo elemento (stella vuota).
         *
         * @param index Indice della stella da ottenere (da 0 a 4).
         * @return L'elemento {@link IMGtype} corrispondente alla stella richiesta.
         */

        public IMGtype getStar(int index) {
            if (index < 0 || index >= stars.length) {
                return stars[0];
            }
            return stars[index];
        }
    }
}
