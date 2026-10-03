package eu.oberon.oss.tools.cc;

/**
 * Exception used by country code table code
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class CountryCodeTableException extends RuntimeException {
    /**
     * Constructs a new CountryCodeTableException with the specified detail message.
     *
     * @param message The detail message explaining the reason for the exception.
     *
     * @since 1.0.0
     */
    public CountryCodeTableException(String message) {
        super(message);
    }

    /**
     * Constructs a new CountryCodeTableException with the specified detail message and cause.
     *
     * @param message The detail message explaining the reason for the exception.
     * @param cause   The cause of the exception. A null value is permitted and indicates that the cause is nonexistent or unknown.
     *
     * @since 1.0.0
     */
    public CountryCodeTableException(String message, Throwable cause) {
        super(message, cause);
    }
}
