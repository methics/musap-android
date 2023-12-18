package fi.methics.musap.sdk.internal.datatype;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import fi.methics.musap.sdk.internal.util.MLog;

public class ExternalSignaturePayload {

    @SerializedName("clientid")
    public String clientid;

    @SerializedName("sscdname")
    public String sscdname;

    @SerializedName("data")
    public String data;

    @SerializedName("display")
    public String display;

    @SerializedName("format")
    public String format;

    @SerializedName("publickey")
    public String publickey;

    @SerializedName("timeout")
    public String timeout;

    @SerializedName("transid")
    public String transid;

    @SerializedName("attributes")
    public Map<String, String> attributes = new HashMap<>();

    public ExternalSignaturePayload(String clientid) {
        this.clientid = clientid;
    }

    public ExternalSignaturePayload() {

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
