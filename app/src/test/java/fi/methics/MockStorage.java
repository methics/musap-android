package fi.methics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fi.methics.musap.sdk.internal.discovery.Storage;

/**
 * Mock implementation of the low level storage interface.
 * This allows testing with no framework dependencies.
 */
public class MockStorage implements Storage {

    private Map<String, String> storage = new HashMap<>();
    private Map<String, Set<String>> setStorage = new HashMap<>();

    @Override
    public String getString(String key, String defaultValue) {
        return this.storage.getOrDefault(key, defaultValue);
    }

    @Override
    public Storage putString(String key, String value) {
        this.storage.put(key, value);
        return this;
    }

    @Override
    public Storage putStringSet(String key, Set<String> value) {
        this.setStorage.put(key, value);
        return this;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return this.setStorage.getOrDefault(key, defaultValue);
    }

    @Override
    public Storage removeString(String key) {
        this.storage.remove(key);
        this.setStorage.remove(key);
        return this;
    }
}
