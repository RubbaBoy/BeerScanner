package is.yarr.beerscanner.controller;

import is.yarr.beerscanner.dto.BarDTO;
import is.yarr.beerscanner.dto.BeerDTO;
import is.yarr.beerscanner.dto.BeerRequestDTO;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.BeerRequest;
import is.yarr.beerscanner.security.UserPrincipal;
import is.yarr.beerscanner.service.BeerService;
import is.yarr.beerscanner.service.DTOMapperService;
import is.yarr.beerscanner.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for beer-related operations.
 */
@RestController
public class BeerController {

    private final BeerService beerService;
    private final UserService userService;
    private final DTOMapperService dtoMapperService;

    public BeerController(BeerService beerService, UserService userService, DTOMapperService dtoMapperService) {
        this.beerService = beerService;
        this.userService = userService;
        this.dtoMapperService = dtoMapperService;
    }

    /**
     * Get all beers (public endpoint).
     *
     * @param pageable pagination information
     * @return a page of beer DTOs
     */
    @GetMapping("/api/v1/beers/public")
    public ResponseEntity<Page<BeerDTO>> getAllBeers(Pageable pageable) {
        Page<Beer> beers = beerService.getAllBeers(pageable);
        List<BeerDTO> beerDTOs = beers.getContent().stream()
                .map(dtoMapperService::toDTOExtended)
                .collect(Collectors.toList());
        Page<BeerDTO> beerDTOPage = new PageImpl<>(beerDTOs, pageable, beers.getTotalElements());
        return ResponseEntity.ok(beerDTOPage);
    }

    /**
     * Search for beers (public endpoint).
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return a page of beer DTOs matching the search term
     */
    @GetMapping("/api/v1/beers/public/search")
    public ResponseEntity<Page<BeerDTO>> searchBeers(
            @RequestParam String searchTerm,
            Pageable pageable) {

        Page<Beer> beers = beerService.searchBeers(searchTerm, pageable);
        List<BeerDTO> beerDTOs = beers.getContent().stream()
                .map(dtoMapperService::toDTOExtended)
                .collect(Collectors.toList());
        Page<BeerDTO> beerDTOPage = new PageImpl<>(beerDTOs, pageable, beers.getTotalElements());
        return ResponseEntity.ok(beerDTOPage);
    }

    /**
     * Get a beer by ID (public endpoint).
     *
     * @param id the beer ID
     * @return the beer DTO
     */
    @GetMapping("/api/v1/beers/public/{id}")
    public ResponseEntity<BeerDTO> getBeerById(@PathVariable Long id) {
        Beer beer = beerService.getBeerById(id);
        BeerDTO beerDTO = dtoMapperService.toDTOExtended(beer);
        return ResponseEntity.ok(beerDTO);
    }

    /**
     * Get beers by type (public endpoint).
     *
     * @param type the beer type
     * @param pageable pagination information
     * @return a page of beer DTOs of the specified type
     */
    @GetMapping("/api/v1/beers/public/by-type")
    public ResponseEntity<Page<BeerDTO>> getBeersByType(
            @RequestParam String type,
            Pageable pageable) {

        Page<Beer> beers = beerService.getBeersByType(type, pageable);
        List<BeerDTO> beerDTOs = beers.getContent().stream()
                .map(dtoMapperService::toDTOExtended)
                .collect(Collectors.toList());
        Page<BeerDTO> beerDTOPage = new PageImpl<>(beerDTOs, pageable, beers.getTotalElements());
        return ResponseEntity.ok(beerDTOPage);
    }

    /**
     * Get beers by brewery (public endpoint).
     *
     * @param brewery the beer brewery
     * @param pageable pagination information
     * @return a page of beer DTOs from the specified brewery
     */
    @GetMapping("/api/v1/beers/public/by-brewery")
    public ResponseEntity<Page<BeerDTO>> getBeersByBrewery(
            @RequestParam String brewery,
            Pageable pageable) {

        Page<Beer> beers = beerService.getBeersByBrewery(brewery, pageable);
        List<BeerDTO> beerDTOs = beers.getContent().stream()
                .map(dtoMapperService::toDTOExtended)
                .collect(Collectors.toList());
        Page<BeerDTO> beerDTOPage = new PageImpl<>(beerDTOs, pageable, beers.getTotalElements());
        return ResponseEntity.ok(beerDTOPage);
    }

