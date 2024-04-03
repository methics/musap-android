package fi.methics.musap.sdk.internal.security.keystorage;

import android.security.keystore.KeyProtection;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.SecretKey;

public interface KeyStorage {

    void storeKey(String keyName, SecretKey key, KeyProtection protection) throws GeneralSecurityException, IOException;
    SecretKey loadKey(String keyName) throws GeneralSecurityException, IOException;
    boolean keyExists(String keyName) throws GeneralSecurityException, IOException;
}
