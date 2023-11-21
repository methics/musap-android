package fi.methics.musap.sdk.internal.datatype;

import java.util.List;

public class KeyAttestation {

    private String attestationType;
    private byte[] signature;
    private MusapCertificate certificate;
    private List<MusapCertificate> certificateChain;
    private String aaguid;

    private KeyAttestation(Builder builder) {
        this.attestationType = builder.attestationType;
        this.signature = builder.signature;
        this.certificate = builder.certificate;
        this.certificateChain = builder.certificateChain;
        this.aaguid = builder.aaguid;
    }

    public static class Builder {

        private String attestationType;
        private byte[] signature;
        private MusapCertificate certificate;
        private List<MusapCertificate> certificateChain;
        private String aaguid;

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
        public KeyAttestation build() {
            return new KeyAttestation(this);
        }

    }

}
