package eu.oberon.oss.tools.bank;

/**
 * Represents an embedded data segment (such as bank_code or branch_code) within an IBAN structure.
 *
 * @param embeddedTypeName the type of embedded data (e.g., `bank_code`, `branch_code`)
 * @param offset           the 0-based character index where the embedded data begins
 * @param length           the character length of the embedded data
 *
 * @author TigerLilly64
 * @since 2.1.0
 */
public record Embedded(String embeddedTypeName, int offset, int length) {
}
