package fi.methics.musap.sdk.internal.datatype;

import com.google.gson.annotations.SerializedName;

import fi.methics.musap.sdk.internal.util.MLog;

public class EnrollDataResponsePayload {

    @SerializedName("status")
    public String status;

    @SerializedName("musapid")
    public String musapId;

    public boolean isSuccess() {
        MLog.d("Status=" + this.status);
        return "success".equalsIgnoreCase(this.status);
    }

    public String getMusapId() {
        return this.musapId;
    }
}
