package fi.methics.musap.sdk.internal.datatype;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fi.methics.musap.sdk.internal.util.MLog;

public class KeyURI {

    public static final String SSCD            = "sscd";
    public static final String PROVIDER        = "provider";
    public static final String COUNTRY         = "country";
    public static final String IDENTITY_SCHEME = "identity-scheme";
    public static final String SERIAL          = "serial";
    public static final String MSISDN          = "msisdn";
    public static final String LOA             = "loa";

    public static final String KEY_USAGE       = "key-usage";
    public static final String KEY_NAME        = "key-name";
    public static final String KEY_ALGORITHM   = "key-algorithm";
    public static final String KEY_LENGTH      = "key-length";
    public static final String KEY_PREGEN      = "key-pregenerated";

    public static final String RSA_EXPONENT    = "rsa-public-exponent";
    public static final String ECC_CURVE       = "ecc-curve";
    public static final String CREATED_DATE    = "created-date";

    private Map<String, String> keyUriMap = new HashMap<>();

    /**
     * Create a new KeyURI
     * @param key Key to create the URI form from
     */
    public KeyURI(MusapKey key) {
        if (key.getKeyAlias()  != null) keyUriMap.put(KEY_NAME, key.getKeyAlias());
        if (key.getAlgorithm() != null) {
            this.addParam(KEY_ALGORITHM,  key.getAlgorithm().isEc() ? "EC" : "RSA");
            this.addParam(KEY_LENGTH, key.getAlgorithm().bits + "");
            this.addParam(ECC_CURVE, key.getAlgorithm().curve);
        }
        if (key.getCreatedDate() != null && key.getCreatedDate().toEpochMilli() != 0) {
            this.addParam(CREATED_DATE, key.getCreatedDate().toString().split("T")[0]);
        }

        this.addParam(KEY_USAGE, key.getKeyUsages());
        if (key.getLoa() != null) {
            this.addParam(LOA, key.getLoa().stream().map(l -> l.toString()).collect(Collectors.toList()));
        }
        if (key.getAttributeValue(MSISDN) != null) this.addParam(MSISDN, key.getAttributeValue(MSISDN));
        if (key.getAttributeValue(SERIAL) != null) this.addParam(SERIAL, key.getAttributeValue(SERIAL));

        if (key.getSscdInfo() != null) {
            String sscdName     = key.getSscdInfo().getSscdName();
            String sscdCountry  = key.getSscdInfo().getCountry();
            String sscdProvider = key.getSscdInfo().getProvider();

            if (sscdName     != null) this.addParam(SSCD,     sscdName);
            if (sscdCountry  != null) this.addParam(COUNTRY,  sscdCountry);
            if (sscdProvider != null) this.addParam(PROVIDER, sscdProvider);
        }
    }

    /**
     * Add a new parameter
     * @param name name
     * @param value value
     */
    public void addParam(String name, String ... value) {
        if (value == null) {
            this.keyUriMap.put(name, null);
        } else {
            MLog.d("Adding param " + name + "=" + Arrays.asList(value));
            if (value.length > 1) {
                this.keyUriMap.put(name, String.join(",", value));
            } else if (value.length == 0) {
                this.keyUriMap.put(name, null);
            } else {
                this.keyUriMap.put(name, value[0]);
            }
        }
    }

    /**
     * Add a new parameter
     * @param name name
     * @param value value
     */
    public void addParam(String name, List<String> value) {
        this.addParam(name, value.toArray(new String[0]));
    }

    /**
     * Create a new KeyURI object from a KeyURI String
     * @param keyURI KeyURI
     */
    public KeyURI(String keyURI) {
        this.keyUriMap = this.parseUri(keyURI);
    }

    /**
     * Create a copy of the given KeyURI
     * @param keyURI KeyURI
     */
    public KeyURI(KeyURI keyURI) {
        this(keyURI.getUri());
    }

    private KeyURI(Map<String, String> params) {
        this.keyUriMap = params;
    }

    /**
     * Get a single-valued parameter
     * @param name parameter name
     * @return parameter value
     */
    public String getParam(String name) {
        return this.keyUriMap.get(name);
    }

    /**
     * Get a multi-valued parameter
     * @param name parameter name
     * @return parameter value
     */
    public List<String> getParams(String name) {
        String value = this.keyUriMap.get(name);
        MLog.d("Getting param " + name + "=" +value);
        if (value == null) return Collections.emptyList();
        if (value.contains(",")) {
            return Arrays.asList(value.split(","));
        }
        return Arrays.asList(value);
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

        // Example keyuri:key?algorithm:rsa&sscd=sim
        String[] parts = keyURI.replace("keyuri:key?", "").split("&");
        if (parts.length == 0) {
            parts = new String[] {keyURI.replace("keyuri:key?", "")};
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

    /**
     * Get the Key Name from the KeyURI
     * @return Key Name
     */
    public String getName() {
        return this.keyUriMap.get(KEY_NAME);
    }

    /**
     * Get the Country value from the KeyURI
     * @return Country
     */
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
        StringBuilder sb = new StringBuilder("keyuri:key");
        boolean first = true;
        for (String key : this.keyUriMap.keySet()) {
            if (!first) {
                sb.append("&");
            } else {
                sb.append("?");
            }
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
                if (thisValue == null) {
                    return false;
                }

                String givenValue = param.getValue();
                if (!this.areParamsPartialMatch(thisValue.toLowerCase(), givenValue.toLowerCase())) {
                    MLog.d(String.format("Param %s is not a partial match with %s", thisValue, givenValue));
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Check if this KeyURI parameter is a partial match of searched parameter value.
     * For example, if this parameter has value "a,b", then
     * @param thisParam
     * @param searchParam
     * @return
     */
    private boolean areParamsPartialMatch(String thisParam, String searchParam) {
        // TODO: How are nulls matched? Can these even be null?
        String[] thisArr = thisParam.split(",");
        String[] searchArr = searchParam.split(",");

        Set<String> thisSet = new HashSet<>(Arrays.asList(thisArr));
        Set<String> searchSet = new HashSet<>(Arrays.asList(searchArr));

        return thisSet.containsAll(searchSet);
    }

    private boolean areParamsExactMatch(String[] thisArr, String[] searchArr) {
        Set<String> set1 = new HashSet<>(Arrays.asList(thisArr));
        Set<String> set2 = new HashSet<>(Arrays.asList(searchArr));
        return set1.equals(set2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyURI keyURI = (KeyURI) o;
        return keyUriMap.equals(keyURI.keyUriMap);
    }

    @Override
    public int hashCode() {
        return keyUriMap.hashCode();
    }

    @Override
    public String toString() {
        return getUri();
    }
}
