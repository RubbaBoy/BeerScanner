package is.yarr.beerscanner.controller;

import is.yarr.beerscanner.dto.BarCheckDTO;
import is.yarr.beerscanner.dto.BarDTO;
import is.yarr.beerscanner.dto.BeerDTO;
import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.BarCheck;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.scheduler.BarCheckScheduler;
import is.yarr.beerscanner.security.UserPrincipal;
import is.yarr.beerscanner.service.BarCheckService;
import is.yarr.beerscanner.service.BarService;
import is.yarr.beerscanner.service.DTOMapperService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for bar-related operations.
 */
@RestController
public class BarController {

    private final BarService barService;
    private final BarCheckService barCheckService;
    private final DTOMapperService dtoMapperService;
    private final BarCheckScheduler barCheckScheduler;

    public BarController(BarService barService, BarCheckService barCheckService, DTOMapperService dtoMapperService, BarCheckScheduler barCheckScheduler) {
        this.barService = barService;
        this.barCheckService = barCheckService;
        this.dtoMapperService = dtoMapperService;
        this.barCheckScheduler = barCheckScheduler;
    }

    /**
     * Get all bars (public endpoint).
     *
     * @param pageable pagination information
     * @return a page of bar DTOs
     */
    @GetMapping("/api/v1/bars/public")
    public ResponseEntity<Page<BarDTO>> getAllBars(Pageable pageable) {
        Page<Bar> bars = barService.getAllBars(pageable);
        List<BarDTO> barDTOs = bars.getContent().stream()
                .map(dtoMapperService::toDTO)
                .collect(Collectors.toList());
        Page<BarDTO> barDTOPage = new PageImpl<>(barDTOs, pageable, bars.getTotalElements());
        return ResponseEntity.ok(barDTOPage);
    }

    /**
     * Search for bars (public endpoint).
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of bar DTOs matching the search term
     */
    @GetMapping("/api/v1/bars/public/search")
    public ResponseEntity<Page<BarDTO>> searchBars(
            @RequestParam String searchTerm,
            Pageable pageable) {

        Page<Bar> bars = barService.searchBars(searchTerm, pageable);
        List<BarDTO> barDTOs = bars.getContent().stream()
                .map(dtoMapperService::toDTO)
                .collect(Collectors.toList());
        Page<BarDTO> barDTOPage = new PageImpl<>(barDTOs, pageable, bars.getTotalElements());
        return ResponseEntity.ok(barDTOPage);
    }

    /**
     * Get a bar by ID (public endpoint).
     *
     * @param id the bar ID
     * @return the bar DTO
     */
    @GetMapping("/api/v1/bars/public/{id}")
    public ResponseEntity<BarDTO> getBarById(@PathVariable Long id) {
        Bar bar = barService.getBarById(id);
        BarDTO barDTO = dtoMapperService.toDTO(bar);
        return ResponseEntity.ok(barDTO);
    }

    /**
     * Get current beers for a bar (public endpoint).
     *
     * @param barId the bar ID
     * @return the current beer DTOs
     */
    @GetMapping("/api/v1/bars/public/{barId}/current-beers")
    public ResponseEntity<List<BeerDTO>> getCurrentBeers(@PathVariable Long barId) {
        Set<Beer> beers = barService.getCurrentBeers(barId);
        List<BeerDTO> beerDTOs = beers.stream()
                .map(dtoMapperService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beerDTOs);
    }

