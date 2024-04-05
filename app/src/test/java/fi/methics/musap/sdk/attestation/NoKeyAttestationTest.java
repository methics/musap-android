package fi.methics.musap.sdk.attestation;

import static org.junit.Assert.*;

import org.junit.Test;

import fi.methics.musap.sdk.internal.datatype.MusapKey;

public class NoKeyAttestationTest {

    @Test
    public void getAttestationData() {
        MusapKey key = new MusapKey.Builder()
                .setKeyId("keyid")
                .build();
        NoKeyAttestation attestation = new NoKeyAttestation();
        KeyAttestationResult result = attestation.getAttestationData(key);
        assertEquals(KeyAttestationResult.AttestationStatus.UNDETERMINED, result.getAttestationStatus());
    }

    @Test
    public void getAttestationType() {
        NoKeyAttestation attestation = new NoKeyAttestation();
        assertEquals(KeyAttestationType.NONE, attestation.getAttestationType());
    }

    @Test
    public void isAttestationSupported() {
        assertFalse(new NoKeyAttestation().isAttestationSupported());
    }
}