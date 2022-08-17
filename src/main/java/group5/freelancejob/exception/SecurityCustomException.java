package group5.freelancejob.exception;

import org.springframework.security.core.AuthenticationException;

public class SecurityCustomException extends AuthenticationException {
    public SecurityCustomException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SecurityCustomException(String msg) {
        super(msg);
    }
}
