package fi.methics.musap.sdk.internal.datatype;


import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fi.methics.musap.sdk.internal.discovery.KeyDiscoveryCriteria;
import fi.methics.musap.sdk.internal.util.LoA;
import fi.methics.musap.sdk.internal.util.MLog;

public class KeyURI {

    public static final String ALIAS      = "alias";
    public static final String LOA        = "loa";
    public static final String COUNTRY    = "country";
    public static final String PROVIDER   = "provider";
    public static final String SSCD       = "sscd";
    public static final String ALGORITHM  = "algorithm";
    public static final String MSISDN     = "msisdn";
    public static final String SERIAL     = "serial";
    public static final String CREATED_DT = "created_dt";

    private Map<String, String> keyUriMap = new HashMap<>();

    /**
     * Create a new KeyURI
     * @param key Key to create the URI form from
     */
    public KeyURI(MusapKey key) {

        // TODO: Rename key.getKeyName() to key.getKeyAlias()
        if (key.getKeyName()     != null) keyUriMap.put(ALIAS,      key.getKeyName());
        if (key.getAlgorithm()   != null) keyUriMap.put(ALGORITHM,  key.getAlgorithm().isEc() ? "EC" : "RSA");
        if (key.getCreatedDate() != null && key.getCreatedDate().toEpochMilli() != 0) {
            keyUriMap.put(CREATED_DT, key.getCreatedDate().toString().split("T")[0]);
        }

        if (key.getKeyAttribute(MSISDN) != null) keyUriMap.put(MSISDN, key.getKeyAttribute(MSISDN));
        if (key.getKeyAttribute(SERIAL) != null) keyUriMap.put(MSISDN, key.getKeyAttribute(SERIAL));

        if (key.getSscdInfo() != null) {
            String sscdName     = key.getSscdInfo().getSscdName();
            String sscdCountry  = key.getSscdInfo().getCountry();
            String sscdProvider = key.getSscdInfo().getProvider();

            if (sscdName     != null) keyUriMap.put(SSCD,     sscdName);
            if (sscdCountry  != null) keyUriMap.put(COUNTRY,  sscdCountry);
            if (sscdProvider != null) keyUriMap.put(PROVIDER, sscdProvider);
        }
    }

    /**
     * Create a new KeyURI object from a KeyURI String
     * @param keyURI KeyURI
     */
    public KeyURI(String keyURI) {
        this.keyUriMap = this.parseUri(keyURI);
    }

    /**
     * Parse a KeyURI
     * @param keyURI
     * @return
     */
    private Map<String, String> parseUri(String keyURI) {
        Map<String, String> keyUriMap = new HashMap<>();
        MLog.d("Parsing keyURI " + keyURI);
        if (keyURI == null || !keyURI.contains(",")) {
            return keyUriMap;
        }

        String[] parts = keyURI.replace("mss:", "").split(",");
        if (parts.length == 0) {
            parts = new String[] {keyURI.replace("mss:", "")};
        }

        for (String attribute : parts) {
            if (attribute.contains("=")) {
                String[] split = attribute.split("=");
                if (split.length < 2) continue;
                String key   = split[0];
                String value = split[1];
                MLog.d("Parsed " + key + "=" + value);
                keyUriMap.put(key, value);
            } else {
                MLog.d("Ignoring invalid attribute " + attribute);
            }
        }
        MLog.d("Parsed keyURI to " + keyUriMap);
        return keyUriMap;
    }

    public Map<String, String> getAsMap() {
        return keyUriMap;
    }

    public String getName() {
        return this.keyUriMap.get(ALIAS);
    }
    public String getLoa() {
        return this.keyUriMap.get(LOA);
    }
    public String getCountry() {
        return this.keyUriMap.get(COUNTRY);
    }

    /**
     * Check if this KeyURI matches given discovery criteria
     * @param criteria List of criteria
     * @return true if match is found
     */
    @Deprecated
    public boolean matchesCriteria(Map<KeyDiscoveryCriteria, String> criteria) {

        // For every given criteria, check if they match
        for (KeyDiscoveryCriteria c: criteria.keySet()) {
            boolean matches = this.matchesCriteria(c, criteria.get(c));
            // Found nonmatching criteria, return false
            if (!matches) {
                return false;
            }
        }

        // All match
        return true;
    }

    @Deprecated
    private boolean matchesCriteria(KeyDiscoveryCriteria criteria, String value) {
        Log.d("KeyURI", "Comparing " + criteria + " to value " + value);
        switch (criteria) {
            case LEVEL_OF_ASSURANCE:
                return this.compareLoA(value, this.getLoa());
            case COUNTRY:
                return this.compareValue(value, this.getCountry());
        }

        // Unhandled criteria, return true
        return true;
    }

    @Deprecated
    private boolean compareLoA(String criteriaValue, String ownValue) {
        return LoA.compareLoA(ownValue, criteriaValue);
    }

    @Deprecated
    private boolean compareValue(String criteriaValue, String ownValue) {
        return Objects.equals(criteriaValue, ownValue);
    }

    /**
     * Get a String representation of this KeyURI (the actual URI)
     * @return URI
     */
    public String getUri() {
        StringBuilder sb = new StringBuilder("mss:");
        boolean first = true;
        for (String key : this.keyUriMap.keySet()) {
            if (!first) sb.append(",");
            sb.append(key);
            sb.append("=");
            sb.append(this.keyUriMap.get(key));
            first = false;
        }
        return sb.toString();
    }

    /**
     * Check if this KeyURI matches given other KeyURI
     * @param keyUri other KeyURI
     * @return true if match
     */
    public boolean matches(KeyURI keyUri) {
        if (this.equals(keyUri)) return true;
        if (this.getUri().equals(keyUri.getUri())) return true;
        return false;
    }

    @Override
    public String toString() {
        return getUri();
    }
}
