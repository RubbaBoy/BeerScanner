package is.yarr.beerscanner.security;

import is.yarr.beerscanner.model.Permission;
import is.yarr.beerscanner.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Custom user principal for authentication.
 */
public class UserPrincipal implements OAuth2User, UserDetails {

    private Long id;
    private String email;
    private String name;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(Long id, String email, String name, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    /**
     * Create a UserPrincipal from a User.
     *
     * @param user the user
     * @return the UserPrincipal
     */
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add basic user role
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // Add permissions as authorities
        for (Permission permission : user.getPermissions()) {
            authorities.add(new SimpleGrantedAuthority("PERMISSION_" + permission.name()));

            // For backward compatibility, add ROLE_ADMIN if user has ADMIN permission
            if (permission == Permission.ADMIN) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }

        // For backward compatibility, check if the user has the admin flag set
        if (user.isAdmin() && !authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
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

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
