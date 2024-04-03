package fi.methics.musap.sdk.internal.security.keystorage;

/*
 * (c) Copyright 2003-2020 Methics Oy. All rights reserved.
 */

import android.security.keystore.KeyProtection;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.crypto.SecretKey;

public class AndroidKeyStorage implements KeyStorage {


    @Override
    public void storeKey(String keyName, SecretKey key, KeyProtection protection) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        keyStore.setEntry(keyName,
                new KeyStore.SecretKeyEntry(key),
                protection);
    }

    @Override
    public SecretKey loadKey(String keyName) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return (SecretKey) keyStore.getKey(keyName, null);
    }

    @Override
    public boolean keyExists(String keyName) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        SecretKey key  = (SecretKey) keyStore.getKey(keyName, null);
        return key != null;
    }
}
