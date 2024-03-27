package fi.methics.musap.sdk.internal.datatype.coupling;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

import fi.methics.musap.sdk.internal.util.MLog;

public class EnrollDataPayload {

    @SerializedName("fcmtoken")
    private final String fcmToken;

    @SerializedName("tokendata")
    private final String tokenData;

    /**
     * Security related tokens & shared secret.
     * This can be encrypted by the app and decrypted on MUSAP Link.
     */
    public static class TokenData {
        @SerializedName("secret")
        public String secret;
    }

    public EnrollDataPayload(String fcmToken, String secret) {
        this.fcmToken = fcmToken;
        TokenData tokenData = new TokenData();
        tokenData.secret = secret;
        String json = new GsonBuilder().disableHtmlEscaping().create().toJson(tokenData);
        this.tokenData = Base64.encodeToString(json.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    }

    public String toBase64() {
        String payloadJson = new GsonBuilder().disableHtmlEscaping().create().toJson(this);
        MLog.d("Payload=" + payloadJson);
        String base64 =  Base64.encodeToString(
                payloadJson.getBytes(StandardCharsets.UTF_8),
                Base64.NO_WRAP);
        MLog.d("Base64=" + base64);
        return base64;
    }

}
