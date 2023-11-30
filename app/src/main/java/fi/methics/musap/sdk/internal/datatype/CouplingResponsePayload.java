package fi.methics.musap.sdk.internal.datatype;

import com.google.gson.annotations.SerializedName;

import fi.methics.musap.sdk.internal.util.MLog;

public class CouplingResponsePayload {

    @SerializedName("status")
    public String status;

    @SerializedName("linkid")
    public String linkid;

    @SerializedName("name")
    public String name;

    public boolean isSuccess() {
        MLog.d("Status=" + this.status);
        return "success".equalsIgnoreCase(this.status);
    }
}
