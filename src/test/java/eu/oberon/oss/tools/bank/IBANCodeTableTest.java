package eu.oberon.oss.tools.bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class IBANCodeTableTest {

    private IBANCodeTable ibanCodeTable;

    @BeforeEach
    void setUp() throws IOException {
        ibanCodeTable = IBANCodeTable.getDefaultInstance();
    }

    @Test
    void defaultInstanceTest() throws IOException {
        IBANCodeTable table1 = assertDoesNotThrow(IBANCodeTable::getDefaultInstance);
        assertNotNull(table1);
        assertEquals(111, table1.getEntryCount());

        IBANCodeTable table2 = IBANCodeTable.getDefaultInstance();
        assertSame(table1, table2);
    }

    @Test
    void loadFromFileTest() {
        File file = new File("src/main/resources/iban_data.yaml");
        IBANCodeTable instance = assertDoesNotThrow(() -> IBANCodeTable.getInstance(file));
        assertEquals(111, instance.getEntryCount());
    }

    @Test
    void loadFromInputStreamTest() {
        String yaml = """
                ibans:
                  -
                    country_code: ZZ
                    length: 20
                    embeds:
                      bank_code:
                        position: 4
                        length: 4
                """;
        assertDoesNotThrow(() -> {
            InputStream is = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
            IBANCodeTable table = assertDoesNotThrow(() -> IBANCodeTable.getInstance(is));
            assertEquals(1, table.getEntryCount());
            IBANCodeEntry entry = table.findEntry("ZZ");
            assertNotNull(entry);
            assertEquals("ZZ", entry.countryCode());
            assertEquals(20, entry.bankAccountNumberLength());
            assertEquals(1, entry.embedded().size());
            Embedded embedded = entry.embedded().iterator().next();
            assertEquals("bank_code", embedded.embeddedTypeName());
            assertEquals(4, embedded.offset());
            assertEquals(4, embedded.length());
        });
    }

    @Test
    void findEntryWithMultipleEmbeds() {
        IBANCodeEntry entry = ibanCodeTable.findEntry("AD");
        assertNotNull(entry);
        assertEquals("AD", entry.countryCode());
        assertEquals(24, entry.bankAccountNumberLength());
        assertEquals(2, entry.embedded().size());

        Embedded bankCode = entry.embedded().stream()
                .filter(e -> "bank_code".equals(e.embeddedTypeName()))
                .findFirst()
                .orElse(null);
        assertNotNull(bankCode);
        assertEquals(4, bankCode.offset());
        assertEquals(4, bankCode.length());

        Embedded branchCode = entry.embedded().stream()
                .filter(e -> "branch_code".equals(e.embeddedTypeName()))
                .findFirst()
                .orElse(null);
        assertNotNull(branchCode);
        assertEquals(8, branchCode.offset());
        assertEquals(4, branchCode.length());
    }

    @Test
    void findEntryWithoutEmbeds() {
        IBANCodeEntry entry = ibanCodeTable.findEntry("AO");
        assertNotNull(entry);
        assertEquals("AO", entry.countryCode());
        assertEquals(25, entry.bankAccountNumberLength());
        assertNotNull(entry.embedded());
        assertTrue(entry.embedded().isEmpty());
    }

    @Test
    void attemptToFindNonExistingEntryTest() {
        assertNull(ibanCodeTable.findEntry("ZZ"));
        assertNull(ibanCodeTable.findEntry("NONE"));
    }

    @Test
    void getAvailableLookupValuesTest() {
        Set<String> values = ibanCodeTable.getAvailableLookupValues();
        assertEquals(111, values.size());
        assertTrue(values.contains("AD"));
        assertTrue(values.contains("NL"));
        assertTrue(values.contains("DE"));
        assertThrows(UnsupportedOperationException.class, values::clear);
    }

    @Test
    void constructorWithNullStreamTest() {
        assertThrows(IllegalArgumentException.class, () -> new IBANCodeTable((InputStream) null));
    }

    @Test
    void constructorWithMapTest() {
        IBANCodeEntry entry = new IBANCodeEntry("TEST", 15);
        IBANCodeTable table = new IBANCodeTable(Map.of("TEST", entry));
        assertEquals(1, table.getEntryCount());
        assertEquals(entry, table.findEntry("TEST"));

        IBANCodeTable emptyTable = new IBANCodeTable((Map<String, IBANCodeEntry>) null);
        assertEquals(0, emptyTable.getEntryCount());
    }

    public static Stream<Arguments> invalidYamlProvider() {
        return Stream.of(
                Arguments.of("", "Invalid YAML format: missing 'ibans' root element"),
                Arguments.of("   \n  ", "Invalid YAML format: missing 'ibans' root element"),
                Arguments.of("not a valid yaml: [", "Error loading IBAN table"),
                Arguments.of("other_key: value", "Invalid YAML format: missing 'ibans' root element"),
                Arguments.of("ibans: not_a_list", "Invalid YAML format: 'ibans' must be a list"),
                Arguments.of("ibans:\n  - not_a_map", "Entry at index 1 is not a map"),
                Arguments.of("ibans:\n  - length: 20", "Entry at index 1 is missing 'country_code'"),
                Arguments.of("ibans:\n  - country_code: ZZ", "Entry at index 1 is missing 'length'")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidYamlProvider")
    void invalidYamlTest(String yaml, String expectedErrorMessage) {
        assertDoesNotThrow(() -> {
            InputStream is = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
            IBANCodeTableException ex = assertThrows(IBANCodeTableException.class, () -> IBANCodeTable.getInstance(is));
            assertEquals(expectedErrorMessage, ex.getMessage());
        });
    }

    @Test
    void loadWithNonMapEmbedsTest() {
        String yaml = """
                ibans:
                  - country_code: ZZ
                    length: 20
                    embeds: "not_a_map"
                """;
        assertDoesNotThrow(() -> {
            InputStream is = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
            IBANCodeTable table = assertDoesNotThrow(() -> IBANCodeTable.getInstance(is));
            IBANCodeEntry entry = table.findEntry("ZZ");
            assertNotNull(entry);
            assertTrue(entry.embedded().isEmpty());
        });
    }

    @Test
    void loadWithNonMapEmbedDetailTest() {
        String yaml = """
                ibans:
                  - country_code: ZZ
                    length: 20
                    embeds:
                      bank_code: "invalid_embed_detail"
                """;
        assertDoesNotThrow(() -> {
            InputStream is = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
            IBANCodeTable table = assertDoesNotThrow(() -> IBANCodeTable.getInstance(is));
            IBANCodeEntry entry = table.findEntry("ZZ");
            assertNotNull(entry);
            assertTrue(entry.embedded().isEmpty());
        });
    }

    @Test
    void loadWithMissingPositionOrLengthInEmbedDetailTest() {
        String yaml = """
                ibans:
                  - country_code: ZZ
                    length: 20
                    embeds:
                      missing_pos:
                        length: 4
                      missing_len:
                        position: 2
                      valid_embed:
                        position: 6
                        length: 4
                """;
        assertDoesNotThrow(() -> {
            InputStream is = new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
            IBANCodeTable table = assertDoesNotThrow(() -> IBANCodeTable.getInstance(is));
            IBANCodeEntry entry = table.findEntry("ZZ");
            assertNotNull(entry);
            assertEquals(1, entry.embedded().size());
            Embedded embed = entry.embedded().iterator().next();
            assertEquals("valid_embed", embed.embeddedTypeName());
            assertEquals(6, embed.offset());
            assertEquals(4, embed.length());
        });
    }

    @Test
    void embeddedModelMethodsTest() {
        Embedded emb1 = new Embedded("bank_code", 4, 4);
        Embedded emb2 = new Embedded("bank_code", 4, 4);
        Embedded emb3 = new Embedded("branch_code", 8, 4);

        assertEquals(emb1, emb2);
        assertNotEquals(emb1, emb3);
        assertEquals(emb1.hashCode(), emb2.hashCode());
        assertNotNull(emb1.toString());
        assertEquals("bank_code", emb1.embeddedTypeName());
        assertEquals(4, emb1.offset());
        assertEquals(4, emb1.length());
    }

    @Test
    void ibanCodeEntryModelMethodsTest() {
        Embedded emb = new Embedded("bank_code", 4, 4);
        IBANCodeEntry entry1 = new IBANCodeEntry("NL", 18, Set.of(emb));
        IBANCodeEntry entry2 = new IBANCodeEntry("NL", 18, Set.of(emb));
        IBANCodeEntry entry3 = new IBANCodeEntry("NL", 18);
        IBANCodeEntry entryNullSet = new IBANCodeEntry("NL", 18, null);

        assertEquals(entry1, entry2);
        assertNotEquals(entry1, entry3);
        assertEquals(entry3, entryNullSet);
        assertEquals(entry1.hashCode(), entry2.hashCode());
        assertNotNull(entry1.toString());
        assertNotNull(entryNullSet.embedded());
        assertTrue(entryNullSet.embedded().isEmpty());

        final Set<Embedded> embedded1 = entry1.embedded();
        assertThrows(UnsupportedOperationException.class, embedded1::clear);

        final Set<Embedded> embedded2 = entry3.embedded();
        assertThrows(UnsupportedOperationException.class, embedded2::clear);

        final Set<Embedded> embedded3 = entryNullSet.embedded();
        assertThrows(UnsupportedOperationException.class, embedded3::clear);
    }
}
