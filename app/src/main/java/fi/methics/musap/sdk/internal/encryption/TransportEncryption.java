package fi.methics.musap.sdk.internal.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface TransportEncryption {

    EncryptedPayload encrypt(String message) throws IOException, GeneralSecurityException;
    EncryptedPayload encrypt(String message, String iv) throws IOException, GeneralSecurityException;

    String decrypt(byte[] message, String iv) throws IOException, GeneralSecurityException;

}