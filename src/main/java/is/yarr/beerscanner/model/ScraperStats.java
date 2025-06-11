package is.yarr.beerscanner.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "scraper_stats")
public class ScraperStats {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "bar_id")
    private Bar bar;

    @Column
    private int totalChecks;

    @Column
    private int successfulChecks;

    @Column
    private int failedChecks;

    @Column
    private int totalChangesDetected;

    @Column
    private int averageCheckTime; // In ms TODO: This is not average, it's the LAST check time!

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "last_check_time", nullable = true)
    private LocalDateTime lastCheckTime;

    public ScraperStats() {
    }

    public ScraperStats(Bar bar, int totalChecks, int successfulChecks, int failedChecks,
                        int totalChangesDetected, int averageCheckTime, LocalDateTime lastCheckTime) {
        this.bar = bar;
        this.totalChecks = totalChecks;
        this.successfulChecks = successfulChecks;
        this.failedChecks = failedChecks;
        this.totalChangesDetected = totalChangesDetected;
        this.averageCheckTime = averageCheckTime;
        this.lastCheckTime = lastCheckTime;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
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

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for ScraperStats
     */
    public static class Builder {
        private Bar bar;
        private int totalChecks;
        private int successfulChecks;
        private int failedChecks;
        private int totalChangesDetected;
        private int averageCheckTime;
        private LocalDateTime lastCheckTime;

        public Builder bar(Bar bar) {
            this.bar = bar;
            return this;
        }

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

        public ScraperStats build() {
            return new ScraperStats(bar, totalChecks, successfulChecks, failedChecks,
                    totalChangesDetected, averageCheckTime, lastCheckTime);
        }
    }

}
