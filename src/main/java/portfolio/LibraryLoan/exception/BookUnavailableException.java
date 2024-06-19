package portfolio.LibraryLoan.exception;

public class BookUnavailableException extends RuntimeException {

    public BookUnavailableException() {
    }

    public BookUnavailableException(String message) {
        super(message);
    }

    public BookUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookUnavailableException(Throwable cause) {
        super(cause);
    }
}
