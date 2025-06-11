package is.yarr.beerscanner.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a notification to a user.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "bar_id")
    private Bar bar;

    @ManyToOne
    @JoinColumn(name = "beer_id")
    private Beer beer;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "is_sent", nullable = false)
    private boolean isSent;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    // Default constructor
    public Notification() {
    }

    // All-args constructor
    public Notification(Long id, User user, Bar bar, Beer beer, String title, String message,
                        NotificationType type, boolean isRead, boolean isSent,
                        LocalDateTime createdAt, LocalDateTime sentAt) {
        this.id = id;
        this.user = user;
        this.bar = bar;
        this.beer = beer;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.isSent = isSent;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    // Builder pattern implementation
    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Bar getBar() {
        return bar;
    }

    public Beer getBeer() {
        return beer;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }

    public boolean isSent() {
        return isSent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return isRead == that.isRead &&
                isSent == that.isSent &&
                Objects.equals(id, that.id) &&
                Objects.equals(user, that.user) &&
                Objects.equals(bar, that.bar) &&
                Objects.equals(beer, that.beer) &&
                Objects.equals(title, that.title) &&
                Objects.equals(message, that.message) &&
                type == that.type &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(sentAt, that.sentAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, bar, beer, title, message, type, isRead, isSent, createdAt, sentAt);
    }

    // toString
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", user=" + user +
                ", bar=" + bar +
                ", beer=" + beer +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", isRead=" + isRead +
                ", isSent=" + isSent +
                ", createdAt=" + createdAt +
                ", sentAt=" + sentAt +
                '}';
    }

    // Builder class
    public static class NotificationBuilder {
        private Long id;
        private User user;
        private Bar bar;
        private Beer beer;
        private String title;
        private String message;
        private NotificationType type;
        private boolean isRead;
        private boolean isSent;
        private LocalDateTime createdAt;
        private LocalDateTime sentAt;

        public NotificationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NotificationBuilder user(User user) {
            this.user = user;
            return this;
        }

        public NotificationBuilder bar(Bar bar) {
            this.bar = bar;
            return this;
        }

        public NotificationBuilder beer(Beer beer) {
            this.beer = beer;
            return this;
        }

        public NotificationBuilder title(String title) {
            this.title = title;
            return this;
        }

        public NotificationBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationBuilder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public NotificationBuilder isRead(boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public NotificationBuilder isSent(boolean isSent) {
            this.isSent = isSent;
            return this;
        }

        public NotificationBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public NotificationBuilder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public Notification build() {
            return new Notification(id, user, bar, beer, title, message, type,
                    isRead, isSent, createdAt, sentAt);
        }
    }

    /**
     * Type of notification.
     */
    public enum NotificationType {
        BEER_AVAILABLE,
        MENU_CHANGED,
        SYSTEM
    }
}