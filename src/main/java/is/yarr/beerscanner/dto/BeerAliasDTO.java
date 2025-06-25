package is.yarr.beerscanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for BeerAlias entity.
 */
public class BeerAliasDTO {
    private Long id;
    private String name;
    private String brewery;
    private Long beerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public BeerAliasDTO() {
    }

    // Private constructor to be used by the builder
    private BeerAliasDTO(Long id, String name, String brewery, Long beerId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.brewery = brewery;
        this.beerId = beerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Public static method to get a new builder instance
    public static BeerAliasBuilder builder() {
        return new BeerAliasBuilder();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrewery() {
        return brewery;
    }

    public Long getBeerId() {
        return beerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeerAliasDTO that = (BeerAliasDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(brewery, that.brewery) &&
                Objects.equals(beerId, that.beerId) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, brewery, beerId, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "BeerAliasDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brewery='" + brewery + '\'' +
                ", beerId=" + beerId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // Static nested Builder class
    public static class BeerAliasBuilder {
        private Long id;
        private String name;
        private String brewery;
        private Long beerId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Private constructor for the builder
        private BeerAliasBuilder() {
        }

        public BeerAliasBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BeerAliasBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BeerAliasBuilder brewery(String brewery) {
            this.brewery = brewery;
            return this;
        }

        public BeerAliasBuilder beerId(Long beerId) {
            this.beerId = beerId;
            return this;
        }

        public BeerAliasBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BeerAliasBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        // Build method to create an instance of BeerAliasDTO
        public BeerAliasDTO build() {
            return new BeerAliasDTO(id, name, brewery, beerId, createdAt, updatedAt);
        }

        @Override
        public String toString() {
            return "BeerAliasDTO.Builder{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", brewery='" + brewery + '\'' +
                    ", beerId=" + beerId +
                    ", createdAt=" + createdAt +
                    ", updatedAt=" + updatedAt +
                    '}';
        }
    }
}