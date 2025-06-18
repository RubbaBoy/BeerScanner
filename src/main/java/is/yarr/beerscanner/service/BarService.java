package is.yarr.beerscanner.service;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.ScraperStats;
import is.yarr.beerscanner.model.User;
import is.yarr.beerscanner.repository.BarRepository;
import is.yarr.beerscanner.repository.BeerRepository;
import is.yarr.beerscanner.repository.ScraperStatsRepository;
import is.yarr.beerscanner.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Service for bar-related operations.
 */
@Service
public class BarService {

    private final BarRepository barRepository;
    private final BeerRepository beerRepository;
    private final UserRepository userRepository;
    private final ScraperStatsRepository scraperStatsRepository;

    public BarService(BarRepository barRepository, BeerRepository beerRepository, UserRepository userRepository, ScraperStatsRepository scraperStatsRepository) {
        this.barRepository = barRepository;
        this.beerRepository = beerRepository;
        this.userRepository = userRepository;
        this.scraperStatsRepository = scraperStatsRepository;
    }

    /**
     * Get all bars.
     *
     * @param pageable pagination information
     * @return a page of bars
     */
    public Page<Bar> getAllBars(Pageable pageable) {
        return barRepository.findByIsApprovedTrue(pageable);
    }

    /**
     * Get a bar by ID.
     *
     * @param id the bar ID
     * @return the bar
     */
    public Bar getBarById(Long id) {
        return barRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bar not found with ID: " + id));
    }

    /**
     * Search for bars.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of bars matching the search term
     */
    public Page<Bar> searchBars(String searchTerm, Pageable pageable) {
        return barRepository.searchBars(searchTerm, pageable);
    }

    /**
     * Create a new bar.
     *
     * @param bar the bar to create
     * @param userId the ID of the user requesting the bar
     * @return the created bar
     */
    @Transactional
    public Bar createBar(Bar bar, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        bar.setRequestedBy(user);
        barRepository.save(bar);

        if (bar.isApproved()) {
            // Only create ScraperStats if the bar is approved
            scraperStatsRepository.save(new ScraperStats(bar, 0, 0, 0, 0, 0, null));
        }

        return bar;
    }

    /**
     * Update a bar.
     *
     * @param id the bar ID
     * @param bar the updated bar
     * @return the updated bar
     */
    @Transactional
    public Bar updateBar(Long id, Bar bar) {
        Bar existingBar = getBarById(id);

        existingBar.setName(bar.getName());
        existingBar.setLocation(bar.getLocation());

        existingBar.setAiInstructions(bar.getAiInstructions());
        existingBar.setMenuUrl(bar.getMenuUrl());
        existingBar.setMenuXPath(bar.getMenuXPath());

        existingBar.setWebpageSettings(bar.getWebpageSettings());

        return barRepository.save(existingBar);
    }

    /**
     * Approve a bar.
     *
     * @param id the bar ID
     * @return the approved bar
     */
    @Transactional
    public Bar approveBar(Long id) {
        Bar bar = getBarById(id);
        bar.setApproved(true);

        // Only create ScraperStats if the bar is approved
        scraperStatsRepository.save(new ScraperStats(bar, 0, 0, 0, 0, 0, null));

        return barRepository.save(bar);
    }

    /**
     * Delete a bar.
     *
     * @param id the bar ID
     */
    @Transactional
    public void deleteBar(Long id) {
        Bar bar = getBarById(id);
        barRepository.delete(bar);
    }

    /**
     * Get bars that need to be checked.
     *
     * @return a list of bars that need to be checked
     */
    public List<Bar> getBarsToCheck() { // TODO
//        LocalDateTime checkTime = LocalDateTime.now().minusHours(24);
//        return barRepository.findByLastCheckedAtBeforeOrLastCheckedAtIsNull(checkTime);
        return barRepository.findAll();
    }

    /**
     * Get current beers for a bar.
     *
     * @param barId the bar ID
     * @return the current beers
     */
    public Set<Beer> getCurrentBeers(Long barId) {
        Bar bar = getBarById(barId);
        return bar.getCurrentBeers();
    }

    /**
     * Get past beers for a bar.
     *
     * @param barId the bar ID
     * @return the past beers
     */
    public Set<Beer> getPastBeers(Long barId) {
        Bar bar = getBarById(barId);
        return bar.getPastBeers();
    }

    /**
     * Update beers for a bar.
     *
     * @param barId the bar ID
     * @param currentBeerIds the IDs of the current beers
     * @return the updated bar
     */
    @Transactional
    public Bar updateBeers(Long barId, List<Long> currentBeerIds) {
        Bar bar = getBarById(barId);

        // Move current beers to past beers
        bar.getPastBeers().addAll(bar.getCurrentBeers());
        bar.getCurrentBeers().clear();

        // Add new current beers
        for (Long beerId : currentBeerIds) {
            Beer beer = beerRepository.findById(beerId)
                    .orElseThrow(() -> new IllegalArgumentException("Beer not found with ID: " + beerId));
            bar.getCurrentBeers().add(beer);
        }

        bar.setLastCheckedAt(LocalDateTime.now());
        return barRepository.save(bar);
    }

    /**
     * Get bars requested by a user.
     *
     * @param userId the user ID
     * @return a list of bars requested by the user
     */
    public List<Bar> getBarsByRequestedUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return barRepository.findByRequestedBy(user);
    }

    /**
     * Get unapproved bars.
     *
     * @param pageable pagination information
     * @return a page of unapproved bars
     */
    public Page<Bar> getUnapprovedBars(Pageable pageable) {
        return barRepository.findByIsApprovedFalse(pageable);
    }
}
