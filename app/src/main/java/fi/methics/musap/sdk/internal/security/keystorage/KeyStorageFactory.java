package fi.methics.musap.sdk.internal.security.keystorage;

public class KeyStorageFactory {

    public static KeyStorage getAndroidKeyStorage() {
        return new AndroidKeyStorage();
    }
}