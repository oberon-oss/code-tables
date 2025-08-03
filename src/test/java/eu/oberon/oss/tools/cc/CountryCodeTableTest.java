package eu.oberon.oss.tools.cc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import static eu.oberon.oss.tools.cc.CountryCodeTableLookupKeys.ISO3166_ALPHA_2;
import static eu.oberon.oss.tools.cc.CountryCodeTableLookupKeys.*;
import static org.junit.jupiter.api.Assertions.*;

class CountryCodeTableTest {

    private CountryCodeTable cct;

    @BeforeEach
    void setUp() throws IOException {
        cct = CountryCodeTable.getDefaultInstance();
    }

    public static Stream<Arguments> findEntryTest() throws IOException {
        CountryCodeTableEntry nl = CountryCodeTable.getDefaultInstance().findEntry("NL", ISO3166_ALPHA_2);
        CountryCodeTableEntry gb = CountryCodeTable.getDefaultInstance().findEntry("GB", ISO3166_ALPHA_2);
        CountryCodeTableEntry um = CountryCodeTable.getDefaultInstance().findEntry("UM", ISO3166_ALPHA_2);
        CountryCodeTableEntry us = CountryCodeTable.getDefaultInstance().findEntry("US", ISO3166_ALPHA_2);

        return Stream.of(
                Arguments.of(us, new String[]{"United States of America (the)", "US", "USA", "840"}),
                Arguments.of(um, new String[]{"United States Minor Outlying Islands (the)", "UM", "UMI", "581"}),
                Arguments.of(nl, new String[]{"Netherlands (the)", "NL", "NLD", "528"}),
                Arguments.of(gb, new String[]{"United Kingdom of Great Britain and Northern Ireland (the)", "GB", "GBR", "826"})
        );
    }

    @ParameterizedTest
    @MethodSource
    void findEntryTest(CountryCodeTableEntry entry, String[] lookupValues) {
        for (CountryCodeTableLookupKeys key : CountryCodeTableLookupKeys.values()) {
            CountryCodeTableEntry entryFound = cct.findEntry(lookupValues[key.ordinal()], key);
            assertEquals(entry, entryFound);
        }
    }

    @Test
    void attemptToFindNonExistingEntryTest() {
        assertNull(cct.findEntry("XX", ISO3166_ALPHA_2));
        assertNull(cct.findEntry("XXX", ISO3166_ALPHA_3));
        assertNull(cct.findEntry("X X X", NAME));
        assertNull(cct.findEntry("-11", UNM49));
    }

    @Test
    void defaultInstanceTest() throws IOException {
        CountryCodeTable cct1 = assertDoesNotThrow(CountryCodeTable::getDefaultInstance);
        assertEquals(249, cct1.getEntryCount());
        CountryCodeTable cct2 = CountryCodeTable.getDefaultInstance();
        assertEquals(cct1, cct2);
    }

    @Test
    void loadFromFileTest() {
        File fromFile = new File("src/main/resources/country-codes.csv");
        CountryCodeTable instance = assertDoesNotThrow(() -> CountryCodeTable.getInstance(fromFile));
        assertEquals(249, instance.getEntryCount());
    }

    public static Stream<Arguments> loadFromFileWithInvalidContentTest() {
        return Stream.of(
                Arguments.of(new File("src/test/resources/csv-files/incorrect-name.csv")),
                Arguments.of(new File("src/test/resources/csv-files/incorrect-alpha3-code.csv")),
                Arguments.of(new File("src/test/resources/csv-files/incorrect-alpha2-code.csv")),
                Arguments.of(new File("src/test/resources/csv-files/incorrect-unm49-code.csv"))
        );
    }

    @ParameterizedTest
    @MethodSource
    void loadFromFileWithInvalidContentTest(File fromFile) {
        CountryCodeTableException exception = assertThrows(
                CountryCodeTableException.class, () -> CountryCodeTable.getInstance(fromFile
                ));
        assertEquals("Error loading table", exception.getMessage());
    }


    public static Stream<Arguments> loadFromFileContainingMoreOrLessFields() {
        return Stream.of(
                Arguments.of(0, new File("src/test/resources/csv-files/line-containing-3-fields.csv")),
                Arguments.of(1, new File("src/test/resources/csv-files/line-containing-5-fields.csv"))
        );
    }

    @ParameterizedTest
    @MethodSource
    void loadFromFileContainingMoreOrLessFields(int expectedEntries, File fromFile) {
        if (expectedEntries <= 0) {
            CountryCodeTableException exception = assertThrows(
                    CountryCodeTableException.class, () -> CountryCodeTable.getInstance(fromFile)
            );
            assertEquals("Line 1 does not have 4 columns", exception.getMessage());
        } else {
            CountryCodeTable table = assertDoesNotThrow(() -> CountryCodeTable.getInstance(fromFile));
            assertEquals(expectedEntries, table.getEntryCount());
        }
    }

    @Test
    void getAvailableLookupValuesTest() {
        for (CountryCodeTableLookupKeys key : CountryCodeTableLookupKeys.values()) {
            Set<String> values = cct.getAvailableLookupValues(key);
            assertEquals(cct.getEntryCount(), values.size());
            assertThrows(UnsupportedOperationException.class, values::clear);
        }
    }
}