    /**
     * Get bars where a beer is currently available (public endpoint).
     *
     * @param beerId the beer ID
     * @return a list of bar DTOs where the beer is currently available
     */
    @GetMapping("/api/v1/beers/public/{beerId}/available-at")
    public ResponseEntity<List<BarDTO>> getBarsWithBeer(@PathVariable Long beerId) {
        Beer beer = beerService.getBeerById(beerId);
        List<BarDTO> barDTOs = beer.getAvailableAt().stream()
                .map(dtoMapperService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(barDTOs);
    }

    /**
     * Request a new beer.
     *
     * @param userPrincipal the authenticated user
     * @param beerRequestDTO the beer DTO to request
     * @return the created beer DTO
     */
    @PostMapping("/api/v1/beers/request") // TODO: Added
    public ResponseEntity<Void> requestBeer(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody BeerRequestDTO beerRequestDTO) {

        var user = userService.getUserById(userPrincipal.getId());

        // Convert DTO to entity
        BeerRequest beerRequest = BeerRequest.builder()
                .name(beerRequestDTO.getName())
                .type(beerRequestDTO.getType())
                .brewery(beerRequestDTO.getBrewery())
                .abv(beerRequestDTO.getAbv())
                .description(beerRequestDTO.getDescription())
                .requestedBy(user)
                .build();

        // Create the beer
        beerService.requestBeer(beerRequest);

        return ResponseEntity.ok().build();
    }

    /**
     * Get beers requested by the current user, that haven't been approved yet.
     *
     * @param userPrincipal the authenticated user
     * @return a list of beer request DTOs requested by the user
     */
    @GetMapping("/api/v1/beers/my-requests")
    public ResponseEntity<Page<BeerRequestDTO>> getMyRequestedBars(@AuthenticationPrincipal UserPrincipal userPrincipal, Pageable pageable) {
        Page<BeerRequest> beerRequests = beerService.getBeerRequestsByRequestedUser(userPrincipal.getId(), pageable);
        List<BeerRequestDTO> beerRequestsDTOs = beerRequests.getContent().stream()
                .map(dtoMapperService::toDTO)
                .collect(Collectors.toList());
        Page<BeerRequestDTO> beerRequestDTOPage = new PageImpl<>(beerRequestsDTOs, pageable, beerRequests.getTotalElements());
        return ResponseEntity.ok(beerRequestDTOPage);
    }

    /**
     * Get unapproved beers (admin only).
     *
     * @param pageable pagination information
     * @return a page of unapproved beer DTOs
     */
    @GetMapping("/api/v1/admin/beers/unapproved")
    @PreAuthorize("hasRole('ADMIN')") // TODO Added
    public ResponseEntity<Page<BeerRequestDTO>> getUnapprovedBars(Pageable pageable) {
        Page<BeerRequest> beerRequests = beerService.getBeerRequests(pageable);
        List<BeerRequestDTO> beerRequestDTOs = beerRequests.getContent().stream()
                .map(dtoMapperService::toDTO)
                .collect(Collectors.toList());
        Page<BeerRequestDTO> beerRequestDTOPage = new PageImpl<>(beerRequestDTOs, pageable, beerRequests.getTotalElements());
        return ResponseEntity.ok(beerRequestDTOPage);
    }

    /**
     * Approve a requested beer (admin only).
     *
     * @param requestId the beer request ID
     * @return the approved beer DTO
     */
    @PutMapping("/api/v1/admin/beers/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')") // TODO Added
    public ResponseEntity<BeerDTO> approveBar(@PathVariable Long requestId) {
        Beer beer = beerService.approveBeer(requestId);
        BeerDTO beerDTO = dtoMapperService.toDTO(beer);
        return ResponseEntity.ok(beerDTO);
    }

    /**
     * Approve a requested beer (admin only).
     *
     * @param requestId the beer request ID
     * @return the approved beer DTO
     */
    @PutMapping("/api/v1/admin/beers/{requestId}/unapprove")
    @PreAuthorize("hasRole('ADMIN')") // TODO Added
    public ResponseEntity<BeerDTO> unapproveBar(@PathVariable Long requestId) {
        beerService.unapproveBeer(requestId);
        return ResponseEntity.ok().build();
    }

    /**
     * Create a new beer (admin only).
     *
     * @param beerDTO the beer DTO to create
     * @return the created beer DTO
     */
    @PostMapping("/api/v1/admin/beers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BeerDTO> createBeer(@RequestBody BeerDTO beerDTO) {
        // TODO: Validate beer more

        if (!beerService.isValidBeerType(beerDTO.getType())) {
            return ResponseEntity.badRequest().build();
        }

        // Convert DTO to entity
        Beer beer = Beer.builder()
                .name(beerDTO.getName())
                .type(beerService.getAbbreviatedBeerType(beerDTO.getType()))
                .brewery(beerDTO.getBrewery())
                .abv(beerDTO.getAbv())
                .description(beerDTO.getDescription())
                .build();

        Beer createdBeer = beerService.createBeer(beer);
        return ResponseEntity.ok(dtoMapperService.toDTO(createdBeer));
    }

    /**
     * Update a beer (admin only).
     *
     * @param id the beer ID
     * @param beerDTO the updated beer DTO
     * @return the updated beer DTO
     */
    @PutMapping("/api/v1/admin/beers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BeerDTO> updateBeer(
            @PathVariable Long id,
            @RequestBody BeerDTO beerDTO) {

        if (!beerService.isValidBeerType(beerDTO.getType())) {
            return ResponseEntity.badRequest().build();
        }

        // Convert DTO to entity
        Beer beer = Beer.builder()
                .name(beerDTO.getName())
                .type(beerService.getAbbreviatedBeerType(beerDTO.getType()))
                .brewery(beerDTO.getBrewery())
                .abv(beerDTO.getAbv())
                .description(beerDTO.getDescription())
                .build();

        Beer updatedBeer = beerService.updateBeer(id, beer);
        return ResponseEntity.ok(dtoMapperService.toDTO(updatedBeer));
    }

    /**
     * Delete a beer (admin only).
     *
     * @param id the beer ID
     * @return no content
     */
    @DeleteMapping("/api/v1/admin/beers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBeer(@PathVariable Long id) {
        beerService.deleteBeer(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Find or create a beer (admin only).
     *
     * @param name the beer name
     * @param brewery the beer brewery
     * @param type the beer type
     * @param abv the beer ABV
     * @return the found or created beer DTO
     */
    @PostMapping("/api/v1/admin/beers/find-or-create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BeerDTO> findOrCreateBeer(
            @RequestParam String name,
            @RequestParam(required = false) String brewery,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double abv,
            @RequestParam(required = false) String description) {

        Beer beer = beerService.findOrCreateBeer(name, brewery, type, abv, description);
        return ResponseEntity.ok(dtoMapperService.toDTO(beer));
    }
}
