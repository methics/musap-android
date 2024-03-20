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
    public KeyAttestationResult attest(MusapKey key) {

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
        return builder.setAttestationStatus(this.getSuccessStatus()).build();
    }

    @Override
    public String getAttestationType() {
        return "Certificate";
    }

    /**
     * What {@link AttestationStatus} to return when attestation is deemed successful?
     * By default the {@link CertificateKeyAttestation} returns {@link AttestationStatus#UNDETERMINED UNDETERMINED}
     * because the credibility of the certificate chain depends on the SSCD.
     * <p>
     *     This method should be overridden by extending classes if another status is wanted.
     * </p>
     * @return Attestation status
     */
    protected AttestationStatus getSuccessStatus() {
        return AttestationStatus.UNDETERMINED;
    }
    
}
