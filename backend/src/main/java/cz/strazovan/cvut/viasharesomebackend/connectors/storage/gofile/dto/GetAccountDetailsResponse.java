package cz.strazovan.cvut.viasharesomebackend.connectors.storage.gofile.dto;

public class GetAccountDetailsResponse {
    private String status;
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String rootFolder;

        public String getRootFolder() {
            return rootFolder;
        }

        public void setRootFolder(String rootFolder) {
            this.rootFolder = rootFolder;
        }
    }
}
