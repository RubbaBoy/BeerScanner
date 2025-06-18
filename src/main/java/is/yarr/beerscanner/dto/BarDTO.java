package is.yarr.beerscanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO for Bar entity with minimal information for API responses.
 */
public class BarDTO {
    private Long id;
    private String name;
    private String location;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastCheckedAt;

    public BarDTO() {
    }

    public BarDTO(Long id, String name, String location, LocalDateTime lastCheckedAt) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.lastCheckedAt = lastCheckedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public static BarDTOBuilder builder() {
        return new BarDTOBuilder();
    }

    public static class BarDTOBuilder {
        private Long id;
        private String name;
        private String location;
        private LocalDateTime lastCheckedAt;

        BarDTOBuilder() {
        }

        public BarDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BarDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BarDTOBuilder location(String location) {
            this.location = location;
            return this;
        }

        public BarDTOBuilder lastCheckedAt(LocalDateTime lastCheckedAt) {
            this.lastCheckedAt = lastCheckedAt;
            return this;
        }

        public BarDTO build() {
            return new BarDTO(id, name, location, lastCheckedAt);
        }
    }
}
