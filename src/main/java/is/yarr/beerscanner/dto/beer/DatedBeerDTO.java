package is.yarr.beerscanner.dto.beer;

import com.fasterxml.jackson.annotation.JsonFormat;
import is.yarr.beerscanner.dto.BeerDTO;

import java.time.LocalDateTime;

/**
 * DTO for BeerAvailability entity.
 */
public class DatedBeerDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastAvailableAt;

    private BeerDTO beer;

    public DatedBeerDTO() {
    }

    public DatedBeerDTO(LocalDateTime lastAvailableAt, BeerDTO beer) {
        this.lastAvailableAt = lastAvailableAt;
        this.beer = beer;
    }

    public LocalDateTime getLastAvailableAt() {
        return lastAvailableAt;
    }

    public void setLastAvailableAt(LocalDateTime lastAvailableAt) {
        this.lastAvailableAt = lastAvailableAt;
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

        public DatedBeerDTO build() {
            return new DatedBeerDTO(availableAt, beer);
        }
    }
}