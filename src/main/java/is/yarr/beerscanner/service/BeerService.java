package is.yarr.beerscanner.service;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.BeerAlias;
import is.yarr.beerscanner.model.BeerRequest;
import is.yarr.beerscanner.model.BeerTracking;
import is.yarr.beerscanner.repository.BarRepository;
import is.yarr.beerscanner.repository.BeerRepository;
import is.yarr.beerscanner.repository.BeerRequestRepository;
import is.yarr.beerscanner.repository.BeerTrackingRepository;
import is.yarr.beerscanner.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for beer-related operations.
 */
@Service
public class BeerService {

    private final BeerRepository beerRepository;
    private final BarRepository barRepository;
    private final BeerRequestRepository beerRequestRepository;
    private final UserRepository userRepository;

    private static final String[] rawBeerTypes = {
            "Lager",
            "Pilsner",
            "German Pilsner",
            "Bohemian Pilsner",
            "American Lager",
            "Light Lager",
            "Dark Lager",
            "Bock",
            "Doppelbock",
            "Eisbock",
            "Maibock",
            "Helles Bock",
            "Traditional Bock",
            "Dunkel",
            "Schwarzbier",
            "Marzen",
            "Oktoberfest",
            "Rauchbier",
            "Vienna Lager",
            "Kellerbier",
            "Zwickelbier",
            "Ale",
            "Pale Ale",
            "APA",
            "American Pale Ale",
            "English Bitter",
            "ESB",
            "Extra Special Bitter",
            "IPA",
            "India Pale Ale",
            "American IPA",
            "English IPA",
            "DIPA",
            "Double IPA",
            "Imperial IPA",
            "Triple IPA",
            "NEIPA",
            "New England IPA",
            "Hazy IPA",
            "West Coast IPA",
            "Session IPA",
            "Black IPA",
            "Belgian IPA",
            "Brut IPA",
            "Milkshake IPA",
            "Brown Ale",
            "American Brown Ale",
            "English Brown Ale",
            "Mild Ale",
            "Porter",
            "American Porter",
            "English Porter",
            "Baltic Porter",
            "Robust Porter",
            "Stout",
            "American Stout",
            "Irish Stout",
            "Dry Stout",
            "Milk Stout",
            "Sweet Stout",
            "Oatmeal Stout",
            "RIS",
            "Russian Imperial Stout",
            "Imperial Stout",
            "Pastry Stout",
            "Oyster Stout",
            "FES",
            "Foreign Extra Stout",
            "Belgian Ale",
            "Belgian Blond Ale",
            "Belgian Dubbel",
            "Belgian Tripel",
            "Belgian Quadrupel",
            "BDSA",
            "Belgian Dark Strong Ale",
            "Saison",
            "Farmhouse Ale",
            "Bière de Garde",
            "Witbier",
            "Belgian White",
            "Flanders Red Ale",
            "Oud Bruin",
            "Lambic",
            "Gueuze",
            "Fruit Lambic",
            "Faro",
            "Cherry Lambic",
            "Kriek",
            "Raspberry Lambic",
            "Framboise",
            "German Ale",
            "Kölsch",
            "Altbier",
            "Hefeweizen",
            "Dunkelweizen",
            "Weizenbock",
            "Kristallweizen",
            "Berliner Weisse",
            "Gose",
            "Scottish Ale",
            "Scotch Ale",
            "Wee Heavy",
            "Irish Red Ale",
            "Barleywine",
            "American Barleywine",
            "English Barleywine",
            "Wheat Ale",
            "American Wheat Ale",
            "Cream Ale",
            "Blonde Ale",
            "California Common",
            "Steam Beer",
            "Rye Beer",
            "Smoked Beer",
            "Fruit Beer",
            "Herb and Spice Beer",
            "Pumpkin Ale",
            "Chile Beer",
            "Christmas/Winter Specialty Spiced Beer",
            "Honey Beer",
            "Coffee Beer",
            "Chocolate Beer",
            "Sour Ale (General)",
            "Wild Ale (General)",
            "Brett Beer",
            "NA",
            "Non-Alcoholic Beer",
            "Low-Alcohol"
    };

    private static final String[][] beerTypeAbbreviations = {
            {"APA", "American Pale Ale"},
            {"ESB", "Extra Special Bitter"},
            {"IPA", "India Pale Ale"},
            {"DIPA", "Double IPA"},
            {"NEIPA", "New England IPA"},
            {"RIS", "Russian Imperial Stout"},
            {"FES", "Foreign Extra Stout"},
            {"BDSA", "Belgian Dark Strong Ale"},
            {"NA", "Non-Alcoholic Beer", "Low-Alcohol"}
    };

    private static final Set<String> beerTypes = new HashSet<>();

    static {
        for (var rawBeerType : rawBeerTypes) {
            beerTypes.add(rawBeerType.toLowerCase());
        }
    }

    private final BeerTrackingRepository beerTrackingRepository;

