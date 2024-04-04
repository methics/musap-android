package fi.methics.musap.sdk.attestation;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.HashMap;

public class YubiKeyAttestationTest {

    @Test
    public void getAttestationData() {
        CertificateKeyAttestation attestation = new CertificateKeyAttestation();
        KeyAttestationResult result = attestation.getAttestationData(null);
        assertEquals(KeyAttestationResult.AttestationStatus.INVALID, result.getAttestationStatus());
    }

    @Test
    public void getAttestationType() {
        YubiKeyAttestation attestation = new YubiKeyAttestation(new HashMap<>());
        assertEquals(KeyAttestationType.YUBIKEY, attestation.getAttestationType());
    }
}