package fi.methics.musap.sdk.attestation;

import fi.methics.musap.sdk.internal.datatype.MusapKey;

/**
 * Simple KeyAttestation class for SSCDs that do not support key attestation.
 * The attestation result will always return a failed attestation response.
 */
public class NoKeyAttestation extends KeyAttestation {

    public NoKeyAttestation() {
        super(KeyAttestationType.NONE);
    }

    @Override
    public KeyAttestationResult getAttestationData(MusapKey key) {
        return new KeyAttestationResult.Builder(getAttestationType()).build();
    }

    @Override
    public String getAttestationType() {
        return this.keyAttestationType;
    }

    @Override
    public boolean isAttestationSupported() {
        return false;
    }

}
