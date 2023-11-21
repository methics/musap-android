package fi.methics.musap.sdk.sscd.rest204.json;

import com.google.gson.annotations.SerializedName;

public class Fault {

    @SerializedName("Code")
    public Code code;

    public static class Code {

        @SerializedName("SubCode")
        public Code subCode;

        @SerializedName("Value")
        public String value;

        @SerializedName("ValueNs")
        public String valueNs;

        @SerializedName("Detail")
        public String detail;
    }

}
