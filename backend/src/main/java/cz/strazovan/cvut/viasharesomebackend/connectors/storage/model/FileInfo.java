package cz.strazovan.cvut.viasharesomebackend.connectors.storage.model;

public record FileInfo(ObjectIdentifier folder,
                       ObjectIdentifier fileIdentifier,
                       String name) {
}
