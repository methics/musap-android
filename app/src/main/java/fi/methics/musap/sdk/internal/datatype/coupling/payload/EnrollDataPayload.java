package fi.methics.musap.sdk.internal.datatype.coupling.payload;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

import fi.methics.musap.sdk.internal.datatype.coupling.payload.MusapLinkPayload;
import fi.methics.musap.sdk.internal.util.MLog;

public class EnrollDataPayload extends MusapLinkPayload {

    private static final String ENROLL_MSG_TYPE = "enrolldata";

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

    @Override
    public String toBase64() {
        String payloadJson = GSON.toJson(this);
        MLog.d("Payload=" + payloadJson);
        String base64 =  Base64.encodeToString(
                payloadJson.getBytes(StandardCharsets.UTF_8),
                Base64.NO_WRAP);
        MLog.d("Base64=" + base64);
        return base64;
    }

    @Override
    public String getType() {
        return ENROLL_MSG_TYPE;
    }

}
