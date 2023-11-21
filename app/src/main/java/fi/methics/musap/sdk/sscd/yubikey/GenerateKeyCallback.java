package fi.methics.musap.sdk.sscd.yubikey;

import fi.methics.musap.sdk.internal.datatype.MusapKey;

/**
 *
 */
public interface GenerateKeyCallback {

    void callback(MusapKey key);
}
