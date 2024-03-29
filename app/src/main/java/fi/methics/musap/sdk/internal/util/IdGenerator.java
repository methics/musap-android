package fi.methics.musap.sdk.internal.util;

import java.util.UUID;

/**
 * Generates a UUID for MUSAP purposes
 * . The main purpose of the ID is for MUSAP internal storage.
 */
public class IdGenerator {

    /**
     * Create a random UUID for a new key.
     * @return
     */
    public static String generateKeyId() {
        return UUID.randomUUID().toString();
    }


}
