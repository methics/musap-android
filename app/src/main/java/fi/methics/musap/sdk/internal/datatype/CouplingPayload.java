package fi.methics.musap.sdk.internal.datatype;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

public class CouplingPayload {

    @SerializedName("couplingcode")
    private final String couplingCode;
    @SerializedName("musapid")
    private final String musapId;

    public CouplingPayload(String couplingCode, String musapId) {
        this.couplingCode = couplingCode;
        this.musapId = musapId;
    }

    /**
     * Get this payload in Base64 format that can be used in a
     * @return
     */
    public String toBase64() {
        String payloadJson = new Gson().toJson(this);
        return Base64.encodeToString(
                payloadJson.getBytes(StandardCharsets.UTF_8),
                Base64.NO_WRAP | Base64.URL_SAFE);
    }
}
