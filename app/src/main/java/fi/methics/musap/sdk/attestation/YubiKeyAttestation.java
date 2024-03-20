package fi.methics.musap.sdk.attestation;

import org.bouncycastle.jcajce.provider.asymmetric.X509;

import java.security.cert.X509Certificate;

import fi.methics.musap.sdk.attestation.KeyAttestationResult.AttestationStatus;
import fi.methics.musap.sdk.internal.datatype.MusapKey;

/**
 * YubiKey key attestation
 */
public class YubiKeyAttestation extends KeyAttestation {

    private X509Certificate certificate;

    public YubiKeyAttestation(X509Certificate certificate) {
        this.certificate = certificate;
    }

    @Override
    public KeyAttestationResult attest(MusapKey key) {

        KeyAttestationResult.Builder builder = new KeyAttestationResult.Builder(getAttestationType());
        if (key == null) {
            return builder.setAttestationStatus(AttestationStatus.INVALID).build();
        }
        if (this.certificate == null) {
            return builder.setAttestationStatus(AttestationStatus.UNDETERMINED).build();
        }
        // TODO: Attest this.certificate
        return builder.setAttestationStatus(AttestationStatus.VALID).build();
    }

    @Override
    public String getAttestationType() {
        return "Yubikey";
    }

}
