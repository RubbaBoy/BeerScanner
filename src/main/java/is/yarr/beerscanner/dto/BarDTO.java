package is.yarr.beerscanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * DTO for Bar entity with minimal information for API responses.
 */
public class BarDTO {
    private Long id;
    private String name;
    private String location;
    private String aiInstructions;
    private String menuUrl;
    private String menuXPath;
    private String lastMenuHash;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastCheckedAt;

    @JsonProperty("approved")
    private boolean isApproved;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public BarDTO() {
    }

    public BarDTO(Long id, String name, String location, String aiInstructions, String menuUrl, String menuXPath,
                  String lastMenuHash, LocalDateTime lastCheckedAt, boolean isApproved,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.aiInstructions = aiInstructions;
        this.menuUrl = menuUrl;
        this.menuXPath = menuXPath;
        this.lastMenuHash = lastMenuHash;
        this.lastCheckedAt = lastCheckedAt;
        this.isApproved = isApproved;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getMenuXPath() {
        return menuXPath;
    }

    public void setMenuXPath(String menuXPath) {
        this.menuXPath = menuXPath;
    }

    public String getLastMenuHash() {
        return lastMenuHash;
    }

    public void setLastMenuHash(String lastMenuHash) {
        this.lastMenuHash = lastMenuHash;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAiInstructions() {
        return aiInstructions;
    }

    public void setAiInstructions(String aiInstructions) {
        this.aiInstructions = aiInstructions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BarDTO barDTO = (BarDTO) o;
        return isApproved == barDTO.isApproved &&
                Objects.equals(id, barDTO.id) &&
                Objects.equals(name, barDTO.name) &&
                Objects.equals(location, barDTO.location) &&
                Objects.equals(aiInstructions, barDTO.aiInstructions) &&
                Objects.equals(menuUrl, barDTO.menuUrl) &&
                Objects.equals(menuXPath, barDTO.menuXPath) &&
                Objects.equals(lastMenuHash, barDTO.lastMenuHash) &&
                Objects.equals(lastCheckedAt, barDTO.lastCheckedAt) &&
                Objects.equals(createdAt, barDTO.createdAt) &&
                Objects.equals(updatedAt, barDTO.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, location, aiInstructions, menuUrl, menuXPath, lastMenuHash,
                lastCheckedAt, isApproved, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "BarDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", aiInstructions='" + aiInstructions + '\'' +
                ", menuUrl='" + menuUrl + '\'' +
                ", menuXPath='" + menuXPath + '\'' +
                ", lastMenuHash='" + lastMenuHash + '\'' +
                ", lastCheckedAt=" + lastCheckedAt +
                ", isApproved=" + isApproved +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public static BarDTOBuilder builder() {
        return new BarDTOBuilder();
    }

    public static class BarDTOBuilder {
        private Long id;
        private String name;
        private String location;
        private String aiInstructions;
        private String menuUrl;
        private String menuXPath;
        private String lastMenuHash;
        private LocalDateTime lastCheckedAt;
        private boolean isApproved;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

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

        public BarDTOBuilder aiInstructions(String aiInstructions) {
            this.aiInstructions = aiInstructions;
            return this;
        }

        public BarDTOBuilder menuUrl(String menuUrl) {
            this.menuUrl = menuUrl;
            return this;
        }

        public BarDTOBuilder menuXPath(String menuXPath) {
            this.menuXPath = menuXPath;
            return this;
        }

        public BarDTOBuilder lastMenuHash(String lastMenuHash) {
            this.lastMenuHash = lastMenuHash;
            return this;
        }

        public BarDTOBuilder lastCheckedAt(LocalDateTime lastCheckedAt) {
            this.lastCheckedAt = lastCheckedAt;
            return this;
        }

        public BarDTOBuilder isApproved(boolean isApproved) {
            this.isApproved = isApproved;
            return this;
        }

        public BarDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BarDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BarDTO build() {
            return new BarDTO(id, name, location, aiInstructions, menuUrl, menuXPath, lastMenuHash,
                    lastCheckedAt, isApproved, createdAt, updatedAt);
        }
    }
}
