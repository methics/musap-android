package fi.methics.musap.sdk.internal.datatype;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import fi.methics.musap.sdk.api.MusapClient;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.internal.util.MLog;

public class MusapKey {

    // TODO: Rename to alias
    private String keyName;
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
        this.keyName          = builder.keyName;
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

    public String getKeyName() {
        return keyName;
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

    public KeyAttestation getAttestation() {
        return this.attestation;
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
            MLog.d("Unable to determine algorithm for key " + this.keyName);
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
    public MusapSscdInterface getSscdImpl() {
        if (this.sscdType == null) {
            MLog.d("No SSCD Type found");
            return null;
        }
        MLog.d("Looking for an SSCD with type " + this.sscdType);
        for (MusapSscdInterface sscd : MusapClient.listEnabledSscds()) {
            if (this.sscdType.equals(sscd.getSscdInfo().getSscdType())) {
                MLog.d("Found SSCD with type " + this.sscdType);
                return sscd;
            } else {
                MLog.d("SSCD " + sscd.getSscdInfo().getSscdType() + " does not match " + this.sscdType);
            }
        }
        return null;
    }

    public MusapSscd getSscdInfo() {
        if (this.sscdId == null) {
            MLog.d("No SSCD ID found");
            return null;
        }
        MLog.d("Looking for an SSCD with id " + this.sscdId);
        for (MusapSscd sscd : MusapClient.listActiveSscds()) {
            if (this.sscdId.equals(sscd.getSscdId())) {
                MLog.d("Found SSCD with id " + this.sscdId);
                return sscd;
            }
        }
        return null;
    }

    public void setAlias(String alias) {
        this.keyName = alias;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static class Builder {
        private String keyName;
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

        public Builder setKeyName(String keyName) {
            this.keyName = keyName;
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
            return new MusapKey(this);
        }
    }
}
