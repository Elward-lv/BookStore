package exception;

public class AccessException extends Exception {
    public AccessException() {
        super();
    }

    public AccessException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }
}
