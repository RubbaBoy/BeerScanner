package is.yarr.beerscanner.service;

import is.yarr.beerscanner.dto.BeerTrackingDTO;
import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.BeerTracking;
import is.yarr.beerscanner.model.Permission;
import is.yarr.beerscanner.model.User;
import is.yarr.beerscanner.repository.BarRepository;
import is.yarr.beerscanner.repository.BeerRepository;
import is.yarr.beerscanner.repository.BeerTrackingRepository;
import is.yarr.beerscanner.repository.UserRepository;
import is.yarr.beerscanner.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for user-related operations.
 */
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Value("${app.data-dir}")
    private String dataDir;

    private final UserRepository userRepository;
    private final BarRepository barRepository;
    private final BeerRepository beerRepository;
    private final BeerTrackingRepository beerTrackingRepository;
    private final DTOMapperService dtoMapperService;

    public UserService(UserRepository userRepository, BarRepository barRepository, BeerRepository beerRepository, BeerTrackingRepository beerTrackingRepository, DTOMapperService dtoMapperService) {
        this.userRepository = userRepository;
        this.barRepository = barRepository;
        this.beerRepository = beerRepository;
        this.beerTrackingRepository = beerTrackingRepository;
        this.dtoMapperService = dtoMapperService;
    }

    /**
     * Get the current user.
     *
     * @param userPrincipal the user principal
     * @return the user
     */
    public User getCurrentUser(UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Get a user by ID.
     *
     * @param id the user ID
     * @return the user
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
    }

    /**
     * Get a user by email.
     *
     * @param email the user email
     * @return the user
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public ResponseEntity<Resource> getProfilePicture(long userId) {
        try {
            if (!userRepository.existsById(userId)) {
                LOGGER.debug("User ID {} not found for profile picture request", userId);
                return ResponseEntity.notFound().build();
            }

            var pictureDir = Paths.get(dataDir, "profile-pictures");
            var file = pictureDir.resolve("%d.jpg".formatted(userId));

            var resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                // Fallback to a default content type if probe fails
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                LOGGER.debug("Serving profile picture for user ID: {} with content type: {}", userId, contentType);
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                LOGGER.debug("File not found at path: {}", file);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            LOGGER.error("Malformed URL for user ID: {}", userId, e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            LOGGER.error("Failed to read profile picture for user ID: {}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update user notification settings.
     *
     * @param userId the user ID
     * @param notificationEnabled whether notifications are enabled
     * @return the updated user
     */
    @Transactional
    public User updateNotificationSettings(Long userId, boolean notificationEnabled) {
        User user = getUserById(userId);
        user.setNotificationEnabled(notificationEnabled);
        return userRepository.save(user);
    }

    /**
     * Track a bar.
     *
     * @param userId the user ID
     * @param barId the bar ID
     * @return the updated user
     */
    @Transactional
    public User trackBar(Long userId, Long barId) {
        User user = getUserById(userId);
        Bar bar = barRepository.findById(barId)
                .orElseThrow(() -> new IllegalArgumentException("Bar not found with ID: " + barId));

        user.getTrackedBars().add(bar);
        return userRepository.save(user);
    }

    /**
     * Untrack a bar.
     *
     * @param userId the user ID
     * @param barId the bar ID
     * @return the updated user
     */
    @Transactional
    public User untrackBar(Long userId, Long barId) {
        User user = getUserById(userId);
        Bar bar = barRepository.findById(barId)
                .orElseThrow(() -> new IllegalArgumentException("Bar not found with ID: " + barId));

        user.getTrackedBars().remove(bar);
        return userRepository.save(user);
    }

    /**
     * Track a beer either globally or at a specific bar.
     *
     * @param userId the user ID
     * @param beerId the beer ID
     * @param barId the bar ID (optional, if null, tracks beer at any bar)
     * @return the updated user
     */
    @Transactional
    public User trackBeer(Long userId, Long beerId, Long barId) {
        User user = getUserById(userId);
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new IllegalArgumentException("Beer not found with ID: " + beerId));

        Bar bar = null;
        if (barId != null) {
            bar = barRepository.findById(barId)
                    .orElseThrow(() -> new IllegalArgumentException("Bar not found with ID: " + barId));
        }

        // Check if already tracking
        if (beerTrackingRepository.existsByUserAndBeerAndBar(user, beer, bar)) {
            return user; // Already tracking this beer with this bar configuration
        }

        BeerTracking tracking = BeerTracking.builder()
                .user(user)
                .beer(beer)
                .bar(bar)
                .build();

        beerTrackingRepository.save(tracking);
        return user;
    }

    /**
     * Untrack a beer either globally or at a specific bar.
     *
     * @param userId the user ID
     * @param beerId the beer ID
     * @param barId the bar ID (optional, if null, untracks beer at any bar)
     * @return the updated user
     */
    @Transactional
    public User untrackBeer(Long userId, Long beerId, Long barId) {
        User user = getUserById(userId);
        Beer beer = beerRepository.findById(beerId)
                .orElseThrow(() -> new IllegalArgumentException("Beer not found with ID: " + beerId));

        Bar bar = null;
        if (barId != null) {
            bar = barRepository.findById(barId)
                    .orElseThrow(() -> new IllegalArgumentException("Bar not found with ID: " + barId));
        }

        Optional<BeerTracking> tracking = beerTrackingRepository.findByUserAndBeerAndBar(user, beer, bar);
        tracking.ifPresent(beerTrackingRepository::delete);

        return user;
    }

    /**
     * Get tracked bars for a user.
     *
     * @param userId the user ID
     * @return the tracked bars
     */
    public List<Bar> getTrackedBars(Long userId) {
        User user = getUserById(userId);
        return List.copyOf(user.getTrackedBars());
    }

    /**
     * Get tracked beers for a user.
     *
     * @param userId the user ID
     * @return the tracked beers
     */
    public List<Beer> getTrackedBeers(Long userId) {
        User user = getUserById(userId);
        List<BeerTracking> trackings = beerTrackingRepository.findByUser(user);
        return trackings.stream()
                .map(BeerTracking::getBeer)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Get beer trackings for a user.
     *
     * @param userId the user ID
     * @return the beer trackings
     */
    public List<BeerTrackingDTO> getBeerTrackings(Long userId) {
        User user = getUserById(userId);
        List<BeerTracking> trackings = beerTrackingRepository.findByUser(user);
        return dtoMapperService.toBeerTrackingDTOList(trackings);
    }

    /**
     * Update a user's permissions.
     *
     * @param userId the user ID
     * @param permissionStrings the new permissions as strings
     * @return the updated user
     */
    @Transactional
    public User updateUserPermissions(Long userId, Set<String> permissionStrings) {
        User user = getUserById(userId);

        // Convert string permissions to Permission enum values
        Set<Permission> permissions = new HashSet<>();
        for (String permissionString : permissionStrings) {
            try {
                Permission permission = Permission.valueOf(permissionString);
                permissions.add(permission);
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Invalid permission: {}", permissionString);
                // Skip invalid permissions
            }
        }

        // Set the permissions on the user
        user.setPermissions(permissions);

        // Save and return the updated user
        return userRepository.save(user);
    }
}
