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
    public void testListKeysWithSearchReq() {
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

        KeySearchReq searchReq = new KeySearchReq.Builder()
                .setKeyAlgorithm(KeyAlgorithm.RSA_2K)
                .build();

        List<MusapKey> keys = storage.listKeys(searchReq);

        assertEquals(1, keys.size());
        MusapKey storedKey = keys.get(0);
        assertEquals(key.getKeyId(), storedKey.getKeyId());
        assertEquals(key.getKeyAlias(), storedKey.getKeyAlias());
    }

    @Test
    public void testRemoveKey() {
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

        storage.removeKey(key);
        assertEquals(0, storage.listKeys().size());
    }

    @Test
    public void testAddSscd() {
        MLog.setTestMode(true);
        storage = new AndroidMetadataStorage(new MockStorage());

        SscdInfo info = new SscdInfo.Builder()
                .setSscdName("test sscd")
                .setSscdId("testsscdid")
                .build();

        storage.addSscd(info);

    }

    @Test
    public void testListActiveSscds() {
        MLog.setTestMode(true);
        storage = new AndroidMetadataStorage(new MockStorage());

        SscdInfo info = new SscdInfo.Builder()
                .setSscdName("test sscd")
                .setSscdId("testsscdid")
                .build();

        assertEquals(0, storage.listActiveSscds().size());
        storage.addSscd(info);
        List<SscdInfo> sscdInfos = storage.listActiveSscds();
        assertEquals(1, sscdInfos.size());
    }


    @Test
    public void testUpdateKeyMetaData() {


    }
}