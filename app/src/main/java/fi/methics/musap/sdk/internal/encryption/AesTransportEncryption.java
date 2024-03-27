/*
 * (c) Copyright 2003-2020 Methics Oy. All rights reserved.
 */

package fi.methics.musap.sdk.internal.encryption;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import fi.methics.musap.sdk.internal.encryption.keygenerator.AlaudaKeyGenerator;
import fi.methics.musap.sdk.internal.encryption.keystorage.KeyStorage;


public class AesTransportEncryption implements TransportEncryption {

    private final String TAG = AesTransportEncryption.class.getSimpleName();

    private final KeyStorage keyStorage;
    private final String cipher;

    public AesTransportEncryption(KeyStorage storage) {
        this.keyStorage = storage;
        this.cipher = KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7;
    }

    // Unit tests have a problem with PKCS7yyy padding, so allow use of custom cipher to use PKCS5
    public AesTransportEncryption(KeyStorage storage, String cipher) {
        this.keyStorage = storage;
        this.cipher = cipher;
    }

    @Override
    public EncryptedPayload encrypt(String message) throws GeneralSecurityException, IOException {
        return this.encrypt(message, null);
    }

    @Override
    public EncryptedPayload encrypt(String message, String iv) throws IOException, GeneralSecurityException {
        if (message == null) {
            throw new IllegalArgumentException("Missing data for encryption");
        }

        Cipher cipher = this.initCipher(Cipher.ENCRYPT_MODE, iv);
        byte[] encrypted = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return new EncryptedPayload(Base64.encodeToString(encrypted, Base64.NO_WRAP),
                Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP));
    }

    @Override
    public String decrypt(byte[] encryptedMessage, String iv)
            throws IOException, GeneralSecurityException {
        if (encryptedMessage == null || iv == null) {
            throw new IllegalArgumentException("Missing data for decryption");
        }

        Cipher cipher = this.initCipher(Cipher.DECRYPT_MODE, iv);
        byte[] decrypted = cipher.doFinal(encryptedMessage);
        return new String(decrypted);
    }

    private Cipher initCipher(int mode, String ivBase64) throws GeneralSecurityException, IOException {

        SecretKey transportSecurityKey = this.keyStorage.loadKey(AlaudaKeyGenerator.TRANSPORT_KEY_ALIAS);

        if (transportSecurityKey == null) {
            throw new IllegalArgumentException("You must generate a transport security key first");
        }

        Cipher cipher = Cipher.getInstance(this.cipher);
        cipher.init(mode, transportSecurityKey, new IvParameterSpec(this.generateIV(ivBase64)));

        return cipher;
    }

    /**
     * Generate IV. If IV is given as a base64 string, decode. Otherwise generate a new
     * random 16 bit IV.
     * @return IV
     */
    private byte[] generateIV(String ivBase64) {
        byte[] iv ;
        if (ivBase64 == null) {
            SecureRandom secureRandom = new SecureRandom();
            iv = new byte[16];
            secureRandom.nextBytes(iv);
        } else {
            iv = Base64.decode(ivBase64, Base64.NO_WRAP);
        }

        return iv;
    }
}
