package bookrecommender.util;

import bookrecommender.enums.IMGtype;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Classe astratta che fornisce un motore comune per la gestione della
 * visibilità delle password in interfacce JavaFX.
 * <p>
 * Consente di:
 * <ul>
 *   <li>collegare in modo bidirezionale un {@link PasswordField} con un {@link TextField} equivalente</li>
 *   <li>mostrare o nascondere dinamicamente il contenuto delle password</li>
 *   <li>aggiornare l’icona di toggle tramite {@link IMGtype}</li>
 * </ul>
 * <p>
 * Per l’utilizzo è necessario estendere questa classe e implementare i metodi
 * getter astratti che forniscono i riferimenti ai componenti FXML.
 * </p>
 *
 * @author Maffioli Gianmarco, 757587, VA
 * @author Rolla Francesca, 757922, VA
 * @author Fabbian Gabriele, 755699, VA
 */
public abstract class PasswordEngine {

    /** Stato di visibilità del primo campo password. */
    private boolean showPassword1 = false;

    /** Stato di visibilità del secondo campo password. */
    private boolean showPassword2 = false;

    /**
     * @return il primo {@link PasswordField} nascosto
     */
    protected abstract PasswordField getPasswordField1();

    /**
     * @return il secondo {@link PasswordField} nascosto
     */
    protected abstract PasswordField getPasswordField2();

    /**
     * @return il primo {@link TextField} usato come campo visibile
     */
    protected abstract TextField getVisiblePasswordField1();

    /**
     * @return il secondo {@link TextField} usato come campo visibile
     */
    protected abstract TextField getVisiblePasswordField2();

    /**
     * @return il pulsante associato al primo campo
     */
    protected abstract Button getButton1();

    /**
     * @return il pulsante associato al secondo campo
     */
    protected abstract Button getButton2();

    /**
     * Inizializza il primo campo password:
     * <ul>
     *   <li>collega bidirezionalmente {@link TextField} e {@link PasswordField}</li>
     *   <li>nasconde il campo visibile di default</li>
     *   <li>imposta l’icona di default a “password visibile”</li>
     * </ul>
     */
    protected void initP1() {
        getVisiblePasswordField1().textProperty().bindBidirectional(getPasswordField1().textProperty());
        getVisiblePasswordField1().setVisible(false);
        getVisiblePasswordField1().setManaged(false);
        getButton1().setGraphic(IMGtype.VISIBLE_PASSWORD.getImageView(24,24));
    }

    /**
     * Inizializza il secondo campo password con logica identica a {@link #initP1()}.
     */
    protected void initP2() {
        getVisiblePasswordField2().textProperty().bindBidirectional(getPasswordField2().textProperty());
        getVisiblePasswordField2().setVisible(false);
        getVisiblePasswordField2().setManaged(false);
        getButton2().setGraphic(IMGtype.VISIBLE_PASSWORD.getImageView(24,24));
    }

    /**
     * Alterna la visibilità del primo campo password.
     * <p>
     * Se la password è nascosta:
     * <ul>
     *   <li>mostra il campo testuale</li>
     *   <li>nasconde il {@link PasswordField}</li>
     *   <li>cambia l’icona su “invisibile”</li>
     * </ul>
     * Se la password è visibile fa l’opposto.
     * </p>
     */
    @FXML
    private void showP1() {
        if (showPassword1) {
            getButton1().setGraphic(IMGtype.VISIBLE_PASSWORD.getImageView(24,24));
            getPasswordField1().setVisible(true);
            getPasswordField1().setManaged(true);
            getVisiblePasswordField1().setVisible(false);
            getVisiblePasswordField1().setManaged(false);
            showPassword1 = false;
        } else {
            getButton1().setGraphic(IMGtype.INVISIBLE_PASSWORD.getImageView(24,24));
            getPasswordField1().setVisible(false);
            getPasswordField1().setManaged(false);
            getVisiblePasswordField1().setVisible(true);
            getVisiblePasswordField1().setManaged(true);
            showPassword1 = true;
        }
    }

    /**
     * Alterna la visibilità del secondo campo password.
     * <p>Stessa logica di {@link #showP1()} applicata al secondo campo.</p>
     */
    @FXML
    private void showP2() {
        if (showPassword2) {
            getButton2().setGraphic(IMGtype.VISIBLE_PASSWORD.getImageView(24,24));
            getPasswordField2().setVisible(true);
            getPasswordField2().setManaged(true);
            getVisiblePasswordField2().setVisible(false);
            getVisiblePasswordField2().setManaged(false);
            showPassword2 = false;
        } else {
            getButton2().setGraphic(IMGtype.INVISIBLE_PASSWORD.getImageView(24,24));
            getPasswordField2().setVisible(false);
            getPasswordField2().setManaged(false);
            getVisiblePasswordField2().setVisible(true);
            getVisiblePasswordField2().setManaged(true);
            showPassword2 = true;
        }
    }
}
