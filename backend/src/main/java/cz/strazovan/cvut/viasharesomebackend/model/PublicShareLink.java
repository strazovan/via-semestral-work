package cz.strazovan.cvut.viasharesomebackend.model;

import java.time.Instant;

public class PublicShareLink {
    private String url;
    private Instant expirationDateTime;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Instant getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(Instant expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }
}
