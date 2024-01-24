package fi.methics.musap.sdk.internal.datatype.coupling;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.datatype.PublicKey;
import fi.methics.musap.sdk.internal.util.MLog;

public class SignatureCallbackPayload {

    @SerializedName("signature")
    public String signature;

    @SerializedName("publickey")
    public String publickey;

    @SerializedName("keyuri")
    public String keyuri;

    @SerializedName("keyid")
    public String keyid;

    public SignatureCallbackPayload(MusapKey key) {
        if (key != null) {
            this.keyid = key.getKeyId();
            this.keyuri = key.getKeyUri().toString();
            PublicKey publickey = key.getPublicKey();
            if (publickey != null) {
                this.publickey = publickey.getPEM();
            }
        }
    }

    public SignatureCallbackPayload(MusapSignature signature) {
        if (signature != null) {
            this.signature = signature.getB64Signature();

            PublicKey publickey = signature.getPublicKey();
            if (publickey != null) {
                MLog.d("Setting PublicKey");
                this.publickey = publickey.getPEM();
            } else {
                MLog.d("No PublicKey");
            }
            MusapKey key = signature.getKey();
            if (key != null) {
                MLog.d("Setting MusapKey");
                this.keyid = key.getKeyId();
                this.keyuri = key.getKeyUri().toString();
            } else {
                MLog.d("No MusapKey");
            }
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
