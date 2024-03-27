package fi.methics.musapsdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import fi.methics.musap.sdk.internal.datatype.KeyURI;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapLoA;
import fi.methics.musap.sdk.internal.util.MLog;

/**
 * Tests the KeyURI implementation
 */
public class KeyUriTest {

    @Before
    public void init() {
        MLog.setTestMode(true);
    }

    @Test
    public void testParseKeyUri() {

        KeyURI uri = new KeyURI("keyuri:key?sscd=sim&provider=test");

        assertEquals("sim", uri.getParam("sscd"));
        assertEquals("test", uri.getParam("provider"));
    }

    @Test
    public void testReParseKeyUri() {

        KeyURI uri1 = new KeyURI("keyuri:key?sscd=sim&provider=test");
        KeyURI uri2 = new KeyURI(uri1);
        KeyURI uri3 = new KeyURI(uri2.getUri());

        assertEquals(uri1, uri2);
        assertEquals(uri2, uri3);
    }
    @Test
    public void testParseMusapKey() {

        MusapKey key = new MusapKey.Builder()
                .setKeyAlias("test")
                .setLoa(Arrays.asList(MusapLoA.EIDAS_HIGH, MusapLoA.NIST_AAL3))
                .setKeyUsages(Arrays.asList("digitalSignature", "nonRepudiation"))
                .build();

        KeyURI uri = new KeyURI(key);

        assertEquals("digitalSignature,nonRepudiation", uri.getParam("key-usage"));
        assertEquals("digitalSignature", uri.getParams("key-usage").get(0));
        assertEquals("eidas-high", uri.getParams("loa").get(0));
        assertEquals("test", uri.getParam("key-name"));
    }
    @Test
    public void testParseKeyUriWithMultiValuedParams() {

        KeyURI uri = new KeyURI("keyuri:key?sscd=sim&provider=test&loa=eidas-high,nist-ial3");

        List<String> loa = uri.getParams("loa");

        System.out.println(uri);
        assertEquals(2, loa.size());
        assertTrue(loa.contains("eidas-high"));
        assertTrue(loa.contains("nist-ial3"));
    }

    @Test
    public void testKeyUriSearch() {
        KeyURI uri    = new KeyURI("keyuri:key?sscd=sim&provider=test&loa=eidas-high,nist-ial3");
        KeyURI search = new KeyURI("keyuri:key?loa=eidas-high");
        assertTrue(uri.isPartialMatch(search));
    }

    @Test
    public void testKeyUriSearchNoMatch() {
        KeyURI uri    = new KeyURI("keyuri:key?sscd=sim&provider=test&loa=eidas-high,nist-ial3");
        KeyURI search = new KeyURI("keyuri:key?sscd=remote");
        assertFalse(uri.isPartialMatch(search));
    }

    @Test
    public void testDisplayString() {
        KeyURI uri = new KeyURI("keyuri:key?sscd=sim&provider=test&loa=eidas-high,nist-ial3");
        assertEquals("keyuri:key?provider=test&sscd=sim", uri.getDisplayString("sscd", "provider"));
    }

}