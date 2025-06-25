package is.yarr.beerscanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an alias for a beer in the system.
 */
@Entity
@Table(name = "beer_aliases")
public class BeerAlias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brewery;

    @ManyToOne
    @JoinColumn(name = "beer_id", nullable = false)
    private Beer beer;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor
    public BeerAlias() {
    }

    // All-args constructor
    public BeerAlias(Long id, String name, String brewery, Beer beer, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.brewery = brewery;
        this.beer = beer;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder pattern implementation
    public static BeerAliasBuilder builder() {
        return new BeerAliasBuilder();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrewery() {
        return brewery;
    }

    public Beer getBeer() {
        return beer;
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

    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
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
        BeerAlias beerAlias = (BeerAlias) o;
        return Objects.equals(id, beerAlias.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString
    @Override
    public String toString() {
        return "BeerAlias{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brewery='" + brewery + '\'' +
                ", beer=" + (beer != null ? beer.getId() : null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // Builder class
    public static class BeerAliasBuilder {
        private Long id;
        private String name;
        private String brewery;
        private Beer beer;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public BeerAliasBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BeerAliasBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BeerAliasBuilder brewery(String brewery) {
            this.brewery = brewery;
            return this;
        }

        public BeerAliasBuilder beer(Beer beer) {
            this.beer = beer;
            return this;
        }

        public BeerAliasBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BeerAliasBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BeerAlias build() {
            return new BeerAlias(id, name, brewery, beer, createdAt, updatedAt);
        }
    }
}