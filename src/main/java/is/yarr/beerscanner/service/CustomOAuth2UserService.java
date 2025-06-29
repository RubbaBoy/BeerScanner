package is.yarr.beerscanner.service;

import is.yarr.beerscanner.model.User;
import is.yarr.beerscanner.repository.UserRepository;
import is.yarr.beerscanner.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

/**
 * Custom OAuth2 user service for handling Google authentication.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;

    @Value("${app.data-dir}")
    private String dataDir;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load a user by OAuth2 user request.
     *
     * @param userRequest the OAuth2 user request
     * @return the OAuth2 user
     * @throws OAuth2AuthenticationException if an error occurs during authentication
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Process the OAuth2 user.
     *
     * @param userRequest the OAuth2 user request
     * @param oAuth2User  the OAuth2 user
     * @return the processed OAuth2 user
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        String googleId = (String) attributes.get("sub");
        String name = (String) attributes.get("name");
        String pictureUrl = (String) attributes.get("picture");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Create new user
                    boolean isAdmin = "adamyarris@gmail.com".equals(email);

                    return User.builder()
                            .email(email)
                            .googleId(googleId)
                            .profilePicture(pictureUrl)
                            .notificationEnabled(true)
                            .isAdmin(isAdmin)
                            .build();
                });

        user.setName(name);
        user.setProfilePicture(pictureUrl);

        user = userRepository.save(user);

        if (pictureUrl != null && !pictureUrl.isEmpty()) {
            try {
                downloadAndSetProfilePicture(user, pictureUrl);
            } catch (IOException e) {
                LOGGER.error("Failed to download profile picture for user {}", user.getGoogleId(), e);
            }
        }

        return UserPrincipal.create(user, attributes);
    }

    private void downloadAndSetProfilePicture(User user, String pictureUrl) throws IOException {
        var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        var profilePicturesDir = Paths.get(dataDir, "profile-pictures");
        Files.createDirectories(profilePicturesDir);

        var destinationFile = profilePicturesDir.resolve("%d.jpg".formatted(user.getId()));

        try (var in = new URI(pictureUrl).toURL().openStream()) {
            Files.copy(in, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.debug("Successfully downloaded profile picture for user {} to {}", user.getId(), destinationFile);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        user.setProfilePicture("%s/api/v1/users/profile-picture/%d".formatted(baseUrl, user.getId()));
        userRepository.save(user);
    }

}