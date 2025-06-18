package is.yarr.beerscanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for Beer entity with minimal information for API responses.
 */
public class BeerDTO {
    private Long id;
    private String name;
    private String type;
    private String brewery;
    private Double abv;
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private Set<BarDTO> availableAt;
    private Set<BarDTO> previouslyAvailableAt;

    public BeerDTO() {
    }

    public BeerDTO(Long id, String name, String type, String brewery, Double abv, String description, LocalDateTime createdAt, LocalDateTime updatedAt, Set<BarDTO> availableAt, Set<BarDTO> previouslyAvailableAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.brewery = brewery;
        this.abv = abv;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.availableAt = availableAt;
        this.previouslyAvailableAt = previouslyAvailableAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static BeerDTOBuilder builder() {
        return new BeerDTOBuilder();
    }

    public Set<BarDTO> getAvailableAt() {
        return availableAt;
    }

    public Set<BarDTO> getPreviouslyAvailableAt() {
        return previouslyAvailableAt;
    }

    public static class BeerDTOBuilder {
        private Long id;
        private String name;
        private String type;
        private String brewery;
        private Double abv;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Set<BarDTO> availableAt;
        private Set<BarDTO> previouslyAvailableAt;

        public BeerDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BeerDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BeerDTOBuilder type(String type) {
            this.type = type;
            return this;
        }

        public BeerDTOBuilder brewery(String brewery) {
            this.brewery = brewery;
            return this;
        }

        public BeerDTOBuilder abv(Double abv) {
            this.abv = abv;
            return this;
        }

        public BeerDTOBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BeerDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BeerDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BeerDTOBuilder availableAt(Set<BarDTO> availableAt) {
            this.availableAt = availableAt;
            return this;
        }

        public BeerDTOBuilder previouslyAvailableAt(Set<BarDTO> previouslyAvailableAt) {
            this.previouslyAvailableAt = previouslyAvailableAt;
            return this;
        }

        public BeerDTO build() {
            return new BeerDTO(id, name, type, brewery, abv, description, createdAt, updatedAt, availableAt, previouslyAvailableAt);
        }
    }
}
