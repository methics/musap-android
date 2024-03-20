package fi.methics.musap.sdk.attestation;

import fi.methics.musap.sdk.internal.datatype.MusapKey;

/**
 * Simple KeyAttestation class for SSCDs that use PKI certificates.
 */
public class CertificateKeyAttestation extends KeyAttestation {

    public CertificateKeyAttestation(MusapKey key) {
        super(key);
    }

    @Override
    public KeyAttestationResult attest() {
        //TODO
        return new KeyAttestationResult.Builder().build();
    }
    
}
