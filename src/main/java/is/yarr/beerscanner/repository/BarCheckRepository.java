package is.yarr.beerscanner.repository;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.BarCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for BarCheck entity.
 */
@Repository
public interface BarCheckRepository extends JpaRepository<BarCheck, Long> {

    /**
     * Find the most recent check for a specific bar.
     *
     * @param bar the bar to find the check for
     * @return an Optional containing the most recent check if found
     */
    Optional<BarCheck> findFirstByBarOrderByCreatedAtDesc(Bar bar);

    /**
     * Find all checks for a specific bar, ordered by creation time (descending).
     *
     * @param bar the bar to find checks for
     * @param pageable pagination information
     * @return a Page of checks for the bar
     */
    Page<BarCheck> findByBarOrderByCreatedAtDesc(Bar bar, Pageable pageable);

    /**
     * Find checks with a specific processing status.
     *
     * @param processingStatus the processing status to search for
     * @return a list of checks with the specified processing status
     */
    List<BarCheck> findByProcessingStatus(BarCheck.ProcessingStatus processingStatus);

    /**
     * Find checks that have changes.
     *
     * @param pageable pagination information
     * @return a Page of checks that have changes
     */
    Page<BarCheck> findByHasChangesTrue(Pageable pageable);

    /**
     * Find checks created after a specific time.
     *
     * @param time the time to compare against
     * @param pageable pagination information
     * @return a Page of checks created after the specified time
     */
    Page<BarCheck> findByCreatedAtAfter(LocalDateTime time, Pageable pageable);

    /**
     * Find checks for a specific bar that have changes.
     *
     * @param bar the bar to find checks for
     * @return a list of checks for the bar that have changes
     */
    List<BarCheck> findByBarAndHasChangesTrue(Bar bar);
}