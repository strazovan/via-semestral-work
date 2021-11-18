package cz.strazovan.cvut.viasharesomebackend.connectors.storage.model;

public record Folder(ObjectIdentifier folderIdentifier,
                     boolean isPublic,
                     ObjectIdentifier parentFolderIdentifier,
                     String name) {
}
