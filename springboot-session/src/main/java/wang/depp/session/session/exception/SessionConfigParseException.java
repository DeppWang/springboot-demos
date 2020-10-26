package wang.depp.session.session.exception;

public class SessionConfigParseException extends RuntimeException {
    public SessionConfigParseException() {
    }

    public SessionConfigParseException(String message) {
        super(message);
    }

    public SessionConfigParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionConfigParseException(Throwable cause) {
        super(cause);
    }
}
