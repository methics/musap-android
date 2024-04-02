package fi.methics.musap.sdk.internal.datatype.coupling.payload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.security.SecureRandom;

public abstract class MusapLinkPayload {

    protected static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @SerializedName("nonce")
    public String nonce;

    public abstract String toBase64();

    public abstract String getType();

    protected MusapLinkPayload() {
        this.nonce = this.generateNonce();
    }

    private String generateNonce() {
        return Integer.toString(new SecureRandom().nextInt());
    }
}
