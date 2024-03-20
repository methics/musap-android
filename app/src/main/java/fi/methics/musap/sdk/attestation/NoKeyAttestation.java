package fi.methics.musap.sdk.attestation;

import fi.methics.musap.sdk.internal.datatype.MusapKey;

/**
 * Simple KeyAttestation class for SSCDs that do not support key attestation.
 * The attestation result will always return a failed attestation response.
 */
public class NoKeyAttestation extends KeyAttestation {

    public NoKeyAttestation(MusapKey key) {
        super(key);
    }

    @Override
    public KeyAttestationResult attest() {
        return new KeyAttestationResult.Builder().build();
    }

}
