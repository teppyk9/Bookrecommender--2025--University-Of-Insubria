package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;

public class Token implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String token;
    private final int userId;
    private final String ipClient;

    public Token(String token, int userId, String ipClient) {
        this.token = token;
        this.userId = userId;
        this.ipClient = ipClient;
    }

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }

    public String getIpClient() {
        return ipClient;
    }
}