package fi.methics.musap.sdk.internal.discovery;

import fi.methics.musap.sdk.internal.datatype.KeyAlgorithm;
import fi.methics.musap.sdk.internal.datatype.MusapSscd;

/**
 * MUSAP SSCD Search request
 */
public class SscdSearchReq {

    private String sscdType;
    private String country;
    private String provider;
    private KeyAlgorithm algorithm;

    public SscdSearchReq(Builder builder) {
        this.sscdType     = builder.sscdType;
        this.country      = builder.country;
        this.provider     = builder.provider;
        this.algorithm    = builder.algorithm;
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

    public KeyAlgorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * Check if the given SSCD matches this search request
     * @param sscd SSCD to compare against
     * @return true if SSCD matches
     */
    public boolean matches(MusapSscd sscd) {
        if (this.algorithm != null && !sscd.getSupportedAlgorithms().contains(this.algorithm)) return false;
        if (this.country   != null && !this.country.equals(sscd.getCountry()))   return false;
        if (this.provider  != null && !this.provider.equals(sscd.getProvider())) return false;
        if (this.sscdType  != null && !this.sscdType.equals(sscd.getSscdType())) return false;
        return true;
    }

    public class Builder {
        private String sscdType;
        private String country;
        private String provider;
        private KeyAlgorithm algorithm;

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

        public Builder setAlgorithm(KeyAlgorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public SscdSearchReq build() {
            return new SscdSearchReq(this);
        }
    }

}
