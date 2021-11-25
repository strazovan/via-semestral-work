package cz.strazovan.cvut.viasharesomebackend.dao;

import cz.strazovan.cvut.viasharesomebackend.model.UserDocument;

import java.util.Optional;

public interface UserDocumentDao {
    UserDocument save(UserDocument userDocument);
    Optional<UserDocument> findByUsername(String username);
    Optional<UserDocument> findById(String id);
}
