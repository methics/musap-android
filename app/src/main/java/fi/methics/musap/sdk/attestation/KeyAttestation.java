package fi.methics.musap.sdk.attestation;

import fi.methics.musap.sdk.internal.datatype.MusapKey;

/**
 * MUSAP Key Attestation abstract base class
 */
public abstract class KeyAttestation {

    /**
     * Attest the given key
     * @param key MUSAP key
     * @return attestation result
     */
    public abstract KeyAttestationResult attest(MusapKey key);

    /**
     * Get the attestation type.
     * This can be a simple String that tells what attestation mechanism was used
     * (e.g. "UICC").
     * @return Attestation type
     */
    public abstract String getAttestationType();

    /**
     * Check if key attestation is supported
     * @return true if supported
     */
    public boolean isAttestationSupported() {
        return true;
    }

}
