package fi.methics.musap.sdk.internal.datatype;


import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fi.methics.musap.sdk.internal.discovery.KeyDiscoveryCriteria;
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
        if (key.getKeyAlias()     != null) keyUriMap.put(ALIAS,      key.getKeyAlias());
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
    public String getCountry() {
        return this.keyUriMap.get(COUNTRY);
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
