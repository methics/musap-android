package fi.methics.musap.sdk.internal.security.encryption;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import fi.methics.MockKeyStorage;
import fi.methics.musap.sdk.internal.security.keygenerator.MusapKeyGenerator;
import fi.methics.musap.sdk.internal.util.MLog;

@RunWith(RobolectricTestRunner.class)
public class AesTransportEncryptionTest {
    private static final String MESSAGE = "teststring";

    // Expected encryption result calculated with https://www.devglan.com/online-tools/aes-encryption-decryption
    private static final String ENCRYPTED = "WS1XGISmwEXYo60botyTPA==";

    private static final String IV = "MTIzNDU2Nzg5ODc2NTQzMg==";
    private static final String SECRET = "1234123456789878";

    // Java does not natively support PKCS7, so use 5 for testing
    private static final String CIPHER = "AES/CBC/PKCS5Padding";


    @Before
    public void init() {
        MLog.setTestMode(true);
    }

    @Test
    public void testEncrypt() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        AesTransportEncryption encryption = new AesTransportEncryption(storage, CIPHER);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey encKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        storage.storeKey(MusapKeyGenerator.TRANSPORT_KEY_ALIAS, encKey, null);

        PayloadHolder encrypted = encryption.encrypt(MESSAGE, IV);

        assertEquals("Output is AES encrypted", ENCRYPTED, encrypted.getPayload());
        assertEquals("Payload contains IV", IV, encrypted.getIv());
    }

    @Test
    public void testDecrypt() throws IOException, GeneralSecurityException {
        MockKeyStorage storage = new MockKeyStorage();
        AesTransportEncryption encryption = new AesTransportEncryption(storage, CIPHER);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey encKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        storage.storeKey(MusapKeyGenerator.TRANSPORT_KEY_ALIAS, encKey, null);

        String decrypted = encryption.decrypt(Base64.getDecoder().decode(ENCRYPTED), IV);

        assertEquals("Output is decrypted", MESSAGE, decrypted);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncryptNoKey() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        AesTransportEncryption encryption = new AesTransportEncryption(storage, CIPHER);

        encryption.encrypt(MESSAGE, IV);
    }

    @Test
    public void testDifferentIv() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        AesTransportEncryption encryption = new AesTransportEncryption(storage, CIPHER);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey encKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        storage.storeKey(MusapKeyGenerator.TRANSPORT_KEY_ALIAS, encKey, null);

        PayloadHolder encrypted = encryption.encrypt(MESSAGE, "MTIzNDU2Nzg5ODc2NTQzNg==");

        assertNotEquals("Different IV changes encryption result", ENCRYPTED, encrypted.getPayload());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecryptNullIv() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        AesTransportEncryption encryption = new AesTransportEncryption(storage, CIPHER);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey encKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        storage.storeKey(MusapKeyGenerator.TRANSPORT_KEY_ALIAS, encKey, null);

        encryption.decrypt(Base64.getDecoder().decode(ENCRYPTED), null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecryptNullMessage() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        AesTransportEncryption encryption = new AesTransportEncryption(storage, CIPHER);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey encKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        storage.storeKey(MusapKeyGenerator.TRANSPORT_KEY_ALIAS, encKey, null);

        encryption.decrypt(null, IV);
    }

    @Test
    public void testIvNotReused() throws IOException, GeneralSecurityException {
        MockKeyStorage storage = new MockKeyStorage();
        AesTransportEncryption encryption = new AesTransportEncryption(storage, CIPHER);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey encKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        storage.storeKey(MusapKeyGenerator.TRANSPORT_KEY_ALIAS, encKey, null);

        PayloadHolder encrypted1 = encryption.encrypt(MESSAGE);

        assertNotNull("Payload contains IV", encrypted1.getIv());

        PayloadHolder encrypted2 = encryption.encrypt(MESSAGE);

        assertNotEquals("Same IV is not used twice", encrypted1.getIv(), encrypted2.getIv());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEncryptNullMessage() throws GeneralSecurityException, IOException {
        MockKeyStorage storage = new MockKeyStorage();
        AesTransportEncryption encryption = new AesTransportEncryption(storage, CIPHER);

        byte[] encodedKey = SECRET.getBytes();
        SecretKey encKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        storage.storeKey(MusapKeyGenerator.TRANSPORT_KEY_ALIAS, encKey, null);

        encryption.encrypt(null);
    }

}