package eu.oberon.oss.tools.bank;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Allows lookup of IBAN configuration and structure from a code table.
 *
 * @author TigerLilly64
 * @since 2.1.0
 */
@Slf4j
public class IBANCodeTable {
    private static final String DEFAULT_RESOURCE_NAME = "iban_data.yaml";

    private final Map<String, IBANCodeEntry> entries;

    private static IBANCodeTable defaultIBANCodeTable;

    /**
     * Creates an {@code IBANCodeTable} with the given map of entries.
     *
     * @param entries The map of country code to {@link IBANCodeEntry}.
     *
     * @since 2.1.0
     */
    public IBANCodeTable(Map<String, IBANCodeEntry> entries) {
        this.entries = entries != null ? Map.copyOf(entries) : Map.of();
    }

    /**
     * Constructs an {@code IBANCodeTable} by reading and parsing YAML from the specified input stream.
     *
     * @param inputStream The input stream containing the YAML data.
     *
     * @since 2.1.0
     */
    public IBANCodeTable(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        this.entries = loadIBANTable(inputStream);
    }

    /**
     * Performs a lookup operation for the specified country code.
     *
     * @param countryCode The 2-letter ISO country code to lookup.
     *
     * @return the {@link IBANCodeEntry} for the specified country code, or {@literal null} if not present.
     *
     * @since 2.1.0
     */
    public @Nullable IBANCodeEntry findEntry(@NotNull String countryCode) {
        return entries.get(countryCode);
    }

    /**
     * Returns the number of IBAN code table entries present.
     *
     * @return The number of records in the IBAN code table.
     *
     * @since 2.1.0
     */
    public int getEntryCount() {
        return entries.size();
    }

    /**
     * Returns the available country codes in this IBAN code table.
     *
     * @return An immutable set of country codes.
     *
     * @since 2.1.0
     */
    public Set<String> getAvailableLookupValues() {
        return entries.keySet();
    }

    /**
     * Returns the default IBAN code table loaded from {@code iban_data.yaml}.
     *
     * @return The default {@code IBANCodeTable} instance.
     *
     * @throws IOException when failed to load the default IBAN code table resource.
     * @since 2.1.0
     */
    public static IBANCodeTable getDefaultInstance() throws IOException {
        if (defaultIBANCodeTable == null) {
            try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(DEFAULT_RESOURCE_NAME)) {
                defaultIBANCodeTable = new IBANCodeTable(inputStream);
            }
        }
        return defaultIBANCodeTable;
    }

    /**
     * Loads a user-supplied IBAN table from the specified file.
     *
     * @param fromFile The file to read from.
     *
     * @return The {@code IBANCodeTable} loaded from the input file.
     *
     * @throws IOException if an error occurred opening or reading from the file.
     * @since 2.1.0
     */
    public static IBANCodeTable getInstance(File fromFile) throws IOException {
        try (InputStream inputStream = new FileInputStream(fromFile)) {
            return getInstance(inputStream);
        }
    }

    /**
     * Loads a user-supplied IBAN table from the specified input stream.
     *
     * @param inputStream The input stream to read from.
     *
     * @return The {@code IBANCodeTable} loaded from the input stream.
     *
     * @since 2.1.0
     */
    public static IBANCodeTable getInstance(InputStream inputStream) {
        return new IBANCodeTable(inputStream);
    }

    private static Map<String, IBANCodeEntry> loadIBANTable(InputStream inputStream) {
        try {
            LoaderOptions options = new LoaderOptions();
            Yaml yaml = new Yaml(new SafeConstructor(options));
            Map<String, Object> data = yaml.load(inputStream);

            if (data == null || !data.containsKey("ibans")) {
                throw new IBANCodeTableException("Invalid YAML format: missing 'ibans' root element");
            }

            Object ibansObj = data.get("ibans");
            if (!(ibansObj instanceof List<?> ibansList)) {
                throw new IBANCodeTableException("Invalid YAML format: 'ibans' must be a list");
            }

            Map<String, IBANCodeEntry> entryMap = new HashMap<>();
            loadEntries(ibansList, entryMap);
            return Map.copyOf(entryMap);
        } catch (IBANCodeTableException e) {
            throw e;
        } catch (Exception e) {
            throw new IBANCodeTableException("Error loading IBAN table", e);
        }
    }

    private static void loadEntries(List<?> ibansList, Map<String, IBANCodeEntry> entryMap) {
        int index = 0;
        for (Object itemObj : ibansList) {
            index++;
            Map<?, ?> itemMap = getItemMap(itemObj, index);

            Object countryCodeObj = getObjectFromItemMap(itemMap, "country_code", index);
            Object lengthObj = getObjectFromItemMap(itemMap, "length", index);

            String countryCode = countryCodeObj.toString();
            int length = ((Number) lengthObj).intValue();

            Set<Embedded> embeddedSet = new HashSet<>();
            Object embedsObj = itemMap.get("embeds");
            if (embedsObj instanceof Map<?, ?> embedsMap) {
                loadEmbeddedDetails(embedsMap, embeddedSet);
            }
            entryMap.put(countryCode, new IBANCodeEntry(countryCode, length, embeddedSet));
        }
    }

    private static void loadEmbeddedDetails(Map<?, ?> embedsMap, Set<Embedded> embeddedSet) {
        for (Map.Entry<?, ?> embedEntry : embedsMap.entrySet()) {
            String embeddedTypeName = embedEntry.getKey().toString();
            if (embedEntry.getValue() instanceof Map<?, ?> embedDetail) {
                Object posObj = embedDetail.get("position");
                Object lenObj = embedDetail.get("length");
                if (posObj != null && lenObj != null) {
                    int offset = ((Number) posObj).intValue();
                    int embLength = ((Number) lenObj).intValue();
                    embeddedSet.add(new Embedded(embeddedTypeName, offset, embLength));
                }
            }
        }
    }

    private static Object getObjectFromItemMap(Map<?, ?> itemMap, String itemName, int index) {
        Object valueObject = itemMap.get(itemName);
        if (valueObject == null) {
            throw new IBANCodeTableException("Entry at index " + index + " is missing '" + itemName + "'");
        }

        return valueObject;
    }

    private static Map<?, ?> getItemMap(Object itemObj, int index) {
        if (!(itemObj instanceof Map<?, ?> itemMap)) {
            throw new IBANCodeTableException("Entry at index " + index + " is not a map");
        }

        return itemMap;
    }
}
