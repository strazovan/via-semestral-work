package cz.strazovan.cvut.viasharesomebackend.service;

import cz.strazovan.cvut.viasharesomebackend.api.model.NewFileEntry;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.model.ObjectIdentifier;
import cz.strazovan.cvut.viasharesomebackend.model.FileDescriptor;
import cz.strazovan.cvut.viasharesomebackend.model.UserDocument;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDocument createUserDocument(String username);

    void saveToken(String username, String token);

    Optional<FileDescriptor> getFileDescriptor(String username, String path);

    FileDescriptor createFile(String username, NewFileEntry newFileEntry);

    List<FileDescriptor> getChildren(String username, ObjectIdentifier objectIdentifier);

    void deleteFile(String username, ObjectIdentifier objectIdentifier);

    Optional<String> getFileDownloadLink(String username, ObjectIdentifier objectIdentifier);
}
