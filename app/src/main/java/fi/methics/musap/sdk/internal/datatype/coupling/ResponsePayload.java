package fi.methics.musap.sdk.internal.datatype.coupling;

import com.google.gson.annotations.SerializedName;

public class ResponsePayload {

    @SerializedName("status")
    public String status;

    @SerializedName("errorcode")
    public String errorcode;

    public Integer getErrorCode() {
        if (this.errorcode == null) return null;
        return Integer.valueOf(errorcode);
    }

}
