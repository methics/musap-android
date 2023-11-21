package fi.methics.musap.sdk.internal.sign;

import android.app.Activity;

import java.util.List;

import fi.methics.musap.sdk.internal.datatype.KeyAttribute;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.SignatureAlgorithm;
import fi.methics.musap.sdk.internal.datatype.SignatureAttribute;
import fi.methics.musap.sdk.internal.datatype.SignatureFormat;

/**
 * MUSAP Signature Request
 * <p>
 * Usage:
 * <pre>
 *     new SignatureReq.Builder().setKey(key).setData(data).createSignatureReq();
 * </pre>
 * </p>
 */
public class SignatureReq {

    protected MusapKey key;
    protected byte[]  data;
    protected String  displayText;
    protected SignatureAlgorithm algorithm;
    protected SignatureFormat format;
    protected Activity activity;
    protected List<SignatureAttribute> attributes;

    private SignatureReq(Builder builder) {
        this.key         = builder.key;
        this.data        = builder.data;
        this.displayText = builder.displayText;
        this.algorithm   = builder.algorithm;
        this.format      = builder.format;
        this.attributes  = builder.attributes;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setAttributes(List<SignatureAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * Get the public key reference of the key to use
     * @return key reference
     */
    public MusapKey getKey() {
        return key;
    }

    /**
     * Get the data to sign
     * @return data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Get the desired signature algorithm.
     * @return signature algorithm
     */
    public SignatureAlgorithm getAlgorithm() {
        if (this.algorithm == null && this.key != null) return this.key.getDefaultsignatureAlgorithm();
        return algorithm;
    }

    /**
     * Get the desired signature format. If not defined, {@link SignatureFormat#CMS} is used.
     * @return signature format
     */
    public SignatureFormat getFormat() {
        if (this.format == null) return SignatureFormat.RAW;
        return format;
    }

    /**
     * Get signature request related attributes
     * @return attributes
     */
    public List<SignatureAttribute> getAttributes() {
        return this.attributes;
    }

    /**
     * Get a signature attribute value
     * @param name Attribute name
     * @return Attribute value or null if not found
     */
    public String getAttribute(String name) {
        if (this.getAttributes() == null) return null;
        if (name == null) return null;
        for (SignatureAttribute attr : getAttributes()) {
            if (attr == null) continue;
            if (name.equals(attr.name)) {
                return attr.value;
            }
        }
        return null;
    }

    /**
     * Get the text to display to the user during the signature request
     * @return Display text (a.k.a. DTBD). Default is "Sign with MUSAP".
     */
    public String getDisplayText() {
        return this.displayText;
    }

    public Activity getActivity() {
        return activity;
    }

    public static class Builder {
        private MusapKey key;
        private byte[] data;
        protected String displayText = "Sign with MUSAP";
        private SignatureAlgorithm algorithm;
        private SignatureFormat format;
        protected List<SignatureAttribute> attributes;

        public Builder(SignatureAlgorithm algorithm) {
            this.algorithm = algorithm;
        }

        public Builder setKey(MusapKey key) {
            this.key = key;
            return this;
        }

        public Builder setData(byte[] data) {
            this.data = data;
            return this;
        }

        public Builder setFormat(SignatureFormat format) {
            this.format = format;
            return this;
        }

        public Builder setAttributes(List<SignatureAttribute> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder setDisplayText(String text) {
            this.displayText = text;
            return this;
        }

        public SignatureReq createSignatureReq() {
            return new SignatureReq(this);
        }
    }

}