    /**
     * Get past beers for a bar (public endpoint).
     *
     * @param barId the bar ID
     * @return the past beer DTOs
     */
    @GetMapping("/api/v1/bars/public/{barId}/past-beers")
    public ResponseEntity<List<BeerDTO>> getPastBeers(@PathVariable Long barId) {
        Set<Beer> beers = barService.getPastBeers(barId);
        List<BeerDTO> beerDTOs = beers.stream()
                .map(dtoMapperService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(beerDTOs);
    }

    /**
     * Request a new bar.
     *
     * @param userPrincipal the authenticated user
     * @param barDTO the bar DTO to create
     * @return the created bar DTO
     */
    @PostMapping("/api/v1/bars")
    public ResponseEntity<BarDTO> requestBar(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody BarDTO barDTO) {

        // Convert DTO to entity
        Bar bar = Bar.builder()
                .name(barDTO.getName())
                .location(barDTO.getLocation())
                .menuUrl(barDTO.getMenuUrl())
                .isApproved(false) // New bars are not approved by default
                .build();

        Bar createdBar = barService.createBar(bar, userPrincipal.getId());
        return ResponseEntity.ok(dtoMapperService.toDTO(createdBar));
    }

    /**
     * Get bars requested by the current user.
     *
     * @param userPrincipal the authenticated user
     * @return a list of bar DTOs requested by the user
     */
    @GetMapping("/api/v1/bars/my-requests")
    public ResponseEntity<List<BarDTO>> getMyRequestedBars(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Bar> bars = barService.getBarsByRequestedUser(userPrincipal.getId());
        List<BarDTO> barDTOs = bars.stream()
                .map(dtoMapperService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(barDTOs);
    }

    /**
     * Administratively creates a new bar (already approved).
     *
     * @param userPrincipal the authenticated user
     * @param barDTO the bar DTO to create
     * @return the created bar DTO
     */
    @PostMapping("/api/v1/admin/bars")
    public ResponseEntity<BarDTO> createBar(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody BarDTO barDTO) {

        // Convert DTO to entity
        Bar bar = Bar.builder()
                .name(barDTO.getName())
                .location(barDTO.getLocation())
                .menuUrl(barDTO.getMenuUrl())
                .isApproved(true)
                .build();

        Bar createdBar = barService.createBar(bar, userPrincipal.getId());
        return ResponseEntity.ok(dtoMapperService.toDTO(createdBar));
    }

    /**
     * Get unapproved bars (admin only).
     *
     * @param pageable pagination information
     * @return a page of unapproved bar DTOs
     */
    @GetMapping("/api/v1/admin/bars/unapproved")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BarDTO>> getUnapprovedBars(Pageable pageable) {
        Page<Bar> bars = barService.getUnapprovedBars(pageable);
        List<BarDTO> barDTOs = bars.getContent().stream()
                .map(dtoMapperService::toDTO)
                .collect(Collectors.toList());
        Page<BarDTO> barDTOPage = new PageImpl<>(barDTOs, pageable, bars.getTotalElements());
        return ResponseEntity.ok(barDTOPage);
    }

    /**
     * Approve a bar (admin only).
     *
     * @param id the bar ID
     * @return the approved bar DTO
     */
    @PostMapping("/api/v1/admin/bars/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BarDTO> approveBar(@PathVariable Long id) {
        Bar bar = barService.approveBar(id);
        BarDTO barDTO = dtoMapperService.toDTO(bar);
        return ResponseEntity.ok(barDTO);
    }

    /**
     * Update a bar (admin only).
     *
     * @param id the bar ID
     * @param barDTO the updated bar DTO
     * @return the updated bar DTO
     */
    @PutMapping("/api/v1/admin/bars/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BarDTO> updateBar(
            @PathVariable Long id,
            @RequestBody BarDTO barDTO) {

        // Convert DTO to entity
        Bar bar = Bar.builder()
                .name(barDTO.getName())
                .location(barDTO.getLocation())
                .aiInstructions(barDTO.getAiInstructions())
                .menuUrl(barDTO.getMenuUrl())
                .build();

        Bar updatedBar = barService.updateBar(id, bar);
        return ResponseEntity.ok(dtoMapperService.toDTO(updatedBar));
    }

    /**
     * Delete a bar (admin only).
     *
     * @param id the bar ID
     * @return no content
     */
    @DeleteMapping("/api/v1/admin/bars/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBar(@PathVariable Long id) {
        barService.deleteBar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get checks for a bar (admin only).
     *
     * @param barId the bar ID
     * @param pageable pagination information
     * @return a page of check DTOs for the bar
     */
    @GetMapping("/api/v1/admin/bars/{barId}/checks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BarCheckDTO>> getChecksForBar(
            @PathVariable Long barId,
            Pageable pageable) {

        Page<BarCheck> checks = barCheckService.getChecksForBar(barId, pageable);
        List<BarCheckDTO> checkDTOs = checks.getContent().stream()
                .map(dtoMapperService::toDTO)
                .collect(Collectors.toList());
        Page<BarCheckDTO> checkDTOPage = new PageImpl<>(checkDTOs, pageable, checks.getTotalElements());
        return ResponseEntity.ok(checkDTOPage);
    }

    /**
     * Get the most recent check for a bar (admin only).
     *
     * @param barId the bar ID
     * @return the most recent check DTO for the bar
     */
    @GetMapping("/api/v1/admin/bars/{barId}/checks/latest")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BarCheckDTO> getLatestCheckForBar(@PathVariable Long barId) {
        Optional<BarCheck> check = barCheckService.getMostRecentCheckForBar(barId);
        return check.map(c -> ResponseEntity.ok(dtoMapperService.toDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Manually check a bar (admin only).
     *
     * @param barId the bar ID
     * @return the created check DTO
     */
    @PostMapping("/api/v1/admin/bars/{barId}/check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BarCheckDTO> checkBar(@PathVariable Long barId) {
        Bar bar = barService.getBarById(barId);

        return barCheckScheduler.checkBar(bar)
                .map(dtoMapperService::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.internalServerError().build());
    }

    /**
     * Manually check a bar (admin only).
     *
     * @param barId the bar ID
     * @return the created check DTO
     */
//    @GetMapping("/api/v1/admin/bars/check")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<BarCheckDTO> checkAllBar() {
//        System.out.println("aaaaaaaa");
//        List<Bar> barsToCheck = barService.getBarsToCheck();
//
//        for (Bar bar : barsToCheck) {
//            System.out.println("bar = " + bar);
//
//            try {
//                barCheckScheduler.checkBar(bar);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        return ResponseEntity.ok(null);
//    }
}
