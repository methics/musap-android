package fi.methics.musap.sdk.internal.encryption;

public class EncryptedPayload {

    private final String payload;
    private final String iv;

    public EncryptedPayload(String payload, String iv) {
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