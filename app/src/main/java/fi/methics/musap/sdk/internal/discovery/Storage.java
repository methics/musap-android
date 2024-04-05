package fi.methics.musap.sdk.internal.discovery;

import java.util.Set;

/**
 * Low-level storage for any long duration data that MUSAP generates.
 */
public interface Storage {

    /**
     * Get a string from storage
     * @param key
     * @param defaultValue
     * @return
     */
    String getString(String key, String defaultValue);

    /**
     * Put a string in storage
     * @param key
     * @param value
     */
    Storage putString(String key, String value);

    Storage putStringSet(String key, Set<String> value);

    Set<String> getStringSet(String key, Set<String> defaultValue);

    Storage removeString(String key);
}
