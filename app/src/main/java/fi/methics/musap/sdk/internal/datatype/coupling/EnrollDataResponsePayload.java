package fi.methics.musap.sdk.internal.datatype.coupling;

import com.google.gson.annotations.SerializedName;

import fi.methics.musap.sdk.internal.util.MLog;

public class EnrollDataResponsePayload extends ResponsePayload {

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
