package is.yarr.beerscanner.model;

import is.yarr.beerscanner.model.beer.BarBeerCurrent;
import is.yarr.beerscanner.model.beer.BarBeerHistory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "bar", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BarBeerCurrent> currentBeers = new HashSet<>();

    @OneToMany(mappedBy = "bar")
    private Set<BarBeerHistory> beerHistory = new HashSet<>();

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
               Set<BarBeerCurrent> currentBeers, Set<BarBeerHistory> beerHistory, Set<User> trackedBy,
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
        this.beerHistory = beerHistory != null ? beerHistory : new HashSet<>();
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

    public Set<BarBeerCurrent> getCurrentBeers() {
        return currentBeers;
    }

    public Set<Beer> getCurrentBeersAsOrderedBeerSet() {
        return currentBeers.stream().map(BarBeerCurrent::getBeer).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<BarBeerHistory> getBeerHistory() {
        return beerHistory;
    }

    public Set<Beer> getBeerHistoryAsOrderedBeerSet() {
        return beerHistory.stream().map(BarBeerHistory::getBeer).collect(Collectors.toCollection(LinkedHashSet::new));
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

    public void setCurrentBeers(Set<BarBeerCurrent> currentBeers) {
        this.currentBeers = currentBeers;
    }

    public void setBeerHistory(Set<BarBeerHistory> beerHistory) {
        this.beerHistory = beerHistory;
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

    // Efficient method to add a beer
    public boolean addCurrentBeer(Beer beer) {
        if (currentBeers.stream().anyMatch(bb -> bb.getBeer().equals(beer))) {
            return false; // Beer already exists in current beers
        }

        BarBeerCurrent barBeer = new BarBeerCurrent(this, beer);
        currentBeers.add(barBeer);
        beer.getAvailableAt().add(barBeer);
        return true;
    }

    // Remove beer and move to history
    public Optional<BarBeerCurrent> removeCurrentBeer(Beer beer) {
        return currentBeers.stream()
                .filter(bb -> bb.getBeer().equals(beer))
                .findFirst()
                .map(current -> {
                    currentBeers.remove(current);
                    beer.getAvailableAt().remove(current);
                    return current;
                });
    }

    // Remove beer and move to history
    public Optional<BarBeerHistory> removePastBeer(Beer beer) {
        return beerHistory.stream()
                .filter(bb -> bb.getBeer().equals(beer))
                .findFirst()
                .map(current -> {
                    beerHistory.remove(current);
                    beer.getBarHistory().remove(current);
                    return current;
                });
    }

    public void addPastBeer(BarBeerCurrent beerCurrent) {
        var beer = beerCurrent.getBeer();

        BarBeerHistory history = new BarBeerHistory();
        history.setBar(this);
        history.setBeer(beer);
        history.setAddedAt(beerCurrent.getAddedAt());
        history.setRemovedAt(LocalDateTime.now());

        beerHistory.add(history);
        beer.getBarHistory().add(history);
    }


    // Remove beer and move to history
    public void removeBeerAndAddToHistory(Beer beer) {
        BarBeerCurrent current = currentBeers.stream()
                .filter(bb -> bb.getBeer().equals(beer))
                .findFirst()
                .orElse(null);

        if (current != null) {
            // Create history record
            BarBeerHistory history = new BarBeerHistory();
            history.setBar(this);
            history.setBeer(beer);
            history.setAddedAt(current.getAddedAt());
            history.setRemovedAt(LocalDateTime.now());

            // Remove from current
            currentBeers.remove(current);
            beer.getAvailableAt().remove(current);

            // Add to history
            beerHistory.add(history);
        }
    }


    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Bar bar)) return false;

        return Objects.equals(id, bar.id);
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
        private Set<BarBeerCurrent> currentBeers = new HashSet<>();
        private Set<BarBeerHistory> beerHistory = new HashSet<>();
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

        public BarBuilder currentBeers(Set<BarBeerCurrent> currentBeers) {
            this.currentBeers = currentBeers;
            return this;
        }

        public BarBuilder beerHistory(Set<BarBeerHistory> beerHistory) {
            this.beerHistory = beerHistory;
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
                    lastCheckedAt, isApproved, currentBeers, beerHistory,
                    trackedBy, checks, requestedBy, createdAt, updatedAt, webpageSettings);
        }
    }
}