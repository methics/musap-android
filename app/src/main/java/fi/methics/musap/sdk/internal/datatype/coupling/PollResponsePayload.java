package fi.methics.musap.sdk.internal.datatype.coupling;

import com.google.gson.annotations.SerializedName;

import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.sign.SignatureReq;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;

public class PollResponsePayload extends ResponsePayload {

    public static final String MODE_SIGN = "sign";
    public static final String MODE_SIGN_CHOICE = "sign-choice";

    public static final String MODE_GENSIGN = "generate-sign";
    public static final String MODE_GENONLY = "generate-only";

    @SerializedName("signaturepayload")
    private final SignaturePayload signaturePayload;

    @SerializedName("transid")
    private final String transId;

    /**
     * Get the mode of this poll response.
     * @return One of: "sign", "generate-sign", "generate-only"
     */
    public String getMode() {
        if (signaturePayload == null) return null;
        return signaturePayload.mode;
    }

    /**
     * Get the text to display to the user
     * @return Text to display
     */
    public String getDisplayText() {
        if (signaturePayload == null) return null;
        return signaturePayload.display;
    }

    /**
     * Get the raw Coupling API SignaturePayload
     * @return Raw SignaturePayload
     */
    public SignaturePayload getSignaturePayload() {
        return signaturePayload;
    }

    /**
     * Does this request ask for new key generation?
     * @return true if new key generation is wanted
     */
    public boolean shouldGenerateKey() {
        return !MODE_SIGN.equalsIgnoreCase(this.getMode()) && !MODE_SIGN_CHOICE.equalsIgnoreCase(this.getMode());

    }

    /**
     * Does this request ask for a signature?
     * @return true if signature is asked
     */
    public boolean shouldSign() {
        return !MODE_GENONLY.equalsIgnoreCase(this.getMode());
    }

    public PollResponsePayload(SignaturePayload signaturePayload, String transId) {
        this.signaturePayload = signaturePayload;
        this.transId = transId;
    }

    /**
     * Get the poll response transaction id
     * @return transaction id
     */
    public String getTransId() {
        return this.transId;
    }

    /**
     * Get the key ID this request is meant for, if any.
     * @return Key ID, or null if key ID is not in request
     */
    public String getKeyId() {
        if (this.signaturePayload == null || this.signaturePayload.key == null) {
            return null;
        }

        return this.signaturePayload.key.keyid;
    }

    /**
     * Get the key name this request is meant for, if any.
     * @return Key name, or null if key ID is not in request
     */
    public String getKeyName() {
        if (this.signaturePayload == null || this.signaturePayload.key == null) {
            return null;
        }

        return this.signaturePayload.key.keyname;
    }


    public SignatureReq toSignatureReq(MusapKey key) throws MusapException {
        SignatureReq req = this.signaturePayload.toSignatureReq(key);
        req.setTransId(this.transId);
        return req;
    }

    public KeyGenReq toKeygenReq() {
        KeyGenReq req = this.signaturePayload.toKeygenReq();
        return req;
    }

}
