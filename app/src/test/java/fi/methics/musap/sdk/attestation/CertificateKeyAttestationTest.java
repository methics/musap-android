package fi.methics.musap.sdk.attestation;

import static org.junit.Assert.*;

import org.junit.Test;

import fi.methics.musap.sdk.internal.datatype.MusapKey;

public class CertificateKeyAttestationTest {

    @Test
    public void getAttestationData() {
        MusapKey key = new MusapKey.Builder()
                .setKeyId("keyid")
                .build();
        CertificateKeyAttestation attestation = new CertificateKeyAttestation();
        KeyAttestationResult result = attestation.getAttestationData(key);
        assertEquals(KeyAttestationResult.AttestationStatus.INVALID, result.getAttestationStatus());
    }

    @Test
    public void getAttestationType() {
        CertificateKeyAttestation attestation = new CertificateKeyAttestation();
        assertEquals(KeyAttestationType.CERTIFICATE, attestation.getAttestationType());
    }
}