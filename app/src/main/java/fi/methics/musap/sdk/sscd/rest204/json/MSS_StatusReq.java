package fi.methics.musap.sdk.sscd.rest204.json;

import com.google.gson.annotations.SerializedName;

public class MSS_StatusReq {

    @SerializedName("MSSP_TransID")
    public String msspTransId;

    @SerializedName("MSSP_Info")
    public MSS_SignatureResp.MsspInfo msspInfo;

    public MSS_StatusReq(MSS_SignatureResp sigResp) {
        this.msspInfo    = sigResp.msspInfo;
        this.msspTransId = sigResp.msspTransId;
    }

}
