package is.yarr.beerscanner.controller;

import is.yarr.beerscanner.dto.NotificationDTO;
import is.yarr.beerscanner.model.Notification;
import is.yarr.beerscanner.security.UserPrincipal;
import is.yarr.beerscanner.service.DTOMapperService;
import is.yarr.beerscanner.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for notification-related operations.
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final DTOMapperService dtoMapperService;

    public NotificationController(NotificationService notificationService, DTOMapperService dtoMapperService) {
        this.notificationService = notificationService;
        this.dtoMapperService = dtoMapperService;
    }

    /**
     * Get notifications for the current user.
     *
     * @param userPrincipal the authenticated user
     * @param pageable pagination information
     * @return a page of notification DTOs for the user
     */
    @GetMapping
    public ResponseEntity<Page<NotificationDTO>> getNotifications(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {

        Page<Notification> notifications = notificationService.getNotificationsForUser(userPrincipal.getId(), pageable);
        List<NotificationDTO> notificationDTOs = notifications.getContent().stream()
                .map(dtoMapperService::toDTO)
                .collect(Collectors.toList());
        Page<NotificationDTO> notificationDTOPage = new PageImpl<>(notificationDTOs, pageable, notifications.getTotalElements());
        return ResponseEntity.ok(notificationDTOPage);
    }

    /**
     * Get unread notifications for the current user.
     *
     * @param userPrincipal the authenticated user
     * @return a list of unread notification DTOs for the user
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userPrincipal.getId());
        List<NotificationDTO> notificationDTOs = dtoMapperService.toNotificationDTOList(notifications);
        return ResponseEntity.ok(notificationDTOs);
    }

    /**
     * Count unread notifications for the current user.
     *
     * @param userPrincipal the authenticated user
     * @return the number of unread notifications
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> countUnreadNotifications(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        long count = notificationService.countUnreadNotifications(userPrincipal.getId());
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Mark a notification as read.
     *
     * @param userPrincipal the authenticated user
     * @param id the notification ID
     * @return the updated notification DTO
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {

        Notification notification = notificationService.markNotificationAsRead(id);

        // Check if the notification belongs to the current user
        if (!notification.getUser().getId().equals(userPrincipal.getId())) {
            return ResponseEntity.status(403).build();
        }

        NotificationDTO notificationDTO = dtoMapperService.toDTO(notification);
        return ResponseEntity.ok(notificationDTO);
    }

    /**
     * Send a system notification to all users (admin only).
     *
     * @param request the request containing the notification details
     * @return success message
     */
    @PostMapping("/admin/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> sendSystemNotification(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String message = request.get("message");

        if (title == null || message == null) {
            return ResponseEntity.badRequest().build();
        }

        notificationService.sendSystemNotificationToAllUsers(title, message);
        return ResponseEntity.ok(Map.of("message", "System notification sent successfully"));
    }

    /**
     * Process unsent notifications (admin only).
     *
     * @return success message
     */
    @PostMapping("/admin/process-unsent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> processUnsentNotifications() {
        notificationService.processUnsentNotifications();
        return ResponseEntity.ok(Map.of("message", "Unsent notifications processed successfully"));
    }
}
