package fi.methics.musap.sdk.attestation;

import java.util.Map;
import java.util.UUID;

import fi.methics.musap.sdk.attestation.KeyAttestationResult.AttestationStatus;
import fi.methics.musap.sdk.internal.datatype.MusapCertificate;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.util.MLog;

/**
 * YubiKey key attestation
 */
public class YubiKeyAttestation extends KeyAttestation {

    private Map<String, byte[]> certificates;
    private String aaguid;

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

        MusapCertificate cert = this.getCertificate(key.getKeyId());
        if (cert != null) {
            try {
                byte[] aaguid = cert.getX509Certificate().getExtensionValue("1.3.6.1.4.1.45724.1.1.4");
                if (aaguid != null) {
                    this.aaguid = UUID.nameUUIDFromBytes(aaguid).toString();
                }
            } catch (Exception e) {
                MLog.d("Failed to resolve AAGUID", e);
            }
        }

        return builder.setAttestationStatus(AttestationStatus.UNDETERMINED)
                .setCertificate(this.getCertificate(key.getKeyId()))
                .setAAGUID(this.aaguid)
                .build();
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
