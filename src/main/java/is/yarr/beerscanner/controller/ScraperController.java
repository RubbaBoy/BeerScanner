package is.yarr.beerscanner.controller;

import is.yarr.beerscanner.dto.ScraperStatsDTO;
import is.yarr.beerscanner.service.ScraperService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class ScraperController {

    private final ScraperService scraperService;

    public ScraperController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @GetMapping("/scraper/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScraperStatsDTO> getAllScraperStates() {
        ScraperStatsDTO stats = scraperService.getAllAggregatedStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/scraper/stats/{barId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScraperStatsDTO> getAllScraperStats(@PathVariable Long barId) {
        ScraperStatsDTO stats = scraperService.getBarStats(barId);
        return ResponseEntity.ok(stats);
    }

}
