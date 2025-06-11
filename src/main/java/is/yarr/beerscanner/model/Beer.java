package is.yarr.beerscanner.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
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

    @ManyToMany(mappedBy = "currentBeers")
    private Set<Bar> availableAt = new HashSet<>();

    @ManyToMany(mappedBy = "pastBeers")
    private Set<Bar> previouslyAvailableAt = new HashSet<>();

    // Replace direct ManyToMany with OneToMany to BeerTracking
    @OneToMany(mappedBy = "beer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BeerTracking> trackings = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor
    public Beer() {
    }

    // All-args constructor
    public Beer(Long id, String name, String type, String brewery, Double abv,
                String description, Set<Bar> availableAt, Set<Bar> previouslyAvailableAt,
                Set<BeerTracking> trackings, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.brewery = brewery;
        this.abv = abv;
        this.description = description;
        this.availableAt = availableAt != null ? availableAt : new HashSet<>();
        this.previouslyAvailableAt = previouslyAvailableAt != null ? previouslyAvailableAt : new HashSet<>();
        this.trackings = trackings != null ? trackings : new HashSet<>();
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

    public Set<Bar> getAvailableAt() {
        return availableAt;
    }

    public Set<Bar> getPreviouslyAvailableAt() {
        return previouslyAvailableAt;
    }

    public Set<BeerTracking> getTrackings() {
        return trackings;
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

    public void setAvailableAt(Set<Bar> availableAt) {
        this.availableAt = availableAt != null ? availableAt : new HashSet<>();
    }

    public void setPreviouslyAvailableAt(Set<Bar> previouslyAvailableAt) {
        this.previouslyAvailableAt = previouslyAvailableAt != null ? previouslyAvailableAt : new HashSet<>();
    }

    public void setTrackings(Set<BeerTracking> trackings) {
        this.trackings = trackings != null ? trackings : new HashSet<>();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Beer beer = (Beer) o;
        return Objects.equals(id, beer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
                ", previouslyAvailableAt=" + previouslyAvailableAt +
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
        private Set<Bar> availableAt = new HashSet<>();
        private Set<Bar> previouslyAvailableAt = new HashSet<>();
        private Set<BeerTracking> trackings = new HashSet<>();
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

        public BeerBuilder availableAt(Set<Bar> availableAt) {
            this.availableAt = availableAt != null ? availableAt : new HashSet<>();
            return this;
        }

        public BeerBuilder previouslyAvailableAt(Set<Bar> previouslyAvailableAt) {
            this.previouslyAvailableAt = previouslyAvailableAt != null ? previouslyAvailableAt : new HashSet<>();
            return this;
        }

        public BeerBuilder trackings(Set<BeerTracking> trackings) {
            this.trackings = trackings != null ? trackings : new HashSet<>();
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
                    availableAt, previouslyAvailableAt, trackings,
                    createdAt, updatedAt);
        }
    }
}