package fi.methics.musap.sdk.internal.datatype.coupling.payload;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

import fi.methics.musap.sdk.internal.util.MLog;

public class LinkAccountPayload extends MusapLinkPayload {
    private static final String COUPLE_MSG_TYPE = "linkaccount";

    @SerializedName("couplingcode")
    public final String couplingCode;
    @SerializedName("musapid")
    public final String musapId;

    public LinkAccountPayload(String couplingCode, String musapId) {
        this.couplingCode = couplingCode;
        this.musapId      = musapId;
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

    @Override
    public String getType() {
        return COUPLE_MSG_TYPE;
    }
}
