package fi.methics.musapsdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import fi.methics.musap.sdk.internal.datatype.KeyURI;
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

        System.out.println(uri);
        assertEquals("sim", uri.getParam("sscd"));
        assertEquals("test", uri.getParam("provider"));
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