package fi.methics.musap.sdk.internal.datatype.coupling.payload;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import fi.methics.musap.sdk.internal.util.MLog;

public class PollPayload extends MusapLinkPayload {

    private static final String POLL_MSG_TYPE = "getdata";

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
        return POLL_MSG_TYPE;
    }
}
