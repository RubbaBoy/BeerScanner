package is.yarr.beerscanner.service;

import is.yarr.beerscanner.dto.BarAdminDTO;
import is.yarr.beerscanner.dto.BarCheckDTO;
import is.yarr.beerscanner.dto.BarDTO;
import is.yarr.beerscanner.dto.BeerAliasDTO;
import is.yarr.beerscanner.dto.BeerAvailabilityDTO;
import is.yarr.beerscanner.dto.BeerDTO;
import is.yarr.beerscanner.dto.BeerRequestDTO;
import is.yarr.beerscanner.dto.BeerTrackingDTO;
import is.yarr.beerscanner.dto.NotificationDTO;
import is.yarr.beerscanner.dto.ScraperStatsDTO;
import is.yarr.beerscanner.dto.UserDTO;
import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.BarCheck;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.BeerAlias;
import is.yarr.beerscanner.model.BeerRequest;
import is.yarr.beerscanner.model.BeerTracking;
import is.yarr.beerscanner.model.Notification;
import is.yarr.beerscanner.model.ScraperStats;
import is.yarr.beerscanner.model.User;
import is.yarr.beerscanner.model.beer.BarBeerCurrent;
import is.yarr.beerscanner.model.beer.BarBeerHistory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for mapping between entities and DTOs.
 */
@Service
public class DTOMapperService {

    /**
     * Convert a Beer entity to a BeerDTO.
     *
     * @param beer the Beer entity
     * @return the BeerDTO
     */
    public BeerDTO toDTO(Beer beer) {
        if (beer == null) {
            return null;
        }

        return BeerDTO.builder()
                .id(beer.getId())
                .name(beer.getName())
                .type(beer.getType())
                .brewery(beer.getBrewery())
                .abv(beer.getAbv())
                .description(beer.getDescription())
                .createdAt(beer.getCreatedAt())
                .updatedAt(beer.getUpdatedAt())
                .build();
    }

    /**
     * Convert a Beer entity to a BeerDTO. This includes currently and previously available at bars.
     *
     * @param beer the Beer entity
     * @return the BeerDTO
     */
    public BeerDTO toDTOExtended(Beer beer) {
        if (beer == null) {
            return null;
        }

        return BeerDTO.builder()
                .id(beer.getId())
                .name(beer.getName())
                .type(beer.getType())
                .brewery(beer.getBrewery())
                .abv(beer.getAbv())
                .description(beer.getDescription())
                .createdAt(beer.getCreatedAt())
                .updatedAt(beer.getUpdatedAt())
                .availableAt(beer.getAvailableAt().stream().map(BarBeerCurrent::getBar)
                        .map(bar -> new BarDTO(bar.getId(), bar.getName(), bar.getLocation(), bar.getCurrentBeers().size(), bar.getLastCheckedAt())).collect(Collectors.toSet()))
                .previouslyAvailableAt(beer.getBarHistory().stream().map(BarBeerHistory::getBar)
                        .map(bar -> new BarDTO(bar.getId(), bar.getName(), bar.getLocation(), bar.getCurrentBeers().size(), bar.getLastCheckedAt())).collect(Collectors.toSet()))
                .build();
    }

