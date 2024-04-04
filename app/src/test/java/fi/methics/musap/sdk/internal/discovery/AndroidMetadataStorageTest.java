package fi.methics.musap.sdk.internal.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import fi.methics.MockStorage;
import fi.methics.musap.sdk.internal.datatype.KeyAlgorithm;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.SscdInfo;
import fi.methics.musap.sdk.internal.util.MLog;

public class AndroidMetadataStorageTest {

    static AndroidMetadataStorage storage;

    @Before
    public void init() {
        MLog.setTestMode(true);
        storage = new AndroidMetadataStorage(new MockStorage());
    }

    @Test
    public void testAddKey() {
        MLog.setTestMode(true);
        storage = new AndroidMetadataStorage(new MockStorage());
        MusapKey key = new MusapKey.Builder()
                .setKeyAlias("test key")
                .setKeyId("testkeyid")
                .setAlgorithm(KeyAlgorithm.RSA_2K)
                .build();
        SscdInfo info = new SscdInfo.Builder()
                .setSscdName("test sscd")
                .setSscdId("testsscdid")
                .build();
        storage.addKey(key, info);
    }

    @Test
    public void testListKeys() {
        MLog.setTestMode(true);
        storage = new AndroidMetadataStorage(new MockStorage());
        MusapKey key = new MusapKey.Builder()
                .setKeyAlias("test key")
                .setKeyId("testkeyid")
                .setAlgorithm(KeyAlgorithm.RSA_2K)
                .build();
        SscdInfo info = new SscdInfo.Builder()
                .setSscdName("test sscd")
                .setSscdId("testsscdid")
                .build();
        storage.addKey(key, info);

        List<MusapKey> keys = storage.listKeys();

        assertEquals(1, keys.size());
        MusapKey storedKey = keys.get(0);
        assertEquals(key.getKeyId(), storedKey.getKeyId());
        assertEquals(key.getKeyAlias(), storedKey.getKeyAlias());

    }

    @Test
    public void testTestListKeys() {
    }

    @Test
    public void testRemoveKey() {
    }

    @Test
    public void testAddSscd() {
    }

    @Test
    public void testListActiveSscds() {
    }

    @Test
    public void testAddImportData() {
    }

    @Test
    public void testGetImportData() {
    }

    @Test
    public void testUpdateKeyMetaData() {
    }
}