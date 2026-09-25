package eu.oberon.oss.tools.bank;

/**
 * Exception used by IBAN code table operations.
 *
 * @author TigerLilly64
 * @since 2.1.0
 */
public class IBANCodeTableException extends RuntimeException {

    /**
     * {@inheritDoc}
     *
     * @since 2.1.0
     */
    public IBANCodeTableException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.1.0
     */
    public IBANCodeTableException(String message, Throwable cause) {
        super(message, cause);
    }
}
