package fi.methics.musap.sdk.attestation;

import fi.methics.musap.sdk.internal.datatype.MusapKey;

/**
 * MUSAP Key Attestation abstract base class
 */
public abstract class KeyAttestation {

    private MusapKey key;

    public KeyAttestation(MusapKey key) {
        this.key = key;
    }

    /**
     * Attest the associated key
     * @return attestation result
     */
    public abstract KeyAttestationResult attest();

    /**
     * Get the key this attestation object is tied to
     * @return MUSAP key
     */
    public MusapKey getKey() {
        return this.key;
    }

}
