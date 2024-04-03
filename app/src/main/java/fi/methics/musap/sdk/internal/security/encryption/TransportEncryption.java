package fi.methics.musap.sdk.internal.security.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface TransportEncryption {

    PayloadHolder encrypt(String message) throws IOException, GeneralSecurityException;
    PayloadHolder encrypt(String message, String iv) throws IOException, GeneralSecurityException;

    String decrypt(byte[] message, String iv) throws IOException, GeneralSecurityException;

}