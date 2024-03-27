package fi.methics.musap.sdk.internal.encryption.keygenerator;

import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;

public class AuthenticationKeyProtection implements MusapKeyProtection {

    @Override
    public KeyProtection getKeyProtection() {
        return new KeyProtection.Builder(
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .build();
    }
}