package fi.methics.musap.sdk.internal.util;

import static org.junit.Assert.*;

import org.junit.Test;

import fi.methics.MockStorage;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.datatype.RelyingParty;

public class MusapStorageTest {

    @Test
    public void storeRelyingParty() {
        MLog.setTestMode(true);

        MockStorage storage = new MockStorage();

        MusapStorage musapStorage = new MusapStorage(storage);

        RelyingParty rp = new RelyingParty("testname", "testlinkid");
        musapStorage.storeRelyingParty(rp);
    }

    @Test
    public void removeRelyingParty() {
        MLog.setTestMode(true);
        MockStorage storage = new MockStorage();
        MusapStorage musapStorage = new MusapStorage(storage);
        RelyingParty rp = new RelyingParty("testname", "testlinkid");
        musapStorage.storeRelyingParty(rp);

        musapStorage.removeRelyingParty(rp);
        assertEquals(0, musapStorage.listRelyingParties().size());
    }

    @Test
    public void removeLink() {
        MLog.setTestMode(true);

        MockStorage storage = new MockStorage();
        MusapStorage musapStorage = new MusapStorage(storage);

        MusapLink link = new MusapLink("http://noop", "testid");
        musapStorage.storeLink(link);
        MusapLink storedLink = musapStorage.getMusapLink();
        assertNotNull(storedLink);
        musapStorage.removeLink();
        storedLink = musapStorage.getMusapLink();
        assertNull(storedLink);
    }

    @Test
    public void listRelyingParties() {
        MLog.setTestMode(true);
        MockStorage storage = new MockStorage();
        MusapStorage musapStorage = new MusapStorage(storage);
        RelyingParty rp = new RelyingParty("testname", "testlinkid");
        musapStorage.storeRelyingParty(rp);

        assertEquals(1, musapStorage.listRelyingParties().size());
    }

    @Test
    public void storeLink() {
        MLog.setTestMode(true);

        MockStorage storage = new MockStorage();
        MusapStorage musapStorage = new MusapStorage(storage);

        MusapLink link = new MusapLink("http://noop", "testid");
        musapStorage.storeLink(link);
    }

    @Test
    public void getMusapLink() {
        MLog.setTestMode(true);

        MockStorage storage = new MockStorage();
        MusapStorage musapStorage = new MusapStorage(storage);

        MusapLink link = new MusapLink("http://noop", "testid");
        musapStorage.storeLink(link);
        MusapLink storedLink = musapStorage.getMusapLink();
        assertNotNull(storedLink);
    }

    @Test
    public void getMusapId() {
        MLog.setTestMode(true);

        MockStorage storage = new MockStorage();
        MusapStorage musapStorage = new MusapStorage(storage);

        MusapLink link = new MusapLink("http://noop", "testid");
        musapStorage.storeLink(link);
        String musapId = musapStorage.getMusapId();
        assertEquals("testid", musapId);
    }
}