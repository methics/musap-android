package fi.methics.musap.sdk.internal.datatype.coupling.payload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.security.SecureRandom;
import java.time.Instant;

public abstract class MusapLinkPayload {

    protected static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @SerializedName("nonce")
    public String nonce;

    @SerializedName("timestamp")
    public String timestamp;

    public abstract String toBase64();

    public abstract String getType();

    protected MusapLinkPayload() {
        this.nonce = this.generateNonce();
        this.timestamp = this.generateTimestamp();
    }

    private String generateNonce() {
        return Integer.toString(new SecureRandom().nextInt());
    }
    private String generateTimestamp() {
        return Instant.now().toString();
    }
}
