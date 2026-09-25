package eu.oberon.oss.tools.bank;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Represents an IBAN entry for a specific country, including account number length and embedded structure rules.
 *
 * @param countryCode             The ISO 3306 Alpha 2 country code
 * @param bankAccountNumberLength The required bank account number length
 * @param embedded                Additional data for the IBAN bank account number.
 *
 * @author TigerLilly64
 * @since 2.1.0
 */
public record IBANCodeEntry(String countryCode, int bankAccountNumberLength, Set<Embedded> embedded) {

    /**
     * Constructs an IBANCodeEntry object with the specified country code and bank account number length. Defaults the embedded structure rules to an empty
     * set.
     *
     * @param countryCode             The ISO 3306 Alpha 2 country code.
     * @param bankAccountNumberLength The required length of the bank account number.
     *
     * @since 2.1.0
     */
    public IBANCodeEntry(@NotNull String countryCode, int bankAccountNumberLength) {
        this(countryCode, bankAccountNumberLength, Set.of());
    }

    /**
     * Constructs an IBANCodeEntry object with the specified country code, bank account number length, and embedded structure rules.
     *
     * @param countryCode             The ISO 3306 Alpha 2 country code.
     * @param bankAccountNumberLength The required length of the bank account number.
     * @param embedded                Additional data for the IBAN bank account number.
     *
     * @since 2.1.0
     */
    public IBANCodeEntry(@NotNull String countryCode, int bankAccountNumberLength, @Nullable Set<Embedded> embedded) {
        this.countryCode = countryCode;
        this.bankAccountNumberLength = bankAccountNumberLength;
        this.embedded = embedded != null ? Set.copyOf(embedded) : Set.of();
    }

    @Override
    public @NotNull String toString() {
        final StringBuilder sb = new StringBuilder("IBANCodeEntry{");
        sb.append("countryCode='").append(countryCode).append('\'');
        sb.append(", bankAccountNumberLength=").append(bankAccountNumberLength);

        if (!embedded.isEmpty()) {
            for (Embedded embeddedEntry : embedded) {
                sb.append(embeddedEntry.embeddedTypeName()).append(": offset=").append(embeddedEntry.offset()).append(", length=").append(embeddedEntry.length());
            }
        }

        sb.append('}');
        return sb.toString();
    }
}