    public BeerService(BeerRepository beerRepository, BarRepository barRepository, BeerRequestRepository beerRequestRepository, UserRepository userRepository, BeerTrackingRepository beerTrackingRepository) {
        this.beerRepository = beerRepository;
        this.barRepository = barRepository;
        this.beerRequestRepository = beerRequestRepository;
        this.userRepository = userRepository;
        this.beerTrackingRepository = beerTrackingRepository;
    }

    /**
     * Get all beers.
     *
     * @param pageable pagination information
     * @return a page of beers
     */
    public Page<Beer> getAllBeers(Pageable pageable) {
        return beerRepository.findAll(pageable);
    }

    /**
     * Get a beer by ID.
     *
     * @param id the beer ID
     * @return the beer
     */
    public Beer getBeerById(Long id) {
        return beerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Beer not found with ID: " + id));
    }

    /**
     * Search for beers.
     *
     * @param searchTerm the search term
     * @param pageable   pagination information
     * @return a page of beers matching the search term
     */
    public Page<Beer> searchBeers(String searchTerm, Pageable pageable) {
        return beerRepository.searchBeers(searchTerm, pageable);
    }

    /**
     * Create a new beer.
     *
     * @param beer the beer to create
     * @return the created beer
     */
    @Transactional
    public Beer createBeer(Beer beer) {
        // Check if beer already exists
        Optional<Beer> existingBeer = beerRepository.findByNameAndBrewery(beer.getName(), beer.getBrewery());
        return existingBeer.orElseGet(() -> beerRepository.save(beer));

    }

    @Transactional
    public void requestBeer(BeerRequest beerRequest) {
        // Check if beer request already exists
        Optional<BeerRequest> existingRequest = beerRequestRepository.findByNameAndBrewery(beerRequest.getName(), beerRequest.getBrewery());
        if (existingRequest.isPresent()) {
            throw new IllegalArgumentException("Beer request already exists for this beer.");
        }

        // Save the new beer request
        beerRequestRepository.save(beerRequest);
    }

    @Transactional
    public Beer approveBeer(Long requestId) {
        BeerRequest beerRequest = beerRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Beer request not found with ID: " + requestId));

        beerRequestRepository.deleteById(requestId);

        var createdBeer = Beer.builder()
                .name(beerRequest.getName())
                .brewery(beerRequest.getBrewery())
                .type(beerRequest.getType())
                .abv(beerRequest.getAbv())
                .description(beerRequest.getDescription())
                .build();

        // Save the new beer request
        return beerRepository.save(createdBeer);
    }

    @Transactional
    public void unapproveBeer(Long requestId) {
        beerRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Beer request not found with ID: " + requestId));

        // TODO: Notify requester?

        beerRequestRepository.deleteById(requestId);
    }

    /**
     * Update a beer.
     *
     * @param id   the beer ID
     * @param beer the updated beer
     * @return the updated beer
     */
    @Transactional
    public Beer updateBeer(Long id, Beer beer) {
        Beer existingBeer = getBeerById(id);

        existingBeer.setName(beer.getName());
        existingBeer.setType(beer.getType());
        existingBeer.setBrewery(beer.getBrewery());
        existingBeer.setAbv(beer.getAbv());
        existingBeer.setDescription(beer.getDescription());

        return beerRepository.save(existingBeer);
    }

    /**
     * Delete a beer.
     *
     * @param id the beer ID
     */
    @Transactional
    public void deleteBeer(Long id) {
        Beer beer = getBeerById(id);

        // Remove beer from all bars' previouslyAvailableBeers collections
        for (Bar bar : beer.getPreviouslyAvailableAt()) {
            bar.getPastBeers().remove(beer);
        }
        beer.getPreviouslyAvailableAt().clear();

        // Also clean up current availability if needed
        for (Bar bar : beer.getAvailableAt()) {
            bar.getCurrentBeers().remove(beer);
        }
        beer.getAvailableAt().clear();

        // Remove tracking for this beer
        beerTrackingRepository.deleteAll(beerTrackingRepository.findByBeer(beer));

        beerRepository.delete(beer);
    }

    /**
     * Merge two beers into one. The first beer will be kept, and the second beer will be deleted. All instances of the
     * second beer will be replaced with the first beer.
     *
     * @param beerId The beer ID to keep
     * @param beerToMergeId The beer ID to merge into the first beer (This <b>DELETES</b> the beer)
     */
    @Transactional
    public void mergeBeers(Long beerId, Long beerToMergeId) {
        var beer = getBeerById(beerId);
        var beerToMerge = getBeerById(beerToMergeId);

        if (beer.getId().equals(beerToMerge.getId())) {
            throw new IllegalArgumentException("Cannot merge a beer with itself.");
        }

        // Replace all tracking with the new beer. Users won't see the old beer anymore.
        // TODO: User may be already tracking the new beer
        beerTrackingRepository.saveAll(beerTrackingRepository.findByBeer(beerToMerge)
                .stream()
                .peek(beerTracking -> beerTracking.setBeer(beer))
                .toList());

        // Replace all old beer's available bars with the new beer.
        barRepository.saveAll(beerToMerge.getAvailableAt().stream().peek(bar -> {
            bar.getCurrentBeers().remove(beerToMerge);
            bar.getCurrentBeers().add(beer);
        }).toList());


        // Replace all old beer's past bars with the new beer.
        barRepository.saveAll(beerToMerge.getPreviouslyAvailableAt().stream().peek(bar -> {
            bar.getPastBeers().remove(beerToMerge);
            bar.getPastBeers().add(beer);
        }).toList());

        beerRepository.delete(beerToMerge);
    }

