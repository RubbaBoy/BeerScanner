package is.yarr.beerscanner.repository;

import is.yarr.beerscanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by their Google ID.
     *
     * @param googleId the Google ID to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByGoogleId(String googleId);

    /**
     * Check if a user with the given email exists.
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user with the given Google ID exists.
     *
     * @param googleId the Google ID to check
     * @return true if a user with the Google ID exists, false otherwise
     */
    boolean existsByGoogleId(String googleId);
}