package fi.methics.musap.sdk.internal.datatype;

import java.security.Signature;
import java.util.Objects;

import fi.methics.musap.sdk.internal.util.MLog;

/**
 * Signature algorithm definition class
 */
public class SignatureAlgorithm {

    public static SignatureAlgorithm SHA256_WITH_ECDSA = new SignatureAlgorithm("SHA256withECDSA");
    public static SignatureAlgorithm SHA384_WITH_ECDSA = new SignatureAlgorithm("SHA384withECDSA");
    public static SignatureAlgorithm SHA512_WITH_ECDSA = new SignatureAlgorithm("SHA512withECDSA");
    public static SignatureAlgorithm NONE_WITH_ECDSA   = new SignatureAlgorithm("NONEwithECDSA");

    public static SignatureAlgorithm EDDSA = new SignatureAlgorithm("NONEwithEdDSA");

    public static SignatureAlgorithm SHA256_WITH_RSA = new SignatureAlgorithm("SHA256withRSA");
    public static SignatureAlgorithm SHA384_WITH_RSA = new SignatureAlgorithm("SHA384withRSA");
    public static SignatureAlgorithm SHA512_WITH_RSA = new SignatureAlgorithm("SHA512withRSA");
    public static SignatureAlgorithm NONE_WITH_RSA   = new SignatureAlgorithm("NONEwithRSA");

    public static SignatureAlgorithm SHA256_WITH_RSAPSS = new SignatureAlgorithm("SHA256withRSASSA-PSS");
    public static SignatureAlgorithm SHA384_WITH_RSAPSS = new SignatureAlgorithm("SHA384withRSASSA-PSS");
    public static SignatureAlgorithm SHA512_WITH_RSAPSS = new SignatureAlgorithm("SHA512withRSASSA-PSS");
    public static SignatureAlgorithm NONE_WITH_RSAPSS   = new SignatureAlgorithm("NONEwithRSASSA-PSS");

    // Note: Schemes according to SOG-IS ACM
    public static final String SCHEME_RSA_PSS  = "RSASSA-PSS"; // a.k.a. PSS (PKCS#1v2.1)
    public static final String SCHEME_RSA      = "RSA";        // a.k.a. PKCS#1v1.5
    public static final String SCHEME_ECDSA    = "ECDSA";      // a.k.a. EC-DSA
    public static final String SCHEME_EDDSA    = "EdDSA";

    public static final String HASH_SHA256 = "SHA256";
    public static final String HASH_SHA384 = "SHA384";
    public static final String HASH_SHA512 = "SHA512";
    public static final String HASH_NONE   = "NONE";

    private String scheme;
    private String hashAlgorithm;

    public SignatureAlgorithm(String javaAlgorithm) {
        String[] split = javaAlgorithm.split("with");
        if (split.length == 2) {
            this.hashAlgorithm = split[0];
            this.scheme        = split[1];
        }
    }

    public SignatureAlgorithm(String scheme, String hashAlgorithm) {
        this.scheme        = scheme;
        this.hashAlgorithm = hashAlgorithm != null ? hashAlgorithm.toUpperCase() : null;
    }

    /**
     * Get the algorithm as expected by Java {@link Signature#getInstance(String)}}
     * @return Algorithm
     */
    public String getJavaAlgorithm() {
        String javaHash   = getHashAlgorithm();
        String javaScheme = getJavaScheme();
        return javaHash + "with" + javaScheme;
    }

    /**
     * Get the hash algorithm
     * @return hash algorithm
     */
    public String getHashAlgorithm() {
        if (this.hashAlgorithm == null) return HASH_NONE;
        return this.hashAlgorithm;
    }

    /**
     * Get JWS algorithm identifier if applicable
     * @return JWS algorithm identifier (e.g. RS256 for RSA with SHA-256)
     */
    public String getJwsAlgorithm() {
        if (SCHEME_RSA_PSS.equals(this.scheme)) {
            if (HASH_SHA256.equals(this.hashAlgorithm)) return "RS256";
            if (HASH_SHA384.equals(this.hashAlgorithm)) return "RS384";
            if (HASH_SHA512.equals(this.hashAlgorithm)) return "RS512";
        }
        if (SCHEME_RSA_PSS.equals(this.scheme)) {
            if (HASH_SHA256.equals(this.hashAlgorithm)) return "PS256";
            if (HASH_SHA384.equals(this.hashAlgorithm)) return "PS384";
            if (HASH_SHA512.equals(this.hashAlgorithm)) return "PS512";
        }
        if (SCHEME_ECDSA.equals(this.scheme)) {
            if (HASH_SHA256.equals(this.hashAlgorithm)) return "ES256";
            if (HASH_SHA384.equals(this.hashAlgorithm)) return "ES384";
            if (HASH_SHA512.equals(this.hashAlgorithm)) return "ES512";
        }
        if (SCHEME_EDDSA.equals(this.scheme)) {
            return "EdDSA";
        }
        MLog.d("No matches for scheme=" + scheme + " and hash " + hashAlgorithm);
        return null;
    }

    /**
     * Get the scheme
     * @return scheme
     */
    public String getScheme() {
        return this.scheme;
    }

    /**
     * Does this signature algorithm use RSA keys?
     * @return true for RSA
     */
    public boolean isRsa() {
        String jwsAlgo = this.getJwsAlgorithm();
        if (jwsAlgo == null) return false;
        return getJwsAlgorithm().startsWith("RS");
    }

    /**
     * Does this signature algorithm use EC keys?
     * @return true for EC
     */
    public boolean isEc() {
        return !isRsa();
    }

    /**
     * Get scheme as understood by Java Signature
     * @return scheme
     */
    private String getJavaScheme() {
        String javaScheme = scheme;
        switch (scheme) {
            case SCHEME_RSA_PSS: javaScheme = "RSASSA-PSS"; break;
            case SCHEME_RSA:     javaScheme = "RSA"; break;
            case SCHEME_ECDSA:   javaScheme = "ECDSA"; break;
        }
        return javaScheme;
    }

    /**
     * Get algorithm as understood by Java signature
     * @return algorithm
     */
    private String getJavaHashAlgorithm() {
        return getHashAlgorithm().replace("-", "").replace("_", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignatureAlgorithm that = (SignatureAlgorithm) o;
        if (!Objects.equals(scheme, that.scheme)) return false;
        return Objects.equals(hashAlgorithm, that.hashAlgorithm);
    }

    @Override
    public int hashCode() {
        int result = scheme != null ? scheme.hashCode() : 0;
        result = 31 * result + (hashAlgorithm != null ? hashAlgorithm.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getJavaAlgorithm();
    }

}
