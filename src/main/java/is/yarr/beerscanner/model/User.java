package is.yarr.beerscanner.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing a user in the system.
 * Users authenticate via Google OAuth.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(name = "google_id", nullable = false, unique = true)
    private String googleId;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "notification_enabled", nullable = false)
    private boolean notificationEnabled;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin = false;

    @ManyToMany
    @JoinTable(
        name = "user_tracked_bars",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "bar_id")
    )
    private Set<Bar> trackedBars = new HashSet<>();

    // Replace direct ManyToMany with OneToMany to BeerTracking
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BeerTracking> beerTrackings = new HashSet<>();

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor
    public User() {
    }

    // All-args constructor
    public User(Long id, String email, String name, String googleId, String profilePicture, 
                boolean notificationEnabled, boolean isAdmin, Set<Bar> trackedBars, 
                Set<BeerTracking> beerTrackings, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.googleId = googleId;
        this.profilePicture = profilePicture;
        this.notificationEnabled = notificationEnabled;
        this.isAdmin = isAdmin;
        this.trackedBars = trackedBars != null ? trackedBars : new HashSet<>();
        this.beerTrackings = beerTrackings != null ? beerTrackings : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder pattern implementation
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getGoogleId() {
        return googleId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public Set<Bar> getTrackedBars() {
        return trackedBars;
    }

    public Set<BeerTracking> getBeerTrackings() {
        return beerTrackings;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setTrackedBars(Set<Bar> trackedBars) {
        this.trackedBars = trackedBars;
    }

    public void setBeerTrackings(Set<BeerTracking> beerTrackings) {
        this.beerTrackings = beerTrackings;
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
        User user = (User) o;
        return notificationEnabled == user.notificationEnabled &&
               isAdmin == user.isAdmin &&
               Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
//                ", name='" + name + '\'' +
//                ", googleId='" + googleId + '\'' +
//                ", profilePicture='" + profilePicture + '\'' +
//                ", notificationEnabled=" + notificationEnabled +
//                ", isAdmin=" + isAdmin +
//                ", createdAt=" + createdAt +
//                ", updatedAt=" + updatedAt +
                '}';
    }

    // Builder class
    public static class UserBuilder {
        private Long id;
        private String email;
        private String name;
        private String googleId;
        private String profilePicture;
        private boolean notificationEnabled;
        private boolean isAdmin = false;
        private Set<Bar> trackedBars = new HashSet<>();
        private Set<BeerTracking> beerTrackings = new HashSet<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder googleId(String googleId) {
            this.googleId = googleId;
            return this;
        }

        public UserBuilder profilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
            return this;
        }

        public UserBuilder notificationEnabled(boolean notificationEnabled) {
            this.notificationEnabled = notificationEnabled;
            return this;
        }

        public UserBuilder isAdmin(boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public UserBuilder trackedBars(Set<Bar> trackedBars) {
            this.trackedBars = trackedBars;
            return this;
        }

        public UserBuilder beerTrackings(Set<BeerTracking> beerTrackings) {
            this.beerTrackings = beerTrackings;
            return this;
        }

        public UserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            return new User(id, email, name, googleId, profilePicture, notificationEnabled, 
                           isAdmin, trackedBars, beerTrackings, createdAt, updatedAt);
        }
    }
}