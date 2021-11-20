package cz.strazovan.cvut.viasharesomebackend.connectors.storage.gofile.dto;

public class GetServerResponse {
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
        private String server;

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }
    }
}
