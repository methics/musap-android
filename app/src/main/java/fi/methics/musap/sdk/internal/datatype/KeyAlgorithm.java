package fi.methics.musap.sdk.internal.datatype;

import java.util.Objects;

/**
 * Key algorithm definition class
 */
public class KeyAlgorithm {

    public static final String PRIMITIVE_RSA = "rsa";
    public static final String PRIMITIVE_EC  = "ec";

    public static final String ALG_STR_RSA_2K      = "rsa2k";
    public static final String ALG_STR_RSA_4K      = "rsa4k";
    public static final String ALG_STR_ECC_P256_K1 = "eccp256k1";
    public static final String ALG_STR_ECC_P256_R1 = "eccp256r1";
    public static final String ALG_STR_ECC_P384_K1 = "eccp384k1";
    public static final String ALG_STR_ECC_P384_R1 = "eccp384r1";
    public static final String ALG_STR_ECC_ED25519 = "ecc_ed25519";

    public static final String CURVE_SECP256K1 = "secp256k1";
    public static final String CURVE_SECP384K1 = "secp384k1";
    public static final String CURVE_SECP256R1 = "secp256r1";
    public static final String CURVE_SECP384R1 = "secp384r1";
    public static final String CURVE_25519     = "ed25519";

    public static final KeyAlgorithm RSA_2K      = new KeyAlgorithm(PRIMITIVE_RSA, 2048);
    public static final KeyAlgorithm RSA_4K      = new KeyAlgorithm(PRIMITIVE_RSA, 4096);
    public static final KeyAlgorithm ECC_P256_K1 = new KeyAlgorithm(PRIMITIVE_EC, CURVE_SECP256K1, 256);
    public static final KeyAlgorithm ECC_P384_K1 = new KeyAlgorithm(PRIMITIVE_EC, CURVE_SECP384K1, 384);
    public static final KeyAlgorithm ECC_P256_R1 = new KeyAlgorithm(PRIMITIVE_EC, CURVE_SECP256R1, 256);
    public static final KeyAlgorithm ECC_P384_R1 = new KeyAlgorithm(PRIMITIVE_EC, CURVE_SECP384R1, 384);
    public static final KeyAlgorithm ECC_ED25519 = new KeyAlgorithm(PRIMITIVE_EC, CURVE_25519, 256);

    public String primitive;
    public String curve;
    public int bits;

    /**
     * Create a new Key Algorithm
     * @param primitive Primitive (usually RSA or EC)
     * @param bits      Bits (key length)
     */
    public KeyAlgorithm(String primitive, int bits) {
        this.primitive  = primitive.toLowerCase();
        this.bits       = bits;
    }

    /**
     * Create a new Key Algorithm with a specific curve
     * @param primitive Primitive (usually RSA or EC)
     * @param curve     Curve (e.g. secp256r1)
     * @param bits      Bits (key length)
     */
    public KeyAlgorithm(String primitive, String curve, int bits) {
        this.primitive  = primitive.toLowerCase();
        this.curve      = curve.toLowerCase();
        this.bits       = bits;
    }

    /**
     * Parse a supported String to a KeyAlgorithm.
     * See the MUSAP spec for a list of supported Strings.
     * @param algo KeyAlgorithm String
     * @return KeyAlgorithm, or null if not supported
     */
    public static KeyAlgorithm fromString(String algo) {
        if (algo == null) return null;
        switch (algo.toLowerCase()) {
            case CURVE_25519: return ECC_ED25519;
            case CURVE_SECP256K1: return ECC_P256_K1;
            case CURVE_SECP256R1: return ECC_P256_R1;
            case CURVE_SECP384K1: return ECC_P384_K1;
            case CURVE_SECP384R1: return ECC_P384_R1;
            case ALG_STR_RSA_2K: return RSA_2K;
            case ALG_STR_RSA_4K: return RSA_4K;
            case ALG_STR_ECC_P256_K1: return ECC_P256_K1;
            case ALG_STR_ECC_P256_R1: return ECC_P256_R1;
            case ALG_STR_ECC_P384_K1: return ECC_P384_K1;
            case ALG_STR_ECC_P384_R1: return ECC_P384_R1;
            case ALG_STR_ECC_ED25519: return ECC_ED25519;
        }
        return null;
    }

    /**
     * Is this an RSA key?
     * @return true for RSA key
     */
    public boolean isRsa() {
        return PRIMITIVE_RSA.equalsIgnoreCase(this.primitive);
    }

    /**
     * Is this an EC key?
     * @return true for EC key
     */
    public boolean isEc() {
        return PRIMITIVE_EC.equalsIgnoreCase(this.primitive);
    }

    /**
     * Map this key to a SignatureAlgorithm with the given hash algorithm
     * @param hashAlgo Hash algorithm (e.g. SHA256)
     * @return
     */
    public SignatureAlgorithm toSignatureAlgorithm(String hashAlgo) {
        if (hashAlgo == null) hashAlgo = SignatureAlgorithm.HASH_SHA256;
        if (this.isRsa()) {
            return new SignatureAlgorithm(SignatureAlgorithm.SCHEME_RSA, hashAlgo);
        } else {
            if (CURVE_25519.equalsIgnoreCase(this.curve)) {
                return SignatureAlgorithm.EDDSA;
            } else {
                return new SignatureAlgorithm(SignatureAlgorithm.SCHEME_ECDSA, hashAlgo);
            }
        }
    }

    @Override
    public String toString() {
        if (curve != null) {
            return "[" + primitive + "/" + curve + "/"  + bits + "]";
        } else {
            return "[" + primitive + "/"  + bits + "]";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyAlgorithm that = (KeyAlgorithm) o;
        if (bits != that.bits) return false;
        if (!Objects.equals(primitive, that.primitive))  return false;
        return Objects.equals(curve, that.curve);
    }

    @Override
    public int hashCode() {
        int result = primitive != null ? primitive.hashCode() : 0;
        result = 31 * result + (curve != null ? curve.hashCode() : 0);
        result = 31 * result + bits;
        return result;
    }
}
