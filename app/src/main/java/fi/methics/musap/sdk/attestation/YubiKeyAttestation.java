package fi.methics.musap.sdk.attestation;

import org.bouncycastle.jcajce.provider.asymmetric.X509;

import java.security.cert.X509Certificate;
import java.util.Map;

import fi.methics.musap.sdk.attestation.KeyAttestationResult.AttestationStatus;
import fi.methics.musap.sdk.internal.datatype.MusapCertificate;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.util.MLog;

/**
 * YubiKey key attestation
 */
public class YubiKeyAttestation extends KeyAttestation {

    private Map<String, byte[]> certificates;

    /**
     * Create a new YubiKey attestation object
     * @param certificates Map of KeyID to attestation X509Certificate
     */
    public YubiKeyAttestation(Map<String, byte[]> certificates) {
        this.certificates = certificates;
    }

    @Override
    public KeyAttestationResult getAttestationData(MusapKey key) {

        KeyAttestationResult.Builder builder = new KeyAttestationResult.Builder(getAttestationType());
        if (key == null) {
            return builder.setAttestationStatus(AttestationStatus.INVALID).build();
        }
        MusapCertificate attestationCertificate = this.getCertificate(key.getKeyId());
        if (attestationCertificate == null) {
            return builder.setAttestationStatus(AttestationStatus.UNDETERMINED).build();
        }
        // TODO: Attest this.certificate
        return builder.setAttestationStatus(AttestationStatus.VALID).setCertificate(attestationCertificate).build();
    }

    @Override
    public String getAttestationType() {
        return "Yubikey";
    }

    /**
     * Get attestation certificate with given keyid
     * @param keyid KeyID
     * @return certificate or null
     */
    private MusapCertificate getCertificate(String keyid) {
        if (this.certificates == null) return null;
        try {
            byte[] cert = this.certificates.get(keyid);
            return new MusapCertificate(cert);
        } catch (Exception e) {
            MLog.d("Failed to parse certificate", e);
        }
        return null;
    }

}
