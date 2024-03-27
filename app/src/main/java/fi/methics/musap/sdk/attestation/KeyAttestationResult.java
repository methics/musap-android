package fi.methics.musap.sdk.attestation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import fi.methics.musap.sdk.internal.datatype.MusapCertificate;
import fi.methics.musap.sdk.internal.util.ByteaMarshaller;

/**
 * Key attestation result object.
 * This contains all the data used by MUSAP to attest the key, and MUSAP's attestation status ({@link #getAttestationStatus()}}.
 * <p>
 *     This can be converted to JSON using the {@link #toJson()} method.
 * </p>
 */
public class KeyAttestationResult {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(byte[].class, new ByteaMarshaller()).create();

    @SerializedName("attestation_type")
    private String attestationType;

    @SerializedName("attestation_signature")
    private byte[] attestationSignature;

    @SerializedName("attestation_certificate")
    private MusapCertificate certificate;

    @SerializedName("certificate_chain")
    private List<MusapCertificate> certificateChain;

    @SerializedName("attestation_status")
    private AttestationStatus attestationStatus;

    /**
     * Build a new {@link KeyAttestationResult} from given Builder
     * @param builder Builder
     */
    private KeyAttestationResult(KeyAttestationResult.Builder builder) {
        this.attestationType      = builder.attestationType;
        this.attestationSignature = builder.signature;
        this.attestationStatus    = builder.attestationStatus;
        this.certificate          = builder.certificate;
        this.certificateChain     = builder.certificateChain;
    }

    /**
     * Get MUSAP's opinion of the Key Attestation.
     *
     * Note that client-side attestation may not be reliable.
     * Therefore, it is recommended to verify the attestation data outside of MUSAP.
     *
     * @return {@link AttestationStatus#INVALID INVALID} or {@link AttestationStatus#UNDETERMINED UNDETERMINED}
     */
    public AttestationStatus getAttestationStatus() {
        return this.attestationStatus;
    }

    /**
     * Get the type of the attestation mechanism
     * @return Attestation mechanism type
     */
    public String getAttestationType() {
        return this.attestationType;
    }

    /**
     * Get the attestation signature
     * @return attestation signature
     */
    public byte[] getSignature() {
        return this.attestationSignature;
    }

    /**
     * Get the attestation certificate
     * @return Attestation certificate
     */
    public MusapCertificate getCertificate() {
        return this.certificate;
    }

    /**
     * Get the certificate chain
     * @return certificate chain
     */
    public List<MusapCertificate> getCertificateChain() {
        return this.certificateChain;
    }

    /**
     * Convert this attestation result to a JSON object.
     * The resulting JSON contains all the relevant evidence used to attest the key.
     * @return JSON representation of this attestation result
     */
    public String toJson() {
        return GSON.toJson(this);
    }

    /**
     * Attestation status that tells MUSAP's opinion of the attestation data.
     * This will always be either INVALID or UNDETERMINED, as MUSAP can only
     * check whether the data is INVALID or not. The remaining attestation should
     * be done by an external party.
     */
    public enum AttestationStatus {
        INVALID,
        UNDETERMINED
    }

    public static class Builder {

        private String attestationType;
        private byte[] signature;
        private MusapCertificate certificate;
        private List<MusapCertificate> certificateChain;
        private AttestationStatus attestationStatus = AttestationStatus.UNDETERMINED;

        /**
         * Create a new result builder
         * @param attestationType Attestation type (e.g. "UICC")
         */
        public Builder(String attestationType) {
            this.attestationType = attestationType;
        }

        public Builder setAttestationType(String attestationType) {
            this.attestationType = attestationType;
            return this;
        }
        public Builder setAttestationSignature(byte[] signature) {
            this.signature = signature;
            return this;
        }
        public Builder setCertificate(MusapCertificate certificate) {
            this.certificate = certificate;
            return this;
        }
        public Builder setSignature(byte[] signature) {
            this.signature = signature;
            return this;
        }
        public Builder setCertificateChain(List<MusapCertificate> certificateChain) {
            this.certificateChain = certificateChain;
            return this;
        }

        public Builder setAttestationStatus(AttestationStatus status) {
            this.attestationStatus = status;
            return this;
        }
        public KeyAttestationResult build() {
            return new KeyAttestationResult(this);
        }
    }

}
