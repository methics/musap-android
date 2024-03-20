package fi.methics.musap.sdk.internal.datatype;

import android.util.Base64;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

/**
 * Key binding, key generation, or key specific attributes
 */
public class KeyAttribute {

    public String name;
    public String value;

    public KeyAttribute(String name, String value) {
        this.name  = name;
        this.value = value;
    }

    public KeyAttribute(String name, X509Certificate cert) throws CertificateEncodingException {
        this.name  = name;
        if (cert != null) {
            this.value = Base64.encodeToString(cert.getEncoded(), Base64.NO_WRAP);
        }
    }

    /**
     * Get attribute name
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get attribute value
     * @return value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Get attribute value as byte[]
     * @return value or null
     */
    public byte[] getValueBytes() {
        if (this.value == null) return null;
        return Base64.decode(this.value, Base64.NO_WRAP);
    }

}
