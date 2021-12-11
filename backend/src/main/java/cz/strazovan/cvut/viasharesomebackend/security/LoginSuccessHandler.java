package cz.strazovan.cvut.viasharesomebackend.security;

import cz.strazovan.cvut.viasharesomebackend.model.UserDocument;
import cz.strazovan.cvut.viasharesomebackend.service.UserService;
import cz.strazovan.cvut.viasharesomebackend.utils.security.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    public LoginSuccessHandler(@Value("${frontend.url}") String frontendUrl,
                               UserService userService) {
        super(frontendUrl);
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
        final String username = UserContext.getOauthUserMail().orElseThrow(); // the principal is already set here
        final Optional<UserDocument> usersDocument = this.userService.getUserDocument(username);
        if (usersDocument.isEmpty()) {
            this.userService.createUserDocument(username);
        }
    }

}
