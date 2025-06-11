package is.yarr.beerscanner.repository;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.Notification;
import is.yarr.beerscanner.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find notifications for a specific user, ordered by creation time (descending).
     *
     * @param user the user to find notifications for
     * @param pageable pagination information
     * @return a Page of notifications for the user
     */
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    /**
     * Find unread notifications for a specific user.
     *
     * @param user the user to find notifications for
     * @return a list of unread notifications for the user
     */
    List<Notification> findByUserAndIsReadFalse(User user);

    /**
     * Find unsent notifications.
     *
     * @return a list of unsent notifications
     */
    List<Notification> findByIsSentFalse();

    /**
     * Find notifications for a specific bar.
     *
     * @param bar the bar to find notifications for
     * @param pageable pagination information
     * @return a Page of notifications for the bar
     */
    Page<Notification> findByBar(Bar bar, Pageable pageable);

    /**
     * Find notifications for a specific beer.
     *
     * @param beer the beer to find notifications for
     * @param pageable pagination information
     * @return a Page of notifications for the beer
     */
    Page<Notification> findByBeer(Beer beer, Pageable pageable);

    /**
     * Find notifications of a specific type for a user.
     *
     * @param user the user to find notifications for
     * @param type the type of notifications to find
     * @param pageable pagination information
     * @return a Page of notifications of the specified type for the user
     */
    Page<Notification> findByUserAndType(User user, Notification.NotificationType type, Pageable pageable);

    /**
     * Find notifications created after a specific time.
     *
     * @param time the time to compare against
     * @param pageable pagination information
     * @return a Page of notifications created after the specified time
     */
    Page<Notification> findByCreatedAtAfter(LocalDateTime time, Pageable pageable);

    /**
     * Count unread notifications for a specific user.
     *
     * @param user the user to count notifications for
     * @return the number of unread notifications for the user
     */
    long countByUserAndIsReadFalse(User user);
}