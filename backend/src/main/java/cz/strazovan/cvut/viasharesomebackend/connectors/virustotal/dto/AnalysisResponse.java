package cz.strazovan.cvut.viasharesomebackend.connectors.virustotal.dto;

public class AnalysisResponse {
    // dto class contains just the interesting items
    private Data data;

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
            private String status; // We will use just queued and completed. We don't use enum to prevent runtime errors in parsing.
            private Stats stats;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public Stats getStats() {
                return stats;
            }

            public void setStats(Stats stats) {
                this.stats = stats;
            }

            public static class Stats {
                private Integer harmless;
                private Integer suspicious;
                private Integer timeout;
                private Integer failure;
                private Integer malicious;
                private Integer undetected;

                public Integer getHarmless() {
                    return harmless;
                }

                public void setHarmless(Integer harmless) {
                    this.harmless = harmless;
                }

                public Integer getSuspicious() {
                    return suspicious;
                }

                public void setSuspicious(Integer suspicious) {
                    this.suspicious = suspicious;
                }

                public Integer getTimeout() {
                    return timeout;
                }

                public void setTimeout(Integer timeout) {
                    this.timeout = timeout;
                }

                public Integer getFailure() {
                    return failure;
                }

                public void setFailure(Integer failure) {
                    this.failure = failure;
                }

                public Integer getMalicious() {
                    return malicious;
                }

                public void setMalicious(Integer malicious) {
                    this.malicious = malicious;
                }

                public Integer getUndetected() {
                    return undetected;
                }

                public void setUndetected(Integer undetected) {
                    this.undetected = undetected;
                }
            }
        }
    }
}
