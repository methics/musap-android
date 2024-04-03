package fi.methics.musap.sdk.internal.encryption.keygenerator;

/*
 * (c) Copyright 2003-2020 Methics Oy. All rights reserved.
 */


import android.security.keystore.KeyProperties;
import android.util.Base64;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import fi.methics.musap.sdk.internal.encryption.keystorage.KeyStorage;
import fi.methics.musap.sdk.internal.encryption.keystorage.KeyStorageFactory;
import fi.methics.musap.sdk.internal.util.HexUtil;
import fi.methics.musap.sdk.internal.util.MLog;


/**
 * Generates transport security and signing keys and stores them to the phone keystore.
 */
public class MusapKeyGenerator implements KeyGenerator {

    // Used in MSSP<->App communication
    public static final String TRANSPORT_KEY_ALIAS = "transportkey";
    public static final String MAC_KEY_ALIAS = "mackey";

    private static final KeyStorage keyStorage = KeyStorageFactory.getAndroidKeyStorage();

    private static final MusapKeyProtection encKeyProtection = new EncryptionKeyProtection();
    private static final MusapKeyProtection authnKeyProtection = new AuthenticationKeyProtection();

    /**
     * Generates a secret, creates authentication and encryption keys using HKDF using it,
     * and stores the keys to the Android keystore.
     * See https://proandroiddev.com/security-best-practices-symmetric-encryption-with-aes-in-java-and-android-part-2-b3b80e99ad36
     * @param useAes256 Use AES-256 in transport encryption key
     * @return Generated secret in base64
     * @throws GeneralSecurityException Cryptographic operation failed, eg due to phone lacking support
     * @throws IOException Failed to open keystore
     */
    public static String hkdfStatic(boolean useAes256) throws GeneralSecurityException, IOException {
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);

        HKDFBytesGenerator hkdf = new HKDFBytesGenerator(new SHA256Digest());
        hkdf.init(new HKDFParameters(key, null, null));

        byte[] macKey = new byte[32];
        byte[] encKey = useAes256 ? new byte[32] : new byte[16];
        byte[] output = new byte[macKey.length+encKey.length];

        hkdf.generateBytes(output, 0, output.length);
        System.arraycopy(output, 0, macKey, 0, macKey.length);
        System.arraycopy(output, macKey.length, encKey, 0, encKey.length);

        SecretKey encryptionKey = new SecretKeySpec(encKey,"AES");
        keyStorage.storeKey(TRANSPORT_KEY_ALIAS,
                encryptionKey,
                encKeyProtection.getKeyProtection());

        SecretKey authenticationKey = new SecretKeySpec(macKey, KeyProperties.KEY_ALGORITHM_HMAC_SHA256);
        keyStorage.storeKey(MAC_KEY_ALIAS,
                authenticationKey,
                authnKeyProtection.getKeyProtection());

        return Base64.encodeToString(key, Base64.NO_WRAP);
    }

    /**
     * Generates a secret, creates authentication and encryption keys using HKDF using it,
     * and stores the keys to the Android keystore.
     * See https://proandroiddev.com/security-best-practices-symmetric-encryption-with-aes-in-java-and-android-part-2-b3b80e99ad36
     * @return Generated secret in base64
     * @throws GeneralSecurityException Cryptographic operation failed, eg due to phone lacking support
     * @throws IOException Failed to open keystore
     */
    public static String hkdfStatic() throws GeneralSecurityException, IOException {
        return hkdfStatic(false);
    }

    @Override
    public String hkdf() throws GeneralSecurityException, IOException {
        return hkdfStatic();
    }


}
