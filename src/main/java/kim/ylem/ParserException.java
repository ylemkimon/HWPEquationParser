package kim.ylem;

/**
 * The exception for parsing errors for XML, HML, and HE parsers.
 */
public class ParserException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception without an message
     */
    public ParserException() {
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public ParserException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause
     */
    public ParserException(Throwable cause) {
        super(cause);
    }
}
