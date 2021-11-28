package cz.strazovan.cvut.viasharesomebackend.service;

import cz.strazovan.cvut.viasharesomebackend.model.UserDocument;

public interface UserService {
    UserDocument createUserDocument(String username);

    void saveToken(String username, String token);
}
