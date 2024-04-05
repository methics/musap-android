package fi.methics.musap.sdk.attestation;

import static org.junit.Assert.*;

import org.junit.Test;

import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.PublicKey;

public class AndroidKeyAttestationTest {

    @Test
    public void testAttestationNoPublicKey() {
        MusapKey key = new MusapKey.Builder()
                .setKeyId("keyid")
                .build();
        AndroidKeyAttestation attestation = new AndroidKeyAttestation();
        KeyAttestationResult result = attestation.getAttestationData(key);
        assertEquals(KeyAttestationResult.AttestationStatus.INVALID, result.getAttestationStatus());
    }

    @Test
    public void testAttestationWithPublicKey() {
        MusapKey key = new MusapKey.Builder()
                .setKeyId("keyid")
                .setPublicKey(new PublicKey(new byte[128]))
                .build();
        AndroidKeyAttestation attestation = new AndroidKeyAttestation();
        KeyAttestationResult result = attestation.getAttestationData(key);
        assertEquals(KeyAttestationResult.AttestationStatus.UNDETERMINED, result.getAttestationStatus());
    }

    @Test
    public void getAttestationType() {
        AndroidKeyAttestation attestation = new AndroidKeyAttestation();
        assertEquals("AKS", attestation.getAttestationType());
    }
}