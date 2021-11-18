package cz.strazovan.cvut.viasharesomebackend.connectors.storage;

import cz.strazovan.cvut.viasharesomebackend.connectors.storage.model.*;

import java.util.List;

public interface FileStorage {
    Folder createFolder(Folder folder) throws StorageException;

    FileInfo createFile(File file) throws StorageException;

    Content getContent(ObjectIdentifier objectIdentifier) throws StorageException;

    List<FileInfo> listFolder(ObjectIdentifier folderIdentifier) throws StorageException;

    void deleteObject(ObjectIdentifier objectIdentifier) throws StorageException;
}
