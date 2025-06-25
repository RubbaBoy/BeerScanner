package is.yarr.beerscanner.dto;

import java.util.Objects;

/**
 * DTO for BeerAlias entity.
 */
public class BeerAliasAddDTO {
    private String name;
    private String brewery;

    public BeerAliasAddDTO() {
    }

    // Private constructor to be used by the builder
    private BeerAliasAddDTO(String name, String brewery) {
        this.name = name;
        this.brewery = brewery;
    }

    // Public static method to get a new builder instance
    public static BeerAliasBuilder builder() {
        return new BeerAliasBuilder();
    }

    public String getName() {
        return name;
    }

    public String getBrewery() {
        return brewery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeerAliasAddDTO that = (BeerAliasAddDTO) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(brewery, that.brewery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, brewery);
    }

    @Override
    public String toString() {
        return "BeerAliasAddDTO{" +
                "name='" + name + '\'' +
                ", brewery='" + brewery + '\'' +
                '}';
    }

    // Static nested Builder class
    public static class BeerAliasBuilder {
        private String name;
        private String brewery;

        // Private constructor for the builder
        private BeerAliasBuilder() {
        }

        public BeerAliasBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BeerAliasBuilder brewery(String brewery) {
            this.brewery = brewery;
            return this;
        }

        // Build method to create an instance of BeerAliasAddDTO
        public BeerAliasAddDTO build() {
            return new BeerAliasAddDTO(name, brewery);
        }

        @Override
        public String toString() {
            return "BeerAliasAddDTO.Builder{" +
                    "name='" + name + '\'' +
                    ", brewery='" + brewery + '\'' +
                    '}';
        }
    }
}
