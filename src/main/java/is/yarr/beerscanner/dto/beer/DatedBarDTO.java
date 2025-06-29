package is.yarr.beerscanner.dto.beer;

import com.fasterxml.jackson.annotation.JsonFormat;
import is.yarr.beerscanner.dto.BarDTO;

import java.time.LocalDateTime;

/**
 * DTO for BarAvailability entity.
 */
public class DatedBarDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastAvailableAt;

    private BarDTO bar;

    public DatedBarDTO() {
    }

    public DatedBarDTO(LocalDateTime lastAvailableAt, BarDTO bar) {
        this.lastAvailableAt = lastAvailableAt;
        this.bar = bar;
    }

    public LocalDateTime getLastAvailableAt() {
        return lastAvailableAt;
    }

    public void setLastAvailableAt(LocalDateTime lastAvailableAt) {
        this.lastAvailableAt = lastAvailableAt;
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

    public static class Builder {
        private LocalDateTime availableAt;
        private BarDTO bar;

        public Builder availableAt(LocalDateTime availableAt) {
            this.availableAt = availableAt;
            return this;
        }

        public Builder bar(BarDTO bar) {
            this.bar = bar;
            return this;
        }

        public DatedBarDTO build() {
            return new DatedBarDTO(availableAt, bar);
        }
    }
}