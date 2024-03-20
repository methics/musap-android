package fi.methics.musap.sdk.attestation;

/**
 * UICC specific certificate-based key attestation.
 * This assumes that the MNO and CA have performed registration operations that verify
 * that the user's private key is kept in an applet inside the UICC.
 */
public class UiccKeyAttestation extends CertificateKeyAttestation {

    @Override
    public String getAttestationType() {
        return "UICC";
    }

    @Override
    protected KeyAttestationResult.AttestationStatus getSuccessStatus() {
        return KeyAttestationResult.AttestationStatus.VALID;
    }

}
