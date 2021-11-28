package cz.strazovan.cvut.viasharesomebackend.controllers;

import cz.strazovan.cvut.viasharesomebackend.api.controllers.V1ApiDelegate;
import cz.strazovan.cvut.viasharesomebackend.api.model.TokenEntry;
import cz.strazovan.cvut.viasharesomebackend.service.UserService;
import cz.strazovan.cvut.viasharesomebackend.utils.security.UserContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class FilesController implements V1ApiDelegate {

    private final UserService userService;

    public FilesController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<Void> setToken(TokenEntry tokenEntry) {
        if (tokenEntry.getValue() == null) {
            throw new IllegalArgumentException("Missing token value");
        }
        this.userService.saveToken(UserContext.getOauthUserMail().orElseThrow(), tokenEntry.getValue());
        return ResponseEntity.ok().build();
    }
}
