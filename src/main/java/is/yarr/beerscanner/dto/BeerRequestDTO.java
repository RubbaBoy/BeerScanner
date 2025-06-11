package is.yarr.beerscanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Objects;

public class BeerRequestDTO {
    private Long id;
    private String name;
    private String type;
    private String brewery;
    private Double abv;
    private String description;
    private UserDTO requestedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime requestedAt;

    public BeerRequestDTO() {
    }

    // Private constructor to be used by the builder
    private BeerRequestDTO(Long id, String name, String type, String brewery, Double abv, String description, UserDTO requestedBy, LocalDateTime requestedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.brewery = brewery;
        this.abv = abv;
        this.description = description;
        this.requestedBy = requestedBy;
        this.requestedAt = requestedAt;
    }

    // Public static method to get a new builder instance
    public static BeerRequestBuilder builder() {
        return new BeerRequestBuilder();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getBrewery() {
        return brewery;
    }

    public Double getAbv() {
        return abv;
    }

    public String getDescription() {
        return description;
    }

    public UserDTO getRequestedBy() {
        return requestedBy;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeerRequestDTO that = (BeerRequestDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(brewery, that.brewery) &&
                Objects.equals(abv, that.abv) &&
                Objects.equals(description, that.description) &&
                Objects.equals(requestedBy, that.requestedBy) &&
                Objects.equals(requestedAt, that.requestedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, brewery, abv, description, requestedBy, requestedAt);
    }

    @Override
    public String toString() {
        return "BeerRequestDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", brewery='" + brewery + '\'' +
                ", abv=" + abv +
                ", description='" + description + '\'' +
                ", requestedBy=" + (requestedBy != null ? requestedBy.toString() : "null") +
                ", requestedAt=" + requestedAt +
                '}';
    }

    // Static nested Builder class
    public static class BeerRequestBuilder {
        private Long id;
        private String name;
        private String type;
        private String brewery;
        private Double abv;
        private String description;
        private UserDTO requestedBy;
        private LocalDateTime requestedAt;

        // Private constructor for the builder
        private BeerRequestBuilder() {
        }

        public BeerRequestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BeerRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BeerRequestBuilder type(String type) {
            this.type = type;
            return this;
        }

        public BeerRequestBuilder brewery(String brewery) {
            this.brewery = brewery;
            return this;
        }

        public BeerRequestBuilder abv(Double abv) {
            this.abv = abv;
            return this;
        }

        public BeerRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BeerRequestBuilder requestedBy(UserDTO requestedBy) {
            this.requestedBy = requestedBy;
            return this;
        }

        public BeerRequestBuilder requestedAt(LocalDateTime requestedAt) {
            this.requestedAt = requestedAt;
            return this;
        }

        // Build method to create an instance of BeerRequestDTO
        public BeerRequestDTO build() {
            return new BeerRequestDTO(id, name, type, brewery, abv, description, requestedBy, requestedAt);
        }

        @Override
        public String toString() {
            return "BeerRequestDTO.Builder{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", brewery='" + brewery + '\'' +
                    ", abv=" + abv +
                    ", description='" + description + '\'' +
                    ", requestedBy=" + (requestedBy != null ? requestedBy.toString() : "null") +
                    ", requestedAt=" + requestedAt +
                    '}';
        }
    }
}