package fi.methics.musap.sdk.attestation;

import java.util.List;

import fi.methics.musap.sdk.internal.datatype.MusapCertificate;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.attestation.KeyAttestationResult.AttestationStatus;

/**
 * Simple KeyAttestation class for SSCDs that use PKI certificates.
 */
public class CertificateKeyAttestation extends KeyAttestation {

    @Override
    public KeyAttestationResult getAttestationData(MusapKey key) {

        KeyAttestationResult.Builder builder = new KeyAttestationResult.Builder(getAttestationType());
        if (key == null) {
            return builder.setAttestationStatus(AttestationStatus.INVALID).build();
        }
        List<MusapCertificate> chain = key.getCertificateChain();
        if (chain == null || chain.isEmpty()) {
            return builder.setAttestationStatus(AttestationStatus.INVALID).build();
        }
        builder.setCertificateChain(chain);
        builder.setCertificate(key.getCertificate());
        return builder.setAttestationStatus(AttestationStatus.UNDETERMINED).build();
    }

    @Override
    public String getAttestationType() {
        return "Certificate";
    }

}
