package cz.strazovan.cvut.viasharesomebackend.utils.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

public class UserContext {

    public static Optional<OAuth2User> getOauthUser() {
        final var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null)
            return Optional.empty();
        return Optional.of((((OAuth2User) principal)));
    }

    public static Optional<String> getOauthUserMail() {
        return getOauthUser().map(oAuth2User -> oAuth2User.getAttribute("email"));
    }


    public static boolean hasRight(String right) {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(authority -> right.equals(authority.getAuthority()));
    }


}
