package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;

/**
 * La classe {@code Token} rappresenta un oggetto identificativo univoco per
 * un utente autenticato nel sistema. Viene generato al momento del login
 * e contiene:
 * <ul>
 *     <li>un token stringa univoco,</li>
 *     <li>l'ID dell'utente associato,</li>
 *     <li>l'indirizzo IP del client che ha effettuato l'accesso.</li>
 * </ul>
 * <p>
 * Questa classe è serializzabile per consentirne la trasmissione attraverso
 * RMI o altre tecnologie di comunicazione remota.
 * </p>
 */
public class Token implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String token;
    private final int userId;
    private final String ipClient;

    /**
     * Costruisce un nuovo {@code Token} associato a un utente autenticato.
     * @param token     stringa univoca che rappresenta il token di sessione
     * @param userId    identificatore numerico dell'utente associato
     * @param ipClient  indirizzo IP del client che ha effettuato l'accesso
     */
    public Token(String token, int userId, String ipClient) {
        this.token = token;
        this.userId = userId;
        this.ipClient = ipClient;
    }

    /**
     * Restituisce la stringa identificativa del token di sessione.
     *
     * @return il token come stringa
     */
    public String getToken() {
        return token;
    }

    /**
     * Restituisce l'ID dell'utente associato a questo token.
     *
     * @return l'identificatore numerico dell'utente
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Restituisce l'indirizzo IP del client che ha generato il token.
     *
     * @return l'indirizzo IP del client
     */
    public String getIpClient() {
        return ipClient;
    }
}