package eu.oberon.oss.tools.cc;

/**
 * Defines the available lookup key types to query a country code table.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public enum CountryCodeTableLookupKeys {
    /**
     * Represent the name of the country to lookup.
     *
     * @since 1.0.0
     */
    NAME,
    /**
     * Represent the 2-letter code of the country to lookup.
     *
     * @since 1.0.0
     */
    ISO3166_ALPHA_2,
    /**
     * Represent the 3-letter code of the country to lookup.
     *
     * @since 1.0.0
     */
    ISO3166_ALPHA_3,
    /**
     * Represents the ' Standard Country or Area Codes for Statistical Use' code of the country to lookup.
     *
     * @since 1.0.0
     */
    UNM49
}
