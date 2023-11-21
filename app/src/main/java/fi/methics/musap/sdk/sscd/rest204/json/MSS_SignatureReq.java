package fi.methics.musap.sdk.sscd.rest204.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import fi.methics.musap.sdk.internal.util.MBase64;

public class MSS_SignatureReq {

    @SerializedName("MobileUser")
    public MobileUser mobileUser;

    @SerializedName("MessagingMode")
    public String messagingMode = "async";

    @SerializedName("TimeOut")
    public String timeOut = "60000"; // 60s

    @SerializedName("MSS_Format")
    public String format;

    @SerializedName("SignatureProfile")
    public String signatureProfile;

    @SerializedName("DataToBeSigned")
    public DTBS dtbs;

    @SerializedName("DataToBeDisplayed")
    public DTBD dtbd;

    @SerializedName("AdditionalServices")
    public List<AdditionalService> additionalServices;

    public MSS_SignatureReq() {}

    public MSS_SignatureReq(String msisdn) {
        this.mobileUser = new MobileUser();
        this.mobileUser.msisdn = msisdn;
    }

    public static class DTBD {

        public DTBD() {}

        public DTBD(String message) {
            this.mimeType = "text/plain";
            this.encoding = "UTF-8";
            this.data     = message;
        }

        @SerializedName("MimeType")
        public String mimeType;

        @SerializedName("Encoding")
        public String encoding;

        @SerializedName("Data")
        public String data;

    }
    public static class DTBS {
        public DTBS() {}
        public DTBS(DTBD dtbd) {
            this.mimeType = "text/plain";
            this.encoding = "UTF-8";
            this.data     = dtbd.data;
        }
        public DTBS(byte[] data) {
            this.data     = MBase64.toBase64(data);
            this.encoding = "BASE64";
            this.mimeType = "application/octet-stream";
        }
        public DTBS(byte[] data, String mimeType) {
            this.data     = MBase64.toBase64(data);
            this.encoding = "BASE64";
            this.mimeType = mimeType;
        }

        @SerializedName("MimeType")
        public String mimeType;

        @SerializedName("Encoding")
        public String encoding;

        @SerializedName("Data")
        public String data;

    }

    public static class MobileUser {

        @SerializedName("MSISDN")
        public String msisdn;

    }

    public static class AdditionalService {

        @SerializedName("Description")
        public String description;

        @SerializedName("EventID")
        public EventId eventId;

        @SerializedName("NoSpamCode")
        public NoSpamCode noSpamCode;

        @SerializedName("UserLang")
        public String userLang;

        @SerializedName("App2App")
        public String app2app;

    }

    public static class NoSpamCode {

        @SerializedName("Verify")
        public String verify = "no";

        @SerializedName("Code")
        public String code;

    }

    public static class EventId {

        @SerializedName("Value")
        public String value;

    }

}
