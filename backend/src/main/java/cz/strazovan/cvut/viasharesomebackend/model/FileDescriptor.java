package cz.strazovan.cvut.viasharesomebackend.model;

import java.util.Map;

public class FileDescriptor {
    private String contentId;
    private String name;
    private String path;
    private Long size;
    private VirusCheckResult virusCheckResult;
    private FileType fileType;
    // path -> FileDescriptor
    private Map<String, FileDescriptor> children;

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public VirusCheckResult getVirusCheckResult() {
        return virusCheckResult;
    }

    public void setVirusCheckResult(VirusCheckResult virusCheckResult) {
        this.virusCheckResult = virusCheckResult;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Map<String, FileDescriptor> getChildren() {
        return children;
    }

    public void setChildren(Map<String, FileDescriptor> children) {
        this.children = children;
    }
}
