package fi.methics.musap.sdk.attestation;

import fi.methics.musap.sdk.internal.datatype.MusapKey;

/**
 * Simple KeyAttestation class for SSCDs that do not support key attestation.
 * The attestation result will always return a failed attestation response.
 */
public class NoKeyAttestation extends KeyAttestation {

    @Override
    public KeyAttestationResult attest(MusapKey key) {
        return new KeyAttestationResult.Builder(getAttestationType()).build();
    }

    @Override
    public String getAttestationType() {
        return "No Attestation";
    }

    @Override
    public boolean isAttestationSupported() {
        return false;
    }

}
