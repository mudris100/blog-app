package dev.mudris.oauth2;

import dev.mudris.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class CustomUserPrincipal implements UserDetails, OAuth2User, OidcUser {

    private final User user;
    private Map<String, Object> attributes;
    private OidcIdToken idToken;
    private OidcUserInfo userInfo;

    public CustomUserPrincipal(User user) {
        this.user = user;
    }

    public static CustomUserPrincipal create(User user) {
        return new CustomUserPrincipal(user);
    }

    public static CustomUserPrincipal create(User user, Map<String, Object> attributes) {
        CustomUserPrincipal principal = new CustomUserPrincipal(user);
        principal.setAttributes(attributes);
        return principal;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getName() {
        return user.getUsername(); // or email, depending on design
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    public void setIdToken(OidcIdToken idToken) {
        this.idToken = idToken;
    }

    public void setUserInfo(OidcUserInfo userInfo) {
        this.userInfo = userInfo;
    }

}

