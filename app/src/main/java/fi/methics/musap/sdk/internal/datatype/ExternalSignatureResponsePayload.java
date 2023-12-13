package fi.methics.musap.sdk.internal.datatype;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import fi.methics.musap.sdk.internal.util.MLog;

public class ExternalSignatureResponsePayload {

    @SerializedName("status")
    public String status;

    @SerializedName("signature")
    public String signature;

    @SerializedName("publickey")
    public String publickey;

    @SerializedName("certificate")
    public String certificate;

    @SerializedName("attributes")
    public Map<String, String> attributes = new HashMap<>();

    public boolean isSuccess() {
        MLog.d("Status=" + this.status);
        return "success".equalsIgnoreCase(this.status);
    }

    public byte[] getRawSignature() {
        return Base64.decode(this.signature, Base64.NO_WRAP);
    }

}
