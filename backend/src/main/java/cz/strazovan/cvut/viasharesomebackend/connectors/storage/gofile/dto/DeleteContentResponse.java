package cz.strazovan.cvut.viasharesomebackend.connectors.storage.gofile.dto;

import java.util.Map;

public class DeleteContentResponse {
    private String status;
    private Map<String, String> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
