package cz.strazovan.cvut.viasharesomebackend.service.impl;

import cz.strazovan.cvut.viasharesomebackend.connectors.storage.FileStorage;
import cz.strazovan.cvut.viasharesomebackend.dao.UserDocumentDao;
import cz.strazovan.cvut.viasharesomebackend.model.GoFileStorageInfo;
import cz.strazovan.cvut.viasharesomebackend.model.UserDocument;
import cz.strazovan.cvut.viasharesomebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserDocumentDao userDocumentDao;
    private final FileStorage goFileStorage;

    public UserServiceImpl(UserDocumentDao userDocumentDao,
                           @Autowired FileStorage goFileStorage) {
        this.userDocumentDao = userDocumentDao;
        this.goFileStorage = goFileStorage;
    }

    @Override
    public UserDocument createUserDocument(String username) {
        if (this.userDocumentDao.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists.");
        }
        final var document = new UserDocument();
        document.setUsername(username);
        return this.userDocumentDao.save(document);
    }

    public void saveToken(String username, String token) {
        final var userDocumentOptional = this.userDocumentDao.findByUsername(username);
        if (userDocumentOptional.isEmpty()) {
            throw new RuntimeException("User doesn't exits");
        }
        final var userDocument = userDocumentOptional.get();
        // todo delete previous data?
        final var storageInfo = new GoFileStorageInfo();
        storageInfo.setToken(token);
        userDocument.setGoFileStorageInfo(storageInfo);
        this.userDocumentDao.save(userDocument);
    }
}
