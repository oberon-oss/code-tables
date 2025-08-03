package eu.oberon.oss.tools.cc;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Default implementation for the {@link CountryCodeTableEntry} interface.
 *
 * @author TigerLilly64
 * @since 1.0.0
 */
@Log4j2
public record CountryCodeTableEntryImpl(
        @NotNull String countryName,
        @NotNull String iso3166Alpha2Code,
        @NotNull String iso3166Alpha3Code,
        @NotNull String unm49Code)
        implements CountryCodeTableEntry {

    /**
     * Constructor.
     *
     * @param countryName       Name of the country record to create. Cannot not be blank.
     * @param iso3166Alpha2Code The ISO 3166 Alpha 2 character code for the country. Must match pattern [A-Z]{2}.
     * @param iso3166Alpha3Code The ISO 3166 Alpha 3 character code for the country. Must match pattern [A-Z]{3}.
     * @param unm49Code         The UNM49  number for the country. Must match pattern \d{3}.
     *
     * @throws IllegalStateException if any of the parameters are invalid
     * @since 1.0.0
     */
    public CountryCodeTableEntryImpl {
        boolean errorFound = false;

        if (countryName.isBlank()) {
            LOGGER.warn("Country name cannot be blank");
            errorFound = true;
        }

        if (!Pattern.matches("[A-Z]{2}", iso3166Alpha2Code)) {
            LOGGER.warn("Invalid ISO 3166 Alpha 2 code: '{}'", iso3166Alpha2Code);
            errorFound = true;
        }

        if (!Pattern.matches("[A-Z]{3}", iso3166Alpha3Code)) {
            LOGGER.warn("Invalid ISO 3166 Alpha 3 code: '{}'", iso3166Alpha3Code);
            errorFound = true;
        }

        if (!Pattern.matches("\\d{3}", unm49Code)) {
            LOGGER.warn("Invalid UNM49 code: '{}'", unm49Code);
            errorFound = true;
        }

        if (errorFound) {
            throw new IllegalStateException("1 or more errors found");
        }
    }
}
