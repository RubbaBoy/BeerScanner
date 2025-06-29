package is.yarr.beerscanner.repository;

import is.yarr.beerscanner.model.beer.BarBeerCurrent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarBeerCurrentRepository extends JpaRepository<BarBeerCurrent, Long> {

}
