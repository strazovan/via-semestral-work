package cz.strazovan.cvut.viasharesomebackend.connectors.storage.gofile;

import cz.strazovan.cvut.viasharesomebackend.connectors.storage.FileStorage;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.FileStorageAuthentication;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.StorageException;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.gofile.dto.*;
import cz.strazovan.cvut.viasharesomebackend.connectors.storage.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("goFileStorage")
public class GoFileFileStorage implements FileStorage, SmartLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(GoFileFileStorage.class);

    @Value("${connectors.gofile.baseURL}")
    private String baseUrl;
    @Value("${connectors.gofile.uploadFileTemplate}")
    private String uploadFileTemplate;
    private volatile boolean running;
    private String server;

    @Override
    public ObjectIdentifier getUsersRootFolder(FileStorageAuthentication authentication) throws StorageException {
        checkRunning();
        checkAuthentication(authentication);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            String urlTemplate = UriComponentsBuilder.fromHttpUrl(baseUrl + "/getAccountDetails")
                    .queryParam("token", "{token}")
                    .queryParam("allDetails", true)
                    .encode()
                    .toUriString();

            final Map<String, String> params = new HashMap<>();
            params.put("token", authentication.getAuthenticationIdentifier());

            final RestTemplate template = new RestTemplate();
            ResponseEntity<GetAccountDetailsResponse> response = template.exchange(
                    urlTemplate,
                    HttpMethod.GET,
                    entity,
                    GetAccountDetailsResponse.class,
                    params
            );
            if (response.getStatusCode().isError() || !response.getBody().getStatus().equals("ok")) {
                throw new StorageException("Failed to fetch account details");
            }
            return new ObjectIdentifier(response.getBody().getData().getRootFolder());
        } catch (RestClientException e) {
            throw new StorageException(e);
        }

    }

    @Override
    public Folder createFolder(Folder folder, FileStorageAuthentication authentication) throws StorageException {
        checkRunning();
        checkAuthentication(authentication);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> values = new LinkedMultiValueMap<>();
            values.add("folderName", folder.name());
            values.add("parentFolderId", folder.parentFolderIdentifier().value());
            values.add("token", authentication.getAuthenticationIdentifier());
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(values, headers);
            final RestTemplate restTemplate = new RestTemplate();
            final ResponseEntity<CreateFolderResponse> response = restTemplate.exchange(baseUrl + "/createFolder", HttpMethod.PUT, request, CreateFolderResponse.class);
            if (response.getStatusCode().isError() || !response.getBody().getStatus().equals("ok")) {
                throw new StorageException("Failed to create the folder");
            }
            final ObjectIdentifier createdFolderId = new ObjectIdentifier(response.getBody().getData().getId());
            if (folder.isPublic()) {
                setFolderOption(authentication, "public", "true", createdFolderId);
            }
            return new Folder(createdFolderId, false, folder.parentFolderIdentifier(), folder.name());
        } catch (RestClientException e) {
            throw new StorageException(e);
        }
    }

    private void setFolderOption(FileStorageAuthentication authentication, String option, String value, ObjectIdentifier folderId) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final MultiValueMap<String, String> values = new LinkedMultiValueMap<>();
        values.add("folderId", folderId.value());
        values.add("token", authentication.getAuthenticationIdentifier());
        values.add("option", option);
        values.add("value", value);
        final HttpEntity<MultiValueMap<String, String>> setOptionsRequest = new HttpEntity<>(values, headers);
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<SetFolderOptionResponse> response = restTemplate.exchange(baseUrl + "/setFolderOption", HttpMethod.PUT, setOptionsRequest, SetFolderOptionResponse.class);
        if (response.getStatusCode().isError() || !response.getBody().getStatus().equals("ok")) {
            throw new StorageException("Failed to set the folder options");
        }
    }

    @Override
    public FileInfo createFile(File file, FileStorageAuthentication authentication) throws StorageException {
        checkRunning();
        checkAuthentication(authentication);
        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
            values.add("folderId", file.fileInfo().folder().value());
            values.add("token", authentication.getAuthenticationIdentifier());
            values.add("file", new ByteArrayResource(file.content()));
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(values, headers);
            final RestTemplate restTemplate = new RestTemplate();
            final ResponseEntity<CreateFileResponse> response = restTemplate.exchange(uploadFileTemplate.replace("{{server}}", this.server), HttpMethod.POST, request, CreateFileResponse.class);
            final CreateFileResponse body = response.getBody();
            if (response.getStatusCode().isError() || !body.getStatus().equals("ok")) {
                throw new StorageException("Failed to upload the file");
            }
            return new FileInfo(file.fileInfo().folder(), new ObjectIdentifier(body.getData().getFileId()), body.getData().getFileName());
        } catch (RestClientException e) {
            throw new StorageException(e);
        }
    }

    @Override
    public Content getContent(ObjectIdentifier objectIdentifier, FileStorageAuthentication authentication) throws StorageException {
        checkRunning();
        throw new StorageException("Storage doesn't support direct downloads"); // todo
    }

    @Override
    public List<FileInfo> listFolder(ObjectIdentifier folderIdentifier, FileStorageAuthentication authentication) throws StorageException {
        checkRunning();
        // todo this started to return notPremium error for some reason, i will probably skip their interface and use own implementation
        throw new StorageException("Storage doesn't support listing.");
    }

    @Override
    public void deleteObject(ObjectIdentifier objectIdentifier, FileStorageAuthentication authentication) throws StorageException {
        checkRunning();
        checkAuthentication(authentication);
        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> values = new LinkedMultiValueMap<>();
            values.add("contentsId", objectIdentifier.value());
            values.add("token", authentication.getAuthenticationIdentifier());
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(values, headers);
            final RestTemplate restTemplate = new RestTemplate();
            final ResponseEntity<DeleteContentResponse> response = restTemplate.exchange(baseUrl + "/deleteContent", HttpMethod.DELETE, request, DeleteContentResponse.class);
            if (response.getStatusCode().isError() || !response.getBody().getStatus().equals("ok")) {
                throw new StorageException("Failed to delete the content");

            }
            if (!response.getBody().getData().get(objectIdentifier.value()).equals("ok")) {
                throw new StorageException("Failed to delete the content");
            }
        } catch (RestClientException e) {
            throw new StorageException(e);
        }
    }

    private void checkRunning() throws StorageException {
        if (!this.running) {
            throw new StorageException("Storage connector is not running.");
        }
    }

    private void checkAuthentication(FileStorageAuthentication authentication) throws StorageException {
        if (authentication == null) {
            throw new StorageException("Storage requires an authentication");
        }
    }

    @Override
    public void start() {
        logger.info("Component is starting...");
        final RestTemplate template = new RestTemplate();
        final ResponseEntity<GetServerResponse> serverResponse = template.getForEntity(baseUrl + "/getServer", GetServerResponse.class);
        if (serverResponse.getStatusCode().isError() || !serverResponse.getBody().getStatus().equals("ok")) {
            throw new RuntimeException("Failed to get the best server for data upload.");
        }
        this.server = serverResponse.getBody().getData().getServer();
        logger.info("The server that will be used is " + this.server + ".");
        logger.info("Component has started.");
        this.running = true;
    }

    @Override
    public void stop() {
        logger.info("Component is stopping...");
        this.server = null;
        logger.info("Component has stopped.");
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }
}
