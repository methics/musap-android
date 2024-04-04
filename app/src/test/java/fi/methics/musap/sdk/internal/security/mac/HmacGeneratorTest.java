package fi.methics.musap.sdk.internal.security.mac;

import android.security.keystore.KeyProtection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.*;

import fi.methics.MockKeyStorage;
import fi.methics.musap.sdk.internal.security.keygenerator.MusapKeyGenerator;

@RunWith(RobolectricTestRunner.class)
public class HmacGeneratorTest {

    private static final String SECRET = "grawlgrawl";
    private static final String MESSAGE = "teststring";
    private static final String IV = "iv";
    private static final String TYPE = "signature";
    private static final String TRANS_ID = "he12";

    //  input = transId + type + iv + message;
    // he12signatureivteststring
    private static final String CORRECT_MAC = "88c0437d77ddcaff4331def8bd453131c77b896b4f604fa2063e32875b9b9e25";


    @Test(expected =  IllegalArgumentException.class)
    public void testValidateNoTransId() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        generator.validate("msg", "iv", null, "type", "mac");
    }

    @Test(expected =  IllegalArgumentException.class)
    public void testValidateNoMessage() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        generator.validate(null, "iv", "transid", "type", "mac");
    }

    @Test(expected =  IllegalArgumentException.class)
    public void testValidateNoIv() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        generator.validate("msg", null, "transid", "type", "mac");
    }

    @Test(expected =  IllegalArgumentException.class)
    public void testValidateNoType() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        generator.validate("msg", "iv", "transid", null, "mac");
    }

    @Test(expected =  IllegalArgumentException.class)
    public void testValidateNoMac() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        generator.validate("msg", "iv", "transid", "type", null);
    }



    @Test
    public void testValidate() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey macKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        storage.storeKey(MusapKeyGenerator.MAC_KEY_ALIAS, macKey, null);

        assertTrue("MAC validates", generator.validate(MESSAGE, IV, TRANS_ID, TYPE, CORRECT_MAC));
    }

    @Test
    public void testValidateWrongMac() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey macKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        storage.storeKey(MusapKeyGenerator.MAC_KEY_ALIAS, macKey, null);

        assertFalse("Wrong MAC does not validate", generator.validate(MESSAGE, IV, TRANS_ID, TYPE, "88c0437d77ddcaff4331def8bd453131c77b896b4f604fa2063e32875b9b9e26"));
    }

    @Test
    public void testValidateWrongMessage() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey macKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        storage.storeKey(MusapKeyGenerator.MAC_KEY_ALIAS, macKey, null);

        assertFalse("Wrong message does not validate", generator.validate("wrong", IV, TRANS_ID, TYPE, CORRECT_MAC));
    }

    @Test
    public void testValidateWrongIv() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey macKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        storage.storeKey(MusapKeyGenerator.MAC_KEY_ALIAS, macKey, null);

        assertFalse("Wrong message does not validate", generator.validate(MESSAGE, "wrong", TRANS_ID, TYPE, CORRECT_MAC));
    }

    @Test
    public void testValidateWrongTransId() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey macKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        storage.storeKey(MusapKeyGenerator.MAC_KEY_ALIAS, macKey, null);

        assertFalse("Wrong message does not validate", generator.validate(MESSAGE, IV, "wrong", TYPE, CORRECT_MAC));
    }


    @Test
    public void testValidateWrongType() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey macKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        storage.storeKey(MusapKeyGenerator.MAC_KEY_ALIAS, macKey, null);

        assertFalse("Wrong message does not validate", generator.validate(MESSAGE, IV, TRANS_ID, "wrong", CORRECT_MAC));
    }

    @Test
    public void testValidateWrongKey() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        byte[] encodedKey = "wrongsecret".getBytes();
        SecretKey macKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        storage.storeKey(MusapKeyGenerator.MAC_KEY_ALIAS, macKey, null);

        assertFalse("Wrong key does not validate", generator.validate(MESSAGE, IV, TRANS_ID, TYPE, CORRECT_MAC));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNoKey() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        generator.validate(MESSAGE, IV, TRANS_ID, TYPE, CORRECT_MAC);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateOnlyTransportKey() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey macKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        storage.storeKey(MusapKeyGenerator.TRANSPORT_KEY_ALIAS, macKey, null);

        generator.validate(MESSAGE, IV, TRANS_ID, TYPE, CORRECT_MAC);
    }

    @Test
    public void testGenerate() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        HmacGenerator generator = new HmacGenerator(storage);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey macKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

        storage.storeKey(MusapKeyGenerator.MAC_KEY_ALIAS, macKey, null);

        String generatedMac = generator.generate(MESSAGE, IV, TRANS_ID, TYPE);
        assertEquals("Generated MAC is correct", CORRECT_MAC, generatedMac);
    }
}