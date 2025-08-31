package bookrecommender.enums;

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
    CONFIRM("/icons/AlertConfirmation_a31621.png"),

    /**
     * Icona di errore.
     */
    RED_CROSS("/icons/Alert_red.png"),

    /**
     * Icona di informazione.
     */
    INFO("/icons/AlertInfo_a31621.png"),

    /**
     * Freccia per espandere o ridurre un elemento.
     */
    ARROW_DOWN("/icons/Expand_arrowDown_fcf7f8.png"),

    /**
     * Spunta verde per indicare un'azione completata con successo.
     */
    CHECK("/icons/Check_green.png"),

    /**
     * Icona di rimozione elemento, rappresentata da un cerchio rosso con un segno meno.
     */
    RIMUOVI("/icons/Rimuovi_fcf7f8.png"),

    /**
     * Icona principale del programma, utilizzata per rappresentare l'applicazione.
     */
    ICONA_PROG("/icons/program_icon.png"),

    /**
     * Icona principale del programma in modalità connessione, utilizzata per rappresentare l'applicazione durante la connessione al server.
     */
    SERVER("/icons/server_connection.png"),

    /**
     * Icona di una stella rossa vuota.
     */
    STAR_0_4_RED("/icons/star-red-0-4.png"),

    /**
     * Icona di una stella rossa piena per 1/4.
     */
    STAR_1_4_RED("/icons/star-red-1-4.png"),

    /**
     * Icona di una stella rossa piena per 2/4.
     */
    STAR_2_4_RED("/icons/star-red-2-4.png"),

    /**
     * Icona di una stella rossa piena per 3/4.
     */
    STAR_3_4_RED("/icons/star-red-3-4.png"),

    /**
     * Icona di una stella rossa piena.
     */
    STAR_4_4_RED("/icons/star-red-4-4.png"),

    /**
     * Icona di una stella bianca vuota.
     */
    STAR_0_4_WHITE("/icons/star-white-0-4.png"),

    /**
     * Icona di una stella bianca piena per 1/4.
     */
    STAR_1_4_WHITE("/icons/star-white-1-4.png"),

    /**
     * Icona di una stella bianca piena per 2/4.
     */
    STAR_2_4_WHITE("/icons/star-white-2-4.png"),

    /**
     * Icona di una stella bianca piena per 3/4.
     */
    STAR_3_4_WHITE("/icons/star-white-3-4.png"),

    /**
     * Icona di una stella bianca piena.
     */
    STAR_4_4_WHITE("/icons/star-white-4-4.png"),

    /**
     * Icona per visualizzare una password in chiaro.
     */
    VISIBLE_PASSWORD("/icons/Visible_FCF7F8.png"),

    /**
     * Icona per nascondere una password.
     */
    INVISIBLE_PASSWORD("/icons/Invisible_FCF7F8.png"),

    /**
     * Icona per tornare indietro nella navigazione.
     */
    INDIETRO("/icons/GoBack_a31621.png"),

    /**
     * Icona per avviare una ricerca.
     */
    CERCA("/icons/Search_a31621.png"),

    /**
     * Icona per visualizzare le impostazioni dell'account.
     */
    IMPOSTAZIONI("/icons/ImpostazioniAccount_A31621.png"),

    /**
     * Icona per effettuare il logout dall'applicazione.
     */
    LOGOUT("/icons/Logout_a31621.png"),

    /**
     * Icona per modificare un elemento.
     */
    EDIT("/icons/Edit_fcf7f8.png"),

    /**
     * Icona per salvare un elemento.
     */
    SAVE("/icons/Save_fcf7f8.png"),;

    /**
     * Percorso del file immagine associato all'elemento enum.
     */
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
        /**
         * Serie di icone stella in colore rosso, dal livello 0/4 (vuota) a 4/4 (piena).
         */
        RED(STAR_0_4_RED, STAR_1_4_RED, STAR_2_4_RED, STAR_3_4_RED, STAR_4_4_RED),

        /**
         * Serie di icone stella in colore bianco, dal livello 0/4 (vuota) a 4/4 (piena).
         */
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