    /**
     * Get beers currently available at a bar.
     *
     * @param barId the bar ID
     * @return a list of beers currently available at the bar
     */
    public List<Beer> getBeersAtBar(Long barId) {
        Bar bar = barRepository.findById(barId)
                .orElseThrow(() -> new IllegalArgumentException("Bar not found with ID: " + barId));

        return beerRepository.findByAvailableAtContains(bar);
    }

    /**
     * Get beers previously available at a bar.
     *
     * @param barId the bar ID
     * @return a list of beers previously available at the bar
     */
    public List<Beer> getPreviousBeersAtBar(Long barId) {
        Bar bar = barRepository.findById(barId)
                .orElseThrow(() -> new IllegalArgumentException("Bar not found with ID: " + barId));

        return beerRepository.findByPreviouslyAvailableAtContains(bar);
    }

    /**
     * Find or create a beer. If the beer is found and it has no description, set the description to the given one.
     *
     * @param name        the beer name
     * @param brewery     the beer brewery
     * @param type        the beer type
     * @param abv         the beer ABV
     * @param description the beer description
     * @return the found or created beer
     */
    @Transactional
    public Beer findOrCreateBeer(String name, String brewery, String type, Double abv, String description) {
        Optional<Beer> existingBeer = beerRepository.findByNameAndBrewery(name, brewery);

        if (existingBeer.isPresent()) {
            var beer = existingBeer.get();
            if (description != null && (beer.getDescription() == null || beer.getDescription().isEmpty())) {
                beer.setDescription(description);
                return beerRepository.save(beer);
            }

            return beer;
        }

        Beer beer = Beer.builder()
                .name(name)
                .brewery(brewery)
                .type(type)
                .abv(abv)
                .description(description)
                .build();

        return beerRepository.save(beer);
    }

    public Page<BeerRequest> getBeerRequestsByRequestedUser(Long userId, Pageable pageable) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return beerRequestRepository.findByRequestedBy(user, pageable);
    }

    public Page<BeerRequest> getBeerRequests(Pageable pageable) {
        return beerRequestRepository.findAll(pageable);
    }

    /**
     * Get beers by type.
     *
     * @param type     the beer type
     * @param pageable pagination information
     * @return a page of beers of the specified type
     */
    public Page<Beer> getBeersByType(String type, Pageable pageable) {
        return beerRepository.findByTypeContainingIgnoreCase(type, pageable);
    }

    /**
     * Get beers by brewery.
     *
     * @param brewery  the beer brewery
     * @param pageable pagination information
     * @return a page of beers from the specified brewery
     */
    public Page<Beer> getBeersByBrewery(String brewery, Pageable pageable) {
        return beerRepository.findByBreweryContainingIgnoreCase(brewery, pageable);
    }

    public boolean isValidBeerType(String type) {
        return beerTypes.contains(type.toLowerCase());
    }

    public String getAbbreviatedBeerType(String type) {
        for (String[] abbreviation : beerTypeAbbreviations) {
            if (abbreviation[1].equalsIgnoreCase(type) || abbreviation[0].equalsIgnoreCase(type)) {
                return abbreviation[0];
            }
        }

        return type;
    }

    /**
     * Get all aliases for a beer.
     *
     * @param beerId the beer ID
     * @return a list of beer aliases
     */
    public List<BeerAlias> getBeerAliases(Long beerId) {
        Beer beer = getBeerById(beerId);
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
        Beer beer = getBeerById(beerId);

        // Check if alias already exists
        for (BeerAlias alias : beer.getAliases()) {
            if (alias.getName().equalsIgnoreCase(name) && alias.getBrewery().equalsIgnoreCase(brewery)) {
                return alias;
            }
        }

        BeerAlias beerAlias = BeerAlias.builder()
                .name(name)
                .brewery(brewery)
                .beer(beer)
                .build();

        beer.getAliases().add(beerAlias);
        beerRepository.save(beer);

        return beerAlias;
    }

    /**
     * Delete an alias from a beer.
     *
     * @param aliasId the alias ID
     */
    @Transactional
    public void deleteBeerAlias(Long aliasId) {
        // Find the beer that has this alias
        Beer beer = beerRepository.findAll().stream()
                .filter(b -> b.getAliases().stream().anyMatch(a -> a.getId().equals(aliasId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Beer alias not found with ID: " + aliasId));

        // Remove the alias
        beer.getAliases().removeIf(alias -> alias.getId().equals(aliasId));
        beerRepository.save(beer);
    }
}
