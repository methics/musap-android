package fi.methics.musap.sdk.internal.datatype;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import fi.methics.musap.sdk.api.MusapClient;
import fi.methics.musap.sdk.attestation.NoKeyAttestation;
import fi.methics.musap.sdk.internal.util.IdGenerator;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapSscd;
import fi.methics.musap.sdk.attestation.KeyAttestation;

/**
 * MUSAP Key class.
 * <p>This contains key details like public key, creation date, etc.</p>
 */
public class MusapKey {

    private String keyAlias;
    private String keyType;

    private String keyId;
    private String sscdId;
    private String sscdType;
    private Instant createdDate;
    private PublicKey publicKey;
    private MusapCertificate certificate;
    private List<MusapCertificate> certificateChain;
    private List<KeyAttribute> attributes;
    private List<String> keyUsages;
    private List<MusapLoA> loa;
    private KeyAlgorithm algorithm;

    private String did;
    private String state;

    private KeyAttestation attestation;

    private MusapKey(Builder builder) {
        this.keyAlias         = builder.keyAlias;
        this.keyType          = builder.keyType;
        this.keyId            = builder.keyId;
        this.sscdId           = builder.sscdId;
        this.sscdType         = builder.sscdType;
        this.publicKey        = builder.publicKey;
        this.certificate      = builder.certificate;
        this.certificateChain = builder.certificateChain;
        this.keyUsages        = builder.keyUsages;
        this.loa              = builder.loa;
        this.algorithm        = builder.algorithm;
        this.attestation      = builder.attestation;
        this.attributes       = builder.attributes;
        this.createdDate      = Instant.now();
    }

