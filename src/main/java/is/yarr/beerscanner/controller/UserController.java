package is.yarr.beerscanner.controller;

import is.yarr.beerscanner.dto.BarDTO;
import is.yarr.beerscanner.dto.BeerDTO;
import is.yarr.beerscanner.dto.BeerTrackingDTO;
import is.yarr.beerscanner.dto.UserDTO;
import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.Permission;
import is.yarr.beerscanner.model.User;
import is.yarr.beerscanner.security.UserPrincipal;
import is.yarr.beerscanner.service.DTOMapperService;
import is.yarr.beerscanner.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for user-related operations.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final DTOMapperService dtoMapperService;

    public UserController(UserService userService, DTOMapperService dtoMapperService) {
        this.userService = userService;
        this.dtoMapperService = dtoMapperService;
    }

    /**
     * Get the current user.
     *
     * @param userPrincipal the authenticated user
     * @return the current user
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        User user = userService.getCurrentUser(userPrincipal);
        var toDto = dtoMapperService.toDTO(user);
        return ResponseEntity.ok(toDto);
    }

    /**
     * Serves a user's profile picture.
     *
     * @param userId The ID of the user whose profile picture is requested.
     * @return The profile picture as a resource.
     */
    @GetMapping("/profile-picture/{userId}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable Long userId) {
        return userService.getProfilePicture(userId);
    }

    /**
     * Update notification settings.
     *
     * @param userPrincipal the authenticated user
     * @param request the request containing notification settings
     * @return the updated user
     */
    @PutMapping("/me/notification-settings")
    public ResponseEntity<UserDTO> updateNotificationSettings(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody Map<String, Boolean> request) {

        Boolean notificationEnabled = request.get("notificationEnabled");
        if (notificationEnabled == null) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.updateNotificationSettings(userPrincipal.getId(), notificationEnabled);
        return ResponseEntity.ok(dtoMapperService.toDTO(user));
    }

    /**
     * Get tracked bars.
     *
     * @param userPrincipal the authenticated user
     * @return the tracked bars
     */
    @GetMapping("/me/tracked-bars")
    public ResponseEntity<List<BarDTO>> getTrackedBars(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Bar> bars = userService.getTrackedBars(userPrincipal.getId());
        return ResponseEntity.ok(dtoMapperService.toBarDTOList(bars));
    }

    /**
     * Track a bar.
     *
     * @param userPrincipal the authenticated user
     * @param barId the bar ID
     * @return the updated user
     */
    @PostMapping("/me/tracked-bars/{barId}")
    public ResponseEntity<UserDTO> trackBar(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long barId) {

        User user = userService.trackBar(userPrincipal.getId(), barId);
        return ResponseEntity.ok(dtoMapperService.toDTO(user));
    }

    /**
     * Untrack a bar.
     *
     * @param userPrincipal the authenticated user
     * @param barId the bar ID
     * @return the updated user
     */
    @DeleteMapping("/me/tracked-bars/{barId}")
    public ResponseEntity<UserDTO> untrackBar(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long barId) {

        User user = userService.untrackBar(userPrincipal.getId(), barId);
        return ResponseEntity.ok(dtoMapperService.toDTO(user));
    }

    /**
     * Get tracked beers.
     *
     * @param userPrincipal the authenticated user
     * @return the tracked beers
     */
    @GetMapping("/me/tracked-beers")
    public ResponseEntity<List<BeerDTO>> getTrackedBeers(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Beer> beers = userService.getTrackedBeers(userPrincipal.getId());
        return ResponseEntity.ok(dtoMapperService.toBeerDTOList(beers));
    }

    /**
     * Get beer trackings.
     *
     * @param userPrincipal the authenticated user
     * @return the beer trackings
     */
    @GetMapping("/me/beer-trackings")
    public ResponseEntity<List<BeerTrackingDTO>> getBeerTrackings(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<BeerTrackingDTO> trackings = userService.getBeerTrackings(userPrincipal.getId());
        return ResponseEntity.ok(trackings);
    }

    /**
     * Track a beer either globally or at a specific bar.
     *
     * @param userPrincipal the authenticated user
     * @param beerId the beer ID
     * @param barId the bar ID (optional, if null, tracks beer at any bar)
     * @return the updated user
     */
    @PostMapping("/me/tracked-beers/{beerId}")
    public ResponseEntity<UserDTO> trackBeer(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long beerId,
            @RequestParam(required = false) Long barId) {

        User user = userService.trackBeer(userPrincipal.getId(), beerId, barId);
        return ResponseEntity.ok(dtoMapperService.toDTO(user));
    }

    /**
     * Untrack a beer either globally or at a specific bar.
     *
     * @param userPrincipal the authenticated user
     * @param beerId the beer ID
     * @param barId the bar ID (optional, if null, untracks beer at any bar)
     * @return the updated user
     */
    @DeleteMapping("/me/tracked-beers/{beerId}")
    public ResponseEntity<UserDTO> untrackBeer(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long beerId,
            @RequestParam(required = false) Long barId) {

        User user = userService.untrackBeer(userPrincipal.getId(), beerId, barId);
        return ResponseEntity.ok(dtoMapperService.toDTO(user));
    }

    /**
     * Get all available permissions.
     *
     * @return the list of all available permissions
     */
    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_ADMIN')")
    public ResponseEntity<List<String>> getAllPermissions() {
        return ResponseEntity.ok(
            Arrays.stream(Permission.values())
                .map(Enum::name)
                .collect(Collectors.toList())
        );
    }

    /**
     * Update a user's permissions.
     *
     * @param userId the user ID
     * @param permissions the new permissions
     * @return the updated user
     */
    @PutMapping("/{userId}/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_ADMIN')")
    public ResponseEntity<UserDTO> updateUserPermissions(
            @PathVariable Long userId,
            @RequestBody Set<String> permissions) {

        User user = userService.updateUserPermissions(userId, permissions);
        return ResponseEntity.ok(dtoMapperService.toDTO(user));
    }
}