    /**
     * Convert a list of Beer entities to a list of BeerDTOs.
     *
     * @param beers the list of Beer entities
     * @return the list of BeerDTOs
     */
    public List<BeerDTO> toBeerDTOList(List<Beer> beers) {
        return beers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of Beer entities to a list of BeerDTOs.
     *
     * @param beers the list of Beer entities
     * @return the list of BeerDTOs
     */
    public Set<BeerDTO> toBeerDTOSet(Set<Beer> beers) {
        return beers.stream()
                .map(this::toDTO)
                .collect(Collectors.toSet());
    }

    /**
     * Convert a Beer entity to a BeerDTO.
     *
     * @param beerRequest the Beer entity
     * @return the BeerDTO
     */
    public BeerRequestDTO toDTO(BeerRequest beerRequest) {
        if (beerRequest == null) {
            return null;
        }

        return BeerRequestDTO.builder()
                .id(beerRequest.getId())
                .name(beerRequest.getName())
                .type(beerRequest.getType())
                .brewery(beerRequest.getBrewery())
                .abv(beerRequest.getAbv())
                .description(beerRequest.getDescription())
                .requestedBy(toDTO(beerRequest.getRequestedBy()))
                .requestedAt(beerRequest.getRequestedAt())
                .build();
    }

    /**
     * Convert a list of Beer entities to a list of BeerDTOs.
     *
     * @param beerRequests the list of BeerRequest entities
     * @return the list of BeerDTOs
     */
    public List<BeerRequestDTO> toBeerRequestDTOList(List<BeerRequest> beerRequests) {
        return beerRequests.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a Bar entity to a BarDTO.
     *
     * @param bar the Bar entity
     * @return the BarDTO
     */
    public BarDTO toDTO(Bar bar) {
        if (bar == null) {
            return null;
        }

        return BarDTO.builder()
                .id(bar.getId())
                .name(bar.getName())
                .location(bar.getLocation())
                .currentBeerCount(bar.getCurrentBeers().size())
                .lastCheckedAt(bar.getLastCheckedAt())
                .build();
    }

    /**
     * Convert a Bar entity to a BarDTO.
     *
     * @param bar the Bar entity
     * @return the BarDTO
     */
    public BarAdminDTO toDTOAdmin(Bar bar) {
        if (bar == null) {
            return null;
        }

        var builder = BarAdminDTO.builder()
                .id(bar.getId())
                .name(bar.getName())
                .location(bar.getLocation())
                .currentBeerCount(bar.getCurrentBeers().size())
                .lastCheckedAt(bar.getLastCheckedAt())
                // Admin parts
                .aiInstructions(bar.getAiInstructions())
                .menuUrl(bar.getMenuUrl())
                .menuXPath(bar.getMenuXPath())
                .lastMenuHash(bar.getLastMenuHash())
                .isApproved(bar.isApproved())
                .createdAt(bar.getCreatedAt())
                .updatedAt(bar.getUpdatedAt());

        var webpageSettings = bar.getWebpageSettings();
        if (webpageSettings != null) {
            builder.menuComponentXPath(webpageSettings.getMenuComponentXPath())
                    .ageVerificationXPath(webpageSettings.getAgeVerificationXPath())
                    .cleanupScript(webpageSettings.getCleanupScript())
                    .processAsText(webpageSettings.isProcessAsText());
        }

        return builder.build();
    }

    /**
     * Convert a list of Bar entities to a list of BarDTOs.
     *
     * @param bars the list of Bar entities
     * @return the list of BarDTOs
     */
    public List<BarDTO> toBarDTOList(List<Bar> bars) {
        return bars.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a User entity to a UserDTO.
     *
     * @param user the User entity
     * @return the UserDTO
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profilePicture(user.getProfilePicture())
                .notificationEnabled(user.isNotificationEnabled())
                .isAdmin(user.isAdmin())
                .updatedAt(user.getUpdatedAt())
                .createdAt(user.getCreatedAt())
                .googleId(user.getGoogleId())
                .build();
    }

    /**
     * Convert a BeerTracking entity to a BeerTrackingDTO.
     *
     * @param beerTracking the BeerTracking entity
     * @return the BeerTrackingDTO
     */
    public BeerTrackingDTO toDTO(BeerTracking beerTracking) {
        if (beerTracking == null) {
            return null;
        }

        return BeerTrackingDTO.builder()
                .id(beerTracking.getId())
                .userId(beerTracking.getUser().getId())
                .beerId(beerTracking.getBeer().getId())
                .barId(beerTracking.getBar() != null ? beerTracking.getBar().getId() : null)
                .beer(toDTO(beerTracking.getBeer()))
                .bar(toDTO(beerTracking.getBar()))
                .build();
    }

    /**
     * Convert a list of BeerTracking entities to a list of BeerTrackingDTOs.
     *
     * @param beerTrackings the list of BeerTracking entities
     * @return the list of BeerTrackingDTOs
     */
    public List<BeerTrackingDTO> toBeerTrackingDTOList(List<BeerTracking> beerTrackings) {
        return beerTrackings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a BarCheck entity to a BarCheckDTO.
     *
     * @param barCheck the BarCheck entity
     * @return the BarCheckDTO
     */
    public BarCheckDTO toDTO(BarCheck barCheck) {
        if (barCheck == null) {
            return null;
        }

        return BarCheckDTO.builder()
                .id(barCheck.getId())
                .bar(toDTO(barCheck.getBar()))
                .menuHash(barCheck.getMenuHash())
                .contentType(barCheck.getContentType())
                .hasChanges(barCheck.isHasChanges())
                .processDuration(barCheck.getProcessDuration())
                .processingStatus(barCheck.getProcessingStatus())
                .errorMessage(barCheck.getErrorMessage())
                .createdAt(barCheck.getCreatedAt())
                .build();
    }

    /**
     * Convert a list of BarCheck entities to a list of BarCheckDTOs.
     *
     * @param barChecks the list of BarCheck entities
     * @return the list of BarCheckDTOs
     */
    public List<BarCheckDTO> toBarCheckDTOList(List<BarCheck> barChecks) {
        return barChecks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a Notification entity to a NotificationDTO.
     *
     * @param notification the Notification entity
     * @return the NotificationDTO
     */
    public NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .barId(notification.getBar() != null ? notification.getBar().getId() : null)
                .beerId(notification.getBeer() != null ? notification.getBeer().getId() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.isRead())
                .sent(notification.isSent())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .user(toDTO(notification.getUser()))
                .bar(toDTO(notification.getBar()))
                .beer(toDTO(notification.getBeer()))
                .beersAdded(toBeerDTOSet(notification.getBeersAdded()))
                .beersRemoved(toBeerDTOSet(notification.getBeersRemoved()))
                .build();
    }

    /**
     * Convert a list of Notification entities to a list of NotificationDTOs.
     *
     * @param notifications the list of Notification entities
     * @return the list of NotificationDTOs
     */
    public List<NotificationDTO> toNotificationDTOList(List<Notification> notifications) {
        return notifications.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert ScraperStats entity to ScraperStatsDTO.
     *
     * @param stats the ScraperStats entity
     * @return the ScraperStatsDTO
     */
    public ScraperStatsDTO toDTO(ScraperStats stats) {
        if (stats == null) {
            return null;
        }

        return ScraperStatsDTO.builder()
                .totalChecks(stats.getTotalChecks())
                .successfulChecks(stats.getSuccessfulChecks())
                .failedChecks(stats.getFailedChecks())
                .totalChangesDetected(stats.getTotalChangesDetected())
                .averageCheckTime(stats.getAverageCheckTime())
                .lastCheckTime(stats.getLastCheckTime())
                .build();
    }

    /**
     * Convert a BeerAlias entity to a BeerAliasDTO.
     *
     * @param beerAlias the BeerAlias entity
     * @return the BeerAliasDTO
     */
    public BeerAliasDTO toDTO(BeerAlias beerAlias) {
        if (beerAlias == null) {
            return null;
        }

        return BeerAliasDTO.builder()
                .id(beerAlias.getId())
                .name(beerAlias.getName())
                .brewery(beerAlias.getBrewery())
                .beerId(beerAlias.getBeer().getId())
                .createdAt(beerAlias.getCreatedAt())
                .updatedAt(beerAlias.getUpdatedAt())
                .build();
    }

    /**
     * Convert a list of BeerAlias entities to a list of BeerAliasDTOs.
     *
     * @param beerAliases the list of BeerAlias entities
     * @return the list of BeerAliasDTOs
     */
    public List<BeerAliasDTO> toBeerAliasDTOList(List<BeerAlias> beerAliases) {
        return beerAliases.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public BeerAvailabilityDTO toDTOAvailability(BarBeerCurrent beer) {
        return BeerAvailabilityDTO.builder()
                .beer(toDTO(beer.getBeer()))
                .availableAt(beer.getAddedAt())
                .build();
    }

    public BeerAvailabilityDTO toDTOAvailability(BarBeerHistory beer) {
        return BeerAvailabilityDTO.builder()
                .beer(toDTO(beer.getBeer()))
                .availableAt(beer.getAddedAt())
                .build();
    }
}
