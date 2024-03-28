package fi.methics.musap.sdk.internal.encryption.mac;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface MacGenerator {

    /**
     * Generate a MAC.
     * @param message Alauda message to calculate a MAC for.
     * @param transId Message transid. Needed to prevent replay attacks.
     * @param type Message type.
     * @return Generated MAC.
     * @throws IOException Failed to access keystore.
     * @throws GeneralSecurityException Cryptograpchic exception, eg not supported algorithm.
     */
    String generate(String message, String iv, String transId, String type) throws IOException, GeneralSecurityException;

    /**
     * Validate the MAC of a message.
     * @param message Alauda message to calculate a MAC for.
     * @param transId Message transid. Needed to prevent replay attacks.
     * @param type Message type.
     * @param mac MAC of the given message.
     * @return True if the calculated MAC is the same as the given MAC.
     */
    boolean validate(String message, String transId, String iv, String type, String mac) throws GeneralSecurityException, IOException;
}

