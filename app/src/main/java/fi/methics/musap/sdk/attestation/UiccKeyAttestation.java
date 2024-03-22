package fi.methics.musap.sdk.attestation;

import java.security.Key;

/**
 * UICC specific certificate-based key attestation.
 * This assumes that the MNO and CA have performed registration operations that verify
 * that the user's private key is kept in an applet inside the UICC.
 */
public class UiccKeyAttestation extends CertificateKeyAttestation {

    @Override
    public String getAttestationType() {
        return KeyAttestationType.UICC;
    }

}
