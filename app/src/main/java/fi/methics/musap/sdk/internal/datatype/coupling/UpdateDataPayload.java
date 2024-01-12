package fi.methics.musap.sdk.internal.datatype.coupling;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

import fi.methics.musap.sdk.internal.util.MLog;

public class UpdateDataPayload {

    @SerializedName("fcmtoken")
    private final String fcmToken;

    public UpdateDataPayload(String fcmToken) {
        this.fcmToken = fcmToken;
    }

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
