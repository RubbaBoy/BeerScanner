package is.yarr.beerscanner.service;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.BarCheck;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.beer.BarBeerCurrent;
import is.yarr.beerscanner.repository.BarBeerCurrentRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private final BarBeerCurrentRepository barBeerCurrentRepository;

    public BarCheckService(BarCheckRepository barCheckRepository, BarRepository barRepository, BeerService beerService, NotificationService notificationService, OpenAIService openAIService, ScraperService scraperService, BarBeerCurrentRepository barBeerCurrentRepository) {
        this.barCheckRepository = barCheckRepository;
        this.barRepository = barRepository;
        this.beerService = beerService;
        this.notificationService = notificationService;
        this.openAIService = openAIService;
        this.scraperService = scraperService;
        this.barBeerCurrentRepository = barBeerCurrentRepository;
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
     * Get a check by ID, ensuring it is associated with a specific bar.
     *
     * @param checkId the check ID
     * @param barId the bar ID
     * @return the check
     */
    public Optional<BarCheck> getCheckById(Long checkId, Long barId) {
        return barCheckRepository.findById(checkId)
                .filter(barCheck -> barCheck.getBar().getId().equals(barId));
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
    public BarCheck createCheck(Bar bar, boolean forced, String menuContent, String contentType, String menuHash, int initialProcessDuration) {
        // Check if the menu has changed
        boolean hasChanges = forced || (bar.getLastMenuHash() == null || !bar.getLastMenuHash().equals(menuHash));

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
                var barUpdateResult = updateBarBeers(bar, beerOutputs);
                changes = barUpdateResult.changes();

                // Send notifications
                notificationService.sendBarMenuChangedNotifications(bar, barUpdateResult);

                check.setBeersAdded(barUpdateResult.beersAdded());
                check.setBeersRemoved(barUpdateResult.beersRemoved());
            }
            
            // Update status to completed
            check.setProcessingStatus(BarCheck.ProcessingStatus.COMPLETED);
            success = true;
        } catch (Exception e) {
            // Update status to failed
            LOGGER.error("Error processing check for bar {}", bar.getName(), e);
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

    public record BarUpdateResult(int changes, Set<Beer> beersAdded, Set<Beer> beersRemoved) {
        BarUpdateResult(Set<Beer> beersAdded, Set<Beer> beersRemoved) {
            this(beersAdded.size() + beersRemoved.size(), beersAdded, beersRemoved);
        }
    }

    /**
     * Update a bar's beers.
     *
     * @param bar the bar
     * @param newBeers the new beers
     * @return Number of changes made
     */
    @Transactional
    public BarUpdateResult updateBarBeers(Bar bar, List<BeerListOutput.BeerOutput> newBeers) {
        var beersAdded = new HashSet<Beer>();
        var beersRemoved = new HashSet<Beer>();

        // Move current beers to past beers
        bar.getCurrentBeers()
                .forEach(bar::addPastBeer);

        var currentBeersSet = new HashSet<>(bar.getCurrentBeers());

        LOGGER.debug("Current beers for bar {}: {}\n{}", bar.getName(), currentBeersSet.size(), currentBeersSet.stream().map(b -> "#%d  %s".formatted(b.getBeer().getId(), b.getBeer().getName())).toList());

        // Beers that are already in the system
        var existingBeersIds = new HashSet<Long>();

        // Add new beers
        for (BeerListOutput.BeerOutput beerOutput : newBeers) {
            // Find or create the beer
            var existingBeerCreate = beerService.findOrCreateBeer(
                    beerOutput.name,
                    beerOutput.brewery,
                    beerOutput.type,
                    beerOutput.abv,
                    beerOutput.description
            );

            var existingBeer = existingBeerCreate.beer();

            if (!existingBeerCreate.alreadyExists()) {
                LOGGER.debug("New beer found: {} ({})", existingBeer.getName(), existingBeer.getId());
                // We don't want beers to be in past beers if they're currently available
                bar.removePastBeer(existingBeer);
            } else {
                LOGGER.debug("Existing beer found: {} ({})", existingBeer.getName(), existingBeer.getId());
                // Don't remove this from current beers at the end, because it's still available
                existingBeersIds.add(existingBeer.getId());
            }

            LOGGER.debug("Processing beer: {} ({})", existingBeer.getName(), existingBeer.getId());

            // If the beer needs an updated BarBeerCurrent, update it. If not, create a new one
            var existingBeerCurrentOptional = currentBeersSet.stream().filter(b -> b.getBeer().getId().equals(existingBeer.getId())).findFirst();

            if (existingBeerCurrentOptional.isPresent()) { // Updating a current beer that was found again
                LOGGER.debug("Already exists");
                var barBeerCurrent = existingBeerCurrentOptional.get();

                barBeerCurrent.setLastVerifiedAt(LocalDateTime.now());
                barBeerCurrentRepository.save(barBeerCurrent);

                LOGGER.debug("Updating last verified time for beer: {} at bar: {}", existingBeer.getName(), bar.getName());
            } else { // Send notifications for new beers
                LOGGER.debug("Doesnt already exist");
                beersAdded.add(existingBeer);
                notificationService.sendBeerAvailableNotifications(bar, existingBeer);

                LOGGER.debug("Adding beer: {} to bar: {}", existingBeer.getName(), bar.getName());
                var added = bar.addCurrentBeer(existingBeer); // I think this should always be true
                LOGGER.debug("\t^ Added: {}", added);
            }
        }

        // Filter beers that weren't found in the new beers list
        for (BarBeerCurrent bb : currentBeersSet) { // TODO: logic makes no sense
            if (!existingBeersIds.contains(bb.getBeer().getId())) {
                LOGGER.debug("Removing beer: {} from bar: {}", bb.getBeer().getName(), bar.getName());
                bar.removeCurrentBeer(bb.getBeer());
                bar.addPastBeer(bb);
                beersRemoved.add(bb.getBeer());
            }
        }

        // Save the bar
        barRepository.save(bar);

        return new BarUpdateResult(beersAdded, beersRemoved);
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