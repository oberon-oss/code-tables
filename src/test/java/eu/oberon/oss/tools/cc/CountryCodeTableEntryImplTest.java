package eu.oberon.oss.tools.cc;


import nl.altindag.log.LogCaptor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryCodeTableEntryImplTest {
    LogCaptor logCaptor = LogCaptor.forClass(CountryCodeTableEntryImpl.class);

    @Test
    void constructorTestWithInvalidParameters() {
        assertThrows(
                IllegalStateException.class,
                () -> new CountryCodeTableEntryImpl("", "INVALID ALPHA 2", "INVALID ALPHA 3", "INVALID UNM49")
        );
        assertThat(logCaptor.getLogs())
                .hasSize(4)
                .contains(
                        "Country name cannot be blank",
                        "Invalid ISO 3166 Alpha 2 code: 'INVALID ALPHA 2'",
                        "Invalid ISO 3166 Alpha 3 code: 'INVALID ALPHA 3'",
                        "Invalid UNM49 code: 'INVALID UNM49'"
                );
    }

    @Test
    void constructorTestWithValidParameters() {
        CountryCodeTableEntryImpl entry = assertDoesNotThrow(
                () -> new CountryCodeTableEntryImpl("NAME", "XX", "XXX", "999")
        );
        assertEquals("NAME", entry.countryName());
        assertEquals("XX", entry.iso3166Alpha2Code());
        assertEquals("XXX", entry.iso3166Alpha3Code());
        assertEquals("999", entry.unm49Code());

    }
}