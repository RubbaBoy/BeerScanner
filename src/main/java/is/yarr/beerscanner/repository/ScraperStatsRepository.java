package is.yarr.beerscanner.repository;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.ScraperStats;
import is.yarr.beerscanner.dto.ScraperStatsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScraperStatsRepository extends JpaRepository<ScraperStats, Bar> {

    /**
     * Aggregates statistics from all {@link ScraperStats} records.
     *
     * @return aggregated statistics as {@link ScraperStatsDTO}
     */
    @Query("SELECT new is.yarr.beerscanner.dto.ScraperStatsDTO(" +
            "CAST(SUM(s.totalChecks) AS int), " +
            "CAST(SUM(s.successfulChecks) AS int), " +
            "CAST(SUM(s.failedChecks) AS int), " +
            "CAST(SUM(s.totalChangesDetected) AS int), " +
            "CAST(FLOOR(COALESCE(AVG(s.averageCheckTime), 0)) AS int), " +
            "MAX(s.lastCheckTime)) " +
            "FROM ScraperStats s")
    ScraperStatsDTO aggregateStats();

    /**
     * Finds the scraper statistics for a specific bar.
     *
     * @param bar the bar to find statistics for
     * @return an Optional containing the ScraperStats if found, or empty if not
     */
    Optional<ScraperStats> findByBar(Bar bar);

    /**
     * Finds the scraper statistics for a specific bar.
     *
     * @param barId the bar ID to find statistics for
     * @return an Optional containing the ScraperStats if found, or empty if not
     */
    Optional<ScraperStats> findByBar_Id(long barId);

}
