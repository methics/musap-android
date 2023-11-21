package fi.methics.musap.sdk.sscd.rest204.json;

import com.google.gson.annotations.SerializedName;

public class MSS_SignatureResp {

    @SerializedName("MobileUser")
    public MSS_SignatureReq.MobileUser mobileUser;

    @SerializedName("MSSP_TransID")
    public String msspTransId;

    @SerializedName("Status")
    public Status status;

    @SerializedName("MSSP_Info")
    public MSS_SignatureResp.MsspInfo msspInfo;

    public static class Status {

        @SerializedName("StatusCode")
        public Code statusCode;

        @SerializedName("StatusMessage")
        public String statusMessage;

        @SerializedName("StatusDetail")
        public String statusDetail;

    }

    public static class Code {

        @SerializedName("Value")
        public String value;
    }


    public static class MsspInfo {

        @SerializedName("MSSP_ID")
        public MssURI msspId;

    }

    public static class MssURI {

        @SerializedName("URI")
        public String uri;
    }

}
