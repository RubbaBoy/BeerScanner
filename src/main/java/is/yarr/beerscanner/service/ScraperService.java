package is.yarr.beerscanner.service;

import is.yarr.beerscanner.dto.ScraperStatsDTO;
import is.yarr.beerscanner.model.ScraperStats;
import is.yarr.beerscanner.repository.BarRepository;
import is.yarr.beerscanner.repository.ScraperStatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScraperService {

    private final ScraperStatsRepository scraperStatsRepository;
    private final BarRepository barRepository;
    private final DTOMapperService dtoMapperService;

    public ScraperService(ScraperStatsRepository scraperStatsRepository, BarRepository barRepository, DTOMapperService dtoMapperService) {
        this.scraperStatsRepository = scraperStatsRepository;
        this.barRepository = barRepository;
        this.dtoMapperService = dtoMapperService;
    }

    /**
     * Retrieves aggregated scraper statistics.
     *
     * @return aggregated statistics as a DTO
     */
    public ScraperStatsDTO getAllAggregatedStats() {
        return scraperStatsRepository.aggregateStats();
    }

    /**
     * Retrieves scraper statistics for a specific bar.
     *
     * @param barId the ID of the bar
     * @return the scraper statistics for the specified bar
     */
    public ScraperStatsDTO getBarStats(Long barId) {
        return scraperStatsRepository.findByBar_Id(barId)
                .map(dtoMapperService::toDTO)
                .orElseGet(() -> new ScraperStatsDTO(0, 0, 0, 0, 0, LocalDateTime.now()));
    }

    /**
     * Updates the scraper statistics for a specific bar.
     *
     * @param barId the ID of the bar
     * @param changesDetected the number of changes detected during the scrape
     * @param checkTime the time taken for the scrape check in milliseconds
     */
    public void updateBarScraperStats(Long barId, int changesDetected, int checkTime, boolean successful) {
        var existingStats = scraperStatsRepository.findByBar_Id(barId)
                .orElseGet(() -> {
                    var bar = barRepository.findById(barId)
                            .orElseThrow(() -> new IllegalArgumentException("Bar not found with ID: " + barId));

                    return new ScraperStats(bar, 0, 0, 0, 0, 0, LocalDateTime.now());
                });

        if (successful) {
            existingStats.setSuccessfulChecks(existingStats.getSuccessfulChecks() + 1);
        } else {
            existingStats.setFailedChecks(existingStats.getFailedChecks() + 1);
        }

        existingStats.setTotalChecks(existingStats.getTotalChecks() + 1);
        existingStats.setTotalChangesDetected(existingStats.getTotalChangesDetected() + changesDetected);
        existingStats.setLastCheckTime(LocalDateTime.now());
        existingStats.setAverageCheckTime(checkTime); // TODO: Just the last check time for now

        scraperStatsRepository.save(existingStats);
    }
}
