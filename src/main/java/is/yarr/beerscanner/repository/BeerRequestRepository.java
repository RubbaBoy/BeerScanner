package is.yarr.beerscanner.repository;

import is.yarr.beerscanner.model.BeerRequest;
import is.yarr.beerscanner.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for BeerRequest entity.
 */
@Repository
public interface BeerRequestRepository extends JpaRepository<BeerRequest, Long> {
    Optional<BeerRequest> findByNameAndBrewery(String name, String brewery);

    Page<BeerRequest> findByRequestedBy(User user, Pageable pageable);
}
