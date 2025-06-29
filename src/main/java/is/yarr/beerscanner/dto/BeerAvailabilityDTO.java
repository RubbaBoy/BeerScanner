package is.yarr.beerscanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO for BeerAvailability entity.
 */
public class BeerAvailabilityDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime availableAt;

    private BeerDTO beer;

    public BeerAvailabilityDTO() {
    }

    public BeerAvailabilityDTO(LocalDateTime availableAt, BeerDTO beer) {
        this.availableAt = availableAt;
        this.beer = beer;
    }

    public LocalDateTime getAvailableAt() {
        return availableAt;
    }

    public void setAvailableAt(LocalDateTime availableAt) {
        this.availableAt = availableAt;
    }

    public BeerDTO getBeer() {
        return beer;
    }

    public void setBeer(BeerDTO beer) {
        this.beer = beer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDateTime availableAt;
        private BeerDTO beer;

        public Builder availableAt(LocalDateTime availableAt) {
            this.availableAt = availableAt;
            return this;
        }

        public Builder beer(BeerDTO beer) {
            this.beer = beer;
            return this;
        }

        public BeerAvailabilityDTO build() {
            return new BeerAvailabilityDTO(availableAt, beer);
        }
    }
}