package fi.methics.musap.sdk.internal.encryption.keygenerator;

import android.security.keystore.KeyProtection;

public interface MusapKeyProtection {

    KeyProtection getKeyProtection();
}
