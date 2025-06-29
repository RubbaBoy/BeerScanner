package is.yarr.beerscanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing a check of a bar's menu.
 * Used to track the history of menu checks and changes.
 */
@Entity
@Table(name = "bar_checks")
public class BarCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bar_id", nullable = false)
    private Bar bar;

    @Column(name = "menu_hash")
    private String menuHash;

    @Column(name = "menu_content", columnDefinition = "TEXT")
    private String menuContent;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "has_changes", nullable = false)
    private boolean hasChanges;

    @Column(name = "process_duration")
    private int processDuration = 0; // Time it took to collect menu content & determine if changes were made

    @Column(name = "processing_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessingStatus processingStatus;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "bar_check_beers_added",
            joinColumns = @JoinColumn(name = "bar_check_id"),
            inverseJoinColumns = @JoinColumn(name = "beer_id")
    )
    private Set<Beer> beersAdded = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "bar_check_beers_removed",
            joinColumns = @JoinColumn(name = "bar_check_id"),
            inverseJoinColumns = @JoinColumn(name = "beer_id")
    )
    private Set<Beer> beersRemoved = new HashSet<>();


    /**
     * Status of the menu processing.
     */
    public enum ProcessingStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    // Default constructor
    public BarCheck() {
    }

    // All-args constructor
    public BarCheck(Long id, Bar bar, String menuHash, String menuContent, String contentType,
                    boolean hasChanges, int processDuration, ProcessingStatus processingStatus,
                    String errorMessage, LocalDateTime createdAt) {
        this.id = id;
        this.bar = bar;
        this.menuHash = menuHash;
        this.menuContent = menuContent;
        this.contentType = contentType;
        this.hasChanges = hasChanges;
        this.processDuration = processDuration;
        this.processingStatus = processingStatus;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }

    // Builder pattern implementation
    public static BarCheckBuilder builder() {
        return new BarCheckBuilder();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Bar getBar() {
        return bar;
    }

    public String getMenuHash() {
        return menuHash;
    }

    public String getMenuContent() {
        return menuContent;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isHasChanges() {
        return hasChanges;
    }

    public int getProcessDuration() {
        return processDuration;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<Beer> getBeersAdded() {
        return beersAdded;
    }

    public Set<Beer> getBeersRemoved() {
        return beersRemoved;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public void setMenuHash(String menuHash) {
        this.menuHash = menuHash;
    }

    public void setMenuContent(String menuContent) {
        this.menuContent = menuContent;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setHasChanges(boolean hasChanges) {
        this.hasChanges = hasChanges;
    }

    public void setProcessDuration(int processDuration) {
        this.processDuration = processDuration;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setBeersAdded(Set<Beer> beersAdded) {
        this.beersAdded = beersAdded;
    }

    public void setBeersRemoved(Set<Beer> beersRemoved) {
        this.beersRemoved = beersRemoved;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BarCheck barCheck = (BarCheck) o;
        return Objects.equals(id, barCheck.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString
    @Override
    public String toString() {
        return "BarCheck{" +
                "id=" + id +
                ", bar=" + bar +
                ", menuHash='" + menuHash + '\'' +
                ", menuContent='" + menuContent + '\'' +
                ", contentType='" + contentType + '\'' +
                ", hasChanges=" + hasChanges +
                ", processingStatus=" + processingStatus +
                ", errorMessage='" + errorMessage + '\'' +
                ", createdAt=" + createdAt +
                ", processDuration=" + processDuration +
                ", beersAdded=" + beersAdded +
                ", beersRemoved=" + beersRemoved +
                '}';
    }

    // Builder class
    public static class BarCheckBuilder {
        private Long id;
        private Bar bar;
        private String menuHash;
        private String menuContent;
        private String contentType;
        private boolean hasChanges;
        private int processDuration;
        private ProcessingStatus processingStatus;
        private String errorMessage;
        private LocalDateTime createdAt;

        public BarCheckBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BarCheckBuilder bar(Bar bar) {
            this.bar = bar;
            return this;
        }

        public BarCheckBuilder menuHash(String menuHash) {
            this.menuHash = menuHash;
            return this;
        }

        public BarCheckBuilder menuContent(String menuContent) {
            this.menuContent = menuContent;
            return this;
        }

        public BarCheckBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public BarCheckBuilder hasChanges(boolean hasChanges) {
            this.hasChanges = hasChanges;
            return this;
        }

        public BarCheckBuilder processDuration(int processDuration) {
            this.processDuration = processDuration;
            return this;
        }

        public BarCheckBuilder processingStatus(ProcessingStatus processingStatus) {
            this.processingStatus = processingStatus;
            return this;
        }

        public BarCheckBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public BarCheckBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BarCheck build() {
            return new BarCheck(id, bar, menuHash, menuContent, contentType, hasChanges, processDuration,
                    processingStatus, errorMessage, createdAt);
        }
    }
}