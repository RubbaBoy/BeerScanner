package is.yarr.beerscanner.repository;

import is.yarr.beerscanner.model.beer.BarBeerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarBeerHistoryRepository extends JpaRepository<BarBeerHistory, Long> {

}
