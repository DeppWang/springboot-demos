package wang.depp.session.session.exception;

public class ModifyReadOnlyAttributeException extends RuntimeException {
    public ModifyReadOnlyAttributeException() {
    }

    public ModifyReadOnlyAttributeException(String message) {
        super(message);
    }

    public ModifyReadOnlyAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModifyReadOnlyAttributeException(Throwable cause) {
        super(cause);
    }
}

