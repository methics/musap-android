package fi.methics.musap.sdk.sscd.rest204.json;

import com.google.gson.annotations.SerializedName;

public class MSS_StatusResp {

    @SerializedName("MobileUser")
    public MSS_SignatureReq.MobileUser mobileUser;

    @SerializedName("MSS_Signature")
    public MSS_Signature signature;

    @SerializedName("Status")
    public MSS_SignatureResp.Status status;

    public static class MSS_Signature {

        @SerializedName("Base64Signature")
        public String base64Signature;

    }

}
