package fi.methics.musap.sdk.internal.datatype.coupling;


import fi.methics.musap.sdk.internal.util.MLog;

public class UpdateDataResponsePayload extends ResponsePayload {

    public boolean isSuccess() {
        MLog.d("Status=" + this.status);
        return "success".equalsIgnoreCase(this.status);
    }
}
