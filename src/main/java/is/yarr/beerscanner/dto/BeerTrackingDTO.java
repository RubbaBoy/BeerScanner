package is.yarr.beerscanner.dto;

import java.util.Objects;

/**
 * DTO for BeerTracking entity with information about a user's beer tracking.
 */
public class BeerTrackingDTO {
    private Long id;
    private Long userId;
    private Long beerId;
    private Long barId; // null means tracking at any bar
    private BeerDTO beer;
    private BarDTO bar; // null means tracking at any bar

    public BeerTrackingDTO() {
    }

    public BeerTrackingDTO(Long id, Long userId, Long beerId, Long barId, BeerDTO beer, BarDTO bar) {
        this.id = id;
        this.userId = userId;
        this.beerId = beerId;
        this.barId = barId;
        this.beer = beer;
        this.bar = bar;
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

    public Long getBeerId() {
        return beerId;
    }

    public void setBeerId(Long beerId) {
        this.beerId = beerId;
    }

    public Long getBarId() {
        return barId;
    }

    public void setBarId(Long barId) {
        this.barId = barId;
    }

    public BeerDTO getBeer() {
        return beer;
    }

    public void setBeer(BeerDTO beer) {
        this.beer = beer;
    }

    public BarDTO getBar() {
        return bar;
    }

    public void setBar(BarDTO bar) {
        this.bar = bar;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeerTrackingDTO that = (BeerTrackingDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(beerId, that.beerId) &&
                Objects.equals(barId, that.barId) &&
                Objects.equals(beer, that.beer) &&
                Objects.equals(bar, that.bar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, beerId, barId, beer, bar);
    }

    @Override
    public String toString() {
        return "BeerTrackingDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", beerId=" + beerId +
                ", barId=" + barId +
                ", beer=" + beer +
                ", bar=" + bar +
                '}';
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private Long beerId;
        private Long barId;
        private BeerDTO beer;
        private BarDTO bar;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder beerId(Long beerId) {
            this.beerId = beerId;
            return this;
        }

        public Builder barId(Long barId) {
            this.barId = barId;
            return this;
        }

        public Builder beer(BeerDTO beer) {
            this.beer = beer;
            return this;
        }

        public Builder bar(BarDTO bar) {
            this.bar = bar;
            return this;
        }

        public BeerTrackingDTO build() {
            return new BeerTrackingDTO(id, userId, beerId, barId, beer, bar);
        }
    }
}
