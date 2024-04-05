package fi.methics.musap.sdk.internal.security.keygenerator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.security.GeneralSecurityException;

import fi.methics.MockKeyProtection;
import fi.methics.MockKeyStorage;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class MusapKeyGeneratorTest {

    @Test
    public void testHkdfGeneratesKeys() throws GeneralSecurityException, IOException {
        MockKeyStorage keyStorage = new MockKeyStorage();

        // TODO: maybe check that correct purpose is used
        int encPurp = 1;
        int authnPurp = 2;

        MusapKeyGenerator.setKeyStorage(keyStorage);

        MockKeyProtection encProt = new MockKeyProtection(encPurp);
        MockKeyProtection authnProt = new MockKeyProtection(authnPurp);

        MusapKeyGenerator.setEncryptionKeyProtection(encProt);
        MusapKeyGenerator.setAuthenticationKeyProtection(authnProt);

        String secret = MusapKeyGenerator.hkdfStatic();

        assertNotNull(secret);
        assertFalse("Secret is a nonempty string", secret.isEmpty());

        assertTrue("Transport key exists", keyStorage.keyExists(MusapKeyGenerator.TRANSPORT_KEY_ALIAS));
        assertNotNull(keyStorage.loadKey(MusapKeyGenerator.TRANSPORT_KEY_ALIAS));
        assertTrue("Transport key has key protection", keyStorage.hasProtection(MusapKeyGenerator.TRANSPORT_KEY_ALIAS));

        assertTrue("Authn key exists", keyStorage.keyExists(MusapKeyGenerator.MAC_KEY_ALIAS));
        assertNotNull(keyStorage.loadKey(MusapKeyGenerator.MAC_KEY_ALIAS));
        assertTrue("Authn key has key protection", keyStorage.hasProtection(MusapKeyGenerator.MAC_KEY_ALIAS));
    }

}