package eu.oberon.oss.tools.bank;

/**
 * Exception used by IBAN code table operations.
 *
 * @author TigerLilly64
 * @since 2.1.0
 */
public class IBANCodeTableException extends RuntimeException {

    /**
     * Constructs a new IBANCodeTableException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     *
     * @since 2.1.0
     */
    public IBANCodeTableException(String message) {
        super(message);
    }

    /**
     * Constructs a new IBANCodeTableException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause   the cause of the exception
     *
     * @since 2.1.0
     */
    public IBANCodeTableException(String message, Throwable cause) {
        super(message, cause);
    }
}
