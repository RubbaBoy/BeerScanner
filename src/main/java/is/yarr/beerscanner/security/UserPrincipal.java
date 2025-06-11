package is.yarr.beerscanner.security;

import is.yarr.beerscanner.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Custom user principal for authentication.
 */
@AllArgsConstructor
@Getter
public class UserPrincipal implements OAuth2User, UserDetails {

    private Long id;
    private String email;
    private String name;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    /**
     * Create a UserPrincipal from a User.
     *
     * @param user the user
     * @return the UserPrincipal
     */
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities;
        
        // Check if the user has the admin email address
        if ("adamyarris@gmail.com".equals(user.getEmail())) {
            authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
            );
        } else {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getName(),
                authorities,
                null
        );
    }

    /**
     * Create a UserPrincipal from a User and attributes.
     *
     * @param user the user
     * @param attributes the attributes
     * @return the UserPrincipal
     */
    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    /**
     * Set the attributes.
     *
     * @param attributes the attributes
     */
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getPassword() {
        return null; // OAuth2 users don't have passwords
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}