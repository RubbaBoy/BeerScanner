package is.yarr.beerscanner.service;

import is.yarr.beerscanner.model.User;
import is.yarr.beerscanner.repository.UserRepository;
import is.yarr.beerscanner.security.UserPrincipal;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * Custom OAuth2 user service for handling Google authentication.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

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
     * @param oAuth2User the OAuth2 user
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

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user
            user.setGoogleId(googleId);
            user.setName(name);
            user.setProfilePicture(pictureUrl);
        } else {
            // Create new user
            boolean isAdmin = "adamyarris@gmail.com".equals(email);
            
            user = User.builder()
                    .email(email)
                    .googleId(googleId)
                    .name(name)
                    .profilePicture(pictureUrl)
                    .notificationEnabled(true)
                    .isAdmin(isAdmin)
                    .build();
        }

        user = userRepository.save(user);
        return UserPrincipal.create(user, attributes);
    }
}