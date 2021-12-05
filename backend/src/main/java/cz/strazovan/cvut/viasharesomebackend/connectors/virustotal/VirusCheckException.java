package cz.strazovan.cvut.viasharesomebackend.connectors.virustotal;

public class VirusCheckException extends RuntimeException {
    public VirusCheckException(String message) {
        super(message);
    }

    public VirusCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public VirusCheckException(Throwable cause) {
        super(cause);
    }
}
