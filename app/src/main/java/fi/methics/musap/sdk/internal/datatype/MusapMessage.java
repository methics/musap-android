package fi.methics.musap.sdk.internal.datatype;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Message between the MUSAP library and MUSAP link
 */
public class MusapMessage {

    @SerializedName("type")
    public String type;

    @SerializedName("payload")
    public String payload;

    public String uuid;
    public String transid;
    public String requestid;
    public String mac;
    public String iv;

    private transient boolean isError;
    private transient boolean isMt;
    private transient boolean isEncrypted;

    public String toJson() {
        return new Gson().toJson(this);
    }
}