    public void setSscdId(String sscdId) {
        this.sscdId = sscdId;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public String getKeyType() {
        return keyType;
    }

    public String getSscdId() {
        return sscdId;
    }

    public String getSscdType() {
        return sscdType;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public MusapCertificate getCertificate() {
        return certificate;
    }

    public List<MusapCertificate> getCertificateChain() {
        return certificateChain;
    }

    public List<String> getKeyUsages() {
        return keyUsages;
    }

    public List<MusapLoA> getLoa() {
        return loa;
    }

    public KeyAlgorithm getAlgorithm() {
        return this.algorithm;
    }

    public KeyURI getKeyUri() {
        return new KeyURI(this);
    }

    /**
     * Get the Key Attestation.
     * @return Key Attestation. Returns {@link NoKeyAttestation} if none explicitly defined by the SSCD.
     */
    public KeyAttestation getAttestation() {
        if (this.attestation == null) {
            return new NoKeyAttestation(this);
        } else {
            return this.attestation;
        }
    }

    public List<KeyAttribute> getAttributes() {
        return this.attributes;
    }

    public String getKeyAttribute(String name) {
        if (name == null) return null;
        if (this.attributes == null) return null;
        for (KeyAttribute attr : this.attributes) {
            if (attr == null) continue;
            if (name.equals(attr.name)) return attr.value;
        }
        return null;
    }

    public KeyAttribute getAttribute(String name) {
        if (name == null) return null;
        return this.attributes.stream().filter(n -> name.equals(n.name)).findFirst().orElse(null);
    }

    /**
     * Add a new attribute to a key. If there is an existing with the name,
     * this replaces the value with a new one.
     * @param attr
     */
    public void addAttribute(KeyAttribute attr) {
        for (KeyAttribute oldAttr: this.attributes) {
            if (oldAttr.name.equalsIgnoreCase(attr.name)) {
                oldAttr.value = attr.value;
                return;
            }
        }

        this.attributes.add(attr);
    }

    public void removeAttribute(String name) {
        MLog.d("Removing attribute " + name);
        if (name == null) return;

        KeyAttribute toRemove = null;
        for (KeyAttribute attr: this.attributes) {
            if (attr.name.equalsIgnoreCase(name)) {
                toRemove = attr;
                break;
            }
        }

        if (toRemove != null) {
            MLog.d("Removed attribute " + name);
            this.attributes.remove(toRemove);
        }
    }

    public String getAttributeValue(String name) {
        KeyAttribute attr = this.getAttribute(name);
        if (attr == null) return null;
        return attr.value;
    }

    public SignatureAlgorithm getDefaultsignatureAlgorithm() {
        if (this.algorithm == null) {
            MLog.d("Unable to determine algorithm for key " + this.keyAlias);
            return SignatureAlgorithm.SHA256_WITH_ECDSA;
        }
        if (this.algorithm.isRsa()) {
            return SignatureAlgorithm.SHA256_WITH_RSA;
        } else {
            return SignatureAlgorithm.SHA256_WITH_ECDSA;
        }
    }

    /**
     * Get a handle to the SSCD implementation that created this MUSAP key
     * @return SSCD
     */
    public MusapSscd getSscd() {
        if (this.sscdType == null) {
            MLog.d("No SSCD Type found");
            return null;
        }
        MLog.d("Looking for an SSCD with type " + this.sscdType);
        for (MusapSscd sscd : MusapClient.listEnabledSscds()) {
            SscdInfo sscdInfo = sscd.getSscdInfo();
            String sscdId = sscd.getSettings().getSetting("id");

            if (this.sscdType.equals(sscdInfo.getSscdType())) {
                if (this.sscdId == null) {
                    MLog.d("Found SSCD with type " + this.sscdType);
                    return sscd;
                } else if (this.sscdId.equals(sscdId)) {
                    MLog.d("Found SSCD with type " + this.sscdType + " and id " + this.sscdId);
                    return sscd;
                } else {
                    MLog.d("SSCD ID " + sscdInfo.getSscdId() + " does not match our SSCD ID " + this.sscdId);
                }
            } else {
                MLog.d("SSCD type " + sscd.getSscdInfo().getSscdType() + " does not match our SSCD type " + this.sscdType);
            }
        }
        MLog.d("Could not find SSCD implementation for key " + this.keyId);
        return null;
    }

    /**
     * Get details of the SSCD that created this SSCD
     * @return SSCD
     */
    public SscdInfo getSscdInfo() {
        MusapSscd sscd = this.getSscd();
        if (sscd == null) return null;
        return sscd.getSscdInfo();
    }

    public void setAlias(String alias) {
        this.keyAlias = alias;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static class Builder {
        private String keyAlias;
        private String keyType;
        private String keyId;
        private String sscdId;
        private String sscdType;
        private PublicKey publicKey;
        private MusapCertificate certificate;
        private List<MusapCertificate> certificateChain;
        private List<KeyAttribute> attributes = new ArrayList<>();
        private List<String> keyUsages;
        private List<MusapLoA> loa;
        private KeyAlgorithm algorithm;

        private KeyAttestation attestation;

        public Builder setKeyAlias(String keyAlias) {
            this.keyAlias = keyAlias;
            return this;
        }

        public Builder setKeyType(String keyType) {
            this.keyType = keyType;
            return this;
        }

        public Builder setSscdId(String sscdId) {
            this.sscdId = sscdId;
            return this;
        }

        public Builder setSscd(SscdInfo sscd) {
            this.sscdId   = sscd.getSscdId();
            this.sscdType = sscd.getSscdType();
            return this;
        }

        public Builder setSscdType(String sscdType) {
            this.sscdType = sscdType;
            return this;
        }

        public Builder setPublicKey(PublicKey publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder setCertificate(MusapCertificate certificate) {
            this.certificate = certificate;
            if (this.certificate != null && this.publicKey == null) {
                this.publicKey = this.certificate.getPublicKey();
            }
            return this;
        }

        public Builder setCertificateChain(List<MusapCertificate> certificateChain) {
            this.certificateChain = certificateChain;
            return this;
        }

        public Builder setKeyUsages(List<String> keyUsages) {
            this.keyUsages = keyUsages;
            return this;
        }

        public Builder setLoa(List<MusapLoA> loa) {
            this.loa = loa;
            return this;
        }

        public Builder setAlgorithm(KeyAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder setKeyId(String keyId) {
            this.keyId = keyId;
            return this;
        }

        public Builder setAttestation(KeyAttestation attestation) {
            this.attestation = attestation;
            return this;
        }

        public Builder addAttribute(String key, String value) {
            this.attributes.add(new KeyAttribute(key, value));
            return this;
        }

        public Builder addAttribute(KeyAttribute attr) {
            this.attributes.add(attr);
            return this;
        }

        public MusapKey build() {
            if (this.keyId == null) {
                this.keyId = IdGenerator.generateKeyId();
            }
            return new MusapKey(this);
        }
    }
}
