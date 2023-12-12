package fi.methics.musap.sdk.internal.datatype;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import fi.methics.musap.sdk.internal.sign.SignatureReq;

public class SignaturePayload {

    @SerializedName("data")
    public String data;

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

    @SerializedName("genkey")
    public boolean genkey;

    public static class KeyIdentifier {

        // Key identified by one of:
        @SerializedName("keyid")
        public String keyid;

        @SerializedName("keyalias")
        public String keyalias;

        @SerializedName("publickeyhash")
        public String publickeyhash;

    }

    /**
     * Create a new {@link SignatureReq} from this MUSAP Link payload and the given key
     * @param key MUSAP Key chosen by the system or by the user
     * @return {@link SignatureReq}
     */
    protected SignatureReq toSignatureReq(MusapKey key) {

        SignatureFormat format = SignatureFormat.fromString(this.format);
        KeyAlgorithm   keyAlgo = key.getAlgorithm();
        SignatureAlgorithm signAlgo;
        if (this.scheme == null) {
            signAlgo = keyAlgo.toSignatureAlgorithm(this.hashalgo);
        } else {
            signAlgo = new SignatureAlgorithm(this.scheme, this.hashalgo);
        }

        return new SignatureReq.Builder(signAlgo)
                .setData(Base64.decode(this.data, Base64.NO_WRAP))
                .setFormat(format)
                .setKey(key)
                .setDisplayText(this.display)
                .setAttributes(this.attributes)
                .createSignatureReq();
    }

}
