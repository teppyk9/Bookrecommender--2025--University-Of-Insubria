package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;
/**
 * Record che rappresenta un token di verifica per la registrazione di un utente.
 * <p>
 * Contiene informazioni sullo stato di disponibilità di username, codice fiscale (CF),
 * email e un controllo aggiuntivo, usati per determinare se la registrazione è possibile.
 * </p>
 * Questa classe implementa {@link Serializable} per permettere la serializzazione,
 * utile nel contesto di comunicazioni RMI o salvataggio dello stato.
 * @param User          true se lo username è disponibile, false altrimenti
 * @param CF            true se il codice fiscale è disponibile, false altrimenti
 * @param Email         true se l'email è disponibile, false altrimenti
 * @param Check           true se il controllo generale è positivo, false altrimenti
 */
public record RegToken(boolean User, boolean CF, boolean Email, boolean Check) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Restituisce true se la registrazione è disponibile, ovvero se username, codice fiscale,
     * email e il controllo generale sono tutti positivi.
     *
     * @return true se la registrazione è consentita, false altrimenti
     */
    public boolean RegistrationAvailable() {
        return User && CF && Email && Check;
    }
}