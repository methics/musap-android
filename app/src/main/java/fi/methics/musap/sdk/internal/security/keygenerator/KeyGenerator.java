package fi.methics.musap.sdk.internal.security.keygenerator;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface KeyGenerator {

    String hkdf() throws GeneralSecurityException, IOException;
}