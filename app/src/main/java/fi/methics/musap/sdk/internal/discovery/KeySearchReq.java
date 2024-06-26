package fi.methics.musap.sdk.internal.discovery;

import fi.methics.musap.sdk.internal.datatype.KeyAlgorithm;
import fi.methics.musap.sdk.internal.datatype.KeyURI;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.SscdInfo;
import fi.methics.musap.sdk.internal.util.MusapSscd;

/**
 * MUSAP Key Search request
 */
public class KeySearchReq {

    private String sscdType;
    private String country;
    private String provider;
    private KeyAlgorithm keyAlgorithm;
    private String keyUri;

    public KeySearchReq(Builder builder) {
        this.sscdType     = builder.sscdType;
        this.country      = builder.country;
        this.provider     = builder.provider;
        this.keyAlgorithm = builder.keyAlgorithm;
        this.keyUri       = builder.keyUri;
    }

    public String getSscdType() {
        return sscdType;
    }

    public String getCountry() {
        return country;
    }

    public String getProvider() {
        return provider;
    }

    public KeyAlgorithm getKeyAlgorithm() {
        return keyAlgorithm;
    }

    /**
     * Check if the given key matches this search request
     * @param key MusapKey to compare against
     * @return true if key matches
     */
    public boolean matches(MusapKey key) {
        if (this.keyAlgorithm != null && !this.keyAlgorithm.equals(key.getAlgorithm())) return false;
        if (this.keyUri       != null && !new KeyURI(this.keyUri).matches(key.getKeyUri())) return false;
        MusapSscd iface = key.getSscd();
        if (iface != null) {
            SscdInfo sscd = iface.getSscdInfo();
            if (sscd != null) {
                if (this.provider != null && !this.provider.equals(sscd.getProvider())) return false;
                if (this.country  != null && !this.country.equals(sscd.getCountry())) return false;
                if (this.sscdType != null && !this.sscdType.equals(sscd.getSscdType())) return false;
            }
        }
        return true;
    }

    public static class Builder {
        private String sscdType;
        private String country;
        private String provider;
        private KeyAlgorithm keyAlgorithm;
        private String keyUri;

        public Builder setSscdType(String sscdType) {
            this.sscdType = sscdType;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setProvider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder setKeyAlgorithm(KeyAlgorithm keyAlgorithm) {
            this.keyAlgorithm = keyAlgorithm;
            return this;
        }

        public Builder setKeyUri(String keyUri) {
            this.keyUri = keyUri;
            return this;
        }

        public KeySearchReq build() {
            return new KeySearchReq(this);
        }
    }

}
