package is.yarr.beerscanner.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing a bar in the system.
 */
@Entity
@Table(name = "bars")
public class Bar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "ai_instructions")
    private String aiInstructions;

    @Column(name = "menu_url")
    private String menuUrl;

    @Column(name = "menu_xpath")
    private String menuXPath;

    @Column(name = "last_menu_hash")
    private String lastMenuHash;

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    @Column(name = "is_approved", nullable = false)
    private boolean isApproved;

    @ManyToMany
    @JoinTable(
            name = "bar_current_beers",
            joinColumns = @JoinColumn(name = "bar_id"),
            inverseJoinColumns = @JoinColumn(name = "beer_id")
    )
    private Set<Beer> currentBeers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "bar_past_beers",
            joinColumns = @JoinColumn(name = "bar_id"),
            inverseJoinColumns = @JoinColumn(name = "beer_id")
    )
    private Set<Beer> pastBeers = new HashSet<>();

    @ManyToMany(mappedBy = "trackedBars")
    private Set<User> trackedBy = new HashSet<>();

    @OneToMany(mappedBy = "bar", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BarCheck> checks = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "requested_by_id")
    private User requestedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Embedded
    private BarWebpageSettings webpageSettings;

    // Default constructor
    public Bar() {
    }

    // All-args constructor
    public Bar(Long id, String name, String location, String aiInstructions, String menuUrl, String menuXPath,
               String lastMenuHash, LocalDateTime lastCheckedAt, boolean isApproved,
               Set<Beer> currentBeers, Set<Beer> pastBeers, Set<User> trackedBy,
               Set<BarCheck> checks, User requestedBy, LocalDateTime createdAt,
               LocalDateTime updatedAt, BarWebpageSettings webpageSettings) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.aiInstructions = aiInstructions;
        this.menuUrl = menuUrl;
        this.menuXPath = menuXPath;
        this.lastMenuHash = lastMenuHash;
        this.lastCheckedAt = lastCheckedAt;
        this.isApproved = isApproved;
        this.currentBeers = currentBeers != null ? currentBeers : new HashSet<>();
        this.pastBeers = pastBeers != null ? pastBeers : new HashSet<>();
        this.trackedBy = trackedBy != null ? trackedBy : new HashSet<>();
        this.checks = checks != null ? checks : new HashSet<>();
        this.requestedBy = requestedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.webpageSettings = webpageSettings;
    }

    // Builder pattern implementation
    public static BarBuilder builder() {
        return new BarBuilder();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getAiInstructions() {
        return aiInstructions;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public String getMenuXPath() {
        return menuXPath;
    }

    public String getLastMenuHash() {
        return lastMenuHash;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public Set<Beer> getCurrentBeers() {
        return currentBeers;
    }

    public Set<Beer> getPastBeers() {
        return pastBeers;
    }

    public Set<User> getTrackedBy() {
        return trackedBy;
    }

    public Set<BarCheck> getChecks() {
        return checks;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public BarWebpageSettings getWebpageSettings() {
        return webpageSettings;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAiInstructions(String aiInstructions) {
        this.aiInstructions = aiInstructions;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public void setMenuXPath(String menuXPath) {
        this.menuXPath = menuXPath;
    }

    public void setLastMenuHash(String lastMenuHash) {
        this.lastMenuHash = lastMenuHash;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public void setCurrentBeers(Set<Beer> currentBeers) {
        this.currentBeers = currentBeers;
    }

    public void setPastBeers(Set<Beer> pastBeers) {
        this.pastBeers = pastBeers;
    }

    public void setTrackedBy(Set<User> trackedBy) {
        this.trackedBy = trackedBy;
    }

    public void setChecks(Set<BarCheck> checks) {
        this.checks = checks;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setWebpageSettings(BarWebpageSettings webpageSettings) {
        this.webpageSettings = webpageSettings;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bar bar = (Bar) o;
        return isApproved == bar.isApproved &&
                Objects.equals(id, bar.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


    // toString
    @Override
    public String toString() {
        return "Bar{" +
                "id=" + id +
//                ", name='" + name + '\'' +
//                ", location='" + location + '\'' +
//                ", aiInstructions='" + aiInstructions + '\'' +
//                ", menuUrl='" + menuUrl + '\'' +
//                ", menuXPath='" + menuXPath + '\'' +
//                ", lastMenuHash='" + lastMenuHash + '\'' +
//                ", lastCheckedAt=" + lastCheckedAt +
//                ", isApproved=" + isApproved +
//                ", currentBeers=" + currentBeers +
//                ", pastBeers=" + pastBeers +
//                ", trackedBy=" + trackedBy +
//                ", checks=" + checks +
//                ", requestedBy=" + requestedBy +
//                ", createdAt=" + createdAt +
//                ", updatedAt=" + updatedAt +
                ", webpageSettings=" + webpageSettings +
                '}';
    }

    // Builder class
    public static class BarBuilder {
        private Long id;
        private String name;
        private String location;
        private String aiInstructions;
        private String menuUrl;
        private String menuXPath;
        private String lastMenuHash;
        private LocalDateTime lastCheckedAt;
        private boolean isApproved;
        private Set<Beer> currentBeers = new HashSet<>();
        private Set<Beer> pastBeers = new HashSet<>();
        private Set<User> trackedBy = new HashSet<>();
        private Set<BarCheck> checks = new HashSet<>();
        private User requestedBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private BarWebpageSettings webpageSettings = new BarWebpageSettings();

        public BarBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BarBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BarBuilder location(String location) {
            this.location = location;
            return this;
        }

        public BarBuilder aiInstructions(String aiInstructions) {
            this.aiInstructions = aiInstructions;
            return this;
        }

        public BarBuilder menuUrl(String menuUrl) {
            this.menuUrl = menuUrl;
            return this;
        }

        public BarBuilder menuXPath(String menuXPath) {
            this.menuXPath = menuXPath;
            return this;
        }

        public BarBuilder lastMenuHash(String lastMenuHash) {
            this.lastMenuHash = lastMenuHash;
            return this;
        }

        public BarBuilder lastCheckedAt(LocalDateTime lastCheckedAt) {
            this.lastCheckedAt = lastCheckedAt;
            return this;
        }

        public BarBuilder isApproved(boolean isApproved) {
            this.isApproved = isApproved;
            return this;
        }

        public BarBuilder currentBeers(Set<Beer> currentBeers) {
            this.currentBeers = currentBeers;
            return this;
        }

        public BarBuilder pastBeers(Set<Beer> pastBeers) {
            this.pastBeers = pastBeers;
            return this;
        }

        public BarBuilder trackedBy(Set<User> trackedBy) {
            this.trackedBy = trackedBy;
            return this;
        }

        public BarBuilder checks(Set<BarCheck> checks) {
            this.checks = checks;
            return this;
        }

        public BarBuilder requestedBy(User requestedBy) {
            this.requestedBy = requestedBy;
            return this;
        }

        public BarBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BarBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BarBuilder webpageSettings(BarWebpageSettings webpageSettings) {
            this.webpageSettings = webpageSettings;
            return this;
        }

        public Bar build() {
            return new Bar(id, name, location, aiInstructions, menuUrl, menuXPath, lastMenuHash,
                    lastCheckedAt, isApproved, currentBeers, pastBeers,
                    trackedBy, checks, requestedBy, createdAt, updatedAt, webpageSettings);
        }
    }
}