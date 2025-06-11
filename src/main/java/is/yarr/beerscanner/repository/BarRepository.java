package is.yarr.beerscanner.repository;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Bar entity.
 */
@Repository
public interface BarRepository extends JpaRepository<Bar, Long> {

    /**
     * Find bars by name containing the given string (case-insensitive).
     *
     * @param name the name to search for
     * @param pageable pagination information
     * @return a Page of bars matching the search criteria
     */
    Page<Bar> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find bars by location containing the given string (case-insensitive).
     *
     * @param location the location to search for
     * @param pageable pagination information
     * @return a Page of bars matching the search criteria
     */
    Page<Bar> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    /**
     * Find bars that have a specific beer currently available.
     *
     * @param beer the beer to search for
     * @return a list of bars that have the beer currently available
     */
    List<Bar> findByCurrentBeersContains(Beer beer);

    /**
     * Find bars that are tracked by a specific user.
     *
     * @param user the user tracking the bars
     * @return a list of bars tracked by the user
     */
    List<Bar> findByTrackedByContains(User user);

    /**
     * Find bars that need to be checked (not checked in the last 24 hours).
     *
     * @param checkTime the time to compare against
     * @return a list of bars that need to be checked
     */
    List<Bar> findByLastCheckedAtBeforeOrLastCheckedAtIsNull(LocalDateTime checkTime);

    /**
     * Find bars that are approved.
     *
     * @param pageable pagination information
     * @return a Page of approved bars
     */
    Page<Bar> findByIsApprovedTrue(Pageable pageable);

    /**
     * Find bars that are not approved.
     *
     * @param pageable pagination information
     * @return a Page of unapproved bars
     */
    Page<Bar> findByIsApprovedFalse(Pageable pageable);

    /**
     * Find bars requested by a specific user.
     *
     * @param user the user who requested the bars
     * @return a list of bars requested by the user
     */
    List<Bar> findByRequestedBy(User user);

    /**
     * Search for bars by name or location.
     *
     * @param searchTerm the term to search for in name or location
     * @param pageable pagination information
     * @return a Page of bars matching the search criteria
     */
    @Query("SELECT b FROM Bar b WHERE b.isApproved = true AND (LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(b.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Bar> searchBars(@Param("searchTerm") String searchTerm, Pageable pageable);
}