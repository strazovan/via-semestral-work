package cz.strazovan.cvut.viasharesomebackend.utils;

import org.springframework.core.io.ByteArrayResource;

public class NamedByteArrayResource extends ByteArrayResource {
    private final String filename;

    public NamedByteArrayResource(String name, byte[] byteArray) {
        super(byteArray);
        this.filename = name;
    }

    public NamedByteArrayResource(String name, byte[] byteArray, String description) {
        super(byteArray, description);
        this.filename = name;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }
}
