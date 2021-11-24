package cz.strazovan.cvut.viasharesomebackend.model;

import java.util.Map;

public class GoFileStorageInfo {
    private String token;
    private String rootFolderId;
    private String applicationFolderId;
    private String publicFolderId;
    // path -> FileDescriptor
    private Map<String, FileDescriptor> files;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRootFolderId() {
        return rootFolderId;
    }

    public void setRootFolderId(String rootFolderId) {
        this.rootFolderId = rootFolderId;
    }

    public String getApplicationFolderId() {
        return applicationFolderId;
    }

    public void setApplicationFolderId(String applicationFolderId) {
        this.applicationFolderId = applicationFolderId;
    }

    public String getPublicFolderId() {
        return publicFolderId;
    }

    public void setPublicFolderId(String publicFolderId) {
        this.publicFolderId = publicFolderId;
    }

    public Map<String, FileDescriptor> getFiles() {
        return files;
    }

    public void setFiles(Map<String, FileDescriptor> files) {
        this.files = files;
    }
}
