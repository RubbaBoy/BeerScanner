package is.yarr.beerscanner.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a user's tracking of a beer.
 * The bar field is optional - if null, the user is tracking the beer at any bar.
 */
@Entity
@Table(name = "beer_trackings")
public class BeerTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "beer_id", nullable = false)
    private Beer beer;

    @ManyToOne
    @JoinColumn(name = "bar_id")
    private Bar bar; // Optional - null means "any bar"

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor
    public BeerTracking() {
    }

    // All-args constructor
    public BeerTracking(Long id, User user, Beer beer, Bar bar, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.beer = beer;
        this.bar = bar;
        this.createdAt = createdAt;
    }

    // Builder pattern implementation
    public static BeerTrackingBuilder builder() {
        return new BeerTrackingBuilder();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Beer getBeer() {
        return beer;
    }

    public Bar getBar() {
        return bar;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeerTracking that = (BeerTracking) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(user, that.user) &&
                Objects.equals(beer, that.beer) &&
                Objects.equals(bar, that.bar) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, beer, bar, createdAt);
    }

    // toString
    @Override
    public String toString() {
        return "BeerTracking{" +
                "id=" + id +
                ", user=" + user +
                ", beer=" + beer +
                ", bar=" + bar +
                ", createdAt=" + createdAt +
                '}';
    }

    // Builder class
    public static class BeerTrackingBuilder {
        private Long id;
        private User user;
        private Beer beer;
        private Bar bar;
        private LocalDateTime createdAt;

        public BeerTrackingBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BeerTrackingBuilder user(User user) {
            this.user = user;
            return this;
        }

        public BeerTrackingBuilder beer(Beer beer) {
            this.beer = beer;
            return this;
        }

        public BeerTrackingBuilder bar(Bar bar) {
            this.bar = bar;
            return this;
        }

        public BeerTrackingBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BeerTracking build() {
            return new BeerTracking(id, user, beer, bar, createdAt);
        }
    }
}