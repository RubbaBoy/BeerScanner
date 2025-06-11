package is.yarr.beerscanner.repository;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.BeerTracking;
import is.yarr.beerscanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BeerTracking entity.
 */
@Repository
public interface BeerTrackingRepository extends JpaRepository<BeerTracking, Long> {

    /**
     * Find all beer trackings for a specific user.
     *
     * @param user the user
     * @return a list of beer trackings for the user
     */
    List<BeerTracking> findByUser(User user);

    /**
     * Find all beer trackings for a specific beer.
     *
     * @param beer the beer
     * @return a list of beer trackings for the beer
     */
    List<BeerTracking> findByBeer(Beer beer);

    /**
     * Find all beer trackings for a specific bar.
     *
     * @param bar the bar
     * @return a list of beer trackings for the bar
     */
    List<BeerTracking> findByBar(Bar bar);

    /**
     * Find all beer trackings for a specific user and beer.
     *
     * @param user the user
     * @param beer the beer
     * @return a list of beer trackings for the user and beer
     */
    List<BeerTracking> findByUserAndBeer(User user, Beer beer);

    /**
     * Find all beer trackings for a specific beer and bar.
     *
     * @param beer the beer
     * @param bar the bar
     * @return a list of beer trackings for the beer and bar
     */
    List<BeerTracking> findByBeerAndBar(Beer beer, Bar bar);

    /**
     * Find a beer tracking for a specific user, beer, and bar.
     *
     * @param user the user
     * @param beer the beer
     * @param bar the bar (can be null for global tracking)
     * @return the beer tracking if found
     */
    Optional<BeerTracking> findByUserAndBeerAndBar(User user, Beer beer, Bar bar);

    /**
     * Delete beer trackings for a specific user, beer, and bar.
     *
     * @param user the user
     * @param beer the beer
     * @param bar the bar (can be null for global tracking)
     */
    void deleteByUserAndBeerAndBar(User user, Beer beer, Bar bar);

    /**
     * Check if a beer tracking exists for a specific user, beer, and bar.
     *
     * @param user the user
     * @param beer the beer
     * @param bar the bar (can be null for global tracking)
     * @return true if the tracking exists
     */
    boolean existsByUserAndBeerAndBar(User user, Beer beer, Bar bar);
}
