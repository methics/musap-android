package fi.methics;

import android.security.keystore.KeyProtection;

import fi.methics.musap.sdk.internal.security.keygenerator.MusapKeyProtection;

public class MockKeyProtection implements MusapKeyProtection {

    private int purpose;

    public MockKeyProtection(int purpose) {
        this.purpose = purpose;
    }

    @Override
    public KeyProtection getKeyProtection() {
        // For testing purposes we can use null
        return null;
    }

    public int getPurpose() {
        return this.purpose;
    }
}
