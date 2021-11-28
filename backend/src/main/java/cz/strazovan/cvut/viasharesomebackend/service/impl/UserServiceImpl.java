package cz.strazovan.cvut.viasharesomebackend.service.impl;

import cz.strazovan.cvut.viasharesomebackend.api.model.NewFileEntry;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.FileStorage;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.model.File;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.model.FileInfo;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.model.Folder;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.model.ObjectIdentifier;
import cz.strazovan.cvut.viasharesomebackend.dao.UserDocumentDao;
import cz.strazovan.cvut.viasharesomebackend.model.FileDescriptor;
import cz.strazovan.cvut.viasharesomebackend.model.FileType;
import cz.strazovan.cvut.viasharesomebackend.model.GoFileStorageInfo;
import cz.strazovan.cvut.viasharesomebackend.model.UserDocument;
import cz.strazovan.cvut.viasharesomebackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

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
        final var userDocument = this.getUserDocumentThrowing(username);
        final ObjectIdentifier usersRootFolder = this.goFileStorage.getUsersRootFolder(() -> token);
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
        final HashMap<String, FileDescriptor> files = new HashMap<>();
        final FileDescriptor applicationFolderFileDescriptor = new FileDescriptor();
        applicationFolderFileDescriptor.setContentId(applicationFolder.folderIdentifier().value());
        applicationFolderFileDescriptor.setName("root");
        applicationFolderFileDescriptor.setCreated(Instant.now());
        applicationFolderFileDescriptor.setFileType(FileType.FOLDER);
        files.put(applicationFolderFileDescriptor.getContentId(), applicationFolderFileDescriptor);
        storageInfo.setFiles(files);
        userDocument.setGoFileStorageInfo(storageInfo);
        this.userDocumentDao.save(userDocument);
    }

    @Override
    public Optional<FileDescriptor> getFileDescriptor(String username, String fileIdentifier) {
        final UserDocument userDocument = getUserDocumentThrowing(username);
        checkUser(userDocument);
        final GoFileStorageInfo storageInfo = userDocument.getGoFileStorageInfo();
        Map<String, FileDescriptor> files = storageInfo.getFiles();
        FileDescriptor descriptor = files.get(fileIdentifier);
        return Optional.ofNullable(descriptor);
    }

    @Override
    public FileDescriptor createFile(String username, NewFileEntry newFileEntry) {
        final UserDocument userDocument = getUserDocumentThrowing(username);
        checkUser(userDocument);
        final GoFileStorageInfo goFileStorageInfo = userDocument.getGoFileStorageInfo();
        final FileDescriptor parent = goFileStorageInfo.getFiles().get(newFileEntry.getParentId());
        if (parent == null || parent.getFileType() != FileType.FOLDER) {
            throw new IllegalArgumentException("Invalid parent id");
        }
        final var descriptor = new FileDescriptor();
        descriptor.setFileType(FileType.valueOf(newFileEntry.getType().getValue()));
        descriptor.setName(newFileEntry.getName());
        descriptor.setParentContentId(parent.getContentId());
        if (newFileEntry.getType() == NewFileEntry.TypeEnum.FOLDER) {
            // we are creating new folder
            final Folder newFolder = this.goFileStorage.createFolder(new Folder(null, false, new ObjectIdentifier(parent.getContentId()), newFileEntry.getName()), goFileStorageInfo::getToken);
            descriptor.setContentId(newFolder.folderIdentifier().value());
        } else {
            // we are uploading file
            final var fileInfo = new FileInfo(new ObjectIdentifier(parent.getContentId()), null, newFileEntry.getName());
            final byte[] bytes = Base64.getDecoder().decode(newFileEntry.getContent());
            final FileInfo uploadedFileInfo = this.goFileStorage.createFile(new File(fileInfo, bytes), goFileStorageInfo::getToken);
            descriptor.setContentId(uploadedFileInfo.fileIdentifier().value());
            descriptor.setSize((long) bytes.length);
        }
        descriptor.setCreated(Instant.now());
        goFileStorageInfo.getFiles().put(descriptor.getContentId(), descriptor);
        this.userDocumentDao.save(userDocument);
        return descriptor;
    }

    @Override
    public List<FileDescriptor> getChildren(String username, ObjectIdentifier objectIdentifier) {
        final UserDocument userDocument = getUserDocumentThrowing(username);
        checkUser(userDocument);
        final GoFileStorageInfo goFileStorageInfo = userDocument.getGoFileStorageInfo();
        final var result = new ArrayList<FileDescriptor>();
        goFileStorageInfo.getFiles().values().stream()
                .filter(descriptor -> descriptor.getParentContentId() != null)
                .filter(descriptor -> descriptor.getParentContentId().equals(objectIdentifier.value()))
                .forEach(result::add);
        return result;

    }


    private void checkUser(UserDocument userDocument) {
        if (userDocument.getGoFileStorageInfo() == null) {
            throw new RuntimeException("The user has not set his token yet.");
        }
    }

    private UserDocument getUserDocumentThrowing(String username) {
        final var userDocumentOptional = this.userDocumentDao.findByUsername(username);
        if (userDocumentOptional.isEmpty()) {
            throw new RuntimeException("User doesn't exits");
        }
        return userDocumentOptional.get();
    }
}
