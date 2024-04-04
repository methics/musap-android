package fi.methics.musap.sdk.attestation;

import static org.junit.Assert.*;

import org.junit.Test;

public class UiccKeyAttestationTest {

    @Test
    public void getAttestationType() {
        UiccKeyAttestation attestation = new UiccKeyAttestation();
        assertEquals(KeyAttestationType.UICC, attestation.getAttestationType());
    }
}