package fi.methics.musap.sdk.internal.datatype;

import static org.junit.Assert.*;

import android.util.Base64;

import org.checkerframework.checker.units.qual.K;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.nio.charset.StandardCharsets;

@RunWith(RobolectricTestRunner.class)
public class KeyAttributeTest {

    @Test
    public void getName() {
        KeyAttribute attr = new KeyAttribute("testname", "testval");
        assertEquals("testname", attr.getName());
    }

    @Test
    public void getValue() {
        KeyAttribute attr = new KeyAttribute("testname", "testval");
        assertEquals("testval", attr.getValue());
    }

    @Test
    public void getValueBytes() {
        KeyAttribute attr = new KeyAttribute("testname", "testval");
        byte[] attrsBytes = Base64.decode(attr.getValue(), Base64.NO_WRAP);
        assertArrayEquals(attrsBytes, attr.getValueBytes());
    }
}