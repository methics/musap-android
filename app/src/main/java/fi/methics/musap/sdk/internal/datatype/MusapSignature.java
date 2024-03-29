package fi.methics.musap.sdk.internal.datatype;

import fi.methics.musap.sdk.attestation.KeyAttestationResult;
import fi.methics.musap.sdk.internal.util.MBase64;
import fi.methics.musap.sdk.internal.util.MusapSscd;

/**
 * MUSAP Signature class. This contains the raw signature and various signature details like
 * algorithm, format and usually a public key reference.
 */
public class MusapSignature {

    private byte[] rawSignature;
    private MusapKey key;
    private SignatureAlgorithm algorithm;
    private SignatureFormat format;
    private KeyAttestationResult attestationData;

    /**
     * Create a new MUSAP Signature object
     * @param rawSignature Raw signature byte[]
     * @param key          Public key reference
     * @param algorithm    Signature algorithm
     * @param format       Signature format
     */
    public MusapSignature(byte[] rawSignature,
                          MusapKey key,
                          SignatureAlgorithm algorithm,
                          SignatureFormat format)
    {
        this.rawSignature = rawSignature;
        this.key          = key;
        this.algorithm    = algorithm;
        this.format       = format;

        // Resolve attestation data
        if (this.key != null) {
            MusapSscd sscd = this.key.getSscd();
            if (sscd != null) {
                this.attestationData = sscd.getKeyAttestation().getAttestationData(this.key);
            }
        }
    }

    /**
     * Create a new raw signature without any meta-data
     * @param rawSignature Raw signature byte[]
     */
    public MusapSignature(byte[] rawSignature) {
        this.rawSignature = rawSignature;
    }

    /**
     * Create a new raw signature with a public key reference
     * @param rawSignature Raw signature byte[]
     * @param key          Public key reference
     */
    public MusapSignature(byte[] rawSignature, MusapKey key) {
        this.rawSignature = rawSignature;
        this.key          = key;
    }

    /**
     * Set the MUSAP key reference to help validation of this signature
     * @param key MUSAP key
     */
    public void setKey(MusapKey key) {
        this.key = key;
    }

    /**
     * Get Signature Algorithm used to generate this signature
     * @return Signature Algorithm
     */
    public SignatureAlgorithm getSignatureAlgorithm() {
        return this.algorithm;
    }

    /**
     * Get the signature format
     * @return format
     */
    public SignatureFormat getSignatureFormat() {
        return this.format;
    }

    /**
     * Get the key that was used to generate this signature
     * @return Key details
     */
    public MusapKey getKey() {
        return this.key;
    }

    /**
     * Get the public key associated with this signature
     * @return public key if available. otherwise null.
     */
    public PublicKey getPublicKey() {
        if (this.key == null) return null;
        return this.key.getPublicKey();
    }

    /**
     * Get the signature bytes
     * @return signature bytes
     */
    public byte[] getRawSignature() {
        return this.rawSignature;
    }

    /**
     * Get the signature as a Base64 String
     * @return signature Base64
     */
    public String getB64Signature() {
        return MBase64.toBase64String(this.rawSignature);
    }

    /**
     * Get Key Attestation result related to this signature
     * @return Key Attestation result (may be null)
     */
    public KeyAttestationResult getKeyAttestationResult() {
        return this.attestationData;
    }

}
