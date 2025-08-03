package eu.oberon.oss.tools.cc;

import org.jetbrains.annotations.NotNull;

/**
 * Represents basic information about a country.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
public interface CountryCodeTableEntry {

    /**
     * Returns the name of the country
     *
     * @return The country's name
     *
     * @since 1.0.0
     */
    @NotNull String countryName();

    /**
     * Returns the ISO 3166 Alpha 2 code for the country
     *
     * @return The country's ISO 3166 Alpha 2 code
     *
     * @since 1.0.0
     */
    @NotNull String iso3166Alpha2Code();

    /**
     * Returns the ISO 3166 Alpha 3 code for the country
     *
     * @return The country's ISO 3166 Alpha 3 code
     *
     * @since 1.0.0
     */
    @NotNull String iso3166Alpha3Code();

    /**
     * Returns the UNM49 code for the country
     *
     * @return The country's UNM49 code
     *
     * @since 1.0.0
     */
    @NotNull String unm49Code();
}
