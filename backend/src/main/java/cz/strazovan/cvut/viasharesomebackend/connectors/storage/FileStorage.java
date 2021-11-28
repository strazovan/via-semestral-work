package cz.strazovan.cvut.viasharesomebackend.connectors.storage;

import cz.strazovan.cvut.viasharesomebackend.connectors.storage.model.*;

import java.util.List;

public interface FileStorage {

    ObjectIdentifier getUsersRootFolder(FileStorageAuthentication authentication) throws StorageException;

    Folder createFolder(Folder folder, FileStorageAuthentication authentication) throws StorageException;

    FileInfo createFile(File file, FileStorageAuthentication authentication) throws StorageException;

    Content getContent(ObjectIdentifier objectIdentifier, FileStorageAuthentication authentication) throws StorageException;

    List<FileInfo> listFolder(ObjectIdentifier folderIdentifier, FileStorageAuthentication authentication) throws StorageException;

    void deleteObject(ObjectIdentifier objectIdentifier, FileStorageAuthentication authentication) throws StorageException;
}
