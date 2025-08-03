package eu.oberon.oss.tools.cc;

/**
 * Exception used by country code table code
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public class CountryCodeTableException extends RuntimeException {
    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    public CountryCodeTableException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    public CountryCodeTableException(String message, Throwable cause) {
        super(message, cause);
    }
}
