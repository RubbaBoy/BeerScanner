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

import java.util.List;
import java.util.Optional;

/**
 * Repository for Beer entity.
 */
@Repository
public interface BeerRepository extends JpaRepository<Beer, Long> {

    /**
     * Find beers by name containing the given string (case-insensitive).
     *
     * @param name the name to search for
     * @param pageable pagination information
     * @return a Page of beers matching the search criteria
     */
    Page<Beer> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find beers by brewery containing the given string (case-insensitive).
     *
     * @param brewery the brewery to search for
     * @param pageable pagination information
     * @return a Page of beers matching the search criteria
     */
    Page<Beer> findByBreweryContainingIgnoreCase(String brewery, Pageable pageable);

    /**
     * Find beers by type containing the given string (case-insensitive).
     *
     * @param type the type to search for
     * @param pageable pagination information
     * @return a Page of beers matching the search criteria
     */
    Page<Beer> findByTypeContainingIgnoreCase(String type, Pageable pageable);

//    /**
//     * Find beers that are currently available at a specific bar.
//     *
//     * @param bar the bar to search for
//     * @return a list of beers currently available at the bar
//     */
//    List<Beer> findByAvailableAtContains(Bar bar);
//
//    /**
//     * Find beers that were previously available at a specific bar.
//     *
//     * @param bar the bar to search for
//     * @return a list of beers previously available at the bar
//     */
//    List<Beer> findByPreviouslyAvailableAtContains(Bar bar);

    /**
     * Find beers that are tracked by a specific user.
     *
     * @param user the user tracking the beers
     * @return a list of beers tracked by the user
     */
    @Query("SELECT DISTINCT b FROM Beer b JOIN b.trackings t WHERE t.user = :user")
    List<Beer> findByTrackedByUser(@Param("user") User user);

    /**
     * Search for beers by name, brewery, or type.
     *
     * @param searchTerm the term to search for in name, brewery, or type
     * @param pageable pagination information
     * @return a Page of beers matching the search criteria
     */
    @Query("SELECT b FROM Beer b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(b.brewery) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(b.type) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Beer> searchBeers(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find a beer by exact name and brewery.
     *
     * @param name the exact name of the beer
     * @param brewery the exact brewery of the beer
     * @return an Optional containing the beer if found
     */
    Optional<Beer> findByNameAndBrewery(String name, String brewery);

    /**
     * Find a beer whose aliases contain the given name and brewery (case-insensitive on both fields).
     *
     * @param name    the alias name of the beer
     * @param brewery the alias brewery of the beer
     * @return an Optional containing the beer if an alias matches on both fields
     */
    @Query("""
           SELECT b
           FROM Beer b
           JOIN b.aliases a
           WHERE LOWER(a.name) = LOWER(:name)
             AND LOWER(a.brewery) = LOWER(:brewery)
           """)
    Optional<Beer> findByAliasNameAndAliasBrewery(@Param("name") String name, @Param("brewery") String brewery);

}
