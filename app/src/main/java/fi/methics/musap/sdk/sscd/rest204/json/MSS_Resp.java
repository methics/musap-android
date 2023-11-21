package fi.methics.musap.sdk.sscd.rest204.json;

import com.google.gson.annotations.SerializedName;

public class MSS_Resp {

    @SerializedName("Fault")
    public Fault fault;

    @SerializedName("MSS_StatusResp")
    public MSS_StatusResp statusResp;

    @SerializedName("MSS_SignatureResp")
    public MSS_SignatureResp signatureResp;

}
