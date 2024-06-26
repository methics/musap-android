package fi.methics;

import android.security.keystore.KeyProtection;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import fi.methics.musap.sdk.internal.security.keystorage.KeyStorage;

/**
 * Mock key storage to use for testing in place of Android key store
 * This reduces dependencies.
 */
public class MockKeyStorage implements KeyStorage {

    private Map<String, SecretKey> keys = new HashMap<>();

    private Map<String, KeyProtection> protections= new HashMap<>();

    @Override
    public void storeKey(String keyName, SecretKey key, KeyProtection protection) throws GeneralSecurityException, IOException {
        this.keys.put(keyName, key);
        this.protections.put(keyName, protection);
    }

    @Override
    public SecretKey loadKey(String keyName) throws GeneralSecurityException, IOException {
        return this.keys.get(keyName);
    }

    @Override
    public boolean keyExists(String keyName) throws GeneralSecurityException, IOException {
        return this.keys.containsKey(keyName);
    }
    public boolean hasProtection(String keyName) {
        return this.protections.containsKey(keyName);
    }

}
