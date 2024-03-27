package fi.methics.musap.sdk.internal.encryption.keygenerator;

import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;

public class EncryptionKeyProtection implements MusapKeyProtection {

    @Override
    public KeyProtection getKeyProtection() {
        return new KeyProtection.Builder(
                KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setRandomizedEncryptionRequired(false)
                .build();
    }
}