package is.yarr.beerscanner.service;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.BeerTracking;
import is.yarr.beerscanner.model.Notification;
import is.yarr.beerscanner.model.User;
import is.yarr.beerscanner.repository.BeerTrackingRepository;
import is.yarr.beerscanner.repository.NotificationRepository;
import is.yarr.beerscanner.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for notification-related operations.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final BeerTrackingRepository beerTrackingRepository;
    private final JavaMailSender mailSender;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository, BeerTrackingRepository beerTrackingRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.beerTrackingRepository = beerTrackingRepository;
        this.mailSender = mailSender;
    }

    /**
     * Get notifications for a user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return a page of notifications for the user
     */
    public Page<Notification> getNotificationsForUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    /**
     * Get unread notifications for a user.
     *
     * @param userId the user ID
     * @return a list of unread notifications for the user
     */
    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return notificationRepository.findByUserAndIsReadFalse(user);
    }

    /**
     * Mark a notification as read.
     *
     * @param notificationId the notification ID
     * @return the updated notification
     */
    @Transactional
    public Notification markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    /**
     * Send a notification.
     *
     * @param notification the notification to send
     * @return the sent notification
     */
    @Transactional
    public Notification sendNotification(Notification notification) {
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());

        // Send email if user has notifications enabled
//        if (notification.getUser().isNotificationEnabled()) {
//            sendEmail(notification);
//        }

        return notificationRepository.save(notification);
    }

    /**
     * Send beer available notifications.
     *
     * @param bar the bar
     * @param beer the beer
     */
    @Transactional
    public void sendBeerAvailableNotifications(Bar bar, Beer beer) {
        // Find users who are tracking this beer at any bar
        List<BeerTracking> globalTrackings = beerTrackingRepository.findByBeer(beer).stream()
                .filter(tracking -> tracking.getBar() == null)
                .toList();

        // Find users who are tracking this beer at this specific bar
        List<BeerTracking> barSpecificTrackings = beerTrackingRepository.findByBeerAndBar(beer, bar);

        // Combine the lists and remove duplicates
        Set<User> usersToNotify = new HashSet<>();
        globalTrackings.forEach(tracking -> usersToNotify.add(tracking.getUser()));
        barSpecificTrackings.forEach(tracking -> usersToNotify.add(tracking.getUser()));

        for (User user : usersToNotify) {
            Notification notification = Notification.builder()
                    .user(user)
                    .bar(bar)
                    .beer(beer)
                    .title("Beer Available")
                    .message(beer.getName() + " is now available at " + bar.getName())
                    .type(Notification.NotificationType.BEER_AVAILABLE)
                    .isRead(false)
                    .isSent(false)
                    .build();

            sendNotification(notification);
        }
    }

    /**
     * Send bar menu changed notifications.
     *
     * @param bar the bar
     */
    @Transactional
    public void sendBarMenuChangedNotifications(Bar bar) {
        // Find users who are tracking this bar
        Set<User> users = bar.getTrackedBy();

        for (User user : users) {
            Notification notification = Notification.builder()
                    .user(user)
                    .bar(bar)
                    .title("Menu Changed")
                    .message("The menu at " + bar.getName() + " has changed")
                    .type(Notification.NotificationType.MENU_CHANGED)
                    .isRead(false)
                    .isSent(false)
                    .build();

            sendNotification(notification);
        }
    }

    /**
     * Send a system notification to all users.
     *
     * @param title the notification title
     * @param message the notification message
     */
    @Transactional
    public void sendSystemNotificationToAllUsers(String title, String message) {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            Notification notification = Notification.builder()
                    .user(user)
                    .title(title)
                    .message(message)
                    .type(Notification.NotificationType.SYSTEM)
                    .isRead(false)
                    .isSent(false)
                    .build();

            sendNotification(notification);
        }
    }

    /**
     * Send email for a notification.
     *
     * @param notification the notification
     */
    @Async
    protected void sendEmail(Notification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getUser().getEmail());
        message.setSubject(notification.getTitle());
        message.setText(notification.getMessage());

        mailSender.send(message);
    }

    /**
     * Process unsent notifications.
     */
    @Transactional
    public void processUnsentNotifications() {
        List<Notification> unsentNotifications = notificationRepository.findByIsSentFalse();

        for (Notification notification : unsentNotifications) {
            sendNotification(notification);
        }
    }

    /**
     * Count unread notifications for a user.
     *
     * @param userId the user ID
     * @return the number of unread notifications
     */
    public long countUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return notificationRepository.countByUserAndIsReadFalse(user);
    }
}
