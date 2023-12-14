package fi.methics.musap.sdk.internal.datatype;

import com.google.gson.annotations.SerializedName;

import fi.methics.musap.sdk.internal.sign.SignatureReq;

public class PollResp {

    @SerializedName("signaturepayload")
    private final SignaturePayload signaturePayload;

    @SerializedName("transid")
    private final String transId;

    public PollResp(SignaturePayload signaturePayload, String transId) {
        this.signaturePayload = signaturePayload;
        this.transId = transId;
    }

    public SignatureReq toSignatureReq(MusapKey key) {
        SignatureReq req = this.signaturePayload.toSignatureReq(key);
        req.setTransId(this.transId);
        return req;
    }

}
