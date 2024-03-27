package fi.methics.musap.sdk.internal.encryption.keygenerator;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface KeyGenerator {

    String hkdf() throws GeneralSecurityException, IOException;
}