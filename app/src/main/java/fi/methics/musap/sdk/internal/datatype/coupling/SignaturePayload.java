package fi.methics.musap.sdk.internal.datatype.coupling;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;
import java.util.List;

import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.internal.datatype.KeyAlgorithm;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.SignatureAlgorithm;
import fi.methics.musap.sdk.internal.datatype.SignatureAttribute;
import fi.methics.musap.sdk.internal.datatype.SignatureFormat;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.sign.SignatureReq;
import fi.methics.musap.sdk.internal.util.MLog;

public class SignaturePayload {

    public static final String DATA_FORMAT_BASE64 = "base64";
    public static final String DATA_FORMAT_UTF8   = "utf-8";

    @SerializedName("mode")
    public String mode;

    @SerializedName("data")
    public String data;

    @SerializedName("dataformat")
    public String dataformat;

    @SerializedName("mimetype")
    public String mimetype;

    @SerializedName("display")
    public String display = "Sign with MUSAP";

    @SerializedName("format")
    public String format;

    @SerializedName("scheme")
    public String scheme;

    @SerializedName("hashalgo")
    public String hashalgo = "SHA-256";

    @SerializedName("linkid")
    public String linkid;

    @SerializedName("key")
    public KeyIdentifier key;

    @SerializedName("attributes")
    public List<SignatureAttribute> attributes;

    /**
     * Data choice for cases where user's chosen keystore defines signed data,
     * such as document signing.
     */
    @SerializedName("datachoice")
    public List<DTBS> datachoice;

    public static class DTBS {

        @SerializedName("data")
        public String data;

        @SerializedName("key")
        public KeyIdentifier key;

        @SerializedName("mimetype")
        public String mimetype;

        @SerializedName("dataformat")
        public String dataformat;

    }

    public static class KeyIdentifier {

        @SerializedName("keyid")
        public String keyid;

        @SerializedName("keyname")
        public String keyname;

        @SerializedName("keyusage")
        public String keyusage;

        @SerializedName("publickeyhash")
        public String publickeyhash;

        @SerializedName("algorithm")
        public String algorithm;

    }

    /**
     * Create a new {@link SignatureReq} from this MUSAP Link payload and the given key
     * @param key MUSAP Key chosen by the system or by the user
     * @return {@link SignatureReq}
     */
    protected SignatureReq toSignatureReq(MusapKey key) throws MusapException {

        SignatureFormat format = SignatureFormat.fromString(this.format);
        KeyAlgorithm keyAlgo = key.getAlgorithm();
        SignatureAlgorithm signAlgo;
        if (this.scheme == null) {
            signAlgo = keyAlgo.toSignatureAlgorithm(this.hashalgo);
        } else {
            signAlgo = new SignatureAlgorithm(this.scheme, this.hashalgo);
        }

        byte[] dtbs = this.resolveDataToBeSigned(key);
        if (dtbs == null) {
            throw new MusapException(MusapException.ERROR_MISSING_PARAM, "Missing data to sign");
        }
        return new SignatureReq.Builder(signAlgo)
                .setData(dtbs)
                .setFormat(format)
                .setKey(key)
                .setDisplayText(this.display)
                .setAttributes(this.attributes)
                .addAttribute("mimetype", this.resolveMimeType(key))
                .createSignatureReq();
    }

    /**
     * Chooses data to be signed. Request may contain multiple in some use cases, like document
     * signing.
     * @param key MUSAP key to help data choice. May be null if {@link #datachoice} is not used.
     * @return Data to be signed. Usually this is the value of the data element.
     */
    private byte[] resolveDataToBeSigned(MusapKey key) throws MusapException {
        if (this.datachoice == null || this.datachoice.isEmpty()) {
            return this.formatDtbs(this.dataformat, this.data);
        }

        String keyId = key.getKeyId();
        if (keyId == null) {
            MLog.e("Missing key ID");
            throw new MusapException("Missing key ID");
        }

        for (DTBS dtbs: this.datachoice) {
            if (dtbs.key == null) {
                MLog.d("Missing key for DTBS " + dtbs);
                continue;
            }

            if (keyId.equalsIgnoreCase(dtbs.key.keyid)) {
                MLog.d("Found matching data choice with " + keyId);
                return this.formatDtbs(dtbs.dataformat, dtbs.data);
            }
        }

        MLog.e("No datachoice for key ID " + key.getKeyId());
        throw new MusapException("No datachoice for key ID" + key.getKeyId());
    }

    /**
     * Format given DTBS based on given DataFormat (utf-8 or base64)
     * @param dataformat Data Format
     * @param dtbs       Data to be signed (String)
     * @return Formatted DTBS
     */
    private byte[] formatDtbs(String dataformat, String dtbs) {
        if (dtbs == null) return null;
        if (dataformat == null || DATA_FORMAT_BASE64.equalsIgnoreCase(dataformat)) {
            return Base64.decode(dtbs, Base64.NO_WRAP);
        }
        // TODO: Support hex, other data formats
        return data.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Resolve Mime-Type to use
     * @param key Selected Key
     * @return Mime-Type or null
     */
    private String resolveMimeType(MusapKey key) {
        if (this.datachoice == null || this.datachoice.isEmpty()) {
            return this.mimetype;
        }
        String keyId = key.getKeyId();
        if (keyId == null) return null;
        for (DTBS dtbs: this.datachoice) {
            if (dtbs.mimetype == null) continue;
            if (keyId.equalsIgnoreCase(dtbs.key.keyid)) return dtbs.mimetype;
        }
        return null;
    }

    /**
     * Create a new {@link KeyGenReq} from this MUSAP Link payload
     * @return {@link KeyGenReq}A
     */
    public KeyGenReq toKeygenReq() {
        KeyGenReq.Builder builder = new KeyGenReq.Builder();
        if (this.key != null) {
            builder.setKeyAlgorithm(KeyAlgorithm.fromString(this.key.algorithm));
            builder.setKeyUsage(this.key.keyusage);
            // RP set name is used here since user is not asked for the name
            builder.setKeyAlias(this.key.keyname);
        }
        return builder.createKeyGenReq();
    }

}
