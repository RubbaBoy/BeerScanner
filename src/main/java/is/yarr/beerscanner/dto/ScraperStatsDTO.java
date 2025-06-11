package is.yarr.beerscanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for scraper statistics information.
 */
public class ScraperStatsDTO {

    private int totalChecks;
    private int successfulChecks;
    private int failedChecks;
    private int totalChangesDetected;
    private int averageCheckTime; // In ms

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastCheckTime;

    public ScraperStatsDTO() {
    }

    public ScraperStatsDTO(int totalChecks, int successfulChecks, int failedChecks,
                           int totalChangesDetected, int averageCheckTime,
                           LocalDateTime lastCheckTime) {
        this.totalChecks = totalChecks;
        this.successfulChecks = successfulChecks;
        this.failedChecks = failedChecks;
        this.totalChangesDetected = totalChangesDetected;
        this.averageCheckTime = averageCheckTime;
        this.lastCheckTime = lastCheckTime;
    }

    public int getTotalChecks() {
        return totalChecks;
    }

    public void setTotalChecks(int totalChecks) {
        this.totalChecks = totalChecks;
    }

    public int getSuccessfulChecks() {
        return successfulChecks;
    }

    public void setSuccessfulChecks(int successfulChecks) {
        this.successfulChecks = successfulChecks;
    }

    public int getFailedChecks() {
        return failedChecks;
    }

    public void setFailedChecks(int failedChecks) {
        this.failedChecks = failedChecks;
    }

    public int getTotalChangesDetected() {
        return totalChangesDetected;
    }

    public void setTotalChangesDetected(int totalChangesDetected) {
        this.totalChangesDetected = totalChangesDetected;
    }

    public int getAverageCheckTime() {
        return averageCheckTime;
    }

    public void setAverageCheckTime(int averageCheckTime) {
        this.averageCheckTime = averageCheckTime;
    }

    public LocalDateTime getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(LocalDateTime lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScraperStatsDTO that = (ScraperStatsDTO) o;
        return totalChecks == that.totalChecks &&
                successfulChecks == that.successfulChecks &&
                failedChecks == that.failedChecks &&
                totalChangesDetected == that.totalChangesDetected &&
                averageCheckTime == that.averageCheckTime &&
                Objects.equals(lastCheckTime, that.lastCheckTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalChecks, successfulChecks, failedChecks,
                totalChangesDetected, averageCheckTime, lastCheckTime);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for ScraperStatsDTO
     */
    public static class Builder {
        private int totalChecks;
        private int successfulChecks;
        private int failedChecks;
        private int totalChangesDetected;
        private int averageCheckTime;
        private LocalDateTime lastCheckTime;

        public Builder totalChecks(int totalChecks) {
            this.totalChecks = totalChecks;
            return this;
        }

        public Builder successfulChecks(int successfulChecks) {
            this.successfulChecks = successfulChecks;
            return this;
        }

        public Builder failedChecks(int failedChecks) {
            this.failedChecks = failedChecks;
            return this;
        }

        public Builder totalChangesDetected(int totalChangesDetected) {
            this.totalChangesDetected = totalChangesDetected;
            return this;
        }

        public Builder averageCheckTime(int averageCheckTime) {
            this.averageCheckTime = averageCheckTime;
            return this;
        }

        public Builder lastCheckTime(LocalDateTime lastCheckTime) {
            this.lastCheckTime = lastCheckTime;
            return this;
        }

        public ScraperStatsDTO build() {
            return new ScraperStatsDTO(totalChecks, successfulChecks, failedChecks,
                    totalChangesDetected, averageCheckTime, lastCheckTime);
        }
    }
}