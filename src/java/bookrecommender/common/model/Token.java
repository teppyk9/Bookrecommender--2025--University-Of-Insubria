package bookrecommender.common.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Il Record {@code Token} rappresenta un oggetto identificativo univoco per
 * un utente autenticato nel sistema. Viene generato al momento del login
 * e contiene:
 * <ul>
 *     @param token    stringa univoca che rappresenta il token di sessione
 *     @param userId   identificatore numerico dell'utente associato
 *     @param ipClient indirizzo IP del client che ha effettuato l'accesso
 * </ul>
 * <p>
 * Questa classe è serializzabile per consentirne la trasmissione attraverso
 * RMI o altre tecnologie di comunicazione remota.
 * </p>
 */
public record Token(String token, int userId, String ipClient) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}