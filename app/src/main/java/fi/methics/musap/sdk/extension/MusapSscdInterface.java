package fi.methics.musap.sdk.extension;

import fi.methics.musap.sdk.attestation.KeyAttestation;
import fi.methics.musap.sdk.attestation.KeyAttestationResult;
import fi.methics.musap.sdk.attestation.NoKeyAttestation;
import fi.methics.musap.sdk.internal.discovery.KeyBindReq;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.SscdInfo;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.sign.SignatureReq;

/**
 * MUSAP SSCD extension interface. Every SSCD should implement this.
 */
public interface MusapSscdInterface<T extends SscdSettings> {

    /**
     * Bind an existing key to this MUSAP library
     * @param req Key bind request
     * @throws Exception if binding failed
     * @return
     */
    MusapKey bindKey(KeyBindReq req) throws Exception;

    /**
     * Generate a new key with this SSCD. Note that this SSCD must support
     * @param req Key generation request
     * @throws Exception if key generation failed
     * @return Recently generated MUSAPKey
     */
    MusapKey generateKey(KeyGenReq req) throws Exception;

    /**
     * Sign with the SSCD
     * @param req Signature request
     * @return Signature response
     * @throws Exception if signature failed
     */
    MusapSignature sign(SignatureReq req) throws Exception;

    /**
     * Get SSCD info. Must not return null.
     * @return SSCD info
     */
    SscdInfo getSscdInfo();

    /**
     * Get the associated key attestation mechanism
     * @return key attestation mechanism
     */
    default KeyAttestation getKeyAttestation() {
        return new NoKeyAttestation();
    }

    /**
     * Attest given key with the KeyAttestation mechanism defined for this SSCD
     * @param key Key to attest
     * @return Key Attestation result
     */
    default KeyAttestationResult attestKey(MusapKey key) {
        return this.getKeyAttestation().getAttestationData(key);
    }

    /**
     * Does this SSCD support key generation?
     * @return true if key generation is supported
     */
    default boolean isKeygenSupported() {
        return this.getSscdInfo().isKeygenSupported();
    }

    /**
     * Get SSCD specific settings
     * @return settings
     */
    T getSettings();

}
