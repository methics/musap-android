package fi.methics.musap.sdk.attestation;

import fi.methics.musap.sdk.internal.datatype.MusapKey;

public class AndroidKeyAttestation extends KeyAttestation {

    public AndroidKeyAttestation() {
        super("AKS");
    }

    @Override
    public KeyAttestationResult getAttestationData(MusapKey key) {
        KeyAttestationResult.Builder builder = new KeyAttestationResult.Builder(getAttestationType());
        if (key == null || key.getPublicKey() == null) {
            return builder.setAttestationStatus(KeyAttestationResult.AttestationStatus.INVALID).build();
        }
        // AKS does not really provide any attestation data
        return builder.setAttestationStatus(KeyAttestationResult.AttestationStatus.UNDETERMINED).build();
    }

    @Override
    public String getAttestationType() {
        return "AKS";
    }

}
