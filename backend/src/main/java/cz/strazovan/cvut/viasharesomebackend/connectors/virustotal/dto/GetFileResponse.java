package cz.strazovan.cvut.viasharesomebackend.connectors.virustotal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetFileResponse {
    private Data data;
    private Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public static class Error {
        private String code;
        private String message;

        public boolean isResourceNotFound() {
            return this.code != null && this.code.equals("NotFoundError");
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private Attributes attributes;

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public static class Attributes {
            @JsonProperty("total_votes")
            private TotalVotes totalVotes;

            public TotalVotes getTotalVotes() {
                return totalVotes;
            }

            public void setTotalVotes(TotalVotes totalVotes) {
                this.totalVotes = totalVotes;
            }

            public static class TotalVotes {
                private Integer harmless;
                private Integer malicious;

                public Integer getHarmless() {
                    return harmless;
                }

                public void setHarmless(Integer harmless) {
                    this.harmless = harmless;
                }

                public Integer getMalicious() {
                    return malicious;
                }

                public void setMalicious(Integer malicious) {
                    this.malicious = malicious;
                }
            }
        }
    }
}
