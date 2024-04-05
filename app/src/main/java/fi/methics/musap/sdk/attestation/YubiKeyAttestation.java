package fi.methics.musap.sdk.attestation;

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
        super(KeyAttestationType.YUBIKEY);
        this.certificates = certificates;
    }

    @Override
    public KeyAttestationResult getAttestationData(MusapKey key) {

        KeyAttestationResult.Builder builder = new KeyAttestationResult.Builder(getAttestationType());
        if (key == null) {
            return builder.setAttestationStatus(AttestationStatus.INVALID).build();
        }

        return builder.setAttestationStatus(AttestationStatus.UNDETERMINED)
                .setCertificate(this.getCertificate(key.getKeyId()))
                .build();
    }

    @Override
    public String getAttestationType() {
        return this.keyAttestationType;
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
