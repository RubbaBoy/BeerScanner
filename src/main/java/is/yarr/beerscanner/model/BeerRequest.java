package is.yarr.beerscanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * An entity that represents a beer request in the system by a user.
 */
@Entity
@Table(name = "beer_requests")
public class BeerRequest {

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

    @ManyToOne
    private User requestedBy;

    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    public BeerRequest() {
    }

    // Constructor made package-private to encourage builder usage,
    // but can be public if direct instantiation is still desired.
    BeerRequest(Long id, String name, String type, String brewery, Double abv, String description, User requestedBy, LocalDateTime requestedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.brewery = brewery;
        this.abv = abv;
        this.description = description;
        this.requestedBy = requestedBy;
        this.requestedAt = requestedAt;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrewery() {
        return brewery;
    }

    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }

    public Double getAbv() {
        return abv;
    }

    public void setAbv(Double abv) {
        this.abv = abv;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeerRequest that = (BeerRequest) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(brewery, that.brewery) &&
                Objects.equals(abv, that.abv) &&
                Objects.equals(description, that.description) &&
                Objects.equals(requestedBy, that.requestedBy) &&
                Objects.equals(requestedAt, that.requestedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, brewery, abv, description, requestedBy);
    }

    @Override
    public String toString() {
        return "BeerRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", brewery='" + brewery + '\'' +
                ", abv=" + abv +
                ", description='" + description + '\'' +
                ", requestedBy=" + (requestedBy != null ? requestedBy.toString() : "null") + // Assuming User has a good toString()
                '}';
    }

    public static BeerRequestBuilder builder() {
        return new BeerRequestBuilder();
    }

    public static class BeerRequestBuilder {
        private Long id;
        private String name;
        private String type;
        private String brewery;
        private Double abv;
        private String description;
        private User requestedBy;
        private LocalDateTime requestedAt = LocalDateTime.now(); // Default to now, can be overridden

        BeerRequestBuilder() {
        }

        public BeerRequestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BeerRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BeerRequestBuilder type(String type) {
            this.type = type;
            return this;
        }

        public BeerRequestBuilder brewery(String brewery) {
            this.brewery = brewery;
            return this;
        }

        public BeerRequestBuilder abv(Double abv) {
            this.abv = abv;
            return this;
        }

        public BeerRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BeerRequestBuilder requestedBy(User requestedBy) {
            this.requestedBy = requestedBy;
            return this;
        }

        public BeerRequestBuilder requestedAt(LocalDateTime requestedAt) {
            this.requestedAt = requestedAt;
            return this;
        }

        public BeerRequest build() {
            return new BeerRequest(id, name, type, brewery, abv, description, requestedBy, requestedAt);
        }

        @Override
        public String toString() {
            return "BeerRequest.BeerRequestBuilder{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", brewery='" + brewery + '\'' +
                    ", abv=" + abv +
                    ", description='" + description + '\'' +
                    ", requestedBy=" + (requestedBy != null ? requestedBy.toString() : "null") +
                    '}';
        }
    }
}