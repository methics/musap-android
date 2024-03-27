package fi.methics.musap.sdk.internal.encryption.keystorage;

public class KeyStorageFactory {

    public static KeyStorage getAndroidKeyStorage() {
        return new AndroidKeyStorage();
    }
}