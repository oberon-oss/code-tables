## Build status:

[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_code-tables&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=oberon-oss_code-tables)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_code-tables&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=oberon-oss_code-tables)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_code-tables&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=oberon-oss_code-tables)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_code-tables&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=oberon-oss_code-tables)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_code-tables&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=oberon-oss_code-tables)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_code-tables&metric=coverage)](https://sonarcloud.io/summary/new_code?id=oberon-oss_code-tables)

[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_code-tables&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=oberon-oss_code-tables)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_code-tables&metric=bugs)](https://sonarcloud.io/summary/new_code?id=oberon-oss_code-tables)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_code-tables&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=oberon-oss_code-tables)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_code-tables&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=oberon-oss_code-tables)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=oberon-oss_code-tables&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=oberon-oss_code-tables)

---

# Code Tables

A lightweight, high-performance Java library providing lookup tables and metadata for standard international codes, including **ISO/UN Country Codes** and **IBAN structures**.

---

## Features

- **Country Code Table (`CountryCodeTable`)**:
  - Comprehensive ISO 3166-1 and UN M49 standard reference data (250 countries/territories).
  - Fast bidirectional lookups by:
    - Country Name (`NAME`)
    - ISO 3166-1 Alpha-2 code (`ISO3166_ALPHA_2`, e.g., `NL`, `US`)
    - ISO 3166-1 Alpha-3 code (`ISO3166_ALPHA_3`, e.g., `NLD`, `USA`)
    - UN M49 numeric code (`UNM49`, e.g., `528`, `840`)
  - Built-in default dataset and support for loading custom semicolon-separated CSV files or streams.

- **IBAN Code Table (`IBANCodeTable`)**:
  - Specification data for International Bank Account Numbers across 110+ countries.
  - Retrieves expected account number lengths and embedded structures (e.g., `bank_code`, `branch_code`) with 0-based character offsets and lengths.
  - Built-in default dataset (`iban_data.yaml`) and support for loading custom YAML configurations or streams.

- **Type-safe & Immutable**:
  - Implemented using Java records (`CountryCodeTableEntryImpl`, `IBANCodeEntry`, `Embedded`).
  - Thread-safe, cached default singletons and immutable lookup collections.

---

## Requirements

- **Java**: 25 or higher
- **Build Tool**: Maven 3.8+

---

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>eu.oberon.oss.tools</groupId>
    <artifactId>code-tables</artifactId>
    <version>2.1.0</version>
</dependency>
```

If needed, configure the repository:

```xml
<repositories>
    <repository>
        <id>nexus-releases</id>
        <name>Nexus Release Repository</name>
        <url>https://nexus.oberon-oss.eu/repository/maven-releases/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

---

## Usage

### 1. Country Code Lookups (`CountryCodeTable`)

#### Basic Lookup Using the Default Instance

```java
import eu.oberon.oss.tools.cc.CountryCodeTable;
import eu.oberon.oss.tools.cc.CountryCodeTableEntry;
import eu.oberon.oss.tools.cc.CountryCodeTableLookupKeys;

import java.io.IOException;
import java.util.Set;

public class CountryExample {
    public static void main(String[] args) throws IOException {
        // Retrieve the default singleton instance (loaded from bundled country-codes.csv)
        CountryCodeTable table = CountryCodeTable.getDefaultInstance();

        // Lookup by ISO Alpha-2
        CountryCodeTableEntry nl = table.findEntry("NL", CountryCodeTableLookupKeys.ISO3166_ALPHA_2);
        if (nl != null) {
            System.out.println("Country Name: " + nl.countryName());         // "Netherlands (the)"
            System.out.println("Alpha-2:      " + nl.iso3166Alpha2Code());   // "NL"
            System.out.println("Alpha-3:      " + nl.iso3166Alpha3Code());   // "NLD"
            System.out.println("UN M49:       " + nl.unm49Code());           // "528"
        }

        // Lookup by Country Name
        CountryCodeTableEntry us = table.findEntry("United States of America (the)", CountryCodeTableLookupKeys.NAME);

        // Lookup by ISO Alpha-3
        CountryCodeTableEntry gb = table.findEntry("GBR", CountryCodeTableLookupKeys.ISO3166_ALPHA_3);

        // Lookup by UN M49 Numeric Code
        CountryCodeTableEntry de = table.findEntry("276", CountryCodeTableLookupKeys.UNM49);

        // Query available keys and counts
        int totalCountries = table.getEntryCount(); // 250
        Set<String> allAlpha2 = table.getAvailableLookupValues(CountryCodeTableLookupKeys.ISO3166_ALPHA_2);
    }
}
```

#### Loading a Custom Country Table

You can load a custom country table from a `File` or `InputStream` (semicolon-separated format):

```java
File customFile = new File("/path/to/custom-country-codes.csv");
CountryCodeTable customTable = CountryCodeTable.getInstance(customFile);
```

---

### 2. IBAN Structure Lookups (`IBANCodeTable`)

#### Basic Lookup Using the Default Instance

```java
import eu.oberon.oss.tools.bank.IBANCodeTable;
import eu.oberon.oss.tools.bank.IBANCodeEntry;
import eu.oberon.oss.tools.bank.Embedded;

import java.io.IOException;

public class IBANExample {
    public static void main(String[] args) throws IOException {
        // Retrieve the default singleton instance (loaded from bundled iban_data.yaml)
        IBANCodeTable table = IBANCodeTable.getDefaultInstance();

        // Lookup IBAN specifications by 2-letter country code
        IBANCodeEntry entry = table.findEntry("AD");
        if (entry != null) {
            System.out.println("Country Code:   " + entry.countryCode());             // "AD"
            System.out.println("IBAN Length:    " + entry.bankAccountNumberLength()); // 24

            // Inspect embedded components (e.g., bank_code, branch_code)
            for (Embedded embed : entry.embedded()) {
                System.out.printf("Component: %s (offset: %d, length: %d)%n",
                        embed.embeddedTypeName(), embed.offset(), embed.length());
            }
        }
    }
}
```

#### Loading a Custom IBAN Table

You can load custom IBAN structure data from a YAML `File` or `InputStream`:

```java
File customYaml = new File("/path/to/custom_iban_data.yaml");
IBANCodeTable customTable = IBANCodeTable.getInstance(customYaml);
```

---

## Data Formats

### Country Codes CSV Format

CSV files must use semicolon (`;`) separators with four required columns:
```csv
Country Name;Alpha-2;Alpha-3;UNM49
Netherlands (the);NL;NLD;528
United Kingdom of Great Britain and Northern Ireland (the);GB;GBR;826
United States of America (the);US;USA;840
```

- **Validation Rules**:
  - `Country Name`: non-blank string
  - `Alpha-2`: 2 uppercase letters (`[A-Z]{2}`)
  - `Alpha-3`: 3 uppercase letters (`[A-Z]{3}`)
  - `UNM49`: 3 digits (`\d{3}`)

### IBAN YAML Format
Based on the format of [IBAN.yaml](https://github.com/barend/java-iban/blame/main/src/main/resources/nl/garvelink/iban/IBAN.yml)
The main difference is that the swift/sepa flags where removed. For the code tables they have no added value

YAML structure defining IBAN configurations:
```yaml
ibans:
  - country_code: AD
    length: 24
    embeds:
      bank_code:
        position: 4
        length: 4
      branch_code:
        position: 8
        length: 4
  - country_code: AO
    length: 25
```
---

## Build & Test

To build the project and execute all tests:

```bash
mvn clean verify
```

To run only the unit tests:

```bash
mvn test
```

---

## License

This project is licensed under the MIT License.
