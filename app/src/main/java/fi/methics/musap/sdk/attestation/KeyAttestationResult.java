package fi.methics.musap.sdk.attestation;

import java.util.List;

import fi.methics.musap.sdk.internal.datatype.MusapCertificate;

public class KeyAttestationResult {

    private String attestationType;
    private byte[] signature;
    private MusapCertificate certificate;
    private List<MusapCertificate> certificateChain;
    private String aaguid;
    private boolean attestationStatus;

    private KeyAttestationResult(KeyAttestationResult.Builder builder) {
        this.attestationType = builder.attestationType;
        this.signature = builder.signature;
        this.certificate = builder.certificate;
        this.certificateChain = builder.certificateChain;
        this.aaguid = builder.aaguid;
        this.attestationStatus = builder.attestationStatus;
    }

    /**
     * Check if the key was attested successfully.
     * @return true if attestation succeeded. false if it failed or could not be done
     */
    public boolean isAttestationSuccessful() {
        return this.attestationStatus;
    }

    public String getAttestationType() {
        return this.attestationType;
    }

    public byte[] getSignature() {
        return this.signature;
    }

    public MusapCertificate getCertificate() {
        return this.certificate;
    }

    public String getAaguid() {
        return this.aaguid;
    }

    public static class Builder {

        private String attestationType;
        private byte[] signature;
        private MusapCertificate certificate;
        private List<MusapCertificate> certificateChain;
        private String aaguid;
        private boolean attestationStatus;

        public Builder setAttestationType(String attestationType) {
            this.attestationType = attestationType;
            return this;
        }
        public Builder setSignature(byte[] signature) {
            this.signature = signature;
            return this;
        }
        public Builder setCertificate(MusapCertificate certificate) {
            this.certificate = certificate;
            return this;
        }
        public Builder setSignature(List<MusapCertificate> certificateChain) {
            this.certificateChain = certificateChain;
            return this;
        }
        public Builder setAAGUID(String aaguid) {
            this.aaguid = aaguid;
            return this;
        }
        public Builder setAttestationStatus(boolean status) {
            this.attestationStatus = status;
            return this;
        }
        public KeyAttestationResult build() {
            return new KeyAttestationResult(this);
        }
    }

}
