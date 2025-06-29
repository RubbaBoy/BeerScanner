package is.yarr.beerscanner.model;

import is.yarr.beerscanner.model.beer.BarBeerCurrent;
import is.yarr.beerscanner.model.beer.BarBeerHistory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing a beer in the system.
 */
@Entity
@Table(name = "beers")
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String type;

    @Column
    private String brewery;

    @Column
    private Double abv;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Current availability - small set, eagerly loaded is fine
    @OneToMany(mappedBy = "beer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("addedAt DESC")
    private Set<BarBeerCurrent> availableAt = new LinkedHashSet<>();

    // Historical availability - lazy loaded, only when needed
    @OneToMany(mappedBy = "beer", fetch = FetchType.LAZY)
    @OrderBy("addedAt DESC")
    private Set<BarBeerHistory> barHistory = new LinkedHashSet<>();

    // Replace direct ManyToMany with OneToMany to BeerTracking
    @OneToMany(mappedBy = "beer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BeerTracking> trackings = new HashSet<>();

    @OneToMany(mappedBy = "beer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BeerAlias> aliases = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor
    public Beer() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // All-args constructor
    public Beer(Long id, String name, String type, String brewery, Double abv,
                String description, Set<BarBeerCurrent> availableAt, Set<BarBeerHistory> barHistory,
                Set<BeerTracking> trackings, Set<BeerAlias> aliases, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.brewery = brewery;
        this.abv = abv;
        this.description = description;
        this.availableAt = availableAt != null ? availableAt : new LinkedHashSet<>();
        this.barHistory = barHistory != null ? barHistory : new LinkedHashSet<>();
        this.trackings = trackings != null ? trackings : new HashSet<>();
        this.aliases = aliases != null ? aliases : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder pattern implementation
    public static BeerBuilder builder() {
        return new BeerBuilder();
    }

    // Getters
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

    public Set<BarBeerCurrent> getAvailableAt() {
        return availableAt;
    }

    public Set<BarBeerHistory> getBarHistory() {
        return barHistory;
    }

    public Set<BeerTracking> getTrackings() {
        return trackings;
    }

    public Set<BeerAlias> getAliases() {
        return aliases;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }

    public void setAbv(Double abv) {
        this.abv = abv;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvailableAt(Set<BarBeerCurrent> availableAt) {
        this.availableAt = availableAt != null ? availableAt : new LinkedHashSet<>();
    }

    public void setBarHistory(Set<BarBeerHistory> barHistory) {
        this.barHistory = barHistory != null ? barHistory : new LinkedHashSet<>();
    }

    public void setTrackings(Set<BeerTracking> trackings) {
        this.trackings = trackings != null ? trackings : new HashSet<>();
    }

    public void setAliases(Set<BeerAlias> aliases) {
        this.aliases = aliases != null ? aliases : new HashSet<>();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Beer beer)) return false;

        return Objects.equals(id, beer.id);
    }

    @Override
    public int hashCode() {
        if (id == null) return super.hashCode();
        return id.hashCode();
    }

    // toString
    @Override
    public String toString() {
        return "Beer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", brewery='" + brewery + '\'' +
                ", abv=" + abv +
                ", description='" + description + '\'' +
                ", availableAt=" + availableAt +
                ", barHistory=" + barHistory +
                ", trackings=" + trackings +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // Builder class
    public static class BeerBuilder {
        private Long id;
        private String name;
        private String type;
        private String brewery;
        private Double abv;
        private String description;
        private Set<BarBeerCurrent> availableAt = new HashSet<>();
        private Set<BarBeerHistory> barHistory = new HashSet<>();
        private Set<BeerTracking> trackings = new HashSet<>();
        private Set<BeerAlias> aliases = new HashSet<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public BeerBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BeerBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BeerBuilder type(String type) {
            this.type = type;
            return this;
        }

        public BeerBuilder brewery(String brewery) {
            this.brewery = brewery;
            return this;
        }

        public BeerBuilder abv(Double abv) {
            this.abv = abv;
            return this;
        }

        public BeerBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BeerBuilder availableAt(Set<BarBeerCurrent> availableAt) {
            this.availableAt = availableAt != null ? availableAt : new HashSet<>();
            return this;
        }

        public BeerBuilder barHistory(Set<BarBeerHistory> barHistory) {
            this.barHistory = barHistory != null ? barHistory : new HashSet<>();
            return this;
        }

        public BeerBuilder trackings(Set<BeerTracking> trackings) {
            this.trackings = trackings != null ? trackings : new HashSet<>();
            return this;
        }

        public BeerBuilder aliases(Set<BeerAlias> aliases) {
            this.aliases = aliases != null ? aliases : new HashSet<>();
            return this;
        }

        public BeerBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BeerBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Beer build() {
            return new Beer(id, name, type, brewery, abv, description,
                    availableAt, barHistory, trackings, aliases,
                    createdAt, updatedAt);
        }
    }
}
