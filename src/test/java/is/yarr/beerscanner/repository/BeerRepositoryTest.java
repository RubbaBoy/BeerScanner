package is.yarr.beerscanner.repository;

import is.yarr.beerscanner.model.Beer;
import is.yarr.beerscanner.model.BeerTracking;
import is.yarr.beerscanner.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class BeerRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private BeerTrackingRepository beerTrackingRepository;

    @Test
    public void testFindByTrackedByUser() {
        // Create a user
        User user = User.builder()
                .email("test@example.com")
                .name("Test User")
                .googleId("123456789")
                .notificationEnabled(true)
                .build();
        entityManager.persist(user);

        // Create beers
        Beer beer1 = Beer.builder()
                .name("Test Beer 1")
                .brewery("Test Brewery")
                .type("IPA")
                .abv(5.0)
                .description("A test beer")
                .build();
        entityManager.persist(beer1);

        Beer beer2 = Beer.builder()
                .name("Test Beer 2")
                .brewery("Another Brewery")
                .type("Stout")
                .abv(7.0)
                .description("Another test beer")
                .build();
        entityManager.persist(beer2);

        // Create beer tracking
        BeerTracking tracking = BeerTracking.builder()
                .user(user)
                .beer(beer1)
                .bar(null) // Track globally
                .build();
        entityManager.persist(tracking);

        entityManager.flush();

        // Test findByTrackedByUser
        List<Beer> trackedBeers = beerRepository.findByTrackedByUser(user);

        assertEquals(1, trackedBeers.size());
        assertEquals(beer1.getId(), trackedBeers.get(0).getId());

        System.out.println("[DEBUG_LOG] Found " + trackedBeers.size() + " beers tracked by user");
        trackedBeers.forEach(beer -> System.out.println("[DEBUG_LOG] Beer: " + beer.getName()));
    }
}
