package is.yarr.beerscanner.service;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.BarCheck;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.repository.BarCheckRepository;
import is.yarr.beerscanner.repository.BarRepository;
import is.yarr.beerscanner.service.openai.BeerListOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for bar check operations.
 */
@Service
public class BarCheckService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BarCheckService.class);

    private final BarCheckRepository barCheckRepository;
    private final BarRepository barRepository;
    private final BeerService beerService;
    private final NotificationService notificationService;
    private final OpenAIService openAIService;
    private final ScraperService scraperService;

    public BarCheckService(BarCheckRepository barCheckRepository, BarRepository barRepository, BeerService beerService, NotificationService notificationService, OpenAIService openAIService, ScraperService scraperService) {
        this.barCheckRepository = barCheckRepository;
        this.barRepository = barRepository;
        this.beerService = beerService;
        this.notificationService = notificationService;
        this.openAIService = openAIService;
        this.scraperService = scraperService;
    }

    /**
     * Get all checks for a bar.
     *
     * @param barId the bar ID
     * @param pageable pagination information
     * @return a page of checks for the bar
     */
    public Page<BarCheck> getChecksForBar(Long barId, Pageable pageable) {
        Bar bar = barRepository.findById(barId)
                .orElseThrow(() -> new IllegalArgumentException("Bar not found with ID: " + barId));
        
        return barCheckRepository.findByBarOrderByCreatedAtDesc(bar, pageable);
    }

    /**
     * Get a check by ID.
     *
     * @param id the check ID
     * @return the check
     */
    public BarCheck getCheckById(Long id) {
        return barCheckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Check not found with ID: " + id));
    }

    /**
     * Create a new check.
     *
     * @param bar the bar to check
     * @param menuContent the menu content
     * @param menuHash the menu hash
     * @return the created check
     */
    @Transactional
    public BarCheck createCheck(Bar bar, String menuContent, String contentType, String menuHash, int initialProcessDuration) {
        // Check if the menu has changed
        boolean hasChanges = bar.getLastMenuHash() == null || !bar.getLastMenuHash().equals(menuHash);

        // Create the check
        BarCheck check = BarCheck.builder()
                .bar(bar)
                .menuContent(menuContent)
                .contentType(contentType)
                .menuHash(menuHash)
                .hasChanges(hasChanges)
                .processDuration(initialProcessDuration)
                .processingStatus(BarCheck.ProcessingStatus.PENDING)
                .build();
        
        // Save the check
        check = barCheckRepository.save(check);
        
        // Update the bar
        bar.setLastMenuHash(menuHash);
        bar.setLastCheckedAt(LocalDateTime.now());
        barRepository.save(bar);
        
        return check;
    }

    /**
     * Process a check.
     *
     * @param checkId the check ID
     * @return the processed check
     */
    @Transactional
    public BarCheck processCheck(Long checkId) {
        BarCheck check = getCheckById(checkId);

        var bar = check.getBar();

        if (check.getProcessingStatus() != BarCheck.ProcessingStatus.PENDING) {
            // Update status to processing
            check.setProcessingStatus(BarCheck.ProcessingStatus.PROCESSING);
            check = barCheckRepository.save(check);
        }

        long startTime = System.currentTimeMillis();
        int changes = 0;
        boolean success = false;
        
        try {
            // Only process if there are changes
            if (check.isHasChanges()) {

                // Extract beers from menu content using OpenAI
                List<BeerListOutput.BeerOutput> beerOutputs = openAIService.extractBeersFromMenu(check.getMenuContent(), check.getContentType(), bar.getAiInstructions());

                for (var beerOutput : beerOutputs) {
                    System.out.println(beerOutput);
                }

                // Update the bar's beers
                // TODO: Add beers and stuff!!!
                changes = updateBarBeers(bar, beerOutputs);

                // Send notifications
                notificationService.sendBarMenuChangedNotifications(bar);
            }
            
            // Update status to completed
            check.setProcessingStatus(BarCheck.ProcessingStatus.COMPLETED);
            success = true;
        } catch (Exception e) {
            // Update status to failed
            LOGGER.error("Error processing check for bar {}: {}", bar.getName(), e.getMessage());
            check.setProcessingStatus(BarCheck.ProcessingStatus.FAILED);
            check.setErrorMessage(e.getMessage());
        } finally {
            int totalTime = (int) (System.currentTimeMillis() - startTime) + check.getProcessDuration();

            check.setProcessDuration(totalTime);
            // Update the statistics for the bar scraper
            scraperService.updateBarScraperStats(bar.getId(), changes, totalTime, success);
        }
        
        return barCheckRepository.save(check);
    }

    /**
     * Update a bar's beers.
     *
     * @param bar the bar
     * @param newBeers the new beers
     * @return Number of changes made
     */
    @Transactional
    public int updateBarBeers(Bar bar, List<BeerListOutput.BeerOutput> newBeers) {
        int changes = 0;

        // Get the current beers
        List<Beer> currentBeers = beerService.getBeersAtBar(bar.getId());

        // Move current beers to past beers
        bar.getPastBeers().addAll(bar.getCurrentBeers());
        bar.getCurrentBeers().clear();

        System.out.println("Current beers for bar " + bar.getName() + ": " + currentBeers.size());
        System.out.println(bar.getCurrentBeers().stream().map(Beer::getName).toList());

        // Add new beers
        for (BeerListOutput.BeerOutput beerOutput : newBeers) {
            // Find or create the beer
            Beer existingBeer = beerService.findOrCreateBeer(
                    beerOutput.name,
                    beerOutput.brewery,
                    beerOutput.type,
                    beerOutput.abv,
                    beerOutput.description
            );

            // We don't want beers to be in past beers if they're currently available
            bar.getPastBeers().remove(existingBeer);
            
            // Add to current beers
            var added = bar.getCurrentBeers().add(existingBeer);
            System.out.println("(" + added + ") Adding beer: " + existingBeer.getName() + " to bar: " + bar.getName());

            // Send notifications for new beers
            if (!currentBeers.contains(existingBeer)) {
                notificationService.sendBeerAvailableNotifications(bar, existingBeer);
                changes++;
            }
        }
        
        // Save the bar
        barRepository.save(bar);

        return changes;
    }

    /**
     * Get checks with a specific processing status.
     *
     * @param status the processing status
     * @return a list of checks with the specified status
     */
    public List<BarCheck> getChecksByStatus(BarCheck.ProcessingStatus status) {
        return barCheckRepository.findByProcessingStatus(status);
    }

    /**
     * Get the most recent check for a bar.
     *
     * @param barId the bar ID
     * @return the most recent check for the bar
     */
    public Optional<BarCheck> getMostRecentCheckForBar(Long barId) {
        Bar bar = barRepository.findById(barId)
                .orElseThrow(() -> new IllegalArgumentException("Bar not found with ID: " + barId));
        
        return barCheckRepository.findFirstByBarOrderByCreatedAtDesc(bar);
    }

    /**
     * Get checks that have changes.
     *
     * @param pageable pagination information
     * @return a page of checks that have changes
     */
    public Page<BarCheck> getChecksWithChanges(Pageable pageable) {
        return barCheckRepository.findByHasChangesTrue(pageable);
    }
}