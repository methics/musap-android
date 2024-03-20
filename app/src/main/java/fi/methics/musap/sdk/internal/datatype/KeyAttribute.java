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

}
