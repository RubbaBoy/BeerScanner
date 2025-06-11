package is.yarr.beerscanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import is.yarr.beerscanner.model.Notification.NotificationType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for Notification entity with information for API responses.
 */
public class NotificationDTO {
    private Long id;
    private Long userId;
    private Long barId;
    private Long beerId;
    private String title;
    private String message;
    private NotificationType type;
    private boolean read;
    private boolean sent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;

    // Optional nested DTOs for related entities
    private UserDTO user;
    private BarDTO bar;
    private BeerDTO beer;

    public NotificationDTO() {
    }

    public NotificationDTO(Long id, Long userId, Long barId, Long beerId, String title, String message, NotificationType type, boolean read, boolean sent, LocalDateTime createdAt, LocalDateTime sentAt, UserDTO user, BarDTO bar, BeerDTO beer) {
        this.id = id;
        this.userId = userId;
        this.barId = barId;
        this.beerId = beerId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = read;
        this.sent = sent;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
        this.user = user;
        this.bar = bar;
        this.beer = beer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBarId() {
        return barId;
    }

    public void setBarId(Long barId) {
        this.barId = barId;
    }

    public Long getBeerId() {
        return beerId;
    }

    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public BarDTO getBar() {
        return bar;
    }

    public void setBar(BarDTO bar) {
        this.bar = bar;
    }

    public BeerDTO getBeer() {
        return beer;
    }

    public void setBeer(BeerDTO beer) {
        this.beer = beer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationDTO that = (NotificationDTO) o;
        return read == that.read &&
                sent == that.sent &&
                Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(barId, that.barId) &&
                Objects.equals(beerId, that.beerId) &&
                Objects.equals(title, that.title) &&
                Objects.equals(message, that.message) &&
                type == that.type &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(sentAt, that.sentAt) &&
                Objects.equals(user, that.user) &&
                Objects.equals(bar, that.bar) &&
                Objects.equals(beer, that.beer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, barId, beerId, title, message, type, read, sent, createdAt, sentAt, user, bar, beer);
    }

    @Override
    public String toString() {
        return "NotificationDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", barId=" + barId +
                ", beerId=" + beerId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", read=" + read +
                ", sent=" + sent +
                ", createdAt=" + createdAt +
                ", sentAt=" + sentAt +
                ", user=" + user +
                ", bar=" + bar +
                ", beer=" + beer +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private Long barId;
        private Long beerId;
        private String title;
        private String message;
        private NotificationType type;
        private boolean read;
        private boolean sent;
        private LocalDateTime createdAt;
        private LocalDateTime sentAt;
        private UserDTO user;
        private BarDTO bar;
        private BeerDTO beer;

        Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder barId(Long barId) {
            this.barId = barId;
            return this;
        }

        public Builder beerId(Long beerId) {
            this.beerId = beerId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder read(boolean read) {
            this.read = read;
            return this;
        }

        public Builder sent(boolean sent) {
            this.sent = sent;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public Builder user(UserDTO user) {
            this.user = user;
            return this;
        }

        public Builder bar(BarDTO bar) {
            this.bar = bar;
            return this;
        }

        public Builder beer(BeerDTO beer) {
            this.beer = beer;
            return this;
        }

        public NotificationDTO build() {
            return new NotificationDTO(id, userId, barId, beerId, title, message, type, read, sent, createdAt, sentAt, user, bar, beer);
        }

        public String toString() {
            return "NotificationDTO.Builder(id=" + this.id + ", userId=" + this.userId + ", barId=" + this.barId + ", beerId=" + this.beerId + ", title=" + this.title + ", message=" + this.message + ", type=" + this.type + ", read=" + this.read + ", sent=" + this.sent + ", createdAt=" + this.createdAt + ", sentAt=" + this.sentAt + ", user=" + this.user + ", bar=" + this.bar + ", beer=" + this.beer + ")";
        }
    }
}
