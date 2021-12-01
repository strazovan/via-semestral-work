package cz.strazovan.cvut.viasharesomebackend.controllers;

import cz.strazovan.cvut.viasharesomebackend.api.controllers.V1ApiDelegate;
import cz.strazovan.cvut.viasharesomebackend.api.model.*;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.model.ObjectIdentifier;
import cz.strazovan.cvut.viasharesomebackend.model.FileDescriptor;
import cz.strazovan.cvut.viasharesomebackend.model.FileType;
import cz.strazovan.cvut.viasharesomebackend.service.UserService;
import cz.strazovan.cvut.viasharesomebackend.utils.security.UserContext;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FilesController implements V1ApiDelegate {

    private final UserService userService;

    public FilesController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<Void> setToken(TokenEntry tokenEntry) {
        if (tokenEntry.getValue() == null) {
            throw new IllegalArgumentException("Missing token value");
        }
        this.userService.saveToken(UserContext.getOauthUserMail().orElseThrow(), tokenEntry.getValue());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<FileEntry> getFiles(String file) {
        final Optional<FileDescriptor> fileDescriptorOpt = this.userService.getFileDescriptor(UserContext.getOauthUserMail().orElseThrow(), file);
        if (fileDescriptorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final FileDescriptor descriptor = fileDescriptorOpt.get();
        final FileEntry entry = this.fileDescriptorMapper(descriptor);
        if (descriptor.getFileType() == FileType.FOLDER) {
            final List<FileDescriptor> children = this.userService.getChildren(UserContext.getOauthUserMail().orElseThrow(), new ObjectIdentifier(descriptor.getContentId()));
            entry.setChildren(children.stream().map(this::fileDescriptorMapper).collect(Collectors.toList()));
        }
        return ResponseEntity.ok(entry);
    }

    @Override
    public ResponseEntity<FileEntry> uploadFile(NewFileEntry newFileEntry) {
        final FileDescriptor descriptor = this.userService.createFile(UserContext.getOauthUserMail().orElseThrow(), newFileEntry);
        return ResponseEntity.created(URI.create("/v1/files/" + descriptor.getContentId())).body(this.fileDescriptorMapper(descriptor));
    }

    @Override
    public ResponseEntity<Void> deleteFile(String file) {
        this.userService.deleteFile(UserContext.getOauthUserMail().orElseThrow(), new ObjectIdentifier(file));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<DownloadLink> getFileContent(String file) {
        final var fileDownloadLink = this.userService.getFileDownloadLink(UserContext.getOauthUserMail().orElseThrow(),
                new ObjectIdentifier(file));
        if(fileDownloadLink.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new DownloadLink().link(fileDownloadLink.get()));
    }

    @Override
    public ResponseEntity<ShareLink> createLink(String file, ShareLink shareLink) {
        final String link = this.userService.createShareLink(UserContext.getOauthUserMail().orElseThrow(),
                file, shareLink.getExpirirationDate().toInstant());
        return ResponseEntity.ok(new ShareLink()
                .expirirationDate(shareLink.getExpirirationDate())
                .url(link));
    }

    private FileEntry fileDescriptorMapper(FileDescriptor descriptor) {
        final var entry = new FileEntry();
        entry.setId(descriptor.getContentId());
        entry.setName(descriptor.getName());
        entry.setCreated(OffsetDateTime.ofInstant(descriptor.getCreated(), ZoneId.of("CET")));
        if (descriptor.getSize() != null)
            entry.setSize(new BigDecimal(descriptor.getSize()));
        entry.setType(FileEntry.TypeEnum.fromValue(descriptor.getFileType().toString()));
        return entry;
    }
}
