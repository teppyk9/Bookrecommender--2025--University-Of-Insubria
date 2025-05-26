package bookrecommender.common;

import java.io.Serial;
import java.io.Serializable;

public class RegToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    boolean UsernameAvailable;
    boolean CFAvailable;
    boolean EmailAvailable;
    boolean Check;

    public RegToken(boolean User, boolean CF, boolean Email, boolean All) {
        UsernameAvailable = User;
        CFAvailable = CF;
        EmailAvailable = Email;
        Check = All;
    }

    public boolean isUsernameAvailable() {
        return UsernameAvailable;
    }
    public boolean isCFAvailable() {
        return CFAvailable;
    }
    public boolean isEmailAvailable() {
        return EmailAvailable;
    }
    public boolean RegistrationAvailable() {
        return UsernameAvailable && CFAvailable && EmailAvailable && Check;
    }
}