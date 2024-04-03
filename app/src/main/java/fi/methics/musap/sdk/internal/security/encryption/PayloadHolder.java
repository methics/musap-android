package fi.methics.musap.sdk.internal.security.encryption;

/**
 * Holds payload and possible IV.
 */
public class PayloadHolder {

    private final String payload;
    private final String iv;

    public PayloadHolder(String payload, String iv) {
        this.payload = payload;
        this.iv = iv;
    }

    public String getIv() {
        return iv;
    }

    public String getPayload() {
        return payload;
    }
}