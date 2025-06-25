package is.yarr.beerscanner.service;

import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.BeerAlias;
import is.yarr.beerscanner.repository.BeerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for beer alias-related operations.
 */
@Service
public class BeerAliasService {

    private final BeerRepository beerRepository;
    private final BeerService beerService;

    public BeerAliasService(BeerRepository beerRepository, BeerService beerService) {
        this.beerRepository = beerRepository;
        this.beerService = beerService;
    }

    /**
     * Get all aliases for a beer.
     *
     * @param beerId the beer ID
     * @return a list of beer aliases
     */
    public List<BeerAlias> getBeerAliases(Long beerId) {
        Beer beer = beerService.getBeerById(beerId);
        return new ArrayList<>(beer.getAliases());
    }

    /**
     * Add an alias to a beer.
     *
     * @param beerId the beer ID
     * @param name the alias name
     * @param brewery the alias brewery
     * @return the created beer alias
     */
    @Transactional
    public BeerAlias addBeerAlias(Long beerId, String name, String brewery) {
        return beerService.addBeerAlias(beerId, name, brewery);
    }

    /**
     * Delete an alias from a beer.
     *
     * @param aliasId the alias ID
     */
    @Transactional
    public void deleteBeerAlias(Long aliasId) {
        beerService.deleteBeerAlias(aliasId);
    }
}