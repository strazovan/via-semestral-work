package cz.strazovan.cvut.viasharesomebackend.service.impl;

import cz.strazovan.cvut.viasharesomebackend.connectors.storage.FileStorage;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.StorageException;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.model.Folder;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.model.ObjectIdentifier;
import cz.strazovan.cvut.viasharesomebackend.dao.UserDocumentDao;
import cz.strazovan.cvut.viasharesomebackend.model.GoFileStorageInfo;
import cz.strazovan.cvut.viasharesomebackend.model.UserDocument;
import cz.strazovan.cvut.viasharesomebackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


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

    @Override
    public void saveToken(String username, String token) {
        final var userDocumentOptional = this.userDocumentDao.findByUsername(username);
        if (userDocumentOptional.isEmpty()) {
            throw new RuntimeException("User doesn't exits");
        }
        final ObjectIdentifier usersRootFolder = this.goFileStorage.getUsersRootFolder(() -> token);
        final var userDocument = userDocumentOptional.get();
        final var storageInfo = new GoFileStorageInfo();
        storageInfo.setToken(token);
        storageInfo.setRootFolderId(usersRootFolder.value());
        // create folder for our system and get the applicationFolderId
        final Folder applicationFolderRequest = new Folder(null, false, usersRootFolder, "sharesome-folder");
        final Folder applicationFolder = this.goFileStorage.createFolder(applicationFolderRequest, () -> token);
        storageInfo.setApplicationFolderId(applicationFolder.folderIdentifier().value());

        // create public folder for sharing
        final Folder publicFolderRequest = new Folder(null, true, usersRootFolder, "sharesome-public");
        final Folder publicFolder = this.goFileStorage.createFolder(publicFolderRequest, () -> token);
        storageInfo.setPublicFolderId(publicFolder.folderIdentifier().value());

        userDocument.setGoFileStorageInfo(storageInfo);
        this.userDocumentDao.save(userDocument);
    }
}
