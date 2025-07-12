package is.yarr.beerscanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import is.yarr.beerscanner.model.BarCheck;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * DTO for BarCheck entity with minimal information for API responses.
 */
public class BarCheckDTO {
    private Long id;
    private BarDTO bar;
    private String menuHash;
    private String contentType;
    private boolean hasChanges;
    private int processDuration; // Duration in seconds for processing the check
    private BarCheck.ProcessingStatus processingStatus;
    private String errorMessage;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Set<BeerDTO> beersAdded;
    private Set<BeerDTO> beersRemoved;

    public BarCheckDTO() {
    }

    public BarCheckDTO(Long id, BarDTO bar, String menuHash, String contentType, boolean hasChanges, int processDuration, BarCheck.ProcessingStatus processingStatus, String errorMessage, LocalDateTime createdAt, Set<BeerDTO> beersAdded, Set<BeerDTO> beersRemoved) {
        this.id = id;
        this.bar = bar;
        this.menuHash = menuHash;
        this.contentType = contentType;
        this.hasChanges = hasChanges;
        this.processDuration = processDuration;
        this.processingStatus = processingStatus;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.beersAdded = beersAdded;
        this.beersRemoved = beersRemoved;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BarDTO getBar() {
        return bar;
    }

    public void setBar(BarDTO bar) {
        this.bar = bar;
    }

    public String getMenuHash() {
        return menuHash;
    }

    public void setMenuHash(String menuHash) {
        this.menuHash = menuHash;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isHasChanges() {
        return hasChanges;
    }

    public void setHasChanges(boolean hasChanges) {
        this.hasChanges = hasChanges;
    }

    public int getProcessDuration() {
        return processDuration;
    }

    public void setProcessDuration(int processDuration) {
        this.processDuration = processDuration;
    }

    public BarCheck.ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(BarCheck.ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<BeerDTO> getBeersAdded() {
        return beersAdded;
    }

    public void setBeersAdded(Set<BeerDTO> beersAdded) {
        this.beersAdded = beersAdded;
    }

    public Set<BeerDTO> getBeersRemoved() {
        return beersRemoved;
    }

    public void setBeersRemoved(Set<BeerDTO> beersRemoved) {
        this.beersRemoved = beersRemoved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BarCheckDTO that = (BarCheckDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BarCheckDTO{" +
                "id=" + id +
                ", bar=" + bar.getId() +
                ", menuHash='" + menuHash + '\'' +
                ", contentType='" + contentType + '\'' +
                ", hasChanges=" + hasChanges +
                ", processingStatus=" + processingStatus +
                ", errorMessage='" + errorMessage + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private BarDTO bar;
        private String contentType;
        private String menuHash;
        private boolean hasChanges;
        private int processDuration;
        private BarCheck.ProcessingStatus processingStatus;
        private String errorMessage;
        private LocalDateTime createdAt;
        private Set<BeerDTO> beersAdded;
        private Set<BeerDTO> beersRemoved;

        Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder bar(BarDTO bar) {
            this.bar = bar;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder menuHash(String menuHash) {
            this.menuHash = menuHash;
            return this;
        }

        public Builder hasChanges(boolean hasChanges) {
            this.hasChanges = hasChanges;
            return this;
        }

        public Builder processDuration(int processDuration) {
            this.processDuration = processDuration;
            return this;
        }

        public Builder processingStatus(BarCheck.ProcessingStatus processingStatus) {
            this.processingStatus = processingStatus;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder beersAdded(Set<BeerDTO> beersAdded) {
            this.beersAdded = beersAdded;
            return this;
        }

        public Builder beersRemoved(Set<BeerDTO> beersRemoved) {
            this.beersRemoved = beersRemoved;
            return this;
        }

        public BarCheckDTO build() {
            return new BarCheckDTO(id, bar, menuHash, contentType, hasChanges, processDuration, processingStatus, errorMessage, createdAt, beersAdded, beersRemoved);
        }

        public String toString() {
            return "BarCheckDTO.Builder(id=" + this.id + ", bar=" + this.bar.getId() + ", menuHash=" + this.menuHash + ", contentType=" + this.contentType + ", hasChanges=" + this.hasChanges + ", processingStatus=" + this.processingStatus + ", errorMessage=" + this.errorMessage + ", createdAt=" + this.createdAt + ")";
        }
    }
}
