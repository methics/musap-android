package fi.methics.musap.sdk.internal.datatype;

import android.util.Base64;

import java.security.KeyPair;

import fi.methics.musap.sdk.internal.util.MBase64;

public class PublicKey {

    private byte[] publickeyDer;

    public PublicKey(byte[] publicKey) {
        this.publickeyDer = publicKey;
    }

    public PublicKey(KeyPair keyPair) {
        this.publickeyDer = keyPair.getPublic().getEncoded();
    }

    /**
     * Get DER representation of the public key
     * @return DER byte[]
     */
    public byte[] getDER() {
        return this.publickeyDer;
    }

    /**
     * Get a PEM representation of the public key
     * @return PEM String
     */
    public String getPEM() {
        String base64Signature = Base64.encodeToString(this.getDER(), Base64.DEFAULT);
        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN PUBLIC KEY-----\n");
        int width  = 64;
        int length = base64Signature.length();
        for (int i = 0; i < length; i += width) {
            int end = Math.min(i + width, length);
            pem.append(base64Signature, i, end);
            pem.append("\n");
        }
        pem.append("-----END PUBLIC KEY-----\n");
        return pem.toString();
    }

}
