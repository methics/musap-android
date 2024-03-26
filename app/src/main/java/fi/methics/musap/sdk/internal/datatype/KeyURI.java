package fi.methics.musap.sdk.internal.datatype;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        if (key.getAttributeValue(MSISDN) != null) keyUriMap.put(MSISDN, key.getAttributeValue(MSISDN));
        if (key.getAttributeValue(SERIAL) != null) keyUriMap.put(MSISDN, key.getAttributeValue(SERIAL));

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

    private KeyURI(Map<String, String> params) {
        this.keyUriMap = params;
    }

    /**
     * Parse a KeyURI
     * @param keyURI
     * @return
     */
    private Map<String, String> parseUri(String keyURI) {
        Map<String, String> keyUriMap = new HashMap<>();
        MLog.d("Parsing keyURI " + keyURI);
//        if (keyURI == null || !keyURI.contains("&")) {
//            return keyUriMap;
//        }

        // Example keyuri:key?algorithm:rsa2k&sscd=SIM
        String[] parts = keyURI.replace("keyuri:key?", "").split("&");
        if (parts.length == 0) {
            parts = new String[] {keyURI.replace("keyuri:key?", "")};
        }

        for (String attribute : parts) {
            if (attribute.contains(":")) {
                String[] split = attribute.split(":");
                if (split.length < 2) continue;
                String key   = split[0];
                String value = split[1];
                MLog.d("Parsed " + key + ":" + value);
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
     * Get this URI in a display format with only requested params
     * If no params are given, returns the whole URI
     * @param params Which params the display form URI should contain
     * @return
     */
    public String getDisplayString(String... params) {
        if (params == null || params.length == 0) {
            return this.getUri();
        }

        Map<String, String> subParams = new HashMap<>();

        for (String param: params) {
            if (param == null) {
                continue;
            }
            if (this.keyUriMap.containsKey(param)) {
                subParams.put(param, this.keyUriMap.get(param));
            }
        }

        return new KeyURI(subParams).getUri();
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

    /**
     * Check if this KeyURI is a partial match of another KeyURI.
     * Partial match is defined as
     * 1. This KeyURI has all parameters of the given KeyURI
     * 2. For matching parameters, the parameter value of this KeyURI contains
     *    all comma-separated values of the given KeyURI.
     * For example, if this KeyURI is keyuri:key?algorithm:rsa2k&sscd=SIM,
     * and parameters KeyURI is keyuri:key?algorithm:rsa2k, this is a partial match
     * @param keyURI
     * @return
     */
    public boolean isPartialMatch(KeyURI keyURI) {
        for (Map.Entry<String, String> param : keyURI.getAsMap().entrySet()) {
            if (!this.keyUriMap.containsKey(param.getKey())) {
                MLog.d("This KeyURI does not have param " + param.getKey());
                return false;
            } else {
                String thisValue = this.keyUriMap.get(param.getKey());
                String givenValue = param.getValue();
                if (!this.areParamsPartialMatch(thisValue, givenValue)) {
                    MLog.d(String.format("Param %s is not a partial match with %s", thisValue, givenValue));
                    return false;
                }
            }
        }
        return true;
    }


    private boolean areParamsPartialMatch(String thisParam, String searchParam) {
        // TODO: How are nulls matched? Can these even be null?
        String[] thisArr = thisParam.split(",");
        String[] searchArr = searchParam.split(",");

        Set<String> thisSet = new HashSet<>(Arrays.asList(thisArr));
        Set<String> searchSet = new HashSet<>(Arrays.asList(searchArr));

        // Check if the sets are equal
        return thisSet.containsAll(searchSet);
    }

    private boolean areParamsExactMatch(String[] thisArr, String[] searchArr) {
        Set<String> set1 = new HashSet<>(Arrays.asList(thisArr));
        Set<String> set2 = new HashSet<>(Arrays.asList(searchArr));
        return set1.equals(set2);
    }

    @Override
    public String toString() {
        return getUri();
    }
}
