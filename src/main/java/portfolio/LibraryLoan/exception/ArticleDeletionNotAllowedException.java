package portfolio.LibraryLoan.exception;

public class ArticleDeletionNotAllowedException extends RuntimeException {

    public ArticleDeletionNotAllowedException() {
    }

    public ArticleDeletionNotAllowedException(String message) {
        super(message);
    }

    public ArticleDeletionNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArticleDeletionNotAllowedException(Throwable cause) {
        super(cause);
    }
}
