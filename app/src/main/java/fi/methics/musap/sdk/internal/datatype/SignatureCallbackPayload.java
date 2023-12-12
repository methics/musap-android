package fi.methics.musap.sdk.internal.datatype;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

import fi.methics.musap.sdk.internal.util.MLog;

public class SignatureCallbackPayload {

    @SerializedName("linkid")
    public final String linkid;
    @SerializedName("publickey")
    public final String publickey;
    @SerializedName("signature")
    public final String signature;

    public SignatureCallbackPayload(String linkid, MusapSignature signature) {
        this.linkid    = linkid;
        this.signature = signature != null ? signature.getB64Signature() : null;
        PublicKey publickey = signature.getPublicKey();
        if (publickey != null) {
            this.publickey = publickey.getPEM();
        } else {
            this.publickey = null;
        }
    }

    /**
     * Get this payload in Base64 format that can be used in a
     * @return
     */
    public String toBase64() {
        String payloadJson = new Gson().toJson(this);
        MLog.d("Payload=" + payloadJson);
        String base64 =  Base64.encodeToString(
                payloadJson.getBytes(StandardCharsets.UTF_8),
                Base64.NO_WRAP);
        MLog.d("Base64=" + base64);
        return base64;
    }
}
