package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;
/**
 * Classe che rappresenta un token di verifica per la registrazione di un utente.
 * <p>
 * Contiene informazioni sullo stato di disponibilità di username, codice fiscale (CF),
 * email e un controllo aggiuntivo, usati per determinare se la registrazione è possibile.
 * </p>
 * Questa classe implementa {@link Serializable} per permettere la serializzazione,
 * utile nel contesto di comunicazioni RMI o salvataggio dello stato.
 */
public class RegToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final boolean UsernameAvailable;
    private final boolean CFAvailable;
    private final boolean EmailAvailable;
    private final boolean Check;

    /**
     * Costruttore che inizializza lo stato di disponibilità per username, codice fiscale,
     * email e un controllo generale.
     *
     * @param User          true se lo username è disponibile, false altrimenti
     * @param CF            true se il codice fiscale è disponibile, false altrimenti
     * @param Email         true se l'email è disponibile, false altrimenti
     * @param All           true se il controllo generale è positivo, false altrimenti
     */
    public RegToken(boolean User, boolean CF, boolean Email, boolean All) {
        UsernameAvailable = User;
        CFAvailable = CF;
        EmailAvailable = Email;
        Check = All;
    }

    /**
     * Restituisce true se lo username è disponibile, false altrimenti.
     *
     * @return true se lo username è disponibile, false altrimenti
     */
    public boolean isUsernameAvailable() {
        return UsernameAvailable;
    }

    /**
     * Restituisce true se il codice fiscale è disponibile, false altrimenti.
     *
     * @return true se il codice fiscale è disponibile, false altrimenti
     */
    public boolean isCFAvailable() {
        return CFAvailable;
    }

    /**
     * Restituisce true se l'email è disponibile, false altrimenti.
     *
     * @return true se l'email è disponibile, false altrimenti
     */
    public boolean isEmailAvailable() {
        return EmailAvailable;
    }

    /**
     * Restituisce true se la registrazione è disponibile, ovvero se username, codice fiscale,
     * email e il controllo generale sono tutti positivi.
     *
     * @return true se la registrazione è consentita, false altrimenti
     */
    public boolean RegistrationAvailable() {
        return UsernameAvailable && CFAvailable && EmailAvailable && Check;
    }
}