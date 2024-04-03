package fi.methics.musap.sdk.internal.datatype;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import fi.methics.musap.sdk.internal.datatype.coupling.payload.MusapLinkPayload;
import fi.methics.musap.sdk.internal.util.ByteaMarshaller;

/**
 * Message between the MUSAP library and MUSAP link
 */
public class MusapMessage {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(byte[].class, new ByteaMarshaller())
            .create();

    @SerializedName("type")
    public String type;

    @SerializedName("musapid")
    public String musapId;

    @SerializedName("payload")
    public String payload;

    @SerializedName("transid")
    public String transid;
    public String requestid;
    public String mac;
    public String iv;

    public String toJson() {
        return GSON.toJson(this);
    }
    public void setPayload(MusapLinkPayload payload) {
        this.payload = payload.toBase64();
    }

    public void setType(MusapLinkPayload payload) {
        this.type = payload.getType();
    }

    /**
     * Get this message identifier for MAC calculation.
     * If this message does not have a trans id, use MUSAP ID instead.
     * @return
     */
    public String getIdentifier() {
        if (this.transid != null) {
            return this.transid;
        } else {
            return this.musapId;
        }
    }

}
