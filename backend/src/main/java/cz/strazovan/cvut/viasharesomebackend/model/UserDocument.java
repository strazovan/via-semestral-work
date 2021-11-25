package cz.strazovan.cvut.viasharesomebackend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "users_data")
public class UserDocument {
    @Id
    private String id;

    private String username;

    private GoFileStorageInfo goFileStorageInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GoFileStorageInfo getGoFileStorageInfo() {
        return goFileStorageInfo;
    }

    public void setGoFileStorageInfo(GoFileStorageInfo goFileStorageInfo) {
        this.goFileStorageInfo = goFileStorageInfo;
    }
}